package mods.immibis.ars.beams;

import mods.immibis.ars.R;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiInventoryContentsFilter extends GuiContainer {

	public GuiInventoryContentsFilter(Container par1Container) {
		super(par1Container);
		
		xSize = 176;
		ySize = 222;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		GL11.glColor4f(1, 1, 1, 1);
		
        mc.renderEngine.bindTexture(R.gui.invContentsFilter);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        
        fontRendererObj.drawString("Filter:", guiLeft+37, guiTop+33, 0x404040);
        
	}

}