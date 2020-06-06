package mods.immibis.ars.DeFence;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockDFDoor extends BlockContainer implements ISimpleBlockRenderingHandler
{
	private int renderID = 0;//RenderingRegistry.getNextAvailableRenderId();

	public BlockDFDoor(Material mat, SoundType ss)
	{
		super(mat);
		
        RenderingRegistry.registerBlockHandler(renderID, this);
        
        setStepSound(ss);
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityCodeDoor();
	}
	
	@Override
	public boolean isOpaqueCube()
    {
        return false;
    }
	
	/**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    @Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z)
    {
    	if(y >= 255)
    	{
    		return false;
    	}
        return world.doesBlockHaveSolidTopSurface(world, x, y - 1, z) && super.canPlaceBlockAt(world, x, y, z) && super.canPlaceBlockAt(world, x, y + 1, z);
    }
    
    @Override
	public boolean shouldRender3DInInventory(int model)
	{
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
	    return false;
	}

	@Override
	public int getRenderType() {
	    return renderID;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void getSubBlocks(Item id, CreativeTabs tabs, List list) {
	}
	

	@Override
	public void breakBlock(World world, int i, int j, int k, Block par5, int par6) {
			super.breakBlock(world, i, j, k, par5, par6);
		
		    boolean flag = true;
		    ArrayList<ItemStack> dropped = getDrops(world, i, j, k, world.getBlockMetadata(i, j, k), 0);
		    if(dropped == null)
		    {
		    	return;
		    }
		    for (Iterator<ItemStack> iterator = dropped.iterator(); iterator.hasNext();)
		    {
		        ItemStack itemstack = (ItemStack)iterator.next();
		
		        if (flag)
		        {
		            flag = false;
		        }else
		        {
		            Utils.dropAsEntity(world, i, j, k, itemstack);
		        }
		    }
		}
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess w, int x, int y, int z) {
		TileEntity te = w.getTileEntity(x, y, z);
		if(!(te instanceof TileEntityCodeDoor)) return;
		
		AxisAlignedBB bb = ((TileEntityCodeDoor)te).getBoundsWithinBlock();
		setBlockBounds((float)bb.minX, (float)bb.minY, (float)bb.minZ, (float)bb.maxX, (float)bb.maxY, (float)bb.maxZ);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		if(!(world.getTileEntity(x, y, z) instanceof TileEntityCodeDoor))
		{
			return RenderNormalBlock.render(renderer, x, y, z, block);
		}
		return ((TileEntityCodeDoor)world.getTileEntity(x, y, z)).renderWorldBlock(block, renderer);
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
	}

	@Override
	public int getRenderId() {
		return renderID;
	}
	
	private IIcon iTop, iBottom;
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister) {
		iTop = par1IconRegister.registerIcon("advrepsys:DeFence_door_top");
		iBottom = par1IconRegister.registerIcon("advrepsys:DeFence_door_bottom");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		if(world.getTileEntity(x, y - 1, z) instanceof TileEntityCodeDoor)
    		return iTop;
    	return iBottom;
	}

	@Override
	public void onBlockAdded(World world, int x, int y,
		int z) {
			if(world.getTileEntity(x, y, z) instanceof TileEntityCodeDoor)
			{
				((TileEntityCodeDoor)world.getTileEntity(x, y, z)).onAdded(null);
			}
		}


	@Override
	public boolean onBlockActivated(World world, int x, int y,
		int z, EntityPlayer player, int par6, float par7, float par8,
		float par9) {
			if(world.getTileEntity(x, y, z) instanceof TileEntityCodeDoor)
			{
				return ((TileEntityCodeDoor)world.getTileEntity(x, y, z)).onActivated(player);
			}
		    return super.onBlockActivated(world, x, y, z, player, par6, par7, par8, par9);
		}

	@Override
	public void onBlockPlacedBy(World world, int x, int y,
		int z, EntityLivingBase entity, ItemStack stack) {
			if(world.getTileEntity(x, y, z) instanceof TileEntityCodeDoor)
			{
				((TileEntityCodeDoor)world.getTileEntity(x, y, z)).onAdded(entity);
			}
		}

	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x,
		int y, int z) {
			if(world.getTileEntity(x, y, z) instanceof TileEntityCodeDoor)
			{
				return ((TileEntityCodeDoor)world.getTileEntity(x, y, z)).isOwner(player) && world.setBlockToAir(x, y, z);
			}
		    return super.removedByPlayer(world, player, x, y, z);
		}

	@Override
	public float getBlockHardness(World world, int x, int y,
		int z) {
			if(world.getTileEntity(x, y, z) instanceof TileEntityCodeDoor)
			{
				return ((TileEntityCodeDoor)world.getTileEntity(x, y, z)).getHardness();
			}
		    return super.getBlockHardness(world, x, y, z);
		}

	@Override
	public float getPlayerRelativeBlockHardness(EntityPlayer player, World world,
		int x, int y, int z) {
			if(world.getTileEntity(x, y, z) instanceof TileEntityCodeDoor)
			{
				int metadata = world.getBlockMetadata(x, y, z);
		        float hardness = ((TileEntityCodeDoor)world.getTileEntity(x, y, z)).getHardnessFor(player);
		        if (hardness < 0.0F)
		        {
		            return 0.0F;
		        }
		
		        if (!ForgeHooks.canHarvestBlock(this, player, metadata))
		        {
		            float speed = ForgeEventFactory.getBreakSpeed(player, this, metadata, 1.0f, x, y, z);
		            return (speed < 0 ? 0 : speed) / hardness / 100F;
		        }else
		        {
		             return player.getBreakSpeed(this, false, metadata, x, y, z) / hardness / 30F;
		        }
			}
		    return super.getPlayerRelativeBlockHardness(player, world, x, y, z);
		}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		return getCollisionBoundingBoxFromPool(world, x, y, z);
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
		TileEntity te = par1World.getTileEntity(par2, par3, par4);
		if(!(te instanceof TileEntityCodeDoor))
			return null;
		return ((TileEntityCodeDoor)te).getCollisionBoundingBox();
	}
	
	@Override
	public MovingObjectPosition collisionRayTrace(World par1World, int par2, int par3, int par4, Vec3 par5Vec3, Vec3 par6Vec3) {
		MovingObjectPosition rv = getCollisionBoundingBoxFromPool(par1World, par2, par3, par4).calculateIntercept(par5Vec3, par6Vec3);
		if(rv != null) {
			rv.blockX = par2;
			rv.blockY = par3;
			rv.blockZ = par4;
			rv.typeOfHit = MovingObjectType.BLOCK;
		}
		return rv;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess par1iBlockAccess, int par2, int par3, int par4, int par5) {
		return true;
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		return new ItemStack(DeFenceCore.codedooritem);
	}
	
	@Override
	public Item getItemDropped(int par1, Random par2Random, int par3) {
		return DeFenceCore.codedooritem;
	}
}
