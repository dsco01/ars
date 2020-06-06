package mods.immibis.ars.projectors;

import static mods.immibis.ars.projectors.TileProjector.*;
import mods.immibis.ars.CoordinateList;
import mods.immibis.core.api.util.Dir;

public class FFShapeDirectional extends FFShape {

	public FFShapeDirectional(TileProjector tile, short facing, int distance, int length) {
		super(tile);
		this.facing = facing;
		this.distance = distance;
		this.length = length;
	}
	
	private final short facing;
	private final int distance, length;
	
	@Override
	public void getFieldBlocks(CoordinateList list) {
		
		for(int k = 0; k <= length; k++) {
			switch(facing) {
			case Dir.NY: list.add(centX, centY - distance - k, centZ, MODE_FIELD); break;
			case Dir.PY: list.add(centX, centY + distance + k, centZ, MODE_FIELD); break;
			case Dir.NZ: list.add(centX, centY, centZ - distance - k, MODE_FIELD); break;
			case Dir.PZ: list.add(centX, centY, centZ + distance + k, MODE_FIELD); break;
			case Dir.NX: list.add(centX - distance - k, centY, centZ, MODE_FIELD); break;
			case Dir.PX: list.add(centX + distance + k, centY, centZ, MODE_FIELD); break;
			}
		}
	}
	
	@Override
	public int getBlockMode(int x, int y, int z) {
		int dist;
		switch(facing) {
		case Dir.NX:
			if(y != centY || z != centZ) return 0;
			dist = centX - x;
			break;
		case Dir.PX:
			if(y != centY || z != centZ) return 0;
			dist = x - centX;
			break;
		case Dir.NY:
			if(x != centX || z != centZ) return 0;
			dist = centY - y;
			break;
		case Dir.PY:
			if(x != centX || z != centZ) return 0;
			dist = y - centY;
			break;
		case Dir.NZ:
			if(y != centY || x != centX) return 0;
			dist = centZ - z;
			break;
		case Dir.PZ:
			if(y != centY || x != centX) return 0;
			dist = z - centZ;
			break;
		default:
			return 0;
		}
		
		if(dist < distance || dist > distance + length)
			return 0;
		
		return MODE_FIELD;
	}

}
