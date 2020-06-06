package mods.immibis.ars.beams;

import mods.immibis.core.BasicInventory;
import mods.immibis.core.api.util.BaseContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerPotionUpgrade extends BaseContainer<TilePotionUpgrade> {

	public ContainerPotionUpgrade(EntityPlayer player, TilePotionUpgrade tilePotionUpgrade) {
		super(player, tilePotionUpgrade);
		
		for(int x = 0; x < 9; x++)
			addSlotToContainer(new Slot(player.inventory, x, 8 + 18*x, 142));
		
		for(int y = 0; y < 3; y++)
			for(int x = 0; x < 9; x++)
				addSlotToContainer(new Slot(player.inventory, x + y*9 + 9, 8 + 18*x, 84 + 18*y));
		
		for(int x = 0; x < 3; x++)
			for(int y = 0; y < 3; y++)
				addSlotToContainer(new Slot(tilePotionUpgrade, x + y*3, 62 + x*18, 17 + y*18));
	}

	@Override
	public ItemStack transferStackInSlot(int slot) {
		if(slot < 36)
			BasicInventory.mergeStackIntoRange(player.inventory, inv, slot, 0, 9);
		else
			BasicInventory.mergeStackIntoRange(inv, player.inventory, slot-36, 0, 36);
		return null;
	}
}
