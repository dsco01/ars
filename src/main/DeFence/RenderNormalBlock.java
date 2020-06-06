// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package mods.immibis.ars.DeFence;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;
/**
 * 
 * Class for storing a simple method to render a normal block.
 * 
 * @author F4113nb34st
 *
 */
public class RenderNormalBlock
{
    public static boolean render(RenderBlocks renderblocks, int i, int j, int k, Block block)
    {
        block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        renderblocks.renderStandardBlock(block, i, j, k);
        return true;
    }
    
    public static boolean renderInInv(RenderBlocks renderblocks, Block block, int meta)
    {
        block.setBlockBoundsForItemRender();
        renderInv(renderblocks, block, meta);
        return true;
    }
    
    private static void renderInv(RenderBlocks rb, Block block, int meta)
    {
    	Tessellator t = Tessellator.instance;
        
        rb.setRenderBoundsFromBlock(block);
        
        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        
        t.startDrawingQuads();
        t.setNormal(0.0F, -1.0F, 0.0F);
        rb.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(0, meta));
        t.draw();

        t.startDrawingQuads();
        t.setNormal(0.0F, 1.0F, 0.0F);
        rb.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(1, meta));
        t.draw();

        t.startDrawingQuads();
        t.setNormal(0.0F, 0.0F, -1.0F);
        rb.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(2, meta));
        t.draw();
        
        t.startDrawingQuads();
        t.setNormal(0.0F, 0.0F, 1.0F);
        rb.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(3, meta));
        t.draw();
        
        t.startDrawingQuads();
        t.setNormal(-1.0F, 0.0F, 0.0F);
        rb.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(4, meta));
        t.draw();
        
        t.startDrawingQuads();
        t.setNormal(1.0F, 0.0F, 0.0F);
        rb.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(5, meta));
        t.draw();
        
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    }
}
