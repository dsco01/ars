package mods.immibis.ars;


import java.util.List;

import mods.immibis.core.RenderUtilsIC;
import mods.immibis.core.api.util.Dir;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockUpgrades extends BlockContainer
{
	
	// 0 was reactor monitor client
	public static final int META_SUBWATER = 1;
	public static final int META_DOME = 2;
	public static final int META_HARDNER = 3;
	public static final int META_CORE_STORAGE = 4;
	public static final int META_CORE_RANGE = 5;
	public static final int META_ZAPPER = 6;
	public static final int META_CAMO = 7;
	// 8 was reactor connector
	public static final int META_INHIBITOR = 9;
	
	public static final int MAX_META = 10;

	public BlockUpgrades() {

		super(Material.iron);
		setHardness(3F);
		setResistance(50F);
		setStepSound(soundTypeMetal);
		
		setCreativeTab(CreativeTabs.tabMisc);

	}

	public int getGuiPro(World world, int i, int j, int k, EntityPlayer entityplayer) {
		switch (world.getBlockMetadata(i, j, k)) {
		case 0:
			return ARSMod.GUI_ID_GENERATOR;
		case 1:
			return ARSMod.GUI_AREA_PROJECTOR;
		case 2:
			return ARSMod.GUI_DIRECTIONAL_PROJECTOR;
		case 3:
			return ARSMod.GUI_DEFLECTOR_PROJECTOR;
		case 4:
			return ARSMod.GUI_TUBE_PROJECTOR;
		case 5:
			return ARSMod.GUI_DIRECTIONAL_UPGRADE;
		default:
			return -1;
		}
	}

	public int getGuiUp(World world, int i, int j, int k, EntityPlayer entityplayer) {
		switch (world.getBlockMetadata(i, j, k)) {

		case 7:
			return ARSMod.GUI_CAMOFLAGE_UPGRADE;
		default:
			return -1;
		}
	}

	@Override
	public void breakBlock(World world, int i, int j, int k, Block par5, int par6) {
		if(world.isRemote)
			return;

		//TileEntity tileentity = world.getBlockTileEntity(i, j, k);
		
		world.removeTileEntity(i, j, k);
	}

	@Override
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int par6, float par7, float par8, float par9) {
	
		int gui = getGuiUp(world, i, j, k, entityplayer);
		int meta = world.getBlockMetadata(i, j, k);

		if (gui < 0 && meta != 4 && meta != 5) {
			return false;
		}

		if (entityplayer.isSneaking())
			return false;

		if (entityplayer.getCurrentEquippedItem() != null && (entityplayer.getCurrentEquippedItem().getItem() instanceof ItemMFD)) {
			return false;
		}

		if (gui >= 0) {
			if(!world.isRemote)
				entityplayer.openGui(ARSMod.instance, gui, world, i, j, k);
			return true;
		}

		if (meta == 4 || meta == 5) {
			TileUpgradePassive tileentity = (TileUpgradePassive) world.getTileEntity(i, j, k);
			if (tileentity.getconectet_ID() != 0) {
				TileEntityGeneratorCore tileentitygen = Linkgrid.getWorldMap(world).getGenerator().get(tileentity.getconectet_ID());
				if (tileentitygen != null && !world.isRemote) {
					int guiid = getGuiPro(world, tileentitygen.xCoord, tileentitygen.yCoord, tileentitygen.zCoord, entityplayer);
					if(guiid >= 0)
						entityplayer.openGui(ARSMod.instance, gui, world, tileentitygen.xCoord, tileentitygen.yCoord, tileentitygen.zCoord);
				}
			}
		}
		return false;
	}

	@Override
	public int damageDropped(int i) {
		return i;

	}

	@Override
	public TileEntityMaschines createNewTileEntity(World w, int i) {
		switch (i) {
		case META_SUBWATER:
		case META_DOME:
		case META_HARDNER:
		case META_CORE_RANGE:
		case META_CORE_STORAGE:
		case META_ZAPPER:
		case META_INHIBITOR:
			return new TileUpgradePassive();
		case META_CAMO:
			return new TileUpgradeCamouflage();
		}
		return null;
	}

	public static boolean isActive(IBlockAccess iblockaccess, int i, int j, int k) {
		TileEntity tileentity = iblockaccess.getTileEntity(i, j, k);

		if (tileentity instanceof TileUpgradePassive) {
			return ((TileUpgradePassive) tileentity).getconectet_ID() != 0;
		} else {
			return false;
		}
	}
	
	// icons[meta][active][side] -> icon
	private IIcon[][][] icons;
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		String[] names = new String[] {
			null, "underwater", "dome", "breaker", "storage", "range", "zapper", "camo", null, "inhibitor"
		};
		if(names.length != MAX_META)
			throw new AssertionError();
		
		icons = new IIcon[names.length][2][];
		for(int k = 0; k < names.length; k++) {
			if(names[k] != null) {
				icons[k][0] = RenderUtilsIC.loadIconArray(reg, "advrepsys:upgrades!"+names[k]+"0", 6);
				icons[k][1] = RenderUtilsIC.loadIconArray(reg, "advrepsys:upgrades!"+names[k]+"1", 6);
			} else {
				icons[k] = null;
			}
		}
	}

	@Override
	public IIcon getIcon(IBlockAccess iblockaccess, int i, int j, int k, int l) {
		//TileEntity tileentity = iblockaccess.getBlockTileEntity(i, j, k);
		int meta = iblockaccess.getBlockMetadata(i, j, k);
		
		if(l < 2) l ^= 1; // swaps top and bottom (WHY???)

		if(meta >= icons.length || icons[meta] == null)
			return icons[META_ZAPPER][0][Dir.PY];
		
		return icons[meta][isActive(iblockaccess, i, j, k) ? 1 : 0][l];
	}

	@Override
	public IIcon getIcon(int side, int meta) {

		if(meta >= icons.length)
			meta = 0;
		
		if(side < 2) side ^= 1; // swaps top and bottom (WHY???)
		
		if(meta >= icons.length || icons[meta] == null)
			return icons[META_ZAPPER][0][Dir.PY];
		
		return icons[meta][0][side];
	}

	@Override
	public void onNeighborBlockChange(World world, int i, int j, int k, Block l) {
		TileEntity tileentity = world.getTileEntity(i, j, k);
		if (tileentity instanceof TileUpgradePassive) {
			((TileUpgradePassive) tileentity).updatecheck();
		}
	}

	@Override
	public float getExplosionResistance(Entity entity, World world, int i, int j, int k, double d, double d1, double d2) {
		if (world.getTileEntity(i, j, k) instanceof TileEntityMaschines) {
			TileEntity tileentity = world.getTileEntity(i, j, k);
			if (((TileEntityMaschines) tileentity).getActive()) {
				return 60000F;
			} else {
				return 50F;
			}
		}
		return 50F;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public final void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
		for(int k = 0; k < MAX_META; k++)
			if(k != 0 && k != 8) // removed blocks
				par3List.add(new ItemStack(this, 1, k));
	}

}
