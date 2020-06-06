package mods.immibis.ars.projectors;

import mods.immibis.ars.ARSMod;
import mods.immibis.ars.BlockUpgrades;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileProjectorTube extends TileProjector {

	private int length;
	private int maxlength;
	private int radius;
	private int maxradius;
	private short mode_designe;
	private boolean inhiitor;

	public TileProjectorTube() {

		inhiitor = true;
		length = 1;
		radius = 2;
		maxlength = ARSMod.maxSize;
		maxradius = ARSMod.maxTubeRadius;
		mode_designe = 1;
	}
	
	@Override
	protected boolean acceptsUpgradeType(int meta) {
		return super.acceptsUpgradeType(meta) || meta == BlockUpgrades.META_INHIBITOR || meta == BlockUpgrades.META_SUBWATER || meta == BlockUpgrades.META_DOME;
	}
	
	@Override
	public void handleButton(int id) {
		if (!getActive()) {
			switch (id) {

			case 0:
				if (getActive() == false) {

					if (getRadius() < getMaxradius()) {

						setRadius(getRadius() + 1);
					}
				}
				break;
			case 1:
				if (getActive() == false) {

					if (getRadius() > 2) {
						setRadius(getRadius() - 1);
					}

				}
				break;

			case 2:
				if (getActive() == false) {

					if (getlength() < getMaxlength()) {

						setlength(getlength() + 1);
					}
				}
				break;
			case 3:
				if (getActive() == false) {

					if (getlength() > 1) {
						setlength(getlength() - 1);
					}

				}
				break;

			case 4:
				if (getActive() == false) {

					if (getmode_designe() < 2) {

						setmode_designe((short) (getmode_designe() + 1));
					}
				}
				break;
			case 5:
				if (getActive() == false) {

					if (getmode_designe() > 1) {
						setmode_designe((short) (getmode_designe() - 1));
					}

				}
				break;

			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {

		super.readFromNBT(tag);
		length = tag.getInteger("length");
		radius = tag.getInteger("radius");
		mode_designe = tag.getShort("mode_designe");

	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {

		super.writeToNBT(tag);
		tag.setInteger("length", length);
		tag.setInteger("radius", radius);
		tag.setShort("mode_designe", mode_designe);
		
	}

	public int getMaxlength() {
		return maxlength;
	}

	public int getlength() {
		return length;
	}

	public int getRadius() {
		return radius;
	}

	public int getMaxradius() {
		return maxradius;
	}

	public void setRadius(int radius) {

		this.radius = radius;
		updateCount++;
	}

	public void setRadiusinit(int radius) {

		this.radius = radius;
		updateCount++;
	}

	public void setlength(int length) {

		this.length = length;
		updateCount++;
	}

	public void setlengthinit(int length) {

		this.length = length;
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
	public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {

		if (this.getFacing() != side && (!this.getActive())) {
			this.setFacing((short) side);

			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			return true;
		}
		return false;
	}
	
	@Override
	protected FFShape getFieldShape() {
		return new FFShapeTube(this, getFacing(), length, radius, mode_designe);
	}
	
	/*@Override
	protected void getFieldShape(CoordinateList shape) {
		int tempx = 0;
		int tempy = 0;
		int tempz = 0;

		int x_loop = 0;
		int y_loop = 0;
		int z_loop = 0;

		int x_offset_s = 0;
		int y_offset_s = 0;
		int z_offset_s = 0;

		int x_offset_e = 0;
		int y_offset_e = 0;
		int z_offset_e = 0;

		if (this.getFacing() == 0 || this.getFacing() == 1) {

			y_loop = length;
			x_loop = radius;
			z_loop = radius;
			if (mode_designe == 2 && this.getFacing() == 0) {
				y_offset_s = length;
			}
			if (mode_designe == 2 && this.getFacing() == 1) {
				y_offset_s = length - length;
			}
			if (mode_designe == 2 && this.getFacing() == 0) {
				y_offset_s = length - length;
			}
			if (mode_designe == 2 && this.getFacing() == 1) {
				y_offset_s = length;
			}
		}

		if (this.getFacing() == 2 || this.getFacing() == 3) {

			y_loop = radius;
			z_loop = length;
			x_loop = radius;
			if (mode_designe == 2 && this.getFacing() == 3) {
				z_offset_s = length;
			}
			if (mode_designe == 2 && this.getFacing() == 2) {
				z_offset_s = length - length;
			}
			if (mode_designe == 2 && this.getFacing() == 3) {
				z_offset_e = length - length;
			}
			if (mode_designe == 2 && this.getFacing() == 2) {
				z_offset_e = length;
			}

		}
		if (this.getFacing() == 4 || this.getFacing() == 5) {

			y_loop = radius;
			z_loop = radius;
			x_loop = length;
			if (mode_designe == 2 && this.getFacing() == 5) {
				x_offset_s = length;
			}
			if (mode_designe == 2 && this.getFacing() == 4) {
				x_offset_s = length - length;
			}
			if (mode_designe == 2 && this.getFacing() == 5) {
				x_offset_e = length - length;
			}
			if (mode_designe == 2 && this.getFacing() == 4) {
				x_offset_e = length;
			}

		}

		for (int z1 = 0 - z_loop + z_offset_s; z1 <= z_loop - z_offset_e; z1++) {
			for (int x1 = 0 - x_loop + x_offset_s; x1 <= x_loop - x_offset_e; x1++) {
				for (int y1 = 0 - y_loop + y_offset_s; y1 <= y_loop - y_offset_e; y1++) {
					
					if(y1 < 0 && isDome())
						continue;

					int x_loop_temp = x_loop;
					int y_loop_temp = y_loop;
					int z_loop_temp = z_loop;

					if (x_loop == length && (this.getFacing() == 4 || this.getFacing() == 5)) {
						x_loop_temp += 1;
					}
					if (y_loop == length && (this.getFacing() == 0 || this.getFacing() == 1)) {
						y_loop_temp += 1;
					}
					if (z_loop == length && (this.getFacing() == 2 || this.getFacing() == 3)) {
						z_loop_temp += 1;
					}

					if (x1 == 0 - x_loop_temp || x1 == x_loop_temp || y1 == 0 - y_loop_temp || y1 == y_loop_temp || z1 == 0 - z_loop_temp || z1 == z_loop_temp) {
						shape.add(x1 + xCoord, y1 + yCoord, z1 + zCoord);
					} else {
						shape.add(x1 + xCoord, y1 + yCoord, z1 + zCoord, MODE_GAP);
					}
				}
			}
		}
	}*/
	
	@Override
	public void onActivateProjector() {
		//int tempx = 0;
		//int tempy = 0;
		//int tempz = 0;

		int x_loop = 0;
		int y_loop = 0;
		int z_loop = 0;

		int x_offset_s = 0;
		int y_offset_s = 0;
		int z_offset_s = 0;

		int x_offset_e = 0;
		int y_offset_e = 0;
		int z_offset_e = 0;

		if (this.getFacing() == 0 || this.getFacing() == 1) {

			y_loop = length;
			x_loop = radius;
			z_loop = radius;
			if (mode_designe == 2 && this.getFacing() == 0) {
				y_offset_s = length;
			}
			if (mode_designe == 2 && this.getFacing() == 1) {
				y_offset_s = length - length;
			}
			if (mode_designe == 2 && this.getFacing() == 0) {
				y_offset_s = length - length;
			}
			if (mode_designe == 2 && this.getFacing() == 1) {
				y_offset_s = length;
			}
		}

		if (this.getFacing() == 2 || this.getFacing() == 3) {

			y_loop = radius;
			z_loop = length;
			x_loop = radius;
			if (mode_designe == 2 && this.getFacing() == 3) {
				z_offset_s = length;
			}
			if (mode_designe == 2 && this.getFacing() == 2) {
				z_offset_s = length - length;
			}
			if (mode_designe == 2 && this.getFacing() == 3) {
				z_offset_e = length - length;
			}
			if (mode_designe == 2 && this.getFacing() == 2) {
				z_offset_e = length;
			}

		}
		if (this.getFacing() == 4 || this.getFacing() == 5) {
			y_loop = radius;
			z_loop = radius;
			x_loop = length;
			if (mode_designe == 2 && this.getFacing() == 5) {
				x_offset_s = length;
			}
			if (mode_designe == 2 && this.getFacing() == 4) {
				x_offset_s = length - length;
			}
			if (mode_designe == 2 && this.getFacing() == 5) {
				x_offset_e = length - length;
			}
			if (mode_designe == 2 && this.getFacing() == 4) {
				x_offset_e = length;
			}

		}

		for (int z1 = 0 - z_loop + z_offset_s; z1 <= z_loop - z_offset_e; z1++) {
			for (int x1 = 0 - x_loop + x_offset_s; x1 <= x_loop - x_offset_e; x1++) {
				for (int y1 = 0 - y_loop + y_offset_s; y1 <= y_loop - y_offset_e; y1++) {
					
					if(y1 < 0 && isDome())
						continue;

					int x_loop_temp = x_loop;
					int y_loop_temp = y_loop;
					int z_loop_temp = z_loop;

					if (x_loop == length && (this.getFacing() == 4 || this.getFacing() == 5)) {
						x_loop_temp += 1;
					}
					if (y_loop == length && (this.getFacing() == 0 || this.getFacing() == 1)) {
						y_loop_temp += 1;
					}
					if (z_loop == length && (this.getFacing() == 2 || this.getFacing() == 3)) {
						z_loop_temp += 1;
					}

					if (x1 == 0 - x_loop_temp || x1 == x_loop_temp || y1 == 0 - y_loop_temp || y1 == y_loop_temp || z1 == 0 - z_loop_temp || z1 == z_loop_temp) {

					} else {

						if (this.isSubwater()) {
							if (worldObj.getBlock(xCoord + x1, yCoord + y1, zCoord + z1).getMaterial().isLiquid()) {
								worldObj.setBlockToAir(xCoord + x1, yCoord + y1, zCoord + z1);
							}
						}

						// Let tube projectors poke holes in forcefields
						// This was neat, but buggy, and made it possible
						// to break into FF-protected bases...
						/*
						hasher.setLength(0);
						hasher.append(xCoord + x1).append("/").append(yCoord + y1).append("/").append(zCoord + z1);

						ForceFieldWorldMap ffworldmap = WorldMap.getForceFieldforWorld(worldObj).addandgetffmp(xCoord + x1, yCoord + y1, zCoord + z1);

						if (ffworldmap.listsize() > 0) {
							if (ffworldmap.ffworld_getfistactive() && ffworldmap.ffworld_getfirstfreespace() && ffworldmap.ffworld_getfirstGenerator_ID() == this.getLinkGenerator_ID()) {
								ffworldmap.ffworld_setfistactive(false);
								ffworldmap.ffworld_setfirstfreeospace(true);
								worldObj.setBlockWithNotify(xCoord + x1, yCoord + y1, zCoord + z1, 0);
							}
						}
						*/
					}
				}
			}
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int var1) {
		return null;
	}

	@Override
	public int[] getUpdate() {
		return new int[] {length, maxlength, radius, maxradius, mode_designe, inhiitor?1:0};
	}

	@Override
	public void handleUpdate(int[] p) {
		length = p[0];
		maxlength = p[1];
		radius = p[2];
		maxradius = p[3];
		mode_designe = (short)p[4];
		inhiitor = p[5]!=0;
	}

	@Override
	public int estimateBlockCount() {
		return (radius * 2 + 1) * (isDome() ? 2 : 4) * length * (mode_designe == 2 ? 1 : 2);
	}

}
