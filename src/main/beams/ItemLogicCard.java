package mods.immibis.ars.beams;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemLogicCard extends Item {
	public ItemLogicCard() {
		setHasSubtypes(true);
		
		LogicType.init();
		
		setCreativeTab(CreativeTabs.tabMisc);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack is) {
		return "item." + LogicType.getIconName(is.getItemDamage());
	}
	
	private Map<Integer, IIcon> icons;
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister r) {
		icons = new HashMap<Integer, IIcon>();
		for(int meta : LogicType.getAllTypeIDs()) {
			String iconName = LogicType.getIconName(meta);
			if(iconName != null)
				icons.put(meta, r.registerIcon(iconName.replace("advrepsys.", "advrepsys:").replace(".","/")));
		}
	}
	
	@Override
	public IIcon getIconFromDamage(int meta) {
		return icons.get(meta);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void getSubItems(Item id, CreativeTabs tab, @SuppressWarnings("rawtypes") List list) {
		for(int meta : LogicType.getAllTypeIDs())
			list.add(new ItemStack(this, 1, meta));
	}
}
