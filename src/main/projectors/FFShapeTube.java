package mods.immibis.ars.projectors;

import static mods.immibis.ars.projectors.TileProjector.*;
import mods.immibis.ars.CoordinateList;
import mods.immibis.core.api.util.Dir;

public class FFShapeTube extends FFShape {

	public FFShapeTube(TileProjector tile, short facing, int length, int radius, short mode_designe) {
		super(tile);
		
		this.facing = facing;
		//this.length = length;
		//this.radius = radius;
		// this.mode_designe = mode_designe;
		
		int cx = radius, cy = radius, cz = radius;
		switch(facing) {
		case Dir.NX: case Dir.PX: cx = length; break;
		case Dir.NY: case Dir.PY: cy = length; break;
		case Dir.NZ: case Dir.PZ: cz = length; break;
		default: throw new RuntimeException("facing invalid direction "+facing);
		}
		
		minx = centX - cx;
		miny = centY - cy;
		minz = centZ - cz;
		maxx = centX + cx;
		maxy = centY + cy;
		maxz = centZ + cz;
		
		if(mode_designe == 2) {
			switch(facing) {
			case Dir.NX: maxx = centX; break;
			case Dir.NY: maxy = centY; break;
			case Dir.NZ: maxz = centZ; break;
			case Dir.PX: minx = centX; break;
			case Dir.PY: miny = centY; break;
			case Dir.PZ: minz = centZ; break;
			}
		}
		
		if(dome)
			miny = centY;
	}
	
	//private final int length, radius;
	private final short facing;//, mode_designe;
	
	private int minx, miny, minz, maxx, maxy, maxz;

	@Override
	public void getFieldBlocks(CoordinateList list) {
		for(int x = minx; x <= maxx; x++)
			for(int y = miny; y <= maxy; y++)
				for(int z = minz; z <= maxz; z++) {
					int mode = getBlockMode(x, y, z);
					if(mode == MODE_FIELD || mode == MODE_GAP)
						list.add(x, y, z, mode);
				}
	}

	@Override
	public int getBlockMode(int x, int y, int z) {
		if(x < minx || x > maxx || y < miny || y > maxy || z < minz || z > maxz)
			return 0;
		
		if((x == minx || x == maxx) && facing != Dir.NX && facing != Dir.PX)
			return MODE_FIELD;
		if(((y == miny && !dome) || y == maxy) && facing != Dir.NY && facing != Dir.PY)
			return MODE_FIELD;
		if((z == minz || z == maxz) && facing != Dir.NZ && facing != Dir.PZ)
			return MODE_FIELD;
		return MODE_GAP;
	}

}
