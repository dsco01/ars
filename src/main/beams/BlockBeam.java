package mods.immibis.ars.beams;


import java.util.Random;

import mods.immibis.core.RenderUtilsIC;
import mods.immibis.core.api.util.Dir;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockBeam extends Block {
	
	public static final int MAX_LENGTH = 8;
	
	// metadata&7 is direction, metadata&8 is beam colour (0=red 1=blue)
	
	private IIcon blueIcon, redIcon;

	public BlockBeam() {
		super(Material.vine); // must be a replaceable material (TODO: fixed yet?)
		
		setTickRandomly(true);
		
		setBlockName("advrepsys.beam");
		GameRegistry.registerBlock(this, "beam");
		
		setLightLevel(3 /15.0f);
	}
	
	@Override
	public IIcon getIcon(int side, int meta) {
		if(meta < 6)
			return redIcon;
		else
			return blueIcon;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister r) {
		redIcon = RenderUtilsIC.loadIcon(r, "advrepsys:tesla!redbeam");
		blueIcon = RenderUtilsIC.loadIcon(r, "advrepsys:tesla!bluebeam");
	}
	
	//@Override
	//public boolean renderAsNormalBlock() {return false;}
	@Override
	public boolean isOpaqueCube() {return false;}
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
        return null;
    }
	
	public static int directionFromMetadata(int meta) {
		return meta % 6;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getMixedBrightnessForBlock(IBlockAccess par1iBlockAccess, int par2, int par3, int par4) {
		return 0x00F000F0;
	}
	
	private void checkContinuity(World w, int x, int y, int z) {
		int meta = w.getBlockMetadata(x, y, z);
		int dir = directionFromMetadata(meta);
		int dx = 0, dy = 0, dz = 0;
		switch(dir) {
		case Dir.NX: dx = -1; break;
		case Dir.NY: dy = -1; break;
		case Dir.NZ: dz = -1; break;
		case Dir.PX: dx =  1; break;
		case Dir.PY: dy =  1; break;
		case Dir.PZ: dz =  1; break;
		}
		
		if(dx == 0 && dy == 0 && dz == 0) {
			// invalid metadata
			w.setBlock(x, y, z, Blocks.air, 0, 3);
			return;
		}
		
		boolean ok = false;
		int prevX = x, prevY = y, prevZ = z;
		
		int length;
		
		for(length = 0; length < MAX_LENGTH; length++) {
			prevX -= dx;
			prevY -= dy;
			prevZ -= dz;
			// at prev must be another beam in the same direction, or an emitter
			if(w.getBlock(prevX, prevY, prevZ) != this || w.getBlockMetadata(prevX, prevY, prevZ) != meta) {
				TileEntity te = w.getTileEntity(prevX, prevY, prevZ);
				ok = (te instanceof TileBeamEmitter) && ((TileBeamEmitter)te).isOutputFace(dir);
				break;
			}
		}
		
		if(!ok)
			w.setBlock(x, y, z, Blocks.air, 0, 3);
		else if(length < MAX_LENGTH - 1) {
			int nextX = x+dx;
			int nextY = y+dy;
			int nextZ = z+dz;
			if(nextY >= 0 && nextY < w.getHeight() && w.isAirBlock(nextX, nextY, nextZ))
				w.setBlock(nextX, nextY, nextZ, this, meta, 3);
		}
	}
	
	@Override
	public boolean isReplaceable(IBlockAccess w, int x, int y, int z) {
		return true;
	}
	
	@Override
	public void onNeighborBlockChange(World w, int x, int y, int z, Block par5) {
		checkContinuity(w, x, y, z);
	}
	
	@Override
	public void onBlockAdded(World w, int x, int y, int z) {
		checkContinuity(w, x, y, z);
	}
	
	/*@Override
	public void updateTick(World w, int x, int y, int z, Random random) {
		// check that the beam is actually connected to an emitter
		// if not, delete it
		
		int myX = x, myY = y, myZ = z;
		
		
		
		if(!ok) {
			w.setBlock(myX, myY, myZ, 0);
		}
	}*/
	
	@Override
	public int getMobilityFlag() {
		return 2; // can't be pushed by pistons
	}
	
	@Override
	public int quantityDropped(int meta, int fortune, Random random) {
		return 0;
	}
	
	@Override
	public boolean canSilkHarvest(World world, EntityPlayer player, int x, int y, int z, int metadata) {
		return false;
	}
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		int dir = directionFromMetadata(world.getBlockMetadata(x, y, z));
		
		final float min = 7/16.0f;
		final float max = 9/16.0f;
		
		if(dir == 0 || dir == 1)
			setBlockBounds(min, 0, min, max, 1, max);
		else if(dir == 2 || dir == 3)
			setBlockBounds(min, min, 0, max, max, 1);
		else if(dir == 4 || dir == 5)
			setBlockBounds(0, min, min, 1, max, max);
		else
			setBlockBounds(0, 0, 0, 1, 1, 1);
	}

}
