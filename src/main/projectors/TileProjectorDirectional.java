package mods.immibis.ars.projectors;

import mods.immibis.ars.ARSMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileProjectorDirectional extends TileProjector {

	private int length;
	private int maxlength;
	private int distance;
	private int maxdistance;

	public TileProjectorDirectional() {

		length = 1;
		distance = 0;
		maxlength = ARSMod.maxSize;
		maxdistance = 10;

	}

	@Override
	public void handleButton(int id) {
		if (!getActive()) {
			switch (id) {

			case 0:
				if (getActive() == false) {

					if (getlength() < getMaxlength()) {

						setlength(getlength() + 1);
					}
				}
				break;
			case 1:
				if (getActive() == false) {

					if (getlength() > 1) {
						setlength(getlength() - 1);
					}

				}
				break;
			case 2:
				if (getActive() == false) {

					if (getDistance() < getMaxdistance()) {
						setDistance(getDistance() + 1);
					}
				}
				break;
			case 3:
				if (getActive() == false) {

					if (getDistance() > 0) {
						setDistance(getDistance() - 1);
					}

				}
				break;

			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {

		super.readFromNBT(nbttagcompound);
		length = nbttagcompound.getInteger("length");
		distance = nbttagcompound.getInteger("distance");

	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {

		super.writeToNBT(nbttagcompound);
		nbttagcompound.setInteger("length", length);
		nbttagcompound.setInteger("distance", distance);

	}

	public void Check_con_Extender() {
		for (int x = xCoord - 1; x <= xCoord + 1; x++) {
			for (int y = yCoord - 1; y <= yCoord + 1; y++) {
				for (int z = zCoord - 1; z <= zCoord + 1; z++) {

					TileEntity tileEntity = worldObj.getTileEntity(x, y, z);

					if (tileEntity != null) {
						if (tileEntity instanceof TileProjectorExtender) {
							TileProjectorExtender ext = (TileProjectorExtender)tileEntity;
							if (ext.getRemProjektor_ID() == 0 && ext.isCon_to_projektor() == false && this.getLinkGenerator_ID() != 0 && this.getProjektor_ID() != 0) {

								ext.setRemGenerator_ID(this.getLinkGenerator_ID());
								ext.setRemProjektor_ID(this.getProjektor_ID());
								ext.setCon_to_projektor(true);
							}
							if (ext.isCon_to_projektor() == true) {

								if (ext.getffmeta() != this.getffmeta()) {
									ext.setffmeta(this.getffmeta());
								}
								if (ext.getTextur() != this.getTextur()) {
									ext.setTextur(this.getTextur());
								}

								if (ext.getFacing() != this.getFacing()) {
									ext.setFacing(this.getFacing());
									worldObj.markBlockForUpdate(x, y, z);
								}
								if (ext.getDistance() != this.getDistance()) {
									ext.setDistance(this.getDistance());
								}

								if (ext.getLength() != this.getlength()) {
									ext.setlength(this.getlength());
								}
								if (ext.isBlockBreaker() != this.isBlockBreaker()) {
									ext.hardner = this.isBlockBreaker();
								}
								if (ext.isPreactive() != this.getActive()) {
									ext.setPreactive(this.getActive());
								}
								
								ext.offsetX = offsetX;
								ext.offsetY = offsetY;
								ext.offsetZ = offsetZ;

							}

						}
					}
				}
			}
		}
	}

	public int getMaxlength() {
		return maxlength;
	}

	public int getlength() {
		return length;
	}

	public int getDistance() {
		return distance;
	}

	public int getMaxdistance() {
		return maxdistance;
	}

	public void setDistance(int distance) {

		this.distance = distance;
		updateCount++;
	}

	public void setDistanceinit(int distance) {

		this.distance = distance;
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
	public void checkupgrades() {
		super.checkupgrades();
		Check_con_Extender();
	}
	
	@Override
	protected FFShape getFieldShape() {
		return new FFShapeDirectional(this, getFacing(), distance, getlength());
	}

	/*
	@Override
	public void getFieldShape(CoordinateList shape) {
		int tempx = 0;
		int tempy = 0;
		int tempz = 0;

		for (int y1 = 1; y1 < getlength() + 1; y1++) {

			if (this.getFacing() == 0) {
				tempy = y1 - y1 - y1 - distance;
			}

			if (this.getFacing() == 1) {
				tempy = y1 + distance;
			}

			if (this.getFacing() == 2) {
				tempz = y1 - y1 - y1 - distance;
			}

			if (this.getFacing() == 3) {
				tempz = y1 + distance;
			}

			if (this.getFacing() == 4) {
				tempx = y1 - y1 - y1 - distance;
			}
			if (this.getFacing() == 5) {
				tempx = y1 + distance;
			}
			
			shape.add(xCoord + tempx, yCoord + tempy, zCoord + tempz);
		}
		
	}*/

	@Override
	public ItemStack getStackInSlotOnClosing(int var1) {
		return null;
	}

	@Override
	public int[] getUpdate() {
		return new int[] {length, maxlength, distance, maxdistance};
	}

	@Override
	public void handleUpdate(int[] p) {
		length = p[0];
		maxlength = p[1];
		distance = p[2];
		maxdistance = p[3];
	}

	@Override
	public int estimateBlockCount() {
		return getlength();
	}
}
