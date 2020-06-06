package mods.immibis.ars.beams;

import mods.immibis.ars.ARSMod;
import mods.immibis.core.BasicInventory;
import mods.immibis.core.api.util.NBTType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileProgrammableFilter extends TileBeamEmitter implements IInventory {
	public static final int SLOT_COL1 = 0;
	public static final int SLOT_COL2 = 6;
	public static final int SLOT_COL3 = 9;
	public static final int SLOT_FINAL = 10;
	
	private static class TopologyNode {
		public final int filterSlot;
		public final TopologyNode[] inputs;
		public TopologyNode(int filterSlot, TopologyNode... inputs) {
			this.filterSlot = filterSlot;
			this.inputs = inputs;
		}
	}
	// Rooted at col3
	private static TopologyNode topology = new TopologyNode(SLOT_COL3,
		new TopologyNode(SLOT_COL2 + 0, new TopologyNode(SLOT_COL1 + 0), new TopologyNode(SLOT_COL1 + 1)),
		new TopologyNode(SLOT_COL2 + 1, new TopologyNode(SLOT_COL1 + 2), new TopologyNode(SLOT_COL1 + 3)),
		new TopologyNode(SLOT_COL2 + 2, new TopologyNode(SLOT_COL1 + 4), new TopologyNode(SLOT_COL1 + 5)));
	
	private LogicType getLTInSlot(int slot) {
		return LogicType.getForItem(inv.contents[slot]);
	}
	
	private EntityFilter buildFilter(TopologyNode root) {
		LogicType lt = getLTInSlot(root.filterSlot);
		if(lt == null)
			return null;
		
		EntityFilter[] ins = new EntityFilter[root.inputs.length];
		for(int k = 0; k < ins.length; k++)
			ins[k] = buildFilter(root.inputs[k]);
		
		return lt.createFilter(this, ins);
	}
	
	@Override
	public Object getOutput() {
		LogicType finalLT = getLTInSlot(SLOT_FINAL);
		if(finalLT == null)
			return buildFilter(topology);
		else if(!inputsOnRight)
			return finalLT.createFilter(this, new EntityFilter[] {buildFilter(topology)});
		else {
			EntityFilter[] ins = new EntityFilter[7];
			ins[0] = buildFilter(topology);
			for(int k = 0; k < 6; k++) {
				Object o = getInput(k);
				if(o instanceof EntityFilter)
					ins[k+1] = (EntityFilter)o;
			}
			return finalLT.createFilter(this, ins);
		}
	}
	
	public TileProgrammableFilter() {
		
	}
	
	@Override
	public void updateEntity() {
		if(worldObj.isRemote)
			return;
		
		
	}
	
	@Override
	public boolean onBlockActivated(EntityPlayer player) {
		if(!worldObj.isRemote)
			player.openGui(ARSMod.instance, ARSMod.GUI_UPGRADE_UNIT, worldObj, xCoord, yCoord, zCoord);
		return true;
	}
	
	private BasicInventory inv = new BasicInventory(11);
	
	public boolean inputsOnRight;
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setTag("inv", inv.writeToNBT());
		tag.setBoolean("oldInputs", inputsOnRight);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		inv.readFromNBT(tag.getTagList("inv", NBTType.COMPOUND));
		if(tag.hasKey("oldInputs"))
			inputsOnRight = tag.getBoolean("oldInputs");
		else
			inputsOnRight = true;

		// Seems redundant, but actually initializes the part cache
		//for(int k = 0; k < 11; k++)
			//setInventorySlotContents(k, inv.contents[k]);
	}

	@Override
	public int getSizeInventory() {
		return inv.contents.length;
	}

	@Override
	public ItemStack getStackInSlot(int var1) {
		return inv.contents[var1];
	}

	@Override
	public ItemStack decrStackSize(int var1, int var2) {
		ItemStack rv = inv.decrStackSize(var1, var2);
		//setInventorySlotContents(var1, inv.contents[var2]); // update part cache
		return rv;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int var1) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int var1, ItemStack var2) {
		inv.contents[var1] = var2;
		/*if(var2 == null || var2.getItem() != mod_AdvancedRepulsionSystems.itemLogicCard)
			parts[var1].setLT(null);
		else {
			LogicType<?> lt = LogicType.get(var2.getItemDamage());
			if(lt != null && lt.getBDClass() != EntityFilter.class)
				lt = null;
			parts[var1].setLT((LogicType<EntityFilter>)lt);
		}
		
		blockOut = (parts[SLOT_FINAL].lt != null ? parts[SLOT_FINAL].output
				: parts[SLOT_COL3].lt != null ? parts[SLOT_COL3].output
				: null);*/
	}

	@Override
	public String getInventoryName() {
		return "TileUpgradeUnit";
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer var1) {
		return var1.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) <= 64;
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return false;
	}
}
