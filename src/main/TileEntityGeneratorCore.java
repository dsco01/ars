package mods.immibis.ars;

import mods.immibis.core.api.traits.IEnergyConsumerTrait;
import mods.immibis.core.api.traits.IEnergyConsumerTrait.EnergyUnit;
import mods.immibis.core.api.traits.IEnergyConsumerTraitUser;
import mods.immibis.core.api.traits.TraitField;
import mods.immibis.core.api.traits.UsesTraits;
import mods.immibis.core.api.util.NBTType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

@UsesTraits
public class TileEntityGeneratorCore extends TileEntityMaschines implements IInventory, IEnergyConsumerTraitUser {
	public static int FP_PER_EU = 10; // Forcepower per EU
	
	@Override public EnergyUnit EnergyConsumer_getPreferredUnit() {return EnergyUnit.EU;}
	@Override public double EnergyConsumer_getPreferredBufferSize() {return 0;}
	@Override public boolean EnergyConsumer_isBufferingPreferred() {return false;}
	
	@TraitField
	private IEnergyConsumerTrait energyConsumer;

	private ItemStack inventory[];
	private int forcepower;
	private int maxforcepower;
	private short transmitrange;
	private int Generator_ID;
	private boolean create;
	private short linketprojektor;
	private byte delayupdate = 0;

	public TileEntityGeneratorCore() {

		inventory = new ItemStack[1];
		transmitrange = 8;
		forcepower = 0;
		maxforcepower = 10000000;
		Generator_ID = 0;
		linketprojektor = 0;
		create = true;

	}
	
	public ItemStack getCodedCard() {
		ItemStack is = new ItemStack(ARSMod.MFFSitemfc);
		Functions.getTAGfromItemstack(is).setInteger("Generator_ID", Generator_ID);
		return is;
	}

	public void freqencoding() {

		if(!worldObj.isRemote) {

			if (getStackInSlot(0) != null) {
				if (getStackInSlot(0).getItem() == ARSMod.MFFSitemcardempty && getStackInSlot(0).stackSize == 1) {

					this.setInventorySlotContents(0, getCodedCard());
					this.markDirty();

				}
			}
		}
	}

	public void setMaxforcepower(int maxforcepower) {
		this.maxforcepower = maxforcepower;
		updateCount++;
	}

	public int getMaxforcepower() {
		return maxforcepower;
	}

	public Short getLinketprojektor() {
		return linketprojektor;
	}

	public void setLinketprojektor(Short linketprojektor) {
		this.linketprojektor = linketprojektor;
		updateCount++;
	}

	public void addtogrid() {
		if(!worldObj.isRemote) {
			// Linkgrid.getWorldMap(worldObj).getGenerator().put(getGenerator_ID(),
			// this);
		}
	}

	public void removefromgrid() {
		if(!worldObj.isRemote) {
			Linkgrid.getWorldMap(worldObj).getGenerator().remove(getGenerator_ID());
		}
	}

	public int gaugeFuelScaled(int i) {
		return (i * this.getForcepower()) / maxforcepower;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {

		super.readFromNBT(nbttagcompound);
		
		energyConsumer.readFromNBT(nbttagcompound);
		
		forcepower = nbttagcompound.getInteger("forcepower");
		maxforcepower = nbttagcompound.getInteger("maxforcepower");
		transmitrange = nbttagcompound.getShort("transmitrange");
		Generator_ID = nbttagcompound.getInteger("Generator_ID");

		NBTTagList nbttaglist = nbttagcompound.getTagList("Items", NBTType.COMPOUND);
		inventory = new ItemStack[getSizeInventory()];
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			byte byte0 = nbttagcompound1.getByte("Slot");
			if (byte0 >= 0 && byte0 < inventory.length) {
				inventory[byte0] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
			}
		}

	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		
		energyConsumer.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("forcepower", forcepower);
		nbttagcompound.setInteger("maxforcepower", maxforcepower);
		nbttagcompound.setShort("transmitrange", transmitrange);
		nbttagcompound.setInteger("Generator_ID", Generator_ID);

		NBTTagList nbttaglist = new NBTTagList();
		for (int i = 0; i < inventory.length; i++) {
			if (inventory[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				inventory[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}

		nbttagcompound.setTag("Items", nbttaglist);
	}

	public void Energylost(int fpcost) {

		if (this.getForcepower() >= 0) {
			this.setForcepower(this.getForcepower() - fpcost);
		}
		if (this.getForcepower() < 0) {
			this.setForcepower(0);
		}

	}

	public void CheckconUprades() {
		short temp_transmitrange = (short)ARSMod.core_range_default;
		int temp_maxforcepower = ARSMod.core_storage_default;

		for (int xoffset = -1; xoffset <= +1; xoffset++) {
			for (int zoffset = -1; zoffset <= +1; zoffset++) {

				if (!(xoffset == 0 && zoffset == 0)) {

					TileEntity te = worldObj.getTileEntity(xCoord + xoffset, yCoord, zCoord + zoffset);
					if (te instanceof TileUpgradePassive) {
						
						TileUpgradePassive upgrade = (TileUpgradePassive)te;

						int meta = worldObj.getBlockMetadata(xCoord + xoffset, yCoord, zCoord + zoffset);

						if (upgrade.getconectet_ID() == 0 && (meta == BlockUpgrades.META_CORE_STORAGE || meta == BlockUpgrades.META_CORE_RANGE)) {
							upgrade.setconectet_ID(Generator_ID);
							upgrade.setConnectet_typID((short) 1);

							worldObj.markBlockForUpdate(upgrade.xCoord, upgrade.yCoord, upgrade.zCoord);
						}

						if (upgrade.getconectet_ID() == this.Generator_ID) {

							if (upgrade.getActive() != this.getActive()) {
								upgrade.setActive(this.getActive());
							}

							switch (meta) {
							case BlockUpgrades.META_CORE_STORAGE:
								temp_maxforcepower += ARSMod.core_storage_upgrade_amount;
								break;
							case BlockUpgrades.META_CORE_RANGE:
								temp_transmitrange = (short)(temp_transmitrange * 2);
								//temp_transmitrange += mod_AdvancedRepulsionSystems.core_range_upgrade_amount;
								break;
							}

						}
					}
				}
			}
		}

		if (this.getTransmitrange() != temp_transmitrange) {
			this.setTransmitrange(temp_transmitrange);

		}
		if (this.getMaxforcepower() != temp_maxforcepower) {
			this.setMaxforcepower(temp_maxforcepower);
		}
		if (this.getForcepower() > this.maxforcepower) {
			this.setForcepower(maxforcepower);
		}

	}
	
	@Override
	public void updateEntity() {

		if(!worldObj.isRemote) {
			
			{
				int move = (maxforcepower - forcepower) / FP_PER_EU;
				move = Math.min(move, (int)energyConsumer.getStoredEnergy());
				forcepower += (int)(energyConsumer.useEnergy(0, move) * FP_PER_EU);
			}

			if (create) {

				if (Generator_ID == 0) {
					Generator_ID = Linkgrid.getWorldMap(worldObj).newGenerator_ID(this);
					Linkgrid.getWorldMap(worldObj).getGenerator().put(getGenerator_ID(), this);
				} else {
					Linkgrid.getWorldMap(worldObj).getGenerator().put(getGenerator_ID(), this);
				}
				create = false;
			}

			if (delayupdate == 10) {
				CheckconUprades();
				delayupdate = 0;
			} else {
				delayupdate++;
			}

			setLinketprojektor((short) Linkgrid.getWorldMap(worldObj).conProjektors(getGenerator_ID(), xCoord, yCoord, zCoord, getTransmitrange()));

			boolean powerdirekt = worldObj.getBlockPowerInput(xCoord, yCoord, zCoord) > 0;
			boolean powerindrekt = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);

			if (powerdirekt || powerindrekt) {

				if (getActive() != true) {
					setActive(true);
				}
			} else {
				if (getActive() != false) {
					setActive(false);
				}
			}

			freqencoding();
		}
		if (getActive() && getWrenchDropRate() != -1.0F) {
			setWrenchRate(-1.0F);
		}
		if (!getActive() && getWrenchDropRate() != 1.0F) {
			setWrenchRate(1.0F);
		}
	}

	public int getForcepower() {
		return forcepower;
	}

	public void setForcepower(int f) {

		forcepower = f;
		updateCount++;
	}

	public void setTransmitrange(short transmitrange) {

		this.transmitrange = transmitrange;
		updateCount++;
	}

	public short getTransmitrange() {
		return transmitrange;
	}

	public int getGenerator_ID() {
		return Generator_ID;
	}

	@Override
	public int getSizeInventory() {
		return inventory.length;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return inventory[i];
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
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
	public ItemStack decrStackSize(int i, int j) {
		if (inventory[i] != null) {
			if (inventory[i].stackSize <= j) {
				ItemStack itemstack = inventory[i];
				inventory[i] = null;
				return itemstack;
			}
			ItemStack itemstack1 = inventory[i].splitStack(j);
			if (inventory[i].stackSize == 0) {
				inventory[i] = null;
			}
			return itemstack1;
		} else {
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		inventory[i] = itemstack;
		if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
			itemstack.stackSize = getInventoryStackLimit();
		}
	}

	@Override
	public void openInventory() {

	}

	@Override
	public String getInventoryName() {

		return "GeneratorCore";
	}

	@Override
	public void closeInventory() {

	}

	@Override
	public ItemStack getStackInSlotOnClosing(int var1) {
		return null;
	}

	@Override
	public int[] getUpdate() {
		return new int[] {forcepower, maxforcepower, (transmitrange & 0xffff) | (linketprojektor << 16)};
	}

	@Override
	public void handleUpdate(int[] p)  {
		forcepower = p[0];
		maxforcepower = p[1];
		transmitrange = (short)p[2];
		linketprojektor = (short)(p[2] >> 16);
	}

	
	
	
	
	
	@Override
	public void invalidate() {
		super.invalidate();
		energyConsumer.onInvalidate();
	}
	
	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		energyConsumer.onChunkUnload();
	}
	
	@Override
	public void validate() {
		super.validate();
		energyConsumer.onValidate();
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return true;
	}

}
