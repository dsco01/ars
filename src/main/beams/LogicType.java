package mods.immibis.ars.beams;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import mods.immibis.ars.beams.logictypes.ClassDetector;
import mods.immibis.ars.beams.logictypes.FilterInput;
import mods.immibis.ars.beams.logictypes.Intersection;
import mods.immibis.ars.beams.logictypes.Negate;
import mods.immibis.ars.beams.logictypes.NothingDetector;
import mods.immibis.ars.beams.logictypes.RedstoneInput;
import mods.immibis.ars.beams.logictypes.Union;
import mods.immibis.core.api.util.Colour;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public abstract class LogicType {
	private static Map<Integer, LogicType> types = new HashMap<Integer, LogicType>();
	private static Map<Item, LogicType> items = new HashMap<Item, LogicType>();
	private static Map<Integer, String> icons = new HashMap<Integer, String>();
	
	public static LogicType get(int id) {
		return types.get(id);
	}
	
	public static void register(int id, String icon, LogicType type, Item item) {
		if(types.containsKey(id))
			throw new IllegalArgumentException("Logic card type "+id+" is already registered.");
		
		types.put(id, type);
		
		if(item != null) items.put(item, type);
		if(icon != null) icons.put(id, icon);
	}
	
	public static void register(LogicType type, Item item) {
		if(item != null)
			items.put(item, type);
	}
	
	public static Set<Integer> getAllTypeIDs() {
		return types.keySet();
	}
	
	/**
	 * Some entries of in may be null - ignore those entries.
	 */
	public abstract EntityFilter createFilter(TileProgrammableFilter container, EntityFilter[] in);
	
	public static final int ID_PLAYER = 0;
	public static final int ID_AND = 1;
	public static final int ID_OR = 2;
	public static final int ID_INVERT = 3;
	public static final int ID_BLANK = 4;
	public static final int ID_LIVING = 5;
	public static final int ID_REDSTONE = 6;
	public static final int ID_ANIMAL = 7;
	public static final int ID_MOB = 8;
	public static final int ID_ITEM = 9;
	public static final int[] ID_INPUT = {10, 11, 12, 13, 14, 15};
	
	public static final int[] DYE_COLOURS = {Colour.BLUE.dyeId(), Colour.YELLOW.dyeId(), Colour.GREEN.dyeId(), Colour.PURPLE.dyeId(), Colour.RED.dyeId(), Colour.CYAN.dyeId()};
	
	static void init() {
		register(ID_PLAYER, "advrepsys.card.player", new ClassDetector(EntityPlayer.class), null);
		register(ID_AND, "advrepsys.card.and", new Intersection(), null);
		register(ID_OR, "advrepsys.card.or", new Union(), null);
		register(ID_INVERT, "advrepsys.card.not", new Negate(), null);
		register(ID_BLANK, "advrepsys.card.blank", new NothingDetector(), null);
		register(ID_LIVING, "advrepsys.card.life", new ClassDetector(EntityLivingBase.class), null);
		register(ID_REDSTONE, "advrepsys.card.redstone", new RedstoneInput(), Items.redstone);
		register(ID_ANIMAL, "advrepsys.card.animal", new ClassDetector(EntityAnimal.class), null);
		register(ID_MOB, "advrepsys.card.mob", new ClassDetector(IMob.class), null);
		register(ID_ITEM, "advrepsys.card.item", new ClassDetector(EntityItem.class), Item.getItemFromBlock(Blocks.cobblestone));
		for(int k = 0; k < 6; k++)
			register(ID_INPUT[k], "advrepsys.card.in" + k, new FilterInput(k), null);
		register(new ClassDetector(EntityCreeper.class), Items.gunpowder);
		register(new ClassDetector(EntityChicken.class), Items.egg);
		register(new ClassDetector(EntityCow.class), Items.beef);
		register(new ClassDetector(EntityOcelot.class), Items.fish);
		register(new ClassDetector(EntityPig.class), Items.porkchop);
		register(new ClassDetector(EntitySheep.class), Item.getItemFromBlock(Blocks.wool));
		register(new ClassDetector(EntitySquid.class), Items.water_bucket);
		register(new ClassDetector(EntityVillager.class), null);
		register(new ClassDetector(EntityEnderman.class), Items.ender_pearl);
		register(new ClassDetector(EntityWolf.class), Items.bone);
		register(new ClassDetector(EntityPigZombie.class), Items.gold_nugget);
		register(new ClassDetector(EntityBlaze.class), Items.blaze_rod);
		register(new ClassDetector(EntityCaveSpider.class), null);
		register(new ClassDetector(EntityZombie.class), Items.rotten_flesh);
		register(new ClassDetector(EntityGhast.class), Items.ghast_tear);
		register(new ClassDetector(EntityMagmaCube.class), Items.magma_cream);
		register(new ClassDetector(EntitySilverfish.class), null);
		register(new ClassDetector(EntitySkeleton.class), Items.bow);
		register(new ClassDetector(EntitySlime.class), Items.slime_ball);
		register(new ClassDetector(EntitySnowman.class), Items.snowball);
		register(new ClassDetector(EntityIronGolem.class), Items.iron_ingot);
		register(new ClassDetector(EntityArrow.class), Items.arrow);
		register(new ClassDetector(EntityThrowable.class), null);
	}

	public static LogicType getForItem(ItemStack is) {
		if(is == null)
			return null;
		
		if(is.getItem() == BeamsMain.itemLogicCard)
			return get(is.getItemDamage());
		return items.get(is.getItem());
	}

	public static String getIconName(int meta) {
		return icons.get(meta);
	}

}
