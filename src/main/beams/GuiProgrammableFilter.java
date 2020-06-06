package mods.immibis.ars.beams;

import mods.immibis.ars.R;
import mods.immibis.core.api.util.BaseGuiContainer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiProgrammableFilter extends BaseGuiContainer<ContainerProgrammableFilter> {

	public GuiProgrammableFilter(ContainerProgrammableFilter par1Container) {
		super(par1Container, 176, 222, R.gui.programmableFilter);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		super.drawGuiContainerBackgroundLayer(var1, var2, var3);
		
		if(!container.inputsOnRight)
			drawTexturedModalRect(guiLeft+150, guiTop+25, 176, 0, 18, 18);
	}
	
	@Override
	protected void mouseClicked(int par1, int par2, int par3) {
		int x = par1 - guiLeft;
		int y = par2 - guiTop;
		
		if(par3 == 0 && x >= 150 && y >= 25 && x < 168 && y < 43) {
			container.sendButtonPressed(0);
		} else
			super.mouseClicked(par1, par2, par3);
	}

}