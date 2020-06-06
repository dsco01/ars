package mods.immibis.ars;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiGenerator extends GuiContainer {

	private TileEntityGeneratorCore Core;

	public GuiGenerator(EntityPlayer inventoryPlayer, TileEntityGeneratorCore tileentity) {

		super(new ContainerGenerator(inventoryPlayer, tileentity));
		Core = tileentity;

	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(R.gui.projector);
		int w = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		drawTexturedModalRect(w, k, 0, 0, xSize, ySize);
		int i1 = Core.gaugeFuelScaled(69);
		drawTexturedModalRect(w + 93, k + 30, 176, 0, i1 + 1, 69);

	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {

		fontRendererObj.drawString("Forcefield core", 30, 5, 0x404040);
		fontRendererObj.drawString("Force power", 10, 30, 0x404040);
		fontRendererObj.drawString("storage", 22, 40, 0x404040);
		fontRendererObj.drawString((new StringBuilder()).append(" ").append(Core.getForcepower()).toString(), 100, 45, 0x404040);

		fontRendererObj.drawString("Transmit range:", 10, 65, 0x404040);
		fontRendererObj.drawString((new StringBuilder()).append(" ").append(Core.getTransmitrange()).toString(), 120, 65, 0x404040);
		fontRendererObj.drawString("Linked projectors:", 10, 80, 0x404040);
		fontRendererObj.drawString((new StringBuilder()).append(" ").append(Core.getLinketprojektor()).toString(), 120, 80, 0x404040);
		fontRendererObj.drawString("Frequency card:", 10, 123, 0x404040);

	}
	
	@Override
	protected void actionPerformed(GuiButton guibutton) {
		((ContainerMFFS)inventorySlots).sendButtonPressed(guibutton.id);
	}

}
