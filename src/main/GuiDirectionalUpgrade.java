package mods.immibis.ars;

import mods.immibis.ars.projectors.TileProjectorExtender;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiDirectionalUpgrade extends GuiContainer {

	private TileProjectorExtender inventory;

	public GuiDirectionalUpgrade(EntityPlayer inventoryplayer, TileProjectorExtender tileEntity_Directional_Extender) {

		super(new Containerdummy(inventoryplayer, tileEntity_Directional_Extender));
		inventory = tileEntity_Directional_Extender;

	}

	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {

		buttonList.add(new GuiButton(1, (width / 2) + 50, (height / 2) - 55, 10, 10, "+"));
		buttonList.add(new GuiButton(2, (width / 2) + 10, (height / 2) - 55, 10, 10, "-"));

		super.initGui();
	}
	
	@Override
	protected void actionPerformed(GuiButton guibutton) {
		((ContainerMFFS)inventorySlots).sendButtonPressed(guibutton.id);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(R.gui.upgrade);
		int w = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		drawTexturedModalRect(w, k, 0, 0, xSize, ySize);
		int i1 = (69 * inventory.getLinkPower()) / inventory.getMaxlinkPower();
		drawTexturedModalRect(w + 93, k + 30, 176, 0, i1 + 1, 69);

	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {

		fontRendererObj.drawString("Directional extender", 20, 5, 0x404040);

		fontRendererObj.drawString("Width:", 20, 30, 0x404040);
		fontRendererObj.drawString((new StringBuilder()).append(inventory.getWide()).toString(), 120, 30, 0x404040);

	}

}
