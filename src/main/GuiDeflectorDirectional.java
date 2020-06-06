package mods.immibis.ars;

import mods.immibis.ars.projectors.TileProjectorDeflector;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiDeflectorDirectional extends GuiContainer {

	private TileProjectorDeflector inventory;

	public GuiDeflectorDirectional(EntityPlayer inventoryplayer, TileProjectorDeflector tileEntity) {
		super(new ContainerProjektor(inventoryplayer, tileEntity));
		inventory = tileEntity;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {
		buttonList.add(new GuiButton(0, (width / 2) + 50, (height / 2) - 10, 10, 10, "+"));
		buttonList.add(new GuiButton(1, (width / 2) + 10, (height / 2) - 10, 10, 10, "-"));
		buttonList.add(new GuiButton(2, (width / 2) + 50, (height / 2) - 0, 10, 10, "+"));
		buttonList.add(new GuiButton(3, (width / 2) + 10, (height / 2) - 0, 10, 10, "-"));
		buttonList.add(new GuiButton(4, (width / 2) + 50, (height / 2) + 10, 10, 10, "+"));
		buttonList.add(new GuiButton(5, (width / 2) + 10, (height / 2) + 10, 10, 10, "-"));

		super.initGui();
	}

	@Override
	protected void actionPerformed(GuiButton guibutton) {
		((ContainerMFFS)inventorySlots).sendButtonPressed(guibutton.id);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(R.gui.projector);
		int w = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		drawTexturedModalRect(w, k, 0, 0, xSize, ySize);
		int i1 = (69 * inventory.getLinkPower()) / inventory.getMaxlinkPower();
		drawTexturedModalRect(w + 93, k + 30, 176, 0, i1 + 1, 69);

	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {

		fontRendererObj.drawString("Plate projector", 40, 5, 0x404040);
		fontRendererObj.drawString("Force power", 10, 30, 0x404040);
		fontRendererObj.drawString("available", 22, 40, 0x404040);
		fontRendererObj.drawString((new StringBuilder()).append(" ").append(inventory.getLinkPower()).toString(), 100, 45, 0x404040);
		fontRendererObj.drawString("Linked to core:", 10, 60, 0x404040);
		fontRendererObj.drawString((new StringBuilder()).append(" ").append(inventory.isLinkGenerator()).toString(), 120, 60, 0x404040);
		fontRendererObj.drawString("Frequency card:", 10, 123, 0x404040);

		fontRendererObj.drawString("X width:", 10, 75, 0x404040);
		fontRendererObj.drawString((new StringBuilder()).append(inventory.getlengthx()).toString(), 120, 75, 0x404040);
		fontRendererObj.drawString("Z width:", 10, 85, 0x404040);
		fontRendererObj.drawString((new StringBuilder()).append(inventory.getlengthz()).toString(), 120, 85, 0x404040);
		fontRendererObj.drawString("Distance:", 10, 95, 0x404040);
		fontRendererObj.drawString((new StringBuilder()).append(inventory.getDistance()).toString(), 120, 95, 0x404040);
	}

}