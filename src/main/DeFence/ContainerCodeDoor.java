package mods.immibis.ars.DeFence;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerCodeDoor extends Container
{
    public TileEntityCodeDoor tileEntity;

	public ContainerCodeDoor(EntityPlayer entityplayer, TileEntityCodeDoor tileentity) 
    {
		tileEntity = tileentity;

        for (int i = 0; i < 3; i++)
        {
            for (int k = 0; k < 9; k++)
            {
                addSlotToContainer(new Slot(entityplayer.inventory, k + i * 9 + 9, 8 + k * 18, 99 + i * 18));
            }
        }

        for (int j = 0; j < 9; j++)
        {
            addSlotToContainer(new Slot(entityplayer.inventory, j, 8 + j * 18, 157));
        }
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return tileEntity.isUseableByPlayer(entityplayer);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int i) {
	    ItemStack itemstack = null;
	    Slot slot = (Slot)this.inventorySlots.get(i);
	
	    if (slot != null && slot.getHasStack())
	    {
	        ItemStack itemstack1 = slot.getStack();
	        itemstack = itemstack1.copy();
	
	        if (i < inventorySlots.size() - 9)
	        {
	        	transferToSlots(itemstack1, 0, 1, false);
	        }
	        else if (i >= inventorySlots.size() - 9 && i < inventorySlots.size())
	        {
	            transferToSlots(itemstack1, 0, inventorySlots.size() - 9, false);
	        }
	
	        if (itemstack1.stackSize == 0)
	        {
	            slot.putStack(null);
	        }
	        else
	        {
	            slot.onSlotChanged();
	        }
	
	        if (itemstack1.stackSize != itemstack.stackSize)
	        {
	        	slot.onPickupFromSlot(player, itemstack1);
	        }
	        else
	        {
	            return null;
	        }
	    }
	
	    return itemstack;
	}

	/**
	 * used internally
	 */
	public void transferToSlots(ItemStack itemstack, int i, int j,
		boolean flag) {
		    mergeItemStack(itemstack, i, j, flag);
		}
}
