package mods.immibis.ars.packet;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mods.immibis.ars.ARSMod;
import mods.immibis.core.net.AbstractContainerSyncPacket;
import net.minecraft.entity.player.EntityPlayer;

public class PacketGenericUpdate extends AbstractContainerSyncPacket {
	public int[] data;
	public int[] baseData;
	
	@Override
	public void onReceived(EntityPlayer player) {
		if(player != null)
			return; // received on server
		super.onReceived(player);
	}
	
	@Override
	public void write(DataOutputStream out) throws IOException {
		if(data == null)
			out.writeInt(0);
		else {
			out.writeInt(data.length);
			for(int k = 0; k < data.length; k++)
				out.writeInt(data[k]);
		}
		if(baseData == null)
			out.writeInt(0);
		else {
			out.writeInt(baseData.length);
			for(int k = 0; k < baseData.length; k++)
				out.writeInt(baseData[k]);
		}
	}
	
	@Override
	public void read(DataInputStream in) throws IOException {
		data = new int[in.readInt()];
		for(int k = 0; k < data.length; k++)
			data[k] = in.readInt();
		baseData = new int[in.readInt()];
		for(int k = 0; k < baseData.length; k++)
			baseData[k] = in.readInt();
	}
	
	@Override
	public byte getID() {
		return ARSMod.PKT_GENERIC_UPDATE;
	}

	@Override
	public String getChannel() {
		return ARSMod.CHANNEL;
	}
}
