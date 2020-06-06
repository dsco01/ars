package mods.immibis.ars;

import mods.immibis.core.api.porting.PortableBlockRenderer;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BlockRendererField implements PortableBlockRenderer {
	@Override
	public boolean renderWorldBlock(RenderBlocks rb, IBlockAccess w, int x, int y, int z, Block b, int model) {
		if(b == ARSMod.MFFSFieldblock) {
			int meta = w.getBlockMetadata(x, y, z);
			
			//boolean usingCamo = false;
			
			if(BlockForceField.isCamo(meta))
			{
				TileEntity te = w.getTileEntity(x, y, z);
				if(te != null)
				{
					int camoBlock = ((TileCamouflagedField)te).camoBlockId;
					Block block = Block.getBlockById(TileCamouflagedField.getBlockID(camoBlock));
					
					if(block != null) {
						int camoMeta = TileCamouflagedField.getMetadata(camoBlock);
						
						for(int k = 0; k < 6; k++) {
							try {
								BlockForceField.useTextures[k] = block.getIcon(k, camoMeta);
							} catch(Exception e) {
								BlockForceField.useTextures[k] = null;
							}
						}
						
						BlockForceField.renderingCamo = true;
					}
				}
			}
			
			rb.renderStandardBlock(b, x, y, z);
			
			BlockForceField.renderingCamo = false;
			
			return true;
		}
		return false;
	}
	
	@Override
	public void renderInvBlock(RenderBlocks rb, Block b, int meta, int model) {
		// field blocks shouldn't be in inventories - let them be invisible
	}
}

/* $endif$ */