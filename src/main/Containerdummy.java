package mods.immibis.ars;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class Containerdummy extends ContainerMFFS {

	public Containerdummy(EntityPlayer player, TileEntityMFFS tileentity) {
		super(player, tileentity);
	}

	@Override
	public ItemStack transferStackInSlot(int i) {
		return null;
	}
}
