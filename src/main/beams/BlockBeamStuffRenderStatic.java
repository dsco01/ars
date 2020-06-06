package mods.immibis.ars.beams;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BlockBeamStuffRenderStatic implements ISimpleBlockRenderingHandler {

	public static int modelID = RenderingRegistry.getNextAvailableRenderId();
	
	static {
		BlockBeamStuff.renderType = modelID;
	}
	
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		BlockBeamStuff.renderType = 0;
		//int passes = BlockBeamStuff.getRenderPassesForMeta(metadata);
		//for(int k = 0; k < passes; k++) {
		//	BlockBeamStuff.renderPass = k;
			BlockBeamStuff.renderPass = 0;
			renderer.renderBlockAsItem(block, metadata, 1);
		//}
		BlockBeamStuff.renderType = modelID;
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		BlockBeamStuff.renderType = 0;
		int meta = world.getBlockMetadata(x, y, z);
		for(int k = 0; k < BlockBeamStuff.getRenderPassesForMeta(meta); k++) {
			BlockBeamStuff.renderPass = k;
			renderer.renderStandardBlock(block, x, y, z);
		}
		BlockBeamStuff.renderType = modelID;
		
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int model) {
		return true;
	}

	@Override
	public int getRenderId() {
		return modelID;
	}

}
