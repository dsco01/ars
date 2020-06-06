package mods.immibis.ars;


import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemComponent extends Item {
	
	public static final int META_ENERGY_MODULATOR = 0;
	public static final int META_DIAMOND_LENS = 1;
	public static final int META_PROJECTOR_BASE = 2;
	
	private String[] internal_names;

	public ItemComponent() {
		internal_names = new String[] {
			"item.advrepsys.energy-modulator",
			"item.advrepsys.diamond-lens",
			"item.advrepsys.projector-base",
		};

		setMaxDamage(0);
        setHasSubtypes(true);
        
        setCreativeTab(CreativeTabs.tabMisc);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack is) {
		try {
			return internal_names[is.getItemDamage()];
		} catch(ArrayIndexOutOfBoundsException e) {
			return "";
		}
	}
	
	private IIcon[] icons;
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister reg) {
		icons = new IIcon[] {
			reg.registerIcon("advrepsys:energy-modulator"),
			reg.registerIcon("advrepsys:diamond-lens"),
			reg.registerIcon("advrepsys:projector-base")
		};
	}
	
	@Override
	public IIcon getIconFromDamage(int damage) {
		return damage < 0 || damage >= icons.length ? icons[0] : icons[damage];
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void getSubItems(Item id, CreativeTabs tab, List list) {
		for(int meta = 0; meta < 3; meta++)
			list.add(new ItemStack(this, 1, meta));
	}

}
