package mods.immibis.ars;

import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiCamouflageUpgrade extends GuiContainer {

	private TileUpgradeCamouflage inventory;

	public GuiCamouflageUpgrade(EntityPlayer inventoryplayer, TileUpgradeCamouflage tileEntity_Camoflage_Upgrade) {

		super(new ContainerCamoflage(inventoryplayer, tileEntity_Camoflage_Upgrade));
		inventory = tileEntity_Camoflage_Upgrade;

	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(R.gui.camoUpgrade);
		int w = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		drawTexturedModalRect(w, k, 0, 0, xSize, ySize);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {

		fontRendererObj.drawString("Camouflage upgrade", 5, 5, 0x404040);

		fontRendererObj.drawString("Block pattern:", 20, 35, 0x404040);

		int id = inventory.getItem_ID();
		Block block = Block.getBlockById(TileCamouflagedField.getBlockID(id));
		int meta = TileCamouflagedField.getMetadata(id);
		
		boolean ok = block != null;
		
		if(ok) {
			for(int k = 0; k < 6; k++) {
				try {
					if(block.getIcon(k, meta) == null)
						ok = false;
				} catch(Throwable t) {
					ok = false;
				}
			}
		}
		
		if(ok)
			fontRendererObj.drawString("OK", 140, 35, 0x404040);

	}

}
