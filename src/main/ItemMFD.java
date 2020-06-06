package mods.immibis.ars;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public abstract class ItemMFD extends Item {

	protected int tickcounter = 0;
	protected StringBuffer info = new StringBuffer();
	protected StringBuffer hasher = new StringBuffer();

	public ItemMFD(int mode) {
		setMaxStackSize(1);

		setCreativeTab(CreativeTabs.tabMisc);
	}

	@Override
	public boolean isRepairable() {
		return false;
	}

}
