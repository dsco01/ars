package mods.immibis.ars;

import mods.immibis.core.BasicInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerCamoflage extends ContainerMFFS {

	public ContainerCamoflage(EntityPlayer player, TileUpgradeCamouflage tileEntity_Camoflage_Upgrade) {
		
		super(player, tileEntity_Camoflage_Upgrade);

		addSlotToContainer(new Slot(tileEntity_Camoflage_Upgrade, 0, 116, 30));

		for (int j = 0; j < 9; j++) {
			addSlotToContainer(new Slot(player.inventory, j, 8 + j * 18, 66));
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
