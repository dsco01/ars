package mods.immibis.ars;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemForceField extends ItemBlock {

	public ItemForceField(Block i) {
		super(i);
		setMaxDamage(0);
		setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int i) {
		return getPlacedBlockMetadata(i);
	}

	public int getPlacedBlockMetadata(int i) {
		return i;
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		return null;
	}
}
