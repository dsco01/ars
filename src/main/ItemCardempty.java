package mods.immibis.ars;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemCardempty extends Item {

	public ItemCardempty() {
		setUnlocalizedName("advrepsys.freqcard.blank");
		setMaxStackSize(64);
		setTextureName("advrepsys:card-blank");

		setCreativeTab(CreativeTabs.tabMisc);
	}

	@Override
	public boolean isRepairable() {
		return false;
	}

}
