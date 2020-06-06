package mods.immibis.ars.DeFence;

import mods.immibis.core.api.APILocator;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class GuiCodeDoor extends GuiContainer
{
    public ContainerCodeDoor container;
    public GuiButton keyenter;
    public GuiButton keynew;
    
    private String codeEntered = "";
    
	/**
	 * The name that is displayed at the top of this GUI.
	 */
	public String guiname;
	/**
	 * The texture file used for the background.
	 */
	public ResourceLocation backgroundtexture;

	public GuiCodeDoor(ContainerCodeDoor con)
    {
		super(con);
        container = con;
        guiname = I18n.format("gui.advrepsys.defence.codedoor.name");
        backgroundtexture = new ResourceLocation("advrepsys", "textures/gui/DeFence_CodeDoor.png");
        xSize = 176;
        ySize = 184;
    }
    
    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    @Override
	@SuppressWarnings("unchecked")
	public void initGui()
    {
        super.initGui();
        buttonList.add(new GuiButton(0, (width - xSize) / 2 + 56, (height - ySize) / 2 + 27, 20, 20, "1"));
        buttonList.add(new GuiButton(1, (width - xSize) / 2 + 77, (height - ySize) / 2 + 27, 20, 20, "2"));
        buttonList.add(new GuiButton(2, (width - xSize) / 2 + 98, (height - ySize) / 2 + 27, 20, 20, "3"));
        buttonList.add(new GuiButton(3, (width - xSize) / 2 + 56, (height - ySize) / 2 + 48, 20, 20, "4"));
        buttonList.add(new GuiButton(4, (width - xSize) / 2 + 77, (height - ySize) / 2 + 48, 20, 20, "5"));
        buttonList.add(new GuiButton(5, (width - xSize) / 2 + 98, (height - ySize) / 2 + 48, 20, 20, "6"));
        buttonList.add(new GuiButton(6, (width - xSize) / 2 + 56, (height - ySize) / 2 + 69, 20, 20, "7"));
        buttonList.add(new GuiButton(7, (width - xSize) / 2 + 77, (height - ySize) / 2 + 69, 20, 20, "8"));
        buttonList.add(new GuiButton(8, (width - xSize) / 2 + 98, (height - ySize) / 2 + 69, 20, 20, "9"));
        keyenter = new GuiButton(9, (width - xSize) / 2 + 123, (height - ySize) / 2 + 35, 32, 20, "Enter");
        buttonList.add(keyenter);
        if(((TileEntityCodeDoor)container.tileEntity).isOwner(mc.thePlayer))
        {
        	keynew = new GuiButton(10, (width - xSize) / 2 + 123, (height - ySize) / 2 + 59, 32, 20, "Set");
        	buttonList.add(keynew);
        }
    }
    
    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    @Override
	protected void actionPerformed(GuiButton guibutton)
    {
    	if(guibutton.id < 9) {
    		if(codeEntered.length() < TileEntityCodeDoor.MAX_CODE_LENGTH)
    			codeEntered += (guibutton.id + 1);
    	} else {
    		APILocator.getNetManager().sendToServer(new DeFenceDoorGUIPacket(guibutton.id, codeEntered));
    		codeEntered = "";
    	}

        super.actionPerformed(guibutton);
    }
    
    @Override
    public void keyTyped(char par1, int par2) {
    	if(par1 >= '0' && par1 <= '9' && codeEntered.length() < TileEntityCodeDoor.MAX_CODE_LENGTH) {
    		codeEntered += par1;
    	} else if(par2 == Keyboard.KEY_RETURN) {
    		APILocator.getNetManager().sendToServer(new DeFenceDoorGUIPacket(9, codeEntered));
    		codeEntered = "";
    	}
    	super.keyTyped(par1, par2);
    }

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
	    fontRendererObj.drawString(guiname, (xSize - fontRendererObj.getStringWidth(guiname)) / 2, 6, 0x404040);
	    fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
    	fontRendererObj.drawString(codeEntered, (xSize - fontRendererObj.getStringWidth(codeEntered)) / 2, 17, 0x404040);
	}

	/**
	 * Draw the background layer for the GuiContainer (everything behind the items)
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
	    mc.renderEngine.bindTexture(backgroundtexture);
	    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	    int l = (width - xSize) / 2;
	    int i1 = (height - ySize) / 2;
	    drawTexturedModalRect(l, i1, 0, 0, xSize, ySize);
	}
}