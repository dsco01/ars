package mods.immibis.ars.DeFence;

import mods.immibis.ars.ARSMod;
import mods.immibis.ars.Functions;
import mods.immibis.core.api.APILocator;
import mods.immibis.core.api.crossmod.ICrossModIC2;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class DeFenceCore
{
	public static BlockFences fenceCL, fenceBW;
	public static Item codedooritem;
	public static BlockDFDoor codedoorblock;
	
	public static void init(FMLInitializationEvent evt) 
	{
		fenceCL = new BlockFences("advrepsys:DeFence_chainlink");
		fenceBW = new BlockFences("advrepsys:DeFence_barbedwire");
		
		fenceCL.setBlockName("advrepsys.defence.chainlink");
		fenceBW.setBlockName("advrepsys.defence.barbedwire");
		
		codedoorblock = new BlockDFDoor(Material.iron, Block.soundTypeMetal);
		codedoorblock.setCreativeTab(CreativeTabs.tabDecorations);
		
		GameRegistry.registerBlock(fenceBW, ItemFences.class, "DeFenceBW");
		GameRegistry.registerBlock(fenceCL, ItemFences.class, "DeFenceCL");
		GameRegistry.registerBlock(codedoorblock, "DeFenceCodeDoor");
		
		GameRegistry.registerTileEntity(TileEntityCodeDoor.class, "immibis.ars.df.CodeDoor");
		
		codedooritem = new ItemCodeDoor();
		GameRegistry.registerItem(codedooritem, "DeFenceCodeDoorItem");
		
		if(Functions.getConfigBoolean("allowCraftingChainFence", true)) {
			GameRegistry.addRecipe(new ItemStack(fenceCL, 16),
	            "IBI", "BIB", "IBI", 'I', Items.iron_ingot, 'B', Blocks.iron_bars
	        );
		}
		if(Functions.getConfigBoolean("allowCraftingBarbedFence", true)) {
	        GameRegistry.addShapelessRecipe(new ItemStack(fenceBW, 4),
	        	fenceCL, fenceCL, fenceCL, fenceCL, Items.flint 
	        );
		}
		if(Functions.getConfigBoolean("allowCraftingCodeDoor", true)) {
			ICrossModIC2 ic2 = APILocator.getCrossModIC2();
			if(ic2 != null) {
		        GameRegistry.addRecipe(new ItemStack(codedooritem),
		            "RR ", "RRA", "RR ", 'R', ic2.getItem("reinforcedStone"), 'A', ic2.getItem("advancedCircuit")
		        );
		        GameRegistry.addRecipe(new ItemStack(codedooritem),
		            "DA", 'D', ic2.getItem("reinforcedDoor"), 'A', ic2.getItem("advancedCircuit")
		        );
			}
		}
	}
	
	public static Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) 
	{
		if(ID == ARSMod.GUI_DEFENCE_CODE_DOOR)
			return new ContainerCodeDoor(player, (TileEntityCodeDoor)world.getTileEntity(x, y, z));
		return null;
	}

	@SideOnly(Side.CLIENT)
	public static Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) 
	{
		if(ID == ARSMod.GUI_DEFENCE_CODE_DOOR)
			return new GuiCodeDoor(new ContainerCodeDoor(player, (TileEntityCodeDoor)world.getTileEntity(x, y, z)));
		return null;
	}
	
	
}
