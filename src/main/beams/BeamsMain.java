package mods.immibis.ars.beams;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import mods.immibis.ars.ARSMod;
import mods.immibis.ars.Functions;
import mods.immibis.ars.ItemComponent;
import mods.immibis.core.api.APILocator;
import mods.immibis.core.api.crossmod.ICrossModIC2;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BeamsMain {
	public static BlockBeam blockBeam;
	public static BlockBeamStuff blockBeamMachines;
	public static ItemLogicCard itemLogicCard;
	
	public static int maxTeslaRange;

	public static void init() {
		
		GameRegistry.registerTileEntity(TileProgrammableFilter.class, "AdvRepSys TileUpgradeUnit");
		GameRegistry.registerTileEntity(TileTeslaCoil.class, "AdvRepSys TileTeslaCoil");
		GameRegistry.registerTileEntity(TileUpgradeCombiner.class, "AdvRepSys UpgradeCombiner");
		GameRegistry.registerTileEntity(TileRangeUpgrade.class, "AdvRepSys RangeUpgrade");
		GameRegistry.registerTileEntity(TileEMPUpgrade.class, "AdvRepSys EMPUpgrade");
		GameRegistry.registerTileEntity(TileSpeedUpgrade.class, "AdvRepSys SpeedUpgrade");
		GameRegistry.registerTileEntity(TileLootCollector.class, "AdvRepSys LootCollector");
		GameRegistry.registerTileEntity(TileFieldFilter.class, "AdvRepSys FieldFilter");
		GameRegistry.registerTileEntity(TileInventoryContentsFilter.class, "AdvRepSys InventoryContentsFilter");
		GameRegistry.registerTileEntity(TilePotionUpgrade.class, "AdvRepSys PotionUpgrade");
		GameRegistry.registerTileEntity(TileSuppressorUpgrade.class, "AdvRepSys SuppressorUpgrade");
		
		itemLogicCard = new ItemLogicCard();
		blockBeam = new BlockBeam();
		blockBeamMachines = new BlockBeamStuff();
		
		GameRegistry.registerItem(itemLogicCard, "{03A0292A-F829-42D7-9E69-19059C589D43}");
		
		maxTeslaRange = Functions.getBalanceOption("maxTeslaRange", 100);

		try {
			Method m = BeamsMain.class.getDeclaredMethod("initClient");
			m.invoke(null);
		} catch(NoSuchMethodException e) {
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
		
		ItemStack progFilter = new ItemStack(blockBeamMachines, 1, 0);
		ItemStack teslaCoil = new ItemStack(blockBeamMachines, 1, 1);
		ItemStack upgradeCombiner = new ItemStack(blockBeamMachines, 1, 2);
		ItemStack rangeUpgrade = new ItemStack(blockBeamMachines, 1, 3);
		ItemStack empUpgrade = new ItemStack(blockBeamMachines, 1, 4);
		ItemStack speedUpgrade = new ItemStack(blockBeamMachines, 1, 5);
		ItemStack lootCollector = new ItemStack(blockBeamMachines, 1, 6);
		ItemStack fieldFilter = new ItemStack(blockBeamMachines, 1, 7);
		ItemStack invFilter = new ItemStack(blockBeamMachines, 1, 8);
		ItemStack suppressor = new ItemStack(blockBeamMachines, 1, 9);
		ItemStack potionApplicator = new ItemStack(blockBeamMachines, 1, 10);
		
		Object energyModulator = new ItemStack(ARSMod.itemComponent, 1, ItemComponent.META_ENERGY_MODULATOR);
		
		ICrossModIC2 items = APILocator.getCrossModIC2();
		
		Object refinedIron = items.getItem("plateadviron"); // was refinedIronIngot
		//Object copper = Items.getItem("copperIngot");
		
		GameRegistry.addRecipe(teslaCoil,
			" # ",
			" | ",
			"-H-",
			'#', Blocks.iron_block,
			'|', Items.iron_ingot,
			'H', items.getItem("hvTransformer"),
			'-', items.getItem("glassFiberCableItem")
		);
		
		GameRegistry.addRecipe(copyStackWithCount(progFilter, 4),
			"I^I",
			".M.",
			"C.C",
			'M', items.getItem("machine"),
			'C', items.getItem("electronicCircuit"),
			'I', refinedIron,
			'.', Blocks.glass,
			'^', Blocks.redstone_torch
		);
		
		GameRegistry.addRecipe(copyStackWithCount(upgradeCombiner, 4),
			"G^G",
			".M.",
			"G.G",
			'G', Items.gold_ingot,
			'M', items.getItem("machine"),
			'^', Blocks.redstone_torch,
			'.', Blocks.glass
		);
		
		GameRegistry.addRecipe(rangeUpgrade,
			"I^I",
			"CMC",
			"CCC",
			'I', refinedIron,
			'C', items.getItem("copperCableItem"),
			'^', Blocks.redstone_torch,
			'M', items.getItem("machine")
		);
		
		GameRegistry.addRecipe(empUpgrade,
			"I^I",
			"CTC",
			"OCO",
			'I', refinedIron,
			'^', Blocks.redstone_torch,
			'T', items.getItem("mvTransformer"),
			'O', Items.ender_pearl,
			'C', items.getItem("glassFiberCableItem")
		);
		
		GameRegistry.addRecipe(speedUpgrade,
			"I^I",
			"SMS",
			"SSS",
			'I', refinedIron,
			'^', Blocks.redstone_torch,
			'M', items.getItem("machine"),
			'S', Items.sugar
		);
		
		GameRegistry.addRecipe(lootCollector,
			"I^I",
			"OMO",
			"OOO",
			'I', refinedIron,
			'^', Blocks.redstone_torch,
			'M', items.getItem("machine"),
			'O', Items.ender_eye
		);
		
		GameRegistry.addRecipe(potionApplicator,
			"I^I",
			"OMO",
			"BOB",
			'I', refinedIron,
			'^', Blocks.redstone_torch,
			'O', Items.ender_eye,
			'M', items.getItem("machine"),
			'B', Items.glass_bottle
		);
		
		GameRegistry.addRecipe(suppressor,
			"I^I",
			"IMI",
			"ICI",
			'I', refinedIron,
			'^', Blocks.redstone_torch,
			'M', items.getItem("machine"),
			'C', items.getItem("advancedCircuit")
		);
		
		GameRegistry.addShapelessRecipe(fieldFilter,
			progFilter,
			energyModulator
		);
		
		GameRegistry.addShapelessRecipe(invFilter,
			progFilter,
			Items.ender_pearl
		);
		
		ItemStack blankToken = new ItemStack(itemLogicCard, 1, LogicType.ID_BLANK);
		
		GameRegistry.addRecipe(copyStackWithCount(blankToken, 16),
			" P ",
			"PRP",
			" P ",
			'P', Items.paper,
			'R', Items.redstone
		);
		
		GameRegistry.addShapelessRecipe(new ItemStack(itemLogicCard, 1, LogicType.ID_PLAYER), blankToken, Items.apple);
		GameRegistry.addShapelessRecipe(new ItemStack(itemLogicCard, 1, LogicType.ID_LIVING), blankToken, Items.sugar);
		GameRegistry.addShapelessRecipe(new ItemStack(itemLogicCard, 1, LogicType.ID_REDSTONE), blankToken, Items.redstone);
		GameRegistry.addShapelessRecipe(new ItemStack(itemLogicCard, 1, LogicType.ID_ANIMAL), blankToken, Items.porkchop);
		GameRegistry.addShapelessRecipe(new ItemStack(itemLogicCard, 1, LogicType.ID_MOB), blankToken, Items.rotten_flesh);
		GameRegistry.addShapelessRecipe(new ItemStack(itemLogicCard, 1, LogicType.ID_ITEM), blankToken, Items.iron_ingot);
		
		GameRegistry.addRecipe(new ItemStack(itemLogicCard, 1, LogicType.ID_AND),
			"!!!",
			"!C!",
			"! !",
			'C', blankToken,
			'!', Items.redstone
		);
		GameRegistry.addRecipe(new ItemStack(itemLogicCard, 1, LogicType.ID_OR),
			"! !",
			"!C!",
			"!!!",
			'C', blankToken,
			'!', Items.redstone
		);
		GameRegistry.addRecipe(new ItemStack(itemLogicCard, 1, LogicType.ID_INVERT),
			"!!!",
			" C ",
			'C', blankToken,
			'!', Items.redstone
		);
		
		for(int k = 0; k < 6; k++) {
			GameRegistry.addShapelessRecipe(new ItemStack(itemLogicCard, 1, LogicType.ID_INPUT[k]),
				blankToken,
				new ItemStack(Items.dye, 1, LogicType.DYE_COLOURS[k]));
			
			GameRegistry.addRecipe(new ItemStack(itemLogicCard, 1, LogicType.ID_INPUT[(k+1)%6]),
				"X",
				'X', new ItemStack(itemLogicCard, 1, LogicType.ID_INPUT[k]));
		}
		
		GameRegistry.addRecipe(blankToken, "#", '#', new ItemStack(itemLogicCard, 1, -1));
		
		// Logic card crafting doesn't use the ingredients
		// it just "imprints" the card with them
		/*GameRegistry.registerCraftingHandler(new ICraftingHandler() {
			@Override public void onSmelting(EntityPlayer player, ItemStack item) {}			
			@Override public void onCrafting(EntityPlayer player, ItemStack item, IInventory craftMatrix) {
				if(item.getItem() == itemLogicCard && item.getItemDamage() != LogicType.ID_BLANK) {
					for(int k = 0; k < craftMatrix.getSizeInventory(); k++) {
						ItemStack is = craftMatrix.getStackInSlot(k);
						if(is != null && is.getItem() != itemLogicCard)
							is.stackSize++;
					}
				}
			}
		});*/
	}
	
	private static ItemStack copyStackWithCount(ItemStack s, int count) {
		s = s.copy();
		s.stackSize = count;
		return s;
	}
	
	@SideOnly(Side.CLIENT)
	public static void initClient() {
		cpw.mods.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(EntityBoltFX.class, new EntityBoltFXRenderer());
	}
}
