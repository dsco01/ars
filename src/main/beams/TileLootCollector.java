package mods.immibis.ars.beams;


import java.util.Arrays;

import mods.immibis.ars.ARSMod;
import mods.immibis.core.BasicInventory;
import mods.immibis.core.api.util.NBTType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileLootCollector extends TileBeamEmitter implements IInventory, ISidedInventory {
	private UpgradeData upgrade;
	
	@Override public boolean canUpdate() {return false;}
	
	private BasicInventory inv = new BasicInventory(10);
	public static final int SLOT_FILTER = 0;
	public static final int SLOT_INV = 1;
	private static final int[] ACCESSIBLE_SLOTS = new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9};
	
	public TileLootCollector() {
		upgrade = new UpgradeData();
		upgrade.lootCollectors = Arrays.asList(this);
	}
	
	@Override
	public boolean onBlockActivated(EntityPlayer player) {
		if(!worldObj.isRemote)
			player.openGui(ARSMod.instance, ARSMod.GUI_LOOT_COLLECTOR, worldObj, xCoord, yCoord, zCoord);
		return true;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setTag("inv", inv.writeToNBT());
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		inv.readFromNBT(tag.getTagList("inv", NBTType.COMPOUND));
	}
	
	@Override
	public Object getOutput() {
		return upgrade;
	}
	
	@Override
	public int getBeamColour() {return 1;}

	public boolean collectItem(EntityItem destroyed) {
		if(!ARSMod.areItemsEqual(destroyed.getEntityItem(), inv.contents[SLOT_FILTER])) {
			return false;
		}
		
		for(int k = 0; k < 9; k++) {
			int slot = SLOT_INV + k;
			ItemStack is = inv.contents[slot];
			if(is == null) {
				inv.contents[slot] = destroyed.getEntityItem().copy();
				destroyed.getEntityItem().stackSize = 0;
				return true;
				
			} else if(ARSMod.areItemsEqual(destroyed.getEntityItem(), is)) {
				int max = destroyed.getEntityItem().getMaxStackSize();
				int add = Math.min(max - is.stackSize, destroyed.getEntityItem().stackSize);
				destroyed.getEntityItem().stackSize -= add;
				is.stackSize += add;
				if(destroyed.getEntityItem().stackSize == 0)
					return true;
			}
		}
		
		return false;
		
		/*if(!tryDropItem(item, xCoord - 1, yCoord, zCoord, false)
		&& !tryDropItem(item, xCoord + 1, yCoord, zCoord, false)
		&& !tryDropItem(item, xCoord, yCoord - 1, zCoord, false)
		&& !tryDropItem(item, xCoord, yCoord + 1, zCoord, false)
		&& !tryDropItem(item, xCoord, yCoord, zCoord - 1, false)
		&& !tryDropItem(item, xCoord, yCoord, zCoord + 1, false))
		{
			EntityItem _new = new EntityItem(worldObj, xCoord + 0.5, yCoord + 1.5, zCoord + 0.5,item);
			_new.motionY = 0.25; // 5 ms^-1
			worldObj.spawnEntityInWorld(_new);
		}*/
	}
	
	/*private boolean tryDropItem(ItemStack item, int x, int y, int z, boolean checkingDoubleChest) {
		TileEntity te = worldObj.getBlockTileEntity(x, y, z);
		if(checkingDoubleChest && !(te instanceof TileEntityChest))
			return false;
		
		if(te instanceof IInventory) {
			IInventory inv = (IInventory)te;
			int size = inv.getSizeInventory();
			int max = item.getMaxStackSize();
			
			int freeSlot = -1;
			
			for(int k = 0; k < size; k++) {
				ItemStack slot = inv.getStackInSlot(k);
				if(slot != null && mod_AdvancedRepulsionSystems.areItemsEqual(slot, item)) {
					int left = Math.min(max - slot.stackSize, item.stackSize);
					if(left > 0) {
						slot.stackSize += left;
						item.stackSize -= left;
						inv.setInventorySlotContents(k, slot);
						if(item.stackSize == 0)
							return true;
					}
				} else if(slot == null && freeSlot == -1)
					freeSlot = k;
			}
			
			if(freeSlot != -1) {
				inv.setInventorySlotContents(freeSlot, item);
				item.stackSize = 0;
				return true;
			}
		}
		if(item.stackSize == 0)
			return true;
		
		if(checkingDoubleChest || !(te instanceof TileEntityChest))
			return false;
		
		return tryDropItem(item, x+1, y, z, true)
		    || tryDropItem(item, x-1, y, z, true)
		    || tryDropItem(item, x, y, z+1, true)
		    || tryDropItem(item, x, y, z-1, true);
	}*/

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
		return inv.decrStackSize(var1, var2);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int var1) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int var1, ItemStack var2) {
		inv.contents[var1] = var2;
	}

	@Override
	public String getInventoryName() {
		return "Loot collector";
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer var1) {
		return var1.getDistanceSq(xCoord+0.5, yCoord+0.5, zCoord+0.5) <= 64;
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
		return true;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		return ACCESSIBLE_SLOTS;
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		return false;
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return true;
	}
}
