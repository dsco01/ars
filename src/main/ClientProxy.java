package mods.immibis.ars;

import mods.immibis.ars.beams.BlockBeamStuffRenderStatic;
import mods.immibis.ars.beams.EntityBoltFX;
import mods.immibis.ars.beams.PacketBoltFX;
import net.minecraft.client.Minecraft;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends BaseProxy {
	@Override
	public void makeBoltFX(PacketBoltFX p) {
		Minecraft mc = Minecraft.getMinecraft();
		mc.theWorld.spawnEntityInWorld(new EntityBoltFX(mc.theWorld, p.x1, p.y1, p.z1, p.x2, p.y2, p.z2));
	}
	
	public ClientProxy() {
		RenderingRegistry.registerBlockHandler(new BlockBeamStuffRenderStatic());
		RenderingRegistry.registerBlockHandler(new BlockMachineRenderStatic());
	}
}