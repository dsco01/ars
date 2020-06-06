package mods.immibis.ars.beams;


import java.util.HashSet;
import java.util.Set;

import mods.immibis.ars.projectors.FFShape;
import mods.immibis.ars.projectors.TileProjector;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;

public class TileFieldFilter extends TileBeamEmitter {
	private int ticksToFieldCheck = 0;
	
	private TileProjector projector;
	
	private EntityFilter filter = new EntityFilter() {
		@Override
		public Set<Entity> filter(Set<Entity> in) {
			if(projector == null) {
				in.clear();
				return in;
			}
			
			FFShape shape = projector.getShape();
			if(shape == null) {
				in.clear();
				return in;
			}
			
			Set<Entity> rv = new HashSet<Entity>();
			for(Entity e : in)
				if(shape.getBlockMode((int)Math.floor(e.posX), (int)Math.floor(e.posY), (int)Math.floor(e.posZ)) != 0)
					rv.add(e);
			
			return rv;
		}
	};
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		
		if(!worldObj.isRemote) {
			if(ticksToFieldCheck <= 0) {
				checkForField();
				ticksToFieldCheck = 20;
			}
			ticksToFieldCheck--;
		}
	}
	
	@Override
	public Object getOutput() {
		return filter;
	}
	
	private void checkForField() {
		projector = null;
		for(int dx = -1; dx <= 1; dx++)
		for(int dy = -1; dy <= 1; dy++)
		for(int dz = -1; dz <= 1; dz++) {
			TileEntity te = worldObj.getTileEntity(xCoord+dx, yCoord+dy, zCoord+dz);
			if(te instanceof TileProjector) {
				projector = (TileProjector)te;
				return;
			}
		}
	}
	
	@Override
	public int getBeamColour() {return 0;}
}
