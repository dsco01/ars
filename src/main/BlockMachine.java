package mods.immibis.ars;


import java.util.List;

import mods.immibis.ars.projectors.TileProjector;
import mods.immibis.ars.projectors.TileProjectorArea;
import mods.immibis.ars.projectors.TileProjectorDeflector;
import mods.immibis.ars.projectors.TileProjectorDirectional;
import mods.immibis.ars.projectors.TileProjectorExtender;
import mods.immibis.ars.projectors.TileProjectorTube;
import mods.immibis.core.RenderUtilsIC;
import mods.immibis.core.api.util.Dir;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockMachine extends BlockContainer
{

	public static final int META_CORE = 0;
	public static final int META_PROJ_AREA = 1;
	public static final int META_PROJ_DIR = 2;
	public static final int META_PROJ_DEFLECTOR = 3;
	public static final int META_PROJ_TUBE = 4;
	public static final int META_PROJ_EXTENDER = 5;
	
	public static final int MAX_META = 6;

	public BlockMachine() {

		super(Material.iron);
		setHardness(3F);
		setResistance(50F);
		setStepSound(soundTypeMetal);
		
		setCreativeTab(CreativeTabs.tabMisc);

	}
	
	public static int renderType;
	public static int renderPass;
	@Override
	public int getRenderType() {
		return renderType;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getMixedBrightnessForBlock(IBlockAccess par1iBlockAccess, int par2, int par3, int par4) {
		if(renderPass == 1)
			return 0x00F000F0;
		return super.getMixedBrightnessForBlock(par1iBlockAccess, par2, par3, par4);
	}

	public int getGui(World world, int i, int j, int k, EntityPlayer entityplayer) {
		switch (world.getBlockMetadata(i, j, k)) {
		case META_CORE:
			return ARSMod.GUI_ID_GENERATOR;
		case META_PROJ_AREA:
			return ARSMod.GUI_AREA_PROJECTOR;
		case META_PROJ_DIR:
			return ARSMod.GUI_DIRECTIONAL_PROJECTOR;
		case META_PROJ_DEFLECTOR:
			return ARSMod.GUI_DEFLECTOR_PROJECTOR;
		case META_PROJ_TUBE:
			return ARSMod.GUI_TUBE_PROJECTOR;
		case META_PROJ_EXTENDER:
			return ARSMod.GUI_DIRECTIONAL_UPGRADE;
		default:
			return -1;
		}
	}

	@Override
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int par6, float par7, float par8, float par9) {

		if (entityplayer.isSneaking())
			return false;

		if (entityplayer.getCurrentEquippedItem() != null && (entityplayer.getCurrentEquippedItem().getItem() instanceof ItemMFD)) {
			return false;
		}
		
		if(world.isRemote)
			return true;
		
		TileEntity te = world.getTileEntity(i, j, k);
		ItemStack held = entityplayer.getCurrentEquippedItem();
		
		// encode a card by right clicking on a core
		if(held != null && held.getItem() == ARSMod.MFFSitemcardempty && te instanceof TileEntityGeneratorCore) {
			
			held.stackSize--;
			if(held.stackSize == 0)
				held = null;
			entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, held);
			
			ItemStack _new = ((TileEntityGeneratorCore)te).getCodedCard();
			
			if(entityplayer.getCurrentEquippedItem() == null)
				entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, _new);
			else if(!entityplayer.inventory.addItemStackToInventory(_new))
				entityplayer.entityDropItem(_new, 0);
			
			return true;
		}
		
		if(held != null && held.getItem() == ARSMod.MFFSitemfc && te instanceof TileProjector) {
			TileProjector tp = (TileProjector)te;
			if(tp.getStackInSlot(0) == null) {
				entityplayer.destroyCurrentEquippedItem();
				tp.setInventorySlotContents(0, held);
				return true;
			}
		}

		int gui = getGui(world, i, j, k, entityplayer);

		if(gui < 0)
			return false;

		entityplayer.openGui(ARSMod.instance, gui, world, i, j, k);
		return true;
	}

	@Override
	public void onBlockAdded(World world, int i, int j, int k) {
		if(world.isRemote) {
			return;
		}
		int meta = world.getBlockMetadata(i, j, k);
		if (meta == 0) {
			TileEntityGeneratorCore tileentityblock = (TileEntityGeneratorCore) world.getTileEntity(i, j, k);
			tileentityblock.addtogrid();
		}
		if (meta == 1) {
			TileProjectorArea tileentityblock = (TileProjectorArea) world.getTileEntity(i, j, k);
			tileentityblock.addtogrid();
		}
		if (meta == 2) {
			TileProjectorDirectional tileentityblock = (TileProjectorDirectional) world.getTileEntity(i, j, k);
			tileentityblock.addtogrid();
		}
		if (meta == 3) {
			TileProjectorDeflector tileentityblock = (TileProjectorDeflector) world.getTileEntity(i, j, k);
			tileentityblock.addtogrid();
		}
		if (meta == 4) {
			TileProjectorTube tileentityblock = (TileProjectorTube) world.getTileEntity(i, j, k);
			tileentityblock.addtogrid();
		}

	}

	@Override
	public void breakBlock(World world, int i, int j, int k, Block par5, int par6) {
		if(world.isRemote) {
			return;
		}
		
		TileEntity te = world.getTileEntity(i, j, k);
		
		if(te instanceof TileEntityGeneratorCore) {
			((TileEntityGeneratorCore)te).removefromgrid();
		} else if(te instanceof TileProjector) {
			((TileProjector)te).destroyField();
			((TileProjector)te).removefromgrid();
		}

		if (te instanceof TileProjector) {
			ItemStack itemstack = ((TileProjector) te).getStackInSlot(0);
			if (itemstack != null) {
				world.spawnEntityInWorld(new EntityItem(world, i + 0.5f, j + 0.5f, k + 0.5f, itemstack));
			}
		}

		world.removeTileEntity(i, j, k);

	}

	@Override
	public void onBlockPlacedBy(World world, int i, int j, int k, EntityLivingBase entityliving, ItemStack stack) {
		if(world.isRemote) {
			return;
		}

		TileEntityMaschines tileentityblock;
		try {
			tileentityblock = (TileEntityMaschines) world.getTileEntity(i, j, k);
		} catch(ClassCastException e) {
			// no idea why but this happened once
			world.setBlockToAir(i, j, k);
			e.printStackTrace();
			return;
		}

		int l = MathHelper.floor_double((double) ((entityliving.rotationYaw * 4F) / 360F) + 0.5D) & 3;
		int i1 = Math.round(entityliving.rotationPitch);
		if (i1 >= 65) {
			tileentityblock.setFacing((short) 1);
		} else if (i1 <= -65) {
			tileentityblock.setFacing((short) 0);
		} else if (l == 0) {
			tileentityblock.setFacing((short) 2);
		} else if (l == 1) {
			tileentityblock.setFacing((short) 5);
		} else if (l == 2) {
			tileentityblock.setFacing((short) 3);
		} else if (l == 3) {
			tileentityblock.setFacing((short) 4);
		}
	}

	@Override
	public int damageDropped(int i) {
		return i;
	}

	@Override
	public TileEntityMaschines createNewTileEntity(World world, int i) {
		switch (i) {
		case META_CORE:
			return new TileEntityGeneratorCore();
		case META_PROJ_AREA:
			return new TileProjectorArea();
		case META_PROJ_DIR:
			return new TileProjectorDirectional();
		case META_PROJ_DEFLECTOR:
			return new TileProjectorDeflector();
		case META_PROJ_TUBE:
			return new TileProjectorTube();
		case META_PROJ_EXTENDER:
			return new TileProjectorExtender();
		}
		return null;
	}

	public static boolean isActive(IBlockAccess iblockaccess, int i, int j, int k) {
		TileEntity tileentity = iblockaccess.getTileEntity(i, j, k);
		if (tileentity instanceof TileEntityMaschines) {
			return ((TileEntityMaschines) tileentity).getActive();
		} else {
			return false;
		}
	}
	
	
	// for machines: icons[meta][active ? 1 : 0][side] = icon
	// for projectors: icons[meta] = {{front-off, front-on, front-highlight}, {side-off, side-on, side-highlight}}
	private IIcon[][][] icons;
	private IIcon iTransparent;
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister r) {
		String[] names = new String[] {
			"core", "area", "line", "plate", "tube", "ext"
		};
		
		if(names.length != MAX_META)
			throw new AssertionError();
		
		icons = new IIcon[names.length][2][];
		
		for(int k = 0; k < names.length; k++) {
			if(k >= 1 && k <= 5) {
				icons[k][0] = new IIcon[] {
					RenderUtilsIC.loadIcon(r, "advrepsys:machines!"+names[k]+"-front0"),
					RenderUtilsIC.loadIcon(r, "advrepsys:machines!"+names[k]+"-front1"),
					RenderUtilsIC.loadIcon(r, "advrepsys:machines!"+names[k]+"-frontH"),
				};
				icons[k][1] = new IIcon[] {
					RenderUtilsIC.loadIcon(r, "advrepsys:machines!"+names[k]+"-side0"),
					RenderUtilsIC.loadIcon(r, "advrepsys:machines!"+names[k]+"-side1"),
					RenderUtilsIC.loadIcon(r, "advrepsys:machines!"+names[k]+"-sideH"),
				};
			} else {
				icons[k][0] = RenderUtilsIC.loadIconArray(r, "advrepsys:machines!" + names[k] + "0", 6);
				icons[k][1] = RenderUtilsIC.loadIconArray(r, "advrepsys:machines!" + names[k] + "1", 6);
			}
		}
		
		iTransparent = RenderUtilsIC.loadIcon(r, "advrepsys:machines!transparent");
	}

	@Override
	public IIcon getIcon(IBlockAccess iblockaccess, int i, int j, int k, int l) {
		TileEntity tileentity = iblockaccess.getTileEntity(i, j, k);
		short facing = (tileentity instanceof TileProjector) ? ((TileEntityMaschines) tileentity).getFacing() : 0;
		int meta = iblockaccess.getBlockMetadata(i, j, k);
		
		if(meta >= MAX_META)
			return icons[0][0][0]; // some random icon
		
		int active = isActive(iblockaccess, i, j, k) ? 1 : 0;
		
		if(icons[meta][0].length == 3) {
			if(renderPass == 1) {
				if(active == 0)
					return iTransparent;
				else
					return icons[meta][l == facing ? 0 : 1][2];
			} else {
				return icons[meta][l == facing ? 0 : 1][active];
			}
			
		} else {
			if(renderPass != 0)
				return iTransparent;
			
			if (facing == l) {
				return icons[meta][active][0];
			}
			if (facing != 0 && l == 0) {
				return icons[meta][active][1];
			}
			return icons[meta][active][l];
		}
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		if(meta > icons.length)
			meta = 0;
		
		if(side == Dir.PY || side == 0)
			side ^= Dir.PY;
		
		if(icons[meta][0].length == 3)
			return icons[meta][side == 0 ? 0 : 1][0];
		else
			return icons[meta][0][side];
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
			if(k != 8 && k != 9) // removed blocks
				par3List.add(new ItemStack(this, 1, k));
	}

}
