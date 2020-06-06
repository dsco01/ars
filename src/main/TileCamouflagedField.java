package mods.immibis.ars;

import mods.immibis.core.api.porting.PortableTileEntity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;

public class TileCamouflagedField extends PortableTileEntity {
	public int camoBlockId = -1;
	
	@Override public boolean canUpdate() {return false;}
	
	public static int getBlockID(int camoID) {
		return camoID & 4095;
	}
	
	public static int getMetadata(int camoID) {
		return camoID >> 16;
	}
	
	public static int getCamoID(int blockID, int meta) {
		return blockID | (meta << 16);
	}
	
	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setShort("c", (short)camoBlockId);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
	}
	
	@Override
	public void onDataPacket(S35PacketUpdateTileEntity p) {
		camoBlockId = p.func_148857_g().getShort("c");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("camo", camoBlockId);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		camoBlockId = nbt.getInteger("camo");
	}
}
