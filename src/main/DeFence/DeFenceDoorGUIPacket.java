package mods.immibis.ars.DeFence;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mods.immibis.ars.ARSMod;
import mods.immibis.core.api.net.IPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class DeFenceDoorGUIPacket implements IPacket {
	
	public DeFenceDoorGUIPacket() {}
	public DeFenceDoorGUIPacket(int id, String arg) {this.id = id; this.arg = arg;}
	
	public int id;
	public String arg;
	
	@Override
	public String getChannel() {
		return ARSMod.CHANNEL;
	}
	
	@Override
	public byte getID() {
		return ARSMod.PKT_DEFENCE_CODE_DOOR_GUI;
	}
	
	@Override
	public void onReceived(EntityPlayer source) {
		Container c = source.openContainer;
		if(c instanceof ContainerCodeDoor)
			((ContainerCodeDoor)c).tileEntity.onDoorGuiAction(id, arg, source);
	}
	
	@Override
	public void read(DataInputStream in) throws IOException {
		id = in.readInt();
		arg = in.readUTF();
	}
	
	@Override
	public void write(DataOutputStream out) throws IOException {
		out.writeInt(id);
		out.writeUTF(arg);
	}
}
