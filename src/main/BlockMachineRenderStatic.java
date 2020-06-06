package mods.immibis.ars;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BlockMachineRenderStatic implements ISimpleBlockRenderingHandler {
	public static int modelID = RenderingRegistry.getNextAvailableRenderId();
	static {
		BlockMachine.renderType = modelID;
	}
	
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		BlockMachine.renderType = 0;
		BlockMachine.renderPass = 0;
		renderer.renderBlockAsItem(block, metadata, 1);
		BlockMachine.renderType = modelID;
	}
	
	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		BlockMachine.renderPass = 0;
		renderer.renderStandardBlock(block, x, y, z);
		BlockMachine.renderPass = 1;
		renderer.renderStandardBlock(block, x, y, z);
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
