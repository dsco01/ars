package mods.immibis.ars;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockForceField extends BlockContainer
{
	@SuppressWarnings("rawtypes")
	@Override
	public final void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
	}

	//private boolean localFlag;
	//private StringBuffer hasher = new StringBuffer();
	static int model;
	
	public static final int META_NORMAL = 0;
	public static final int META_ZAPPER = 1;
	public static final int META_CAMO = 2;
	public static final int META_REACTOR = 3;
	public static final int META_ZAPPER_CAMO = 4;
	
	public static boolean isZapper(int meta) {
		return meta == META_ZAPPER || meta == META_ZAPPER_CAMO;
	}
	public static boolean isCamo(int meta) {
		return meta == META_CAMO || meta == META_ZAPPER_CAMO;
	}

	public BlockForceField() {
		super(Material.glass);
		setHardness(100000F);
		setResistance(6000F);
		//renderblockpass = 0;
		//localFlag = false;
		setTickRandomly(true);
	}
	
	@Override
	public void updateTick(World w, int x, int y, int z, Random random) {
		FFBlock ffblock = FFWorld.get(w).get(x,y,z);
		if(ffblock == null)
			w.setBlockToAir(x, y, z);
		else
			ffblock.refresh();
	}
	
	@Override
	public int getRenderType()
	{
		return model;
	}

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
	@Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
	@Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    /**
     * Return true if a player with SlikTouch can harvest this block directly, and not it's normal drops.
     */
	@Override
    protected boolean canSilkHarvest()
    {
        return false;
    }
	
	
    /*@Override
	public int getRenderBlockPass()
    {
        return 1;
    }*/

    
	@Override
	public void breakBlock(World world, int i, int j, int k, Block par5, int par6) {

		if(!world.isRemote) {
			FFBlock ffblock = FFWorld.get(world).get(i,j,k);
			if (ffblock != null && ffblock.shouldBeActive()) {
				ffblock.refresh();
				FFWorld.get(world).queueRefresh(i, j, k);
			}
		}
	}
	private static AxisAlignedBB aabb(double a, double b, double c, double d, double e, double f) {
		return AxisAlignedBB.getBoundingBox(a,b,c,d,e,f);
	}

	@Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k) {
		if (isZapper(world.getBlockMetadata(i, j, k))) {
			float f = 0.0625F;
			return aabb(i + f, j + f, k + f, i + 1 - f, j + 1 - f, k + 1 - f);
		}

		return aabb((float) i, j, (float) k, (float) (i + 1), (float) (j + 1), (float) (k + 1));
	}

	@Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int i, int j, int k) {
		return aabb((float) i, j, (float) k, (float) (i + 1), j + 1, (float) (k + 1));
	}

	@Override
    public void onEntityCollidedWithBlock(World world, int i, int j, int k, Entity entity) {

		if(world.isRemote)
			return;
		
		if(entity instanceof EntityBoat) {
			entity.attackEntityFrom(DamageSource.generic, 50);
		}
		
		if(isZapper(world.getBlockMetadata(i, j, k))) {
			if (entity instanceof EntityLivingBase) {
				entity.attackEntityFrom(DamageSource.generic, 5);
			}
		}
	}

	@Override
    public int quantityDropped(Random random) {
		return 0;
	}

	@Override
    public boolean shouldSideBeRendered(IBlockAccess iblockaccess, int i, int j, int k, int l) {
		int myx = i, myy = j, myz = k;
		switch(l) {
		case 0: myy++; break;
		case 1: myy--; break;
		case 2: myz++; break;
		case 3: myz--; break;
		case 4: myx++; break;
		case 5: myx--; break;
		}
		
		if(iblockaccess.getBlock(i, j, k) == this && iblockaccess.getBlockMetadata(i, j, k) == iblockaccess.getBlockMetadata(myx, myy, myz))
			return false;
		
		return super.shouldSideBeRendered(iblockaccess, i, j, k, l);
	}
	
	private IIcon[] normalIcons;
	
	@Override
	public void registerBlockIcons(IIconRegister r) {
		normalIcons = new IIcon[] {
			r.registerIcon("advrepsys:ff-blue"),
			r.registerIcon("advrepsys:ff-red"),
			r.registerIcon("advrepsys:ff-error"),
			r.registerIcon("advrepsys:ff-boxy"),
		};
	}

	static IIcon[] useTextures = new IIcon[6];
	static boolean renderingCamo;
	
	@Override
    public IIcon getIcon(IBlockAccess iblockaccess, int i, int j, int k, int l) {
		if(!renderingCamo || useTextures[l] == null) {
			int meta = iblockaccess.getBlockMetadata(i, j, k);
			if(isCamo(meta))
				meta = 2;
			if(meta >= normalIcons.length)
				meta = 0;
			return normalIcons[meta];
		}
		else
			return useTextures[l];
	}

	@Override
	public float getExplosionResistance(Entity entity, World world, int i, int j, int k, double d, double d1, double d2) {

		if(!world.isRemote) {
			FFBlock ffblock = FFWorld.get(world).get(i,j,k);
			if (ffblock != null && ffblock.shouldBeActive()) {
				ffblock.usePower(ARSMod.forcefieldblockcreatemodifier);
			}
		}

		return 60000F;
	}

	@Override
    public void randomDisplayTick(World world, int i, int j, int k, Random random) {

    	int meta = world.getBlockMetadata(i, j, k);
		if (isZapper(meta) && !isCamo(meta)) {

			double d = i + Math.random();
			double d1 = j + Math.random();
			double d2 = k + Math.random();

			world.spawnParticle("reddust", d, d1, d2, 0.0D, 0.0D, 0.0D);

		}
	}

    @Override
	public TileEntity createNewTileEntity(World world, int meta) {
		if(isCamo(meta))
			return new TileCamouflagedField();
		else
			return null;
	}

}
