package mods.immibis.ars.projectors;

import mods.immibis.ars.ARSMod;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileProjectorExtender extends TileProjector {

	private boolean con_to_projektor;
	private int remProjektor_ID;
	private int remGenerator_ID;
	private int wide;
	private int maxwide;
	private int length;
	private int distance;
	private boolean preactive;
	private short ausrichtungx;
	private short ausrichtungy;
	private short ausrichtungz;
	
	@Override
	public int[] getUpdate() {
		return new int[] {con_to_projektor?1:0, remProjektor_ID, remGenerator_ID, wide, maxwide, length, distance, preactive?1:0, ausrichtungx, ausrichtungy, ausrichtungz};  
	}

	@Override
	public void handleUpdate(int[] p) {
		con_to_projektor = p[0] != 0;
		remProjektor_ID = p[1];
		remGenerator_ID = p[2];
		wide = p[3];
		maxwide = p[4];
		length = p[5];
		distance = p[6];
		preactive = p[7] != 0;
		ausrichtungx = (short)p[8];
		ausrichtungy = (short)p[9];
		ausrichtungz = (short)p[10];
		
	}
	
	@Override
	public void handleButton(int id) {
		if (!getActive()) {
			switch (id) {
	
			case 1:
				if (getActive() == false) {
	
					if (getWide() < getMaxwide()) {
	
						setWide(getWide() + 1);
					}
				}
				break;
			case 2:
				if (getActive() == false) {
	
					if (getWide() > 0) {
						setWide(getWide() - 1);
					}
	
				}
				break;
	
			}
		}
	}

	public TileProjectorExtender() {

		preactive = false;
		con_to_projektor = false;
		remProjektor_ID = 0;
		remGenerator_ID = 0;
		wide = 1;
		maxwide = ARSMod.maxSize;

	}

	public int getMaxwide() {
		return maxwide;
	}

	public int getWide() {
		return wide;
	}

	public void setWide(int wide) {

		this.wide = wide;
		updateCount++;
	}

	public void setWideinit(int wide) {

		this.wide = wide;
	}

	public int getRemGenerator_ID() {
		return remGenerator_ID;
	}

	public void setRemGenerator_ID(int remGenerator_ID) {
		this.remGenerator_ID = remGenerator_ID;
		this.setLinkGenerator_ID(remGenerator_ID);
		updateCount++;
	}

	public boolean isPreactive() {
		return preactive;
	}

	public void setPreactive(boolean preactive) {
		this.preactive = preactive;
		updateCount++;
	}

	public int getLength() {
		return length;
	}

	public void setDistance(int distance) {
		updateCount++;
		this.distance = distance;
	}

	public void setlength(int length) {
		updateCount++;
		this.length = length;
	}

	public int getDistance() {
		return distance;
	}

	public boolean isCon_to_projektor() {
		return con_to_projektor;
	}

	public void setCon_to_projektor(boolean con_to_projektor) {
		this.con_to_projektor = con_to_projektor;
		updateCount++;
	}

	public int getRemProjektor_ID() {
		return remProjektor_ID;
	}

	public void setRemProjektor_ID(int remProjektor_ID) {
		this.remProjektor_ID = remProjektor_ID;
		updateCount++;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {

		super.readFromNBT(nbttagcompound);

		wide = nbttagcompound.getInteger("wide");
		length = nbttagcompound.getInteger("length");
		distance = nbttagcompound.getInteger("distance");

	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {

		super.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("wide", wide);
		nbttagcompound.setInteger("length", length);
		nbttagcompound.setInteger("distance", distance);

	}

	@Override
	public void updateEntity() {
		if(!worldObj.isRemote) {

			if (this.isCreate() && getRemGenerator_ID() != 0) {
				if (this.isPreactive()) {
					createField();
				}
				this.setCreate(false);
			}

			if (this.isPreactive() == true && !this.isCreate()) {

				if (getActive() != true) {
					setActive(true);
					createField();
					FieldGenerate(true);
					worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				}
			}

			if (this.isPreactive() == false && !this.isCreate()) {

				if (getActive() != false) {
					setActive(false);
					destroyField();
					worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				}
			}

			if (getActive()) {
				FieldGenerate(false);
			}

			if (getActive() && getWrenchDropRate() != -1.0F) {
				setWrenchRate(-1.0F);
			}
			if (!getActive() && getWrenchDropRate() != 1.0F) {
				setWrenchRate(1.0F);
			}

		}
	}
	
	@Override
	public FFShape getFieldShape() {
		boolean found = false;

		for (int x = xCoord - 1; x <= xCoord + 1; x++) {
			for (int y = yCoord - 1; y <= yCoord + 1; y++) {
				for (int z = zCoord - 1; z <= zCoord + 1; z++) {

					if (worldObj.getBlock(x, y, z) == ARSMod.MFFSMaschines && worldObj.getBlockMetadata(x, y, z) == 2) {
						ausrichtungx = (short) (xCoord - x);
						ausrichtungy = (short) (yCoord - y);
						ausrichtungz = (short) (zCoord - z);
						found = true;
					}

				}
			}
		}
		
		if(!found)
			return null;
		
		return new FFShapeExtender(this, getFacing(), getWide(), getLength(), getDistance(), ausrichtungx, ausrichtungy, ausrichtungz);
	}

	/*
	@Override
	public void getFieldShape(CoordinateList shape) {
		int tempx = 0;
		int tempy = 0;
		int tempz = 0;

		for (int x1 = 0; x1 <= getWide(); x1++) {
			for (int y1 = 1; y1 < getLength() + 1; y1++) {

				if (this.getFacing() == 0) {
					tempy = y1 - y1 - y1 - getDistance();
					if (ausrichtungx > 0) {
						tempx = x1;
					}
					if (ausrichtungx < 0) {
						tempx = x1 - x1 - x1;
					}
					if (ausrichtungz > 0) {
						tempz = x1;
					}
					if (ausrichtungz < 0) {
						tempz = x1 - x1 - x1;
					}

				}

				if (this.getFacing() == 1) {
					tempy = y1 + getDistance();
					if (ausrichtungx > 0) {
						tempx = x1;
					}
					if (ausrichtungx < 0) {
						tempx = x1 - x1 - x1;
					}
					if (ausrichtungz > 0) {
						tempz = x1;
					}
					if (ausrichtungz < 0) {
						tempz = x1 - x1 - x1;
					}

				}

				if (this.getFacing() == 2) {
					tempz = y1 - y1 - y1 - getDistance();
					if (ausrichtungx > 0) {
						tempx = x1;
					}
					if (ausrichtungx < 0) {
						tempx = x1 - x1 - x1;
					}
					if (ausrichtungy > 0) {
						tempy = x1;
					}
					if (ausrichtungy < 0) {
						tempy = x1 - x1 - x1;
					}
				}

				if (this.getFacing() == 3) {
					tempz = y1 + getDistance();
					if (ausrichtungx > 0) {
						tempx = x1;
					}
					if (ausrichtungx < 0) {
						tempx = x1 - x1 - x1;
					}
					if (ausrichtungy > 0) {
						tempy = x1;
					}
					if (ausrichtungy < 0) {
						tempy = x1 - x1 - x1;
					}
				}

				if (this.getFacing() == 4) {
					tempx = y1 - y1 - y1 - getDistance();
					if (ausrichtungz > 0) {
						tempz = x1;
					}
					if (ausrichtungz < 0) {
						tempz = x1 - x1 - x1;
					}
					if (ausrichtungy > 0) {
						tempy = x1;
					}
					if (ausrichtungy < 0) {
						tempy = x1 - x1 - x1;
					}

				}
				if (this.getFacing() == 5) {
					tempx = y1 + getDistance();
					if (ausrichtungz > 0) {
						tempz = x1;
					}
					if (ausrichtungz < 0) {
						tempz = x1 - x1 - x1;
					}
					if (ausrichtungy > 0) {
						tempy = x1;
					}
					if (ausrichtungy < 0) {
						tempy = x1 - x1 - x1;
					}

				}
				
				shape.add(xCoord + tempx, yCoord + tempy, zCoord + tempz);
			}
		}
	}
	*/

	@Override
	public ItemStack getStackInSlotOnClosing(int var1) {
		return null;
	}

	@Override
	public int estimateBlockCount() {
		return (getWide() + 1) * getLength();
	}
}
