package mods.immibis.ars.beams;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityBoltFX extends Entity {
	
	private AxisAlignedBB bb;
	
	public float[][] points;
	
	private int lifetime = 2;
	
	private double x1, x2, y1, y2, z1, z2;
	private double RANDOMIZATION;

	public EntityBoltFX(World par1World, double x1, double y1, double z1, double x2, double y2, double z2) {
		super(par1World);
		bb = AxisAlignedBB.getBoundingBox(
			Math.min(x1, x2),
			Math.min(y1, y2),
			Math.min(z1, z2),
			Math.max(x1, x2),
			Math.max(y1, y2),
			Math.max(z1, z2));
		posX = prevPosX = lastTickPosX = (x1 + x2) / 2;
		posY = prevPosY = lastTickPosY = (y1 + y2) / 2;
		posZ = prevPosZ = lastTickPosZ = (z1 + z2) / 2;
		boundingBox.setBB(bb);
		
		this.x1 = x1;
		this.y1 = y1;
		this.z1 = z1;
		this.x2 = x2;
		this.y2 = y2;
		this.z2 = z2;
		
		double length = Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2) + (z1-z2)*(z1-z2));
		int nPoints = (int)length + 1;
		points = new float[nPoints][3];
		
		//RANDOMIZATION = Math.sqrt(length / 30) / 6;
		RANDOMIZATION = 0.30;
		
		generatePoints();
	}
	
	private void generatePoints() {
		double dx = 0, dy = 0, dz = 0;
		for(int k = 0; k < points.length; k++) {
			double frac = k / (double)(points.length - 1);
			double xBase = x1 + (x2 - x1) * frac;
			double yBase = y1 + (y2 - y1) * frac;
			double zBase = z1 + (z2 - z1) * frac;
			if(k != 0 && k != points.length - 1) {
				dx += worldObj.rand.nextGaussian()*RANDOMIZATION;
				dy += worldObj.rand.nextGaussian()*RANDOMIZATION;
				dz += worldObj.rand.nextGaussian()*RANDOMIZATION;
			}
			double maxrad = RANDOMIZATION * (points.length - k - 1);
			if(dx > maxrad) dx = maxrad;
			if(dx < -maxrad) dx = -maxrad;
			if(dy > maxrad) dy = maxrad;
			if(dy < -maxrad) dy = -maxrad;
			if(dz > maxrad) dz = maxrad;
			if(dz < -maxrad) dz = -maxrad;
			
			points[k][0] = (float)(xBase + dx - posX);
			points[k][1] = (float)(yBase + dy - posY);
			points[k][2] = (float)(zBase + dz - posZ);
		}
	}
	
	@Override
	public void setPosition(double x, double y, double z) {
		
	}

	@Override
	protected void entityInit() {
	}
	
	@Override
	public void onUpdate() {
		if(--lifetime < 0)
			setDead();
		else
			generatePoints();
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound var1) {
		setDead();
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound var1) {
	}

}