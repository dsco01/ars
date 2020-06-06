package mods.immibis.ars;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemMachines extends ItemBlock {

	public ItemMachines(Block i) {
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
		int i = itemstack.getItemDamage();
		switch (i) {
		case 0:
			return "tile.advrepsys.core";
		case 1:
			return "tile.advrepsys.proj.area";
		case 2:
			return "tile.advrepsys.proj.line";
		case 3:
			return "tile.advrepsys.proj.plate";
		case 4:
			return "tile.advrepsys.proj.tube";
		case 5:
			return "tile.advrepsys.proj.extender";
		}
		return null;
	}
}
