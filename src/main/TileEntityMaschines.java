package mods.immibis.ars;

import ic2.api.tile.IWrenchable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;

public abstract class TileEntityMaschines extends TileEntityMFFS implements IWrenchable {

	private boolean init;
	private boolean active;
	private short facing;
	private float wrenchRate;
	public long activatedTime;

	public TileEntityMaschines()
	{
		init = true;
		active = false;
		facing = -1;
		wrenchRate = 1.0F;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {

		super.readFromNBT(nbttagcompound);
		facing = nbttagcompound.getShort("facing");
		active = nbttagcompound.getBoolean("active");
		wrenchRate = nbttagcompound.getFloat("wrenchRate");
		activatedTime = nbttagcompound.getLong("activatedTime");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setShort("facing", facing);
		nbttagcompound.setBoolean("active", active);
		nbttagcompound.setFloat("wrenchRate", wrenchRate);
		nbttagcompound.setLong("activatedTime", activatedTime);
	}

	@Override
	public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
		return false;
	}
	
	@Override
	public Packet getDescriptionPacket() {
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, (facing & 7) | (active ? 8 : 0), null);
	}
	
	@Override
	public void onDataPacket(S35PacketUpdateTileEntity p) {
		setFacing((byte)(p.func_148853_f() & 7));
		setActive((p.func_148853_f() & 8) != 0);
	}

	@Override
	public void setFacing(short i) {
		facing = i;
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public boolean getActive() {
		return active;
	}

	public void setActive(boolean flag) {
		if(!active && flag)
			activatedTime = worldObj.getWorldTime();
		if(active != flag) {
			active = flag;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}

	@Override
	public short getFacing() {
		return facing;
	}

	public void setWrenchRate(float flag) {
		wrenchRate = flag;
	}

	@Override
	public boolean wrenchCanRemove(EntityPlayer entityPlayer) {
		if (getWrenchDropRate() <= 0.0F) {
			return false;
		}
		return true;
	}

	@Override
	public float getWrenchDropRate() {
		return wrenchRate;
	}

	public boolean isInit() {
		return init;
	}

	public void setInit(boolean init) {
		this.init = init;
	}
	
	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return true;
	}

}
