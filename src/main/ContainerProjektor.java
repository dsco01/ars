package mods.immibis.ars;

import mods.immibis.ars.projectors.TileProjector;
import mods.immibis.core.BasicInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerProjektor extends ContainerMFFS {
	
	public ContainerProjektor(EntityPlayer player, TileProjector tileentity) {
		super(player, tileentity);
		addSlotToContainer(new Slot(tileentity, 0, 97, 120));

		for (int j = 0; j < 9; j++) {
			addSlotToContainer(new Slot(player.inventory, j, 8 + j * 18, 142));
		}
	}

	@Override
	public ItemStack transferStackInSlot(int i) {
		if(i == 0) {
			BasicInventory.mergeStackIntoRange(inv, player.inventory, 0, 0, 9);
		} else {
			BasicInventory.mergeStackIntoRange(player.inventory, inv, i - 1, 0, 1);
		}
		return null;
	}
}