package mods.immibis.ars;

import mods.immibis.ars.projectors.TileProjectorArea;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiProjektorArea extends GuiContainer {

	private TileProjectorArea inventory;
	private String mode;

	public GuiProjektorArea(EntityPlayer inventoryplayer, TileProjectorArea tileEntity_Cube_Projektor) {

		super(new ContainerProjektor(inventoryplayer, tileEntity_Cube_Projektor));
		inventory = tileEntity_Cube_Projektor;

	}

	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {

		buttonList.add(new GuiButton(0, (width / 2) + 50, (height / 2) - 10, 10, 10, "+"));
		buttonList.add(new GuiButton(1, (width / 2) + 10, (height / 2) - 10, 10, 10, "-"));
		buttonList.add(new GuiButton(2, (width / 2) + 60, (height / 2) + 10, 10, 10, "+"));
		buttonList.add(new GuiButton(3, (width / 2) + 0, (height / 2) + 10, 10, 10, "-"));

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

		switch (inventory.getmode_designe()) {
		case 1:
			mode = "cube";
			break;
		case 2:
			mode = "sphere";
			break;
		}

		fontRendererObj.drawString("Area forcefield projector", 20, 5, 0x404040);
		fontRendererObj.drawString("Force power", 10, 30, 0x404040);
		fontRendererObj.drawString("available", 22, 40, 0x404040);
		fontRendererObj.drawString("Linked to core:", 10, 60, 0x404040);
		fontRendererObj.drawString("Frequency card:", 10, 123, 0x404040);
		fontRendererObj.drawString("Radius:", 10, 75, 0x404040);
		fontRendererObj.drawString("Mode:", 10, 95, 0x404040);

		fontRendererObj.drawString((new StringBuilder()).append(" ").append(inventory.getLinkPower()).toString(), 100, 45, 0x404040);
		fontRendererObj.drawString((new StringBuilder()).append(" ").append(inventory.isLinkGenerator()).toString(), 120, 60, 0x404040);
		fontRendererObj.drawString((new StringBuilder()).append(mode).toString(), 105, 95, 0x404040);
		fontRendererObj.drawString((new StringBuilder()).append(inventory.getRadius()).toString(), 120, 75, 0x404040);

	}

}