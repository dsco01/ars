package mods.immibis.ars;

import java.io.File;

import mods.immibis.ars.DeFence.DeFenceCore;
import mods.immibis.ars.DeFence.DeFenceDoorGUIPacket;
import mods.immibis.ars.beams.*;
import mods.immibis.ars.packet.PacketGenericUpdate;
import mods.immibis.ars.projectors.TileProjector;
import mods.immibis.ars.projectors.TileProjectorArea;
import mods.immibis.ars.projectors.TileProjectorDeflector;
import mods.immibis.ars.projectors.TileProjectorDirectional;
import mods.immibis.ars.projectors.TileProjectorExtender;
import mods.immibis.ars.projectors.TileProjectorTube;
import mods.immibis.core.api.APILocator;
import mods.immibis.core.api.FMLModInfo;
import mods.immibis.core.api.crossmod.ICrossModIC2;
import mods.immibis.core.api.net.IPacket;
import mods.immibis.core.api.net.IPacketMap;
import mods.immibis.core.api.porting.PortableBaseMod;
import mods.immibis.core.api.porting.SidedProxy;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(version = "59.0.4", name = "Advanced Repulsion Systems", modid = "AdvancedRepulsionSystems", dependencies = "required-after:ImmibisCore@[55.1.0,);required-after:IC2")
@FMLModInfo(
	url="http://www.minecraftforum.net/topic/1001131-110-immibiss-mods-smp/",
	description="Adds FORCEFIELDS!",
	credits="Originally by Thunderdark, updated by immibis. Some textures by kaj_ and IvySaur1996. Fence textures and original fence code by F4113nb34st.",
	authors="Thunderdark;immibis;kaj_;IvySaur1996;F4113nb34st"
	)
public class ARSMod extends PortableBaseMod implements IPacketMap {

	public static BaseProxy proxy;

	public static Configuration config;

	public static Block MFFSMaschines;
	public static Block MFFSUpgrades;
	public static Block MFFSFieldblock;

	public static Item MFFSitemMFDoffset;
	public static Item MFFSitemMFDdebugger;
	public static Item MFFSitemcardempty;
	public static Item MFFSitemfc;
	public static Item itemComponent;

	public static final int GUI_ID_GENERATOR = 1;
	public static final int GUI_AREA_PROJECTOR = 2;
	public static final int GUI_DIRECTIONAL_PROJECTOR = 3;
	public static final int GUI_DEFLECTOR_PROJECTOR = 4;
	public static final int GUI_DIRECTIONAL_UPGRADE = 5;
	public static final int GUI_TUBE_PROJECTOR = 6;
	public static final int GUI_CAMOFLAGE_UPGRADE = 7;
	// 8 unused (was reactor containment field)
	public static final int GUI_UPGRADE_UNIT = 13;
	public static final int GUI_LOOT_COLLECTOR = 14;
	public static final int GUI_INVENTORY_CONTENTS_FILTER = 15;
	public static final int GUI_POTION_UPGRADE = 16;
	public static final int GUI_DEFENCE_CODE_DOOR = 17;

	public static final String CHANNEL = "AdvRepSys";

	public static final byte PKT_GENERIC_UPDATE = 0;
	public static final byte PKT_PROJECTOR_DESCRIPTION = 1; // unused
	public static final byte PKT_BOLT_FX = 2;
	public static final byte PKT_DEFENCE_CODE_DOOR_GUI = 3;

	public static final boolean DEBUG_MODE = Block.class.getName().equals("net.minecraft.src.Block");

	// begin config options
	public static int forcefieldblockcost;
	public static int forcefieldblockcreatemodifier;
	public static int forcefieldblockzappermodifier;

	public static int reaktorheatcontrolice;
	public static int reaktorheatcontrolwater;
	public static int reaktorheatcontrollava;

	public static int core_storage_default;
	public static int core_storage_upgrade_amount;
	public static int core_range_default;
	//public static int core_range_upgrade_amount;

	public static boolean enableReactorBlocks, enableTeslaCoil;
	public static boolean useOldRecipes;
	// end config options

	public static StringBuffer hasher = new StringBuffer();
	
	public static ARSMod instance;

	public static boolean slowRefresh;
	public static int maxSize, maxDeflectorDistance, maxTubeRadius;
	public static int refreshSpeed;

	public ARSMod() {
		instance = this;
	}

	@EventHandler
	public void load(FMLInitializationEvent evt) {
		config = new Configuration(new File(Functions.getMinecraftDir(), "config/AdvancedRepulsionSystems.cfg"));
		config.load();
		
		proxy = (BaseProxy)SidedProxy.instance.createSidedObject("mods.immibis.ars.ClientProxy", "mods.immibis.ars.BaseProxy");

		BlockForceField.model = SidedProxy.instance.getUniqueBlockModelID("mods.immibis.ars.BlockRendererField", true);

		/*Property slowRefreshProp = config.getOrCreateBooleanProperty("slowRefresh", Configuration.CATEGORY_GENERAL, false);
		slowRefreshProp.comment = "If true, forcefields will refresh every second. If false every tick (as before).";
		slowRefresh = Boolean.valueOf(slowRefreshProp.value);*/
		slowRefresh = false;

		Functions.removeIntFromBlockSection("reaktorheatcontrolice");
		Functions.removeIntFromBlockSection("reaktorheatcontrolwater");
		Functions.removeIntFromBlockSection("reaktorheatcontrollava");

		forcefieldblockcost = Functions.getIntFromBlockSection("forcefieldblockcost", "fieldCostBase", 1);
		forcefieldblockcreatemodifier = Functions.getIntFromBlockSection("forcefieldblockcreatemodifier", "fieldCostCreateMultiplier", 10);
		forcefieldblockzappermodifier = Functions.getIntFromBlockSection("forcefieldblockzappermodifier", "fieldCostZapperMultiplier", 2);
		maxSize = Functions.getBalanceOption("maxProjectorSize", 32);
		maxDeflectorDistance = Functions.getBalanceOption("maxDeflectorDistance", 10);
		maxTubeRadius = Functions.getBalanceOption("maxTubeRadius", 5);
		refreshSpeed = Functions.getBalanceOption("refreshSpeed", 100);
		TileProjector.maxOffset = Functions.getBalanceOption("maxOffset", 48);
		TileProjector.cooldownBlocksPerTick = Functions.getBalanceOption("cooldownBlocksPerTick", 40);

		enableReactorBlocks = Functions.getConfigBoolean("enableReactorBlocks", false);
		enableTeslaCoil = Functions.getConfigBoolean("enableTeslaCoil", true);
		useOldRecipes = Functions.getConfigBoolean("useOldRecipes", false);

		core_range_default = Functions.getBalanceOption("coreRangeDefault", 16);
		core_storage_default = Functions.getBalanceOption("coreStorageDefault", 1000000);
		//core_range_upgrade_amount = Functions.getBalanceOption("coreRangeUpgradeAmount", )
		core_storage_upgrade_amount = Functions.getBalanceOption("coreStorageUpgradeAmount", 2000000);

		DeFenceCore.init(evt);

		config.save();

		MFFSFieldblock = new BlockForceField();
		MFFSMaschines = new BlockMachine();
		MFFSUpgrades = new BlockUpgrades();

		MFFSitemMFDdebugger = new ItemMFDDebugger();
		MFFSitemcardempty = new ItemCardempty();
		MFFSitemfc = new ItemFrequenzCard();
		MFFSitemMFDoffset = new ItemMFDOffset();
		itemComponent = new ItemComponent();
		
		GameRegistry.registerItem(MFFSitemMFDdebugger, "{F3B9C366-4BAE-4A17-A87F-3E75EE7AF025}");
		GameRegistry.registerItem(MFFSitemcardempty, "{85C018E9-503E-45A1-A41D-1A80F3C10085}");
		GameRegistry.registerItem(MFFSitemfc, "{24F03169-149C-4C16-A1E1-46CDF95635A9}");
		GameRegistry.registerItem(MFFSitemMFDoffset, "{7A273772-F99E-4729-A9C9-6F48C63C955F}");
		GameRegistry.registerItem(itemComponent, "{A8F3AF2F-0384-4EAA-9486-8F7E7A1B96E7}");

		if(enableTeslaCoil)
			BeamsMain.init();

		if (config != null) {
			config.save();
		}

		GameRegistry.registerBlock(MFFSMaschines, ItemMachines.class, "machines");
		GameRegistry.registerBlock(MFFSUpgrades, ItemUpgrades.class, "upgrades");
		GameRegistry.registerBlock(MFFSFieldblock, "field");

		GameRegistry.registerTileEntity(TileEntityMaschines.class, "Maschines_Multi");
		GameRegistry.registerTileEntity(TileEntityGeneratorCore.class, "Generator_Core");
		GameRegistry.registerTileEntity(TileProjectorArea.class, "Area_Projektor");
		GameRegistry.registerTileEntity(TileUpgradePassive.class, "Generator_Upgrade");
		GameRegistry.registerTileEntity(TileProjectorDirectional.class, "Directional_Projektor");
		GameRegistry.registerTileEntity(TileProjectorDeflector.class, "Deflector_Projektor");
		GameRegistry.registerTileEntity(TileProjectorExtender.class, "Directional_Extender");
		GameRegistry.registerTileEntity(TileProjectorTube.class, "Tube_Projektor");
		GameRegistry.registerTileEntity(TileUpgradeCamouflage.class, "Projektor_camouflage");
		GameRegistry.registerTileEntity(TileCamouflagedField.class, "MFFS camo");

		enableClockTicks(true);
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new IGuiHandler() {

			@Override
			public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
				TileEntity te = world.getTileEntity(x, y, z);
				if(te == null)
					return null;
				int meta = te.getWorldObj().getBlockMetadata(te.xCoord, te.yCoord, te.zCoord);
				Block blockid = te.getWorldObj().getBlock(te.xCoord, te.yCoord, te.zCoord);
				if ((meta == 4 || meta == 5) && blockid == MFFSUpgrades) {
					// TODO WTF?
					ID = GUI_ID_GENERATOR;
				}

				switch(ID) {
				case GUI_ID_GENERATOR:
					return new ContainerGenerator(player, ((TileEntityGeneratorCore) te));
				case GUI_AREA_PROJECTOR:
					return new ContainerProjektor(player, ((TileProjectorArea) te));
				case GUI_DIRECTIONAL_PROJECTOR:
					return new ContainerProjektor(player, ((TileProjectorDirectional) te));
				case GUI_DEFLECTOR_PROJECTOR:
					return new ContainerProjektor(player, ((TileProjectorDeflector) te));
				case GUI_DIRECTIONAL_UPGRADE:
					return new Containerdummy(player, ((TileProjectorExtender) te));
				case GUI_TUBE_PROJECTOR:
					return new ContainerProjektor(player, ((TileProjectorTube) te));
				case GUI_CAMOFLAGE_UPGRADE:
					return new ContainerCamoflage(player, ((TileUpgradeCamouflage) te));
				case GUI_UPGRADE_UNIT:
					return new ContainerProgrammableFilter(player, (TileProgrammableFilter)te);
				case GUI_LOOT_COLLECTOR:
					return new ContainerLootCollector(player, (TileLootCollector)te);
				case GUI_INVENTORY_CONTENTS_FILTER:
					return new ContainerInventoryContentsFilter(player, (TileInventoryContentsFilter)te);
				case GUI_POTION_UPGRADE:
					return new ContainerPotionUpgrade(player, (TilePotionUpgrade)te);
				case GUI_DEFENCE_CODE_DOOR:
					return DeFenceCore.getServerGuiElement(ID, player, world, x, y, z);
				}
				return null;
			}

			@Override
			public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
				TileEntity te = world.getTileEntity(x, y, z);
				if(te == null)
					return null;
				int meta = te.getWorldObj().getBlockMetadata(te.xCoord, te.yCoord, te.zCoord);
				Block blockid = te.getWorldObj().getBlock(te.xCoord, te.yCoord, te.zCoord);
				if ((meta == 4 || meta == 5) && blockid == MFFSUpgrades) {
					// TODO WTF?
					ID = GUI_ID_GENERATOR;
				}

				switch(ID) {
				case GUI_ID_GENERATOR:
					return new GuiGenerator(player, ((TileEntityGeneratorCore) te));
				case GUI_AREA_PROJECTOR:
					return new GuiProjektorArea(player, ((TileProjectorArea) te));
				case GUI_DIRECTIONAL_PROJECTOR:
					return new GuiProjektorDirectional(player, ((TileProjectorDirectional) te));
				case GUI_DEFLECTOR_PROJECTOR:
					return new GuiDeflectorDirectional(player, ((TileProjectorDeflector) te));
				case GUI_DIRECTIONAL_UPGRADE:
					return new GuiDirectionalUpgrade(player, ((TileProjectorExtender) te));
				case GUI_TUBE_PROJECTOR:
					return new GuiProjTube(player, ((TileProjectorTube) te));
				case GUI_CAMOFLAGE_UPGRADE:
					return new GuiCamouflageUpgrade(player, ((TileUpgradeCamouflage) te));
				case GUI_UPGRADE_UNIT:
					return new GuiProgrammableFilter(new ContainerProgrammableFilter(player, (TileProgrammableFilter)te));
				case GUI_LOOT_COLLECTOR:
					return new GuiLootCollector(new ContainerLootCollector(player, (TileLootCollector)te));
				case GUI_INVENTORY_CONTENTS_FILTER:
					return new GuiInventoryContentsFilter(new ContainerInventoryContentsFilter(player, (TileInventoryContentsFilter)te));
				case GUI_POTION_UPGRADE:
					return new GuiPotionUpgrade(new ContainerPotionUpgrade(player, (TilePotionUpgrade)te));
				case GUI_DEFENCE_CODE_DOOR:
					return DeFenceCore.getClientGuiElement(ID, player, world, x, y, z);
				}
				return null;
			}
		});

		APILocator.getNetManager().listen(this);
	}

	@Override
	public boolean onTickInGame() {
		FFWorld.tickAll();
		return true;
	}

	@EventHandler
	public void modsLoaded(FMLPostInitializationEvent evt)
	{
		ICrossModIC2 ic2 = APILocator.getCrossModIC2();
		
		ic2.addExplosionWhitelist(MFFSFieldblock);
		ic2.addExplosionWhitelist(MFFSUpgrades);
		ic2.addExplosionWhitelist(MFFSMaschines);

		GameRegistry.addRecipe(new ItemStack(MFFSitemMFDoffset),
				"BCB",
				"DAD",
				"DDD",
				'A', Items.diamond,
				'B', ic2.getItem("insulatedCopperCableItem"),
				'C', Items.redstone,
				'D', Items.iron_ingot
				);

		if(!useOldRecipes) {
			
			// reverse crafting
			GameRegistry.addShapelessRecipe(new ItemStack(itemComponent, 1, ItemComponent.META_PROJECTOR_BASE), new ItemStack(MFFSMaschines, 1, BlockMachine.META_PROJ_AREA));
			GameRegistry.addShapelessRecipe(new ItemStack(itemComponent, 1, ItemComponent.META_PROJECTOR_BASE), new ItemStack(MFFSMaschines, 1, BlockMachine.META_PROJ_DEFLECTOR));
			GameRegistry.addShapelessRecipe(new ItemStack(itemComponent, 1, ItemComponent.META_PROJECTOR_BASE), new ItemStack(MFFSMaschines, 1, BlockMachine.META_PROJ_DIR));
			GameRegistry.addShapelessRecipe(new ItemStack(itemComponent, 1, ItemComponent.META_PROJECTOR_BASE), new ItemStack(MFFSMaschines, 1, BlockMachine.META_PROJ_EXTENDER));
			GameRegistry.addShapelessRecipe(new ItemStack(itemComponent, 1, ItemComponent.META_PROJECTOR_BASE), new ItemStack(MFFSMaschines, 1, BlockMachine.META_PROJ_TUBE));
			
			GameRegistry.addRecipe(new ItemStack(MFFSitemcardempty, 8),
					"RAR",
					"ABA",
					"RAR",
					Character.valueOf('A'), Items.paper,
					Character.valueOf('B'), ic2.getItem("electronicCircuit"),
					'R', Items.redstone
					);

			GameRegistry.addRecipe(new ItemStack(itemComponent, 1, ItemComponent.META_ENERGY_MODULATOR),
					" ^ ",
					" | ",
					"-M-",

					'^', ic2.getItem("frequencyTransmitter"),
					'M', ic2.getItem("electronicCircuit"),
					'-', ic2.getItem("glassFiberCableItem"),
					'|', ic2.getItem("glassFiberCableItem") // was goldCableItem
					);

			/*GameRegistry.addRecipe(new ItemStack(itemComponent, 1, ItemComponent.META_CONTROL_PANEL),
				"...",
				"...",
				"#C#",
				'.', Block.button,
				'#', ic2.getItem("reinforcedStone"),
				'C', ic2.getItem("electronicCircuit")
			);*/

			GameRegistry.addRecipe(new ItemStack(itemComponent, 4, ItemComponent.META_DIAMOND_LENS),
					"X-X",
					"|*|",
					"X-X",
					'-', Blocks.glass,
					'|', Blocks.glass,
					'*', Items.diamond,
					'X', ic2.getItem("advancedAlloy")
					);

			GameRegistry.addRecipe(new ItemStack(itemComponent, 1, ItemComponent.META_PROJECTOR_BASE),
					"#^#",
					"$O$",
					"/E/",
					'#', ic2.getItem("reinforcedStone"),
					'$', ic2.getItem("carbonPlate"),
					'O', new ItemStack(itemComponent, 1, ItemComponent.META_DIAMOND_LENS),
					'/', Items.repeater,
					'E', ic2.getItem("electrolyzer"),
					'^', new ItemStack(itemComponent, 1, ItemComponent.META_ENERGY_MODULATOR)
					);

			// core
			GameRegistry.addRecipe(new ItemStack(MFFSMaschines, 1, BlockMachine.META_CORE),
					"#^#",
					"OMO",
					"CEC",

					'M', ic2.getItem("mfeUnit"),
					'^', new ItemStack(itemComponent, 1, ItemComponent.META_ENERGY_MODULATOR),
					'E', ic2.getItem("electrolyzer"),
					'#', ic2.getItem("advancedAlloy"),
					'O', ic2.getItem("electrolyzedWaterCell"),
					'C', ic2.getItem("advancedCircuit")
					);

			// area
			GameRegistry.addRecipe(new ItemStack(MFFSMaschines, 1, BlockMachine.META_PROJ_AREA),
					"###",
					"#P#",
					"###",
					'#', ic2.getItem("reinforcedStone"),
					'P', new ItemStack(itemComponent, 1, ItemComponent.META_PROJECTOR_BASE)
					);

			// dir
			GameRegistry.addRecipe(new ItemStack(MFFSMaschines, 1, BlockMachine.META_PROJ_DIR),
					"#",
					"#",
					"P",
					'#', ic2.getItem("reinforcedStone"),
					'P', new ItemStack(itemComponent, 1, ItemComponent.META_PROJECTOR_BASE)
					);

			// defl
			GameRegistry.addRecipe(new ItemStack(MFFSMaschines, 1, BlockMachine.META_PROJ_DEFLECTOR),
					"###",
					" P ",
					'#', ic2.getItem("reinforcedStone"),
					'P', new ItemStack(itemComponent, 1, ItemComponent.META_PROJECTOR_BASE)
					);

			// tube
			GameRegistry.addRecipe(new ItemStack(MFFSMaschines, 1, BlockMachine.META_PROJ_TUBE),
					"###",
					" P ",
					"###",
					'#', ic2.getItem("reinforcedStone"),
					'P', new ItemStack(itemComponent, 1, ItemComponent.META_PROJECTOR_BASE)
					);
			GameRegistry.addRecipe(new ItemStack(MFFSMaschines, 1, BlockMachine.META_PROJ_TUBE),
					"# #",
					"#P#",
					"# #",
					'#', ic2.getItem("reinforcedStone"),
					'P', new ItemStack(itemComponent, 1, ItemComponent.META_PROJECTOR_BASE)
					);

			// extender
			GameRegistry.addRecipe(new ItemStack(MFFSMaschines, 1, BlockMachine.META_PROJ_EXTENDER),
					" O ",
					"# #",
					" # ",
					'#', ic2.getItem("reinforcedStone"),
					'O', new ItemStack(itemComponent, 1, ItemComponent.META_DIAMOND_LENS)
					);

			// subwater
			GameRegistry.addRecipe(new ItemStack(MFFSUpgrades, 1, BlockUpgrades.META_SUBWATER),
					" A ",
					"ABA",
					" A ",
					Character.valueOf('A'), ic2.getItem("advancedAlloy"),
					Character.valueOf('B'), Items.bucket
					);

			// dome
			GameRegistry.addRecipe(new ItemStack(MFFSUpgrades, 1, BlockUpgrades.META_DOME),
					" A ",
					"ABA",
					" A ",
					Character.valueOf('A'), ic2.getItem("advancedAlloy"),
					Character.valueOf('B'), ic2.getItem("electronicCircuit")
					);

			// hardner
			GameRegistry.addRecipe(new ItemStack(MFFSUpgrades, 1, BlockUpgrades.META_HARDNER),
					" A ",
					"ABA",
					" A ",
					Character.valueOf('A'), ic2.getItem("advancedAlloy"),
					Character.valueOf('B'), ic2.getItem("carbonPlate")
					);

			// core storage
			GameRegistry.addRecipe(new ItemStack(MFFSUpgrades, 1, BlockUpgrades.META_CORE_STORAGE),
					" A ",
					"ABA",
					" A ",
					Character.valueOf('A'), ic2.getItem("advancedAlloy"),
					Character.valueOf('B'), ic2.getItem("electrolyzedWaterCell")
					);

			// core range
			GameRegistry.addRecipe(new ItemStack(MFFSUpgrades, 1, BlockUpgrades.META_CORE_RANGE),
					" A ",
					"ABA",
					" A ",
					Character.valueOf('A'), ic2.getItem("advancedAlloy"),
					Character.valueOf('B'), ic2.getItem("frequencyTransmitter")
					);

			// zapper
			GameRegistry.addRecipe(new ItemStack(MFFSUpgrades, 1, BlockUpgrades.META_ZAPPER),
					" A ",
					"ABA",
					" A ",
					Character.valueOf('A'), ic2.getItem("advancedAlloy"),
					Character.valueOf('B'), ic2.getItem("teslaCoil")
					);

			// camo
			GameRegistry.addRecipe(new ItemStack(MFFSUpgrades, 1, BlockUpgrades.META_CAMO),
					"BAB",
					"ACA",
					"BAB",
					Character.valueOf('A'), ic2.getItem("advancedAlloy"),
					Character.valueOf('B'), Items.diamond,
					Character.valueOf('C'), ic2.getItem("frequencyTransmitter")
					);

			// inhibitor
			GameRegistry.addRecipe(new ItemStack(MFFSUpgrades, 1, BlockUpgrades.META_INHIBITOR),
					" A ",
					"ABA",
					" A ",
					Character.valueOf('A'), ic2.getItem("advancedAlloy"),
					Character.valueOf('B'), ic2.getItem("machine")
					);
		}
		else
		{

			GameRegistry.addRecipe(new ItemStack(MFFSMaschines, 1, BlockMachine.META_PROJ_EXTENDER),
					" * ",
					"#C#",
					" # ",
					'#', ic2.getItem("advancedAlloy"),
					'*', Items.diamond,
					'D', ic2.getItem("frequencyTransmitter")
					);
			GameRegistry.addRecipe(new ItemStack(MFFSMaschines, 1, BlockMachine.META_PROJ_TUBE),
					"#*#",
					"*C*",
					"DED",
					'#', ic2.getItem("advancedAlloy"),
					'*', Items.diamond,
					'D', ic2.getItem("electronicCircuit"),
					'C', ic2.getItem("frequencyTransmitter"),
					'E', ic2.getItem("electrolyzedWaterCell")
					);
			GameRegistry.addRecipe(new ItemStack(MFFSMaschines, 1, BlockMachine.META_PROJ_AREA),
					"BBB",
					"BCB",
					"DED",
					'A', ic2.getItem("advancedAlloy"),
					'B', Items.diamond,
					'D', ic2.getItem("electronicCircuit"),
					'C', ic2.getItem("frequencyTransmitter"),
					'E', ic2.getItem("electrolyzedWaterCell")
					);
			GameRegistry.addRecipe(new ItemStack(MFFSMaschines, 1, BlockMachine.META_PROJ_DEFLECTOR),
					"BAB",
					"ACA",
					"DED",
					'A', ic2.getItem("advancedAlloy"),
					'B', Items.diamond,
					'D', ic2.getItem("electronicCircuit"),
					'C', ic2.getItem("frequencyTransmitter"),
					'E', ic2.getItem("electrolyzedWaterCell")
					);
			GameRegistry.addRecipe(new ItemStack(MFFSMaschines, 1, BlockMachine.META_PROJ_DIR),
					"ABA",
					"ACA",
					"DED",
					'A', ic2.getItem("advancedAlloy"),
					'B', Items.diamond,
					'D', ic2.getItem("electronicCircuit"),
					'C', ic2.getItem("frequencyTransmitter"),
					'E', ic2.getItem("electrolyzedWaterCell")
					);
			/*GameRegistry.addRecipe(new ItemStack(MFFSMaschines, 1, BlockMachine.META_EU_INJECTOR),
				"ABA",
				"CDC",
				"ABA",
				'A', ic2.getItem("refinedIronIngot"),
				'B', ic2.getItem("insulatedCopperCableItem"),
				'C', ic2.getItem("electrolyzedWaterCell"),
				'D', ic2.getItem("batBox")
			);*/ // EU injector recipe disabled
			GameRegistry.addRecipe(new ItemStack(MFFSMaschines, 1, BlockMachine.META_CORE),
					"ABA",
					"CDC",
					"AEA",
					Character.valueOf('A'), ic2.getItem("electrolyzedWaterCell"),
					Character.valueOf('B'), ic2.getItem("advancedAlloy"),
					Character.valueOf('C'), ic2.getItem("electronicCircuit"),
					Character.valueOf('D'), ic2.getItem("frequencyTransmitter"),
					Character.valueOf('E'), ic2.getItem("electrolyzer")
					);
			GameRegistry.addRecipe(new ItemStack(MFFSUpgrades, 1, BlockUpgrades.META_CORE_STORAGE),
					" A ",
					"ABA",
					" A ",
					Character.valueOf('A'), ic2.getItem("advancedAlloy"),
					Character.valueOf('B'), ic2.getItem("electrolyzedWaterCell")
					);
			GameRegistry.addRecipe(new ItemStack(MFFSUpgrades, 1, BlockUpgrades.META_DOME),
					" A ",
					"ABA",
					" A ",
					Character.valueOf('A'), ic2.getItem("advancedAlloy"),
					Character.valueOf('B'), ic2.getItem("electronicCircuit")
					);
			GameRegistry.addRecipe(new ItemStack(MFFSUpgrades, 1, BlockUpgrades.META_HARDNER),
					" A ",
					"ABA",
					" A ",
					Character.valueOf('A'), ic2.getItem("advancedAlloy"),
					Character.valueOf('B'), ic2.getItem("carbonPlate")
					);
			GameRegistry.addRecipe(new ItemStack(MFFSUpgrades, 1, BlockUpgrades.META_CORE_RANGE),
					" A ",
					"ABA",
					" A ",
					Character.valueOf('A'), ic2.getItem("advancedAlloy"),
					Character.valueOf('B'), ic2.getItem("frequencyTransmitter")
					);
			GameRegistry.addRecipe(new ItemStack(MFFSUpgrades, 1, BlockUpgrades.META_INHIBITOR),
					" A ",
					"ABA",
					" A ",
					Character.valueOf('A'), ic2.getItem("advancedAlloy"),
					Character.valueOf('B'), ic2.getItem("machine")
					);
			GameRegistry.addRecipe(new ItemStack(MFFSUpgrades, 1, BlockUpgrades.META_SUBWATER),
					" A ",
					"ABA",
					" A ",
					Character.valueOf('A'), ic2.getItem("advancedAlloy"),
					Character.valueOf('B'), Items.bucket
					);
			GameRegistry.addRecipe(new ItemStack(MFFSitemcardempty),
					"AAA",
					"ABA",
					"AAA",
					Character.valueOf('A'), Items.paper,
					Character.valueOf('B'), ic2.getItem("electronicCircuit")
					);
			GameRegistry.addRecipe(new ItemStack(MFFSUpgrades, 1, BlockUpgrades.META_ZAPPER),
					" A ",
					"ABA",
					" A ",
					Character.valueOf('A'), ic2.getItem("advancedAlloy"),
					Character.valueOf('B'), ic2.getItem("teslaCoil")
					);
			GameRegistry.addRecipe(new ItemStack(MFFSUpgrades, 1, BlockUpgrades.META_CAMO),
					"BAB",
					"ACA",
					"BAB",
					Character.valueOf('A'), ic2.getItem("advancedAlloy"),
					Character.valueOf('B'), Items.diamond,
					Character.valueOf('C'), ic2.getItem("frequencyTransmitter")
					);
		}

		GameRegistry.addShapelessRecipe(new ItemStack(MFFSitemcardempty), new Object[] {MFFSitemfc});
	}

	public static boolean areItemsEqual(ItemStack a, ItemStack b) {
		if(a == null && b == null)
			return true;
		if(a == null || b == null)
			return false;
		if(a.getItem() != b.getItem())
			return false;
		if(a.getHasSubtypes() && a.getItemDamage() != b.getItemDamage())
			return false;
		if(a.stackTagCompound == null && b.stackTagCompound == null)
			return true;
		if(a.stackTagCompound != null || b.stackTagCompound != null)
			return false;
		return a.stackTagCompound.equals(b.stackTagCompound);
	}

	@Override
	public String getChannel() {
		return CHANNEL;
	}

	@Override
	public IPacket createS2CPacket(byte id) {
		if(id == PKT_GENERIC_UPDATE)
			return new PacketGenericUpdate();
		if(id == PKT_BOLT_FX)
			return new PacketBoltFX();
		return null;
	}

	@Override
	public IPacket createC2SPacket(byte id) {
		if(id == PKT_DEFENCE_CODE_DOOR_GUI)
			return new DeFenceDoorGUIPacket();
		return null;
	}
}