package mods.immibis.ars;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemFrequenzCard extends Item {

	private StringBuffer info = new StringBuffer();

	public ItemFrequenzCard() {
		setUnlocalizedName("advrepsys.freqcard");
		setMaxStackSize(1);
		setTextureName("advrepsys:card-link");

		setCreativeTab(CreativeTabs.tabMisc);
	}

	@Override
	public boolean isRepairable() {
		return false;
	}

}
