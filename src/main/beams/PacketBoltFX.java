package mods.immibis.ars.beams;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mods.immibis.ars.ARSMod;
import mods.immibis.core.api.net.IPacket;
import net.minecraft.entity.player.EntityPlayer;

public class PacketBoltFX implements IPacket {
	
	public double x1, x2, y1, y2, z1, z2;
	
	public PacketBoltFX() {}
	public PacketBoltFX(double x1, double y1, double z1, double x2, double y2, double z2) {
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
		this.z1 = z1;
		this.z2 = z2;
	}

	@Override
	public byte getID() {
		return ARSMod.PKT_BOLT_FX;
	}

	@Override
	public void read(DataInputStream in) throws IOException {
		x1 = in.readDouble();
		y1 = in.readDouble();
		z1 = in.readDouble();
		x2 = in.readDouble();
		y2 = in.readDouble();
		z2 = in.readDouble();
	}

	@Override
	public void write(DataOutputStream out) throws IOException {
		out.writeDouble(x1);
		out.writeDouble(y1);
		out.writeDouble(z1);
		out.writeDouble(x2);
		out.writeDouble(y2);
		out.writeDouble(z2);
	}

	@Override
	public void onReceived(EntityPlayer source) {
		if(source == null)
			ARSMod.proxy.makeBoltFX(this);
	}
	@Override
	public String getChannel() {
		return ARSMod.CHANNEL;
	}

}
