package mods.immibis.ars.beams;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityBoltFXRenderer extends Render {

	@Override
	public void doRender(Entity var1, double var2, double var4, double var6, float var8, float var9) {
		EntityBoltFX e = (EntityBoltFX)var1;
		
		if(e.points.length < 2)
			return;
		
		Vec3 eyeToEnt, boltDir = null, perpendicular;
		
		final double WIDTH = 0.1;
		
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor3f(1, 1, 1);
		GL11.glLineWidth(5);
		GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
		for(int k = 0; k < e.points.length; k++) {
			float[] p = e.points[k];
			float[] next = k < e.points.length - 1 ? e.points[k + 1] : null;
			
			eyeToEnt = Vec3.createVectorHelper(p[0] + var2, p[1] + var4, p[2] + var6);
			if(next != null)
				boltDir = Vec3.createVectorHelper(next[0] - p[0], next[1] - p[1], next[2] - p[2]);
			perpendicular = eyeToEnt.crossProduct(boltDir).normalize();
			
			GL11.glVertex3d(p[0]+var2 - WIDTH*perpendicular.xCoord, p[1]+var4 - WIDTH*perpendicular.yCoord, p[2]+var6 - WIDTH*perpendicular.zCoord);
			GL11.glVertex3d(p[0]+var2 + WIDTH*perpendicular.xCoord, p[1]+var4 + WIDTH*perpendicular.yCoord, p[2]+var6 + WIDTH*perpendicular.zCoord);
		}
		GL11.glEnd();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return null; // not called
	}

}