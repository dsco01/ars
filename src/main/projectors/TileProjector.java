package mods.immibis.ars.projectors;


import java.util.Random;

import mods.immibis.ars.*;
import mods.immibis.core.api.util.NBTType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

public abstract class TileProjector extends TileEntityMaschines {
	
	// for field shape CoordinateLists
	public static final int MODE_FIELD = 1; // force field block
	public static final int MODE_GAP = 2; // pokes holes in existing forcefields from the same generator
	public static final int MODE_INHIBITOR = 3;
	public static final int MODE_INSIDE = 4; // inside a forcefield but does not make holes
	
	public static int maxOffset, cooldownBlocksPerTick;
	
	private ItemStack ProjektorItemStacks[];

	protected boolean camouflage = false;
	protected boolean zapper = false;
	protected boolean subwater = false;
	protected boolean dome = false;
	protected boolean hardner = false;
	protected boolean inhibitor = false;
	
	private int Projektor_ID;
	private int linkGenerator_ID;
	private boolean linkGenerator;
	private int linkPower;
	private int maxlinkPower;
	private short ticker;
	//private int fpcost;
	private boolean create;
	private int energy_ticker = 0;
	private short ffmeta;
	private int textur;
	
	public int offsetX, offsetY, offsetZ;
	
	protected short specialffmeta = (short)-1;
	
	@Override
	public void handleBaseUpdate(int[] p) {
		Projektor_ID = p[0];
		linkGenerator_ID = p[1];
		linkPower = p[2];
		maxlinkPower = p[3];
		textur = p[4];
	}
	
	@Override
	public int[] getBaseUpdate() {
		return new int[] {
			Projektor_ID,
			linkGenerator_ID,
			linkPower,
			maxlinkPower,
			textur
		};
	}

	public TileProjector() {
		Random random = new Random();

		ProjektorItemStacks = new ItemStack[1];
		linkGenerator_ID = 0;
		Projektor_ID = random.nextInt();
		linkGenerator = false;
		linkPower = 0;
		maxlinkPower = 1000000;
		ticker = 0;
		create = true;
		textur = -1;
		ffmeta = 0;
	}
	
	public final void addtogrid() {
		if(worldObj.isRemote) throw new RuntimeException("do not call this on smp clients");
		Linkgrid.getWorldMap(worldObj).getProjektor().put(getProjektor_ID(), this);
	}

	public final void removefromgrid() {
		if(worldObj.isRemote) throw new RuntimeException("do not call this on smp clients");
		Linkgrid.getWorldMap(worldObj).getProjektor().remove(getProjektor_ID());
	}
	
	// Can be approximate, but not too far off, since it's used to calculate
	// power consumption
	// (area projectors double-count edges and triple-count corners and always
	// assume cube shape in this; Thunderdark's version did the same thing)
	public abstract int estimateBlockCount();
	
	protected boolean acceptsUpgradeType(int meta) {
		if(meta == BlockUpgrades.META_CAMO) return true;
		if(meta == BlockUpgrades.META_ZAPPER) return true;
		if(meta == BlockUpgrades.META_HARDNER) return true;
		return false;
	}
	
	public void checkupgrades() {
		
		subwater = false;
		dome = false;
		hardner = false;
		zapper = false;
		camouflage = false;
		inhibitor = false;

		for (int x = xCoord - 1; x <= xCoord + 1; x++) {
			for (int y = yCoord - 1; y <= yCoord + 1; y++) {
				for (int z = zCoord - 1; z <= zCoord + 1; z++) {

					if (worldObj.getBlock(x, y, z) == ARSMod.MFFSUpgrades) {

						int meta = worldObj.getBlockMetadata(x, y, z);

						TileEntity te = worldObj.getTileEntity(x, y, z);
						if (acceptsUpgradeType(meta) && te instanceof TileUpgradePassive) {
							
							TileUpgradePassive upgrade = (TileUpgradePassive)te;
							if (upgrade.getconectet_ID() == 0) {
								upgrade.setconectet_ID(getProjektor_ID());
								upgrade.setConnectet_typID((short) 2);
								worldObj.markBlockForUpdate(upgrade.xCoord, upgrade.yCoord, upgrade.zCoord);
							}

							if (meta == BlockUpgrades.META_CAMO) {
								TileUpgradeCamouflage camo = (TileUpgradeCamouflage)upgrade;
								if (camo.getItem_ID() != getTextur())
									this.setTextur(camo.getItem_ID());
							}

							if (upgrade.getconectet_ID() == getProjektor_ID() && upgrade.getConnectet_typID() == 2) {

								if (upgrade.getActive() != this.getActive()) {
									upgrade.setActive(this.getActive());
								}
								
								switch (meta) {
								case BlockUpgrades.META_INHIBITOR:
									inhibitor = true;
									break;
								case BlockUpgrades.META_SUBWATER:
									subwater = true;
									break;
								case BlockUpgrades.META_HARDNER:
									hardner = true;
									break;
								case BlockUpgrades.META_DOME:
									dome = true;
									break;
								case BlockUpgrades.META_ZAPPER:
									zapper = true;
									break;
								case BlockUpgrades.META_CAMO:
									camouflage = true;
									break;
								}
							}
						}
					}
				}
			}
		}
		
		if(specialffmeta != -1)
			setffmeta(specialffmeta);
		else if(isCamouflage() && isZapper())
			setffmeta((short)BlockForceField.META_ZAPPER_CAMO);
		else if(isCamouflage())
			setffmeta((short)BlockForceField.META_CAMO);
		else if(isZapper())
			setffmeta((short)BlockForceField.META_ZAPPER);
		else
			setffmeta((short)BlockForceField.META_NORMAL);
	}
	
	private CoordinateList fieldBlocks = null; // all blocks that are part of the field, regardless of mode
	private CoordinateList refreshBlocks = null; // all blocks that need refreshing each tick
	
	private FFShape ffShape;
	
	protected final void getFieldShape(CoordinateList list) {
		ffShape.getFieldBlocks(list);
		
		if(ARSMod.DEBUG_MODE) {
			CoordinateList.CoordIterator it = list.iterate();
			while(it.hasNext()) {
				it.next();
				int bm = ffShape.getBlockMode(it.x, it.y, it.z);
				if(bm != it.mode)
					System.err.println(ffShape+" has inconsistent mode at "+it.x+","+it.y+","+it.z+" (in block list "+it.mode+", in getBlockMode "+bm+")");
			}
		}
	}
	
	public FFShape getShape() {
		return ffShape;
	}
	
	protected abstract FFShape getFieldShape();

	private final boolean needsRefreshing(int mode) {
		return mode == MODE_FIELD;
	}
	
	protected final void createField() {
		ffShape = getFieldShape();
		
		if(ffShape != null)
			FFWorld.get(worldObj).addShape(ffShape);
		
		fieldBlocks = new CoordinateList(estimateBlockCount());
		getFieldShape(fieldBlocks);
		
		{
			int k = 0;
			CoordinateList.CoordIterator it = fieldBlocks.iterate();
			while(it.hasNext()) {
				it.next();
				if(needsRefreshing(it.mode))
					k++;
			}
			
			refreshBlocks = new CoordinateList(k);
			it = fieldBlocks.iterate();
			while(it.hasNext()) {
				it.next();
				if(needsRefreshing(it.mode))
					refreshBlocks.add(it.x, it.y, it.z, it.mode);
			}
			
			refreshIterator = refreshBlocks.iterate();
		}
		
		CoordinateList.CoordIterator it = fieldBlocks.iterate();

		while(it.hasNext()) {
			it.next();
			
			FFBlock ffblock = FFWorld.get(worldObj).addOrGet(it.x, it.y, it.z);
			
			ffblock.addEntry(new FFBlock.Entry(getffmeta(), getLinkGenerator_ID(), getProjektor_ID(), it.mode, isBlockBreaker(), getTextur(), activatedTime));
		}
	}
	
	public void onActivateProjector() {}
	public void onDeactivateProjector() {}
	public void onEveryTick() {}
	public void onFieldTick() {}
	public boolean overrideActivationStatus(boolean redstone) {return redstone;}
	
	@Override
	public void updateEntity() {
		if(!worldObj.isRemote) {
			
			if(toggleCooldown > 0)
				toggleCooldown--;

			if (this.isCreate() && this.getLinkGenerator_ID() != 0) {
				addtogrid();
				addfreqcard();
				this.setCreate(false);
				if (this.getActive()) {
					checkupgrades();
					onActivateProjector();
					createField();
				}
			}

			if (this.getLinkGenerator_ID() != 0) {
				this.setLinkGenerator(true);
				try {
					this.setLinkPower(Linkgrid.getWorldMap(worldObj).getGenerator().get(this.getLinkGenerator_ID()).getForcepower());
					this.setMaxlinkPower(Linkgrid.getWorldMap(worldObj).getGenerator().get(this.getLinkGenerator_ID()).getMaxforcepower());
				} catch (java.lang.NullPointerException ex) {
					this.setLinkGenerator(false);
					this.setLinkPower(0);
					this.setMaxlinkPower(1000000);
				}
			} else {
				this.setLinkGenerator(false);
				this.setLinkPower(0);
				this.setMaxlinkPower(1000000);
			}

			boolean powerdirekt = worldObj.getBlockPowerInput(xCoord, yCoord, zCoord) > 0;
			boolean powerindrekt = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
			
			boolean shouldActivate = overrideActivationStatus(powerdirekt || powerindrekt);

			if (shouldActivate && this.isLinkGenerator() && this.getLinkPower() > Forcepowerneed(estimateBlockCount(), true)) {

				if (getActive() != true && checkToggleCooldown()) {
					setActive(true);
					createField();
					FieldGenerate(true);
					onActivateProjector();
					worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				}
			}
			if (!shouldActivate || !this.isLinkGenerator() || this.getLinkPower() < Forcepowerneed(estimateBlockCount(), false)) {

				if (getActive() != false && checkToggleCooldown()) {
					setActive(false);
					onDeactivateProjector();
					destroyField();
					worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				}
			}

			if (getActive() && getWrenchDropRate() != -1.0F) {
				setWrenchRate(-1.0F);
			}
			if (!getActive() && getWrenchDropRate() != 1.0F) {
				setWrenchRate(1.0F);
			}
			
			onEveryTick();

			if (getActive()) {
				FieldGenerate(false);
			}

			if (this.getTicker() == 10) {
				checkupgrades();
				addfreqcard();
				onFieldTick();
				this.setTicker((short) 0);
			}
			this.setTicker((short) (this.getTicker() + 1));
		}
	}
	
	private int toggleCooldown = 0;
	
	private boolean checkToggleCooldown() {
		if(toggleCooldown > 0)
			return false;
		toggleCooldown = estimateBlockCount() / cooldownBlocksPerTick;
		return true;
	}

	public final void destroyField() {
		
		if(ffShape != null) {
			FFWorld.get(worldObj).removeShape(ffShape);
			ffShape = null;
		}
		
		if(fieldBlocks == null)
			return;
		
		CoordinateList.CoordIterator it = fieldBlocks.iterate();
		while(it.hasNext()) {
			it.next();
			
			FFBlock ffblock = FFWorld.get(worldObj).get(it.x, it.y, it.z);
			if(ffblock != null)
				ffblock.removeEntry(getProjektor_ID());
		}
	}

	public boolean isCreate() {
		return create;
	}

	public void setCreate(boolean create) {
		this.create = create;
	}

	public short getTicker() {
		return ticker;
	}

	public void setTicker(short ticker) {
		this.ticker = ticker;
	}

	public int getTextur() {
		return textur;
	}

	public void setTextur(int textur) {
		this.textur = textur;
		baseUpdateCount++;
	}

	public short getffmeta() {
		return ffmeta;
	}

	public void setffmeta(short ffmeta) {
		this.ffmeta = ffmeta;
	}

	public int getMaxlinkPower() {
		return maxlinkPower;
	}

	public void setMaxlinkPower(int maxlinkPower) {
		this.maxlinkPower = maxlinkPower;
		baseUpdateCount++;
	}

	public boolean isLinkGenerator() {
		return linkGenerator;
	}

	public void setLinkGenerator(boolean linkGenerator) {
		this.linkGenerator = linkGenerator;
		baseUpdateCount++;
	}

	public int getLinkPower() {
		return linkPower;
	}

	public void setLinkPower(int linkPower) {
		this.linkPower = linkPower;
		baseUpdateCount++;
	}

	public int getLinkGenerator_ID() {
		return linkGenerator_ID;
	}

	public void setLinkGenerator_ID(int linkGenerator_ID) {
		this.linkGenerator_ID = linkGenerator_ID;
		baseUpdateCount++;
	}

	public boolean isCamouflage() {
		return camouflage;
	}
	public boolean isZapper() {
		return zapper;
	}
	public boolean isSubwater() {
		return subwater;
	}
	public boolean isDome() {
		return dome;
	}
	public boolean isBlockBreaker() {
		return hardner;
	}
	public boolean isInhibitor() {
		return inhibitor;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {

		super.readFromNBT(nbttagcompound);
		Projektor_ID = nbttagcompound.getInteger("Projektor_ID");
		ffmeta = nbttagcompound.getShort("ffmeta");
		textur = nbttagcompound.getInteger("textur");
		
		offsetX = nbttagcompound.getInteger("offsetX");
		offsetY = nbttagcompound.getInteger("offsetY");
		offsetZ = nbttagcompound.getInteger("offsetZ");
		
		toggleCooldown = nbttagcompound.getInteger("toggleCooldown");

		NBTTagList nbttaglist = nbttagcompound.getTagList("Items", NBTType.COMPOUND);
		ProjektorItemStacks = new ItemStack[getSizeInventory()];
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			byte byte0 = nbttagcompound1.getByte("Slot");
			if (byte0 >= 0 && byte0 < ProjektorItemStacks.length) {
				ProjektorItemStacks[byte0] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
			}
		}

	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {

		super.writeToNBT(nbttagcompound);
		nbttagcompound.setInteger("Projektor_ID", Projektor_ID);
		nbttagcompound.setShort("ffmeta", ffmeta);
		nbttagcompound.setInteger("textur", textur);
		
		nbttagcompound.setInteger("offsetX", offsetX);
		nbttagcompound.setInteger("offsetY", offsetY);
		nbttagcompound.setInteger("offsetZ", offsetZ);
		
		nbttagcompound.setInteger("toggleCooldown", toggleCooldown);
		
		NBTTagList nbttaglist = new NBTTagList();
		for (int i = 0; i < ProjektorItemStacks.length; i++) {
			if (ProjektorItemStacks[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				ProjektorItemStacks[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}

		nbttagcompound.setTag("Items", nbttaglist);
	}

	public int getProjektor_ID() {
		return Projektor_ID;
	}
	
	private CoordinateList.CoordIterator refreshIterator;

	public void FieldGenerate(boolean init) {
		
		if(fieldBlocks == null) {
			createField();
		}

		//int blockcounter = 0;
		//int maxnbockcounter = 400;

		if (init || energy_ticker == 20) {
			CoordinateList.CoordIterator it = fieldBlocks.iterate();
			while(it.hasNext()) {
				it.next();
				
				FFBlock ffblock = FFWorld.get(worldObj).get(it.x, it.y, it.z);
				if(ffblock != null)
					ffblock.useEnergyFor(getProjektor_ID());
			}

			energy_ticker = 0;
		}
		else
		{
			energy_ticker++;
			if(ARSMod.slowRefresh)
				return; 
		}
		
		int left = ARSMod.refreshSpeed;
		if(left == 0)
			left = Integer.MAX_VALUE;
		
		CoordinateList.CoordIterator it = refreshIterator;
		
		FFWorld ffworld = FFWorld.get(worldObj);
		
		while(it.hasNext() && (left > 0)) {
			it.next();
			
			left--;
			
			FFBlock ffblock = ffworld.get(it.x, it.y, it.z);
			if(ffblock != null)
				ffblock.refresh();
		}
		
		if(!it.hasNext())
			refreshIterator = refreshBlocks.iterate();
	}

	public int Forcepowerneed(int blocks, boolean init) {
		int forcepower;
		forcepower = blocks * ARSMod.forcefieldblockcost;
		if (init) {
			forcepower = (forcepower * ARSMod.forcefieldblockcreatemodifier) + (forcepower * 5);
		}
		return forcepower;
	}

	// card function

	public void addfreqcard() {

		if (getStackInSlot(0) != null) {
			if (getStackInSlot(0).getItem() == ARSMod.MFFSitemfc) {

				if (linkGenerator_ID != Functions.getTAGfromItemstack(getStackInSlot(0)).getInteger("Generator_ID")) {
					linkGenerator_ID = Functions.getTAGfromItemstack(getStackInSlot(0)).getInteger("Generator_ID");
				}

			}
		} else {
			linkGenerator_ID = 0;
		}

	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (ProjektorItemStacks[i] != null) {
			if (ProjektorItemStacks[i].stackSize <= j) {
				ItemStack itemstack = ProjektorItemStacks[i];
				ProjektorItemStacks[i] = null;
				return itemstack;
			}
			ItemStack itemstack1 = ProjektorItemStacks[i].splitStack(j);
			if (ProjektorItemStacks[i].stackSize == 0) {
				ProjektorItemStacks[i] = null;
			}
			return itemstack1;
		} else {
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		ProjektorItemStacks[i] = itemstack;
		if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
			itemstack.stackSize = getInventoryStackLimit();
		}
	}

	public boolean canInteractWith(EntityPlayer entityplayer) {
		if (worldObj.getTileEntity(xCoord, yCoord, zCoord) != this) {
			return false;
		} else {
			return entityplayer.getDistanceSq((double) xCoord + 0.5D, (double) yCoord + 0.5D, (double) zCoord + 0.5D) <= 64D;
		}
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return ProjektorItemStacks[i];
	}

	@Override
	public String getInventoryName() {

		return "Projektor";
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public int getSizeInventory() {
		return ProjektorItemStacks.length;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		if (worldObj.getTileEntity(xCoord, yCoord, zCoord) != this) {
			return false;
		} else {
			return entityplayer.getDistance((double) xCoord + 0.5D, (double) yCoord + 0.5D, (double) zCoord + 0.5D) <= 64D;
		}
	}

	@Override
	public void openInventory() {

	}

	@Override
	public void closeInventory() {

	}
	
	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return true;
	}
	
	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

}
