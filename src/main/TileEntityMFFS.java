package mods.immibis.ars;

import mods.immibis.core.api.porting.PortableTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public abstract class TileEntityMFFS extends PortableTileEntity implements IInventory {
	public void handleButton(int id) {}
	public int[] getUpdate() {return null;}
	public void handleUpdate(int[] p) {}
	public int[] getBaseUpdate() {return null;}
	public void handleBaseUpdate(int[] p) {}
	
	public int updateCount;
	public int baseUpdateCount;
	
	@Override public int getSizeInventory() {return 0;}
	@Override public ItemStack getStackInSlot(int var1) {return null;}
	@Override public ItemStack decrStackSize(int var1, int var2) {return null;}
	@Override public ItemStack getStackInSlotOnClosing(int var1) {return null;}
	@Override public void setInventorySlotContents(int var1, ItemStack var2) {}
	@Override public String getInventoryName() {return "MFFS";}
	@Override public int getInventoryStackLimit() {return 64;}
	@Override public void openInventory() {}
	@Override public void closeInventory() {}

	@Override
	public boolean isUseableByPlayer(EntityPlayer var1) {
		return worldObj.getTileEntity(xCoord, yCoord, zCoord) == this && var1.getDistanceSq(xCoord+0.5, yCoord+0.5, zCoord+0.5) <= 64;
	}
	
	public ItemStack getWrenchDrop(EntityPlayer arg0) {
		return new ItemStack(worldObj.getBlock(xCoord, yCoord, zCoord), 1, worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
	}
}
