package mods.immibis.ars;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemUpgrades extends ItemBlock {

	public ItemUpgrades(Block i) {
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

		case BlockUpgrades.META_SUBWATER:
			return "tile.advrepsys.upgrade.underwater";
		case BlockUpgrades.META_DOME:
			return "tile.advrepsys.upgrade.dome";
		case BlockUpgrades.META_HARDNER:
			return "tile.advrepsys.upgrade.breaker";
		case BlockUpgrades.META_CORE_STORAGE:
			return "tile.advrepsys.upgrade.core.storage";
		case BlockUpgrades.META_CORE_RANGE:
			return "tile.advrepsys.upgrade.core.range";
		case BlockUpgrades.META_ZAPPER:
			return "tile.advrepsys.upgrade.zapper";
		case BlockUpgrades.META_CAMO:
			return "tile.advrepsys.upgrade.camo";
		case BlockUpgrades.META_INHIBITOR:
			return "tile.advrepsys.upgrade.inhibitor";
		}
		return null;
	}
}
