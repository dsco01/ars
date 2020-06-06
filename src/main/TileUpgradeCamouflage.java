package mods.immibis.ars;

import mods.immibis.core.api.util.NBTType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class TileUpgradeCamouflage extends TileUpgradePassive
		implements IInventory {

	private ItemStack CamoflageItemStacks[];
	private int ItemID;

	public TileUpgradeCamouflage() {

		CamoflageItemStacks = new ItemStack[1];
		ItemID = -1;

	}
	
	@Override
	public int[] getUpdate() {
		return new int[] {ItemID};
	}
	
	@Override
	public void handleUpdate(int[] p) {
		ItemID = p[0];
	}

	public void setItem_ID(int itemID) {
		if(ItemID != itemID)
			updateCount++;
		ItemID = itemID;
	}

	public int getItem_ID() {
		return ItemID;
	}

	@Override
	public void updateEntity() {

		if (!worldObj.isRemote && this.getconectet_ID() != 0) {
			ItemStack stack = getStackInSlot(0);
			if (stack != null && stack.getItem() instanceof ItemBlock) {
				setItem_ID(TileCamouflagedField.getCamoID(Item.getIdFromItem(stack.getItem()), stack.getItemDamage()));
			} else {
				setItem_ID(-1);
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {

		super.readFromNBT(nbttagcompound);
		ItemID = nbttagcompound.getInteger("ItemID");

		NBTTagList nbttaglist = nbttagcompound.getTagList("Items", NBTType.COMPOUND);
		CamoflageItemStacks = new ItemStack[getSizeInventory()];
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			byte byte0 = nbttagcompound1.getByte("Slot");
			if (byte0 >= 0 && byte0 < CamoflageItemStacks.length) {
				CamoflageItemStacks[byte0] = ItemStack
						.loadItemStackFromNBT(nbttagcompound1);
			}
		}

	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {

		super.writeToNBT(nbttagcompound);
		nbttagcompound.setInteger("ItemID", ItemID);

		NBTTagList nbttaglist = new NBTTagList();
		for (int i = 0; i < CamoflageItemStacks.length; i++) {
			if (CamoflageItemStacks[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				CamoflageItemStacks[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}

		nbttagcompound.setTag("Items", nbttaglist);
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (CamoflageItemStacks[i] != null) {
			if (CamoflageItemStacks[i].stackSize <= j) {
				ItemStack itemstack = CamoflageItemStacks[i];
				CamoflageItemStacks[i] = null;
				return itemstack;
			}
			ItemStack itemstack1 = CamoflageItemStacks[i].splitStack(j);
			if (CamoflageItemStacks[i].stackSize == 0) {
				CamoflageItemStacks[i] = null;
			}
			return itemstack1;
		} else {
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		CamoflageItemStacks[i] = itemstack;
		if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
			itemstack.stackSize = getInventoryStackLimit();
		}
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return CamoflageItemStacks[i];
	}

	@Override
	public String getInventoryName() {

		return "Camoflageupgrade";
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public int getSizeInventory() {
		return CamoflageItemStacks.length;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		if (worldObj.getTileEntity(xCoord, yCoord, zCoord) != this) {
			return false;
		} else {

			return entityplayer.getDistance((double) xCoord + 0.5D,
					(double) yCoord + 0.5D, (double) zCoord + 0.5D) <= 64D;

		}
	}

	@Override
	public void openInventory() {

	}

	@Override
	public void closeInventory() {

	}

	@Override
	public ItemStack getStackInSlotOnClosing(int var1) {
		return null;
	}

}
