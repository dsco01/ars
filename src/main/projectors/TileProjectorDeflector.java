package mods.immibis.ars.projectors;

import mods.immibis.ars.ARSMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileProjectorDeflector extends TileProjector {

	private int lengthx;
	private int lengthz;
	private int maxlengthx;
	private int maxlengthz;
	private int distance;
	private int maxdistance;
	
	@Override
	public int[] getUpdate() {
		return new int[] {lengthx, lengthz, maxlengthx, maxlengthz, distance, maxdistance};
	}

	@Override
	public void handleUpdate(int[] p) {
		lengthx = p[0];
		lengthz = p[1];
		maxlengthx = p[2];
		maxlengthz = p[3];
		distance = p[4];
		maxdistance = p[5];
	}

	public TileProjectorDeflector() {

		lengthx = 0;
		lengthz = 0;
		distance = 0;
		maxlengthx = ARSMod.maxSize;
		maxlengthz = ARSMod.maxSize;
		maxdistance = ARSMod.maxDeflectorDistance;

	}
	
	@Override
	public void handleButton(int id) {
		if(!getActive()) 
		{
			switch(id) {
			case 0:
				if (getlengthx() < getMaxlengthx())
					setlengthx(getlengthx() + 1);
				break;
			case 1:
				if (getlengthx() > 0)
					setlengthx(getlengthx() - 1);
				break;
			case 2:
				if (getlengthz() < getMaxlengthz())
					setlengthz(getlengthz() + 1);
				break;
			case 3:
				if (getlengthz() > 0)
					setlengthz(getlengthz() - 1);
				break;
			case 4:
				if (getDistance() < getMaxdistance())
					setDistance(getDistance() + 1);
				break;
			case 5:
				if (getDistance() > 0)
					setDistance(getDistance() - 1);
				break;
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {

		super.readFromNBT(nbttagcompound);
		lengthx = nbttagcompound.getInteger("lengthx");
		lengthz = nbttagcompound.getInteger("lengthz");
		distance = nbttagcompound.getInteger("distance");

	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {

		super.writeToNBT(nbttagcompound);
		nbttagcompound.setInteger("lengthx", lengthx);
		nbttagcompound.setInteger("lengthz", lengthz);
		nbttagcompound.setInteger("distance", distance);

	}

	public int getMaxlengthx() {
		return maxlengthx;
	}

	public int getlengthx() {
		return lengthx;
	}

	public int getMaxlengthz() {
		return maxlengthz;
	}

	public int getlengthz() {
		return lengthz;
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

	public void setlengthx(int lengthx) {

		this.lengthx = lengthx;
		updateCount++;
	}

	public void setlengthxinit(int lengthx) {

		this.lengthx = lengthx;
		updateCount++;
	}

	public void setlengthz(int lengthz) {

		this.lengthz = lengthz;
		updateCount++;
	}

	public void setlengthzinit(int lengthz) {

		this.lengthz = lengthz;
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
	public int estimateBlockCount() {
		return lengthx * lengthz;
	}
	
	@Override
	public FFShape getFieldShape() {
		return new FFShapeDeflector(this);
	}

	/*@Override
	public void getFieldShape(CoordinateList shape) {

		int tempx = 0;
		int tempy = 0;
		int tempz = 0;

		for (int x1 = 0 - getlengthx(); x1 < getlengthx() + 1; x1++) {
			for (int z1 = 0 - getlengthz(); z1 < getlengthz() + 1; z1++) {

				if (this.getFacing() == 0) {
					tempy = 0 - distance - 1;
					tempx = x1;
					tempz = z1;
				}

				if (this.getFacing() == 1) {
					tempy = 0 + distance + 1;
					tempx = x1;
					tempz = z1;
				}

				if (this.getFacing() == 2) {
					tempz = 0 - distance - 1;
					tempy = x1;
					tempx = z1;
				}

				if (this.getFacing() == 3) {
					tempz = 0 + distance + 1;
					tempy = x1;
					tempx = z1;
				}

				if (this.getFacing() == 4) {
					tempx = 0 - distance - 1;
					tempy = x1;
					tempz = z1;
				}
				if (this.getFacing() == 5) {
					tempx = 0 + distance + 1;
					tempy = x1;
					tempz = z1;
				}
				
				shape.add(xCoord + tempx, yCoord + tempy, zCoord + tempz);
			}
		}

	}*/

	@Override
	public ItemStack getStackInSlotOnClosing(int var1) {
		// TODO Auto-generated method stub
		return null;
	}

}
