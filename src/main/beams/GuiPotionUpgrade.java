package mods.immibis.ars.beams;

import mods.immibis.ars.R;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiPotionUpgrade extends GuiContainer {
	public GuiPotionUpgrade(Container container) {
		super(container);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int p1, int p2) {
		fontRendererObj.drawString(StatCollector.translateToLocal("tile.immibis/ars:potion-applicator.name"), 8, 6, 0x404040);
		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96 + 2, 0x404040);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		GL11.glColor4f(1, 1, 1, 1);
		mc.renderEngine.bindTexture(R.gui.potionApplicator);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}
}