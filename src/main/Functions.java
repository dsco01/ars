package mods.immibis.ars;


import java.io.File;
import java.util.logging.Logger;

import mods.immibis.core.api.porting.SidedProxy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class Functions {

	private final static Logger log = Logger.getLogger("Client");

	public static void DisplayInfo(String s) {
		log.info(s);
	}

	public static File getMinecraftDir() {
		return SidedProxy.instance.getMinecraftDir();
	}
	
	private static Property getLegacyOption(String category, String name) {
		if(!ARSMod.config.getCategoryNames().contains(category))
			return null;
		
		ConfigCategory c = ARSMod.config.getCategory(category);
		if(c == null)
			return null;
		if(c.isEmpty()) {
			ARSMod.config.removeCategory(c);
			return null;
		}
		
		Property p = c.get(name);
		if(p == null)
			return null;
		
		c.remove(name);
		if(c.isEmpty())
			ARSMod.config.removeCategory(c);
		return p;
	}

	public static int getIntFromBlockSection(String oldName, String newName, int def) {
		Property old = getLegacyOption("1", oldName);
		if(old != null)
			ARSMod.config.get(Configuration.CATEGORY_GENERAL, newName, def).set(old.getInt());
		
		return ARSMod.config.get(Configuration.CATEGORY_GENERAL, newName, def).getInt(def);
	}

	public static void ChattoPlayer(EntityPlayer player, String Message) {
		player.addChatMessage(new ChatComponentText(Message));
	}

	public static NBTTagCompound getTAGfromItemstack(ItemStack itemStack) {
		NBTTagCompound tag = itemStack.getTagCompound();
		if (tag == null) {
			tag = new NBTTagCompound();
			itemStack.setTagCompound(tag);
		}
		return tag;
	}

	public static boolean getConfigBoolean(String key, boolean def) {
		return ARSMod.config.get(Configuration.CATEGORY_GENERAL, key, def).getBoolean(def);
	}

	public static int getBalanceOption(String key, int def) {
		return ARSMod.config.get(Configuration.CATEGORY_GENERAL, key, def).getInt(def);
	}

	public static void removeIntFromBlockSection(String key) {
		getLegacyOption("1", key);
	}

}
