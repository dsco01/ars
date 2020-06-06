package mods.immibis.ars.beams;

import mods.immibis.core.TileCombined;
import mods.immibis.core.api.util.Dir;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileBeamEmitter extends TileCombined {
	public int outputFace;
	
	@Override
	public Packet getDescriptionPacket() {
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, outputFace, null);
	}
	
	@Override
	public void onDataPacket(S35PacketUpdateTileEntity p) {
		outputFace = p.func_148853_f();
	}
	
	public boolean isOutputFace(int side) {
		return side == outputFace;
	}
	
	public Object getOutput() {
		return null;
	}
	
	public Object getOutput(int side) {
		if(side == outputFace)
			return getOutput();
		else
			return null;
	}
	
	public int getBeamColour() {
		return 0;
	}
	
	private TileBeamEmitter[] beamLinkCache = new TileBeamEmitter[6];
	public Object getInput(int side) {
		if(beamLinkCache[side] != null) {
			return beamLinkCache[side].getOutput(side ^ 1);
		}
		
		int dx = 0, dy = 0, dz = 0;
		switch(side) {
		case Dir.NX: dx = -1; break;
		case Dir.NY: dy = -1; break;
		case Dir.NZ: dz = -1; break;
		case Dir.PX: dx =  1; break;
		case Dir.PY: dy =  1; break;
		case Dir.PZ: dz =  1; break;
		}
		
		int x = xCoord + dx;
		int y = yCoord + dy;
		int z = zCoord + dz;
		
		int length = 0;
		
		while(worldObj.getBlock(x, y, z) == BeamsMain.blockBeam && BlockBeam.directionFromMetadata(worldObj.getBlockMetadata(x, y, z)) == (side ^ 1)) {
			x += dx;
			y += dy;
			z += dz;
			
			if(++length > BlockBeam.MAX_LENGTH)
				return null;
		}
		
		TileEntity te = worldObj.getTileEntity(x, y, z);
		if(te instanceof TileBeamEmitter) {
			TileBeamEmitter tbe = (TileBeamEmitter)te;
			beamLinkCache[side] = tbe;
			return tbe.getOutput(side ^ 1);
		}
		
		return null;
	}
	
	protected void setupBeams() {
		setupBeam(outputFace);
	}
	
	@Override
	public void onPlaced(EntityLivingBase player, int look) {
		outputFace = look;
		setupBeams();
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setByte("outputFace", (byte)outputFace);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		outputFace = tag.getByte("outputFace");
	}
	
	protected void setupBeam(int dir) {
		int dx = (dir == Dir.NX ? -1 : dir == Dir.PX ? 1 : 0);
		int dy = (dir == Dir.NY ? -1 : dir == Dir.PY ? 1 : 0);
		int dz = (dir == Dir.NZ ? -1 : dir == Dir.PZ ? 1 : 0);
		int meta = dir + getBeamColour() * 6;
		int x = xCoord, y = yCoord, z = zCoord;
		for(int k = 0; k < BlockBeam.MAX_LENGTH; k++) {
			x += dx;
			y += dy;
			z += dz;
				
			if(worldObj.isAirBlock(x, y, z))
				worldObj.setBlock(x, y, z, BeamsMain.blockBeam, meta, 3);
			else
				break;
		}
	}
	
	@Override
	public void onBlockNeighbourChange() {
		setupBeams();
		for(int k = 0; k < 6; k++)
			beamLinkCache[k] = null;
	}

	public int getTextureIndex() {
		return 0;
	}
}
