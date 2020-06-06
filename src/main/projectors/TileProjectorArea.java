package mods.immibis.ars.projectors;

import mods.immibis.ars.ARSMod;
import mods.immibis.ars.BlockUpgrades;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileProjectorArea extends TileProjector {

	private int radius;
	private int maxradius;
	private short mode_designe;
	private short maxmode = 2;

	public TileProjectorArea() {

		radius = 4;
		maxradius = ARSMod.maxSize;
		mode_designe = 1;
	}
	
	@Override
	public void handleButton(int id) {
		if(!getActive()) {
			switch(id) {
			case 0:
				if(getRadius() < getMaxradius())
					setRadius(getRadius() + 1);
				break;
			case 1:
				if (getRadius() > 4)
					setRadius(getRadius() - 1);
				break;
			case 2:
				if (getmode_designe() < getMaxmode())
					setmode_designe((short) (getmode_designe() + 1));
				break;
			case 3:
				if (getmode_designe() > 1)
					setmode_designe((short) (getmode_designe() - 1));
				break;
			/*case 5:
				if(offsetX > -maxOffset) {
					offsetX--;
					updateCount++;
				}
				break;
			case 4:
				if(offsetX < maxOffset) {
					offsetX++;
					updateCount++;
				}
				break;
			case 7:
				if(offsetY > -maxOffset) {
					offsetY--;
					updateCount++;
				}
				break;
			case 6:
				if(offsetY < maxOffset) {
					offsetY++;
					updateCount++;
				}
				break;
			case 9:
				if(offsetZ > -maxOffset) {
					offsetZ--;
					updateCount++;
				}
				break;
			case 8:
				if(offsetZ < maxOffset) {
					offsetZ++;
					updateCount++;
				}
				break;*/
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {

		super.readFromNBT(tag);
		radius = tag.getInteger("radius");
		mode_designe = tag.getShort("mode_designe");
		
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {

		super.writeToNBT(tag);
		tag.setInteger("radius", radius);
		tag.setShort("mode_designe", mode_designe);

	}
	
	@Override
	protected boolean acceptsUpgradeType(int meta) {
		return super.acceptsUpgradeType(meta) || meta == BlockUpgrades.META_INHIBITOR || meta == BlockUpgrades.META_SUBWATER || meta == BlockUpgrades.META_DOME;
	}

	public int getMaxradius() {
		return maxradius;
	}

	public short getMaxmode() {
		return maxmode;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {

		this.radius = radius;
		updateCount++;
	}

	public void setRadiusinit(int radius) {

		this.radius = radius;
		updateCount++;
	}

	public short getmode_designe() {
		return mode_designe;
	}

	public void setmode_designe(short mode_designe) {

		this.mode_designe = mode_designe;
		updateCount++;

	}

	public void setmode_designeinit(short mode_designe) {

		this.mode_designe = mode_designe;
		updateCount++;
	}
	
	@Override
	public void onActivateProjector() {
		if(isSubwater()) {
			if(mode_designe == 1) {
				// underwater upgrade for cube shape
				int minx = xCoord - radius;
				int maxx = xCoord + radius;
				int miny = Math.max(0, isDome() ? yCoord : yCoord - radius);
				int maxy = Math.min(255, yCoord + radius);
				int minz = zCoord - radius;
				int maxz = zCoord + radius;
				
				for(int x = minx; x <= maxx; x++)
					for(int z = minz; z <= maxz; z++)
						for(int y = miny; y <= maxy; y++) {
							if(worldObj.getBlock(x, y, z).getMaterial().isLiquid())
								worldObj.setBlockToAir(x, y, z);
						}
			} else if(mode_designe == 2) {
				// underwater upgrade for sphere shape
				
				int radius_mode = isDome() ? 0 : radius;

				for (int y1 = 0 - radius_mode; y1 <= radius; y1++) {
					for (int x1 = 0 - radius; x1 <= radius; x1++) {
						for (int z1 = 0 - radius; z1 <= radius; z1++) {
							double dist = Math.sqrt(x1 * x1 + y1 * y1 + z1 * z1);
							if (dist <= (radius - 1)) {
								if (worldObj.getBlock(xCoord + x1, yCoord + y1, zCoord + z1).getMaterial().isLiquid()) {
									worldObj.setBlockToAir(xCoord + x1, yCoord + y1, zCoord + z1);
								}
							}
						}
					}
				}
			}
		}
	}
	
	@Override
	protected FFShape getFieldShape() {
		if(mode_designe == 1) // cube
			return new FFShapeCube(this, radius);
		else // sphere
			return new FFShapeSphere(this, radius); 
	}

	/*@Override
	protected void getFieldShape(CoordinateList shape) {
		if (mode_designe == 1) {
			
			int minx = xCoord - radius;
			int maxx = xCoord + radius;
			int miny = Math.max(0, isDome() ? yCoord : yCoord - radius);
			int maxy = Math.min(255, yCoord + radius);
			int minz = zCoord - radius;
			int maxz = zCoord + radius;
			
			for(int x = minx; x <= maxx; x++)
				for(int y = miny; y <= maxy; y++) {
					shape.add(x, y, minz);
					shape.add(x, y, maxz);
				}
			
			// careful not to double-count edges or triple-count corners
			for(int x = minx; x <= maxx; x++)
				for(int z = minz + 1; z <= maxz - 1; z++) {
					if(!isDome())
						shape.add(x, miny, z);
					shape.add(x, maxy, z);
				}
			
			for(int y = isDome() ? miny : miny + 1; y <= maxy - 1; y++)
				for(int z = minz + 1; z <= maxz - 1; z++) {
					shape.add(minx, y, z);
					shape.add(maxx, y, z);
				}
			
			if(isInhibitor()) {
				for(int x = minx + 1; x < maxx - 1; x++)
					for(int y = miny + 1; y < maxy - 1; y++)
						for(int z = minz + 1; z < maxz - 1; z++)
							shape.add(x, y, z, MODE_INHIBITOR);
			}
		}

		if (mode_designe == 2) {

			int radius_mode = radius;
			
			if (this.isDome()) {
				radius_mode = 0;
			}
			for (int y1 = 0 - radius_mode; y1 <= radius; y1++) {
				for (int x1 = 0 - radius; x1 <= radius; x1++) {
					for (int z1 = 0 - radius; z1 <= radius; z1++) {

						double dist = Math.sqrt(x1*x1 + y1*y1 + z1*z1);

						if (dist <= radius && dist > (radius - 1)) {
							shape.add(xCoord + x1, yCoord + y1, zCoord + z1);
						}
					}
				}
			}
		}

	}*/

	@Override
	public ItemStack getStackInSlotOnClosing(int var1) {
		return null;
	}

	@Override
	public int[] getUpdate() {
		return new int[] {radius, maxradius, mode_designe, maxmode, offsetX, offsetY, offsetZ};
	}

	@Override
	public void handleUpdate(int[] p) {
		radius = p[0];
		maxradius = p[1];
		mode_designe = (short)p[2];
		maxmode = (short)p[3];
		offsetX = p[4];
		offsetY = p[5];
		offsetZ = p[6];
	}

	@Override
	public int estimateBlockCount() {
		return radius * radius * (isDome() ? 12 : 24);
	}

}