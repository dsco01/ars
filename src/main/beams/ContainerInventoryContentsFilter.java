package mods.immibis.ars.beams;

import mods.immibis.core.BasicInventory;
import mods.immibis.core.api.util.BaseContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerInventoryContentsFilter extends BaseContainer<TileInventoryContentsFilter> {

	public ContainerInventoryContentsFilter(EntityPlayer player, TileInventoryContentsFilter inv) {
		super(player, inv);
		
		for(int x = 0; x < 9; x++)
			addSlotToContainer(new Slot(player.inventory, x, 8 + 18*x, 198));
		
		for(int y = 0; y < 3; y++)
			for(int x = 0; x < 9; x++)
				addSlotToContainer(new Slot(player.inventory, x + y*9 + 9, 8 + 18*x, 140 + 18*y));
		
		addSlotToContainer(new Slot(inv, 0, 80, 31));
	}

	@Override
	public ItemStack transferStackInSlot(int slot) {
		if(slot == 36)
			BasicInventory.mergeStackIntoRange(inv, player.inventory, 0, 0, 36);
		else
			BasicInventory.mergeStackIntoRange(player.inventory, inv, slot, 0, 1);
		return null;
	}
}
