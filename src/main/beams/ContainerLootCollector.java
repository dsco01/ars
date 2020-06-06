package mods.immibis.ars.beams;

import mods.immibis.core.BasicInventory;
import mods.immibis.core.api.util.BaseContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerLootCollector extends BaseContainer<TileLootCollector> {

	public ContainerLootCollector(EntityPlayer player, TileLootCollector inv) {
		super(player, inv);
		
		for(int x = 0; x < 9; x++)
			addSlotToContainer(new Slot(player.inventory, x, 8 + 18*x, 198));
		
		for(int y = 0; y < 3; y++)
			for(int x = 0; x < 9; x++)
				addSlotToContainer(new Slot(player.inventory, x + y*9 + 9, 8 + 18*x, 140 + 18*y));
		
		for(int x = 0; x < 3; x++)
			for(int y = 0; y < 3; y++)
				addSlotToContainer(new Slot(inv, x + y*3 + TileLootCollector.SLOT_INV, 62 + x*18, 75 + y*18));
		
		addSlotToContainer(new Slot(inv, TileLootCollector.SLOT_FILTER, 80, 31));
	}

	@Override
	public ItemStack transferStackInSlot(int slot) {
		if(slot < 36) {
			if(inv.getStackInSlot(TileLootCollector.SLOT_FILTER) == null) {
				ItemStack is = player.inventory.getStackInSlot(slot);
				if(is == null)
					return null;
				is.stackSize--;
				if(is.stackSize == 0)
					player.inventory.setInventorySlotContents(slot, null);
				else
					player.inventory.setInventorySlotContents(slot, is);
				
				is = is.copy();
				is.stackSize = 1;
				inv.setInventorySlotContents(TileLootCollector.SLOT_FILTER, is);
				return null;
			}
			
			BasicInventory.mergeStackIntoRange(player.inventory, inv, slot, TileLootCollector.SLOT_INV, TileLootCollector.SLOT_INV+9);
			return null;

		} else {
			Slot sl = (Slot)inventorySlots.get(slot);
			sl.putStack(BasicInventory.mergeStackIntoRange(sl.getStack(), player.inventory, 0, 36));
			return null;
		}
	}
}
