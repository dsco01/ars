package mods.immibis.ars.projectors;

import static mods.immibis.ars.projectors.TileProjector.*;
import mods.immibis.ars.CoordinateList;

public class FFShapeCube extends FFShape {

	public FFShapeCube(TileProjector projector, int radius) {
		super(projector);
		this.radius = radius;
		
		minx = centX - radius;
		maxx = centX + radius;
		miny = Math.max(0, dome ? centY : centY - radius);
		maxy = Math.min(255, centY + radius);
		minz = centZ - radius;
		maxz = centZ + radius;
	}
	
	public int radius;
	public int minx, maxx, miny, maxy, minz, maxz;

	@Override
	public void getFieldBlocks(CoordinateList shape) {
		
		//System.out.println(minx+","+miny+","+minz+" to "+maxx+","+maxy+","+maxz);

		for(int x = minx; x <= maxx; x++)
			for(int y = miny; y <= maxy; y++) {
				shape.add(x, y, minz, MODE_FIELD);
				shape.add(x, y, maxz, MODE_FIELD);
			}
		
		// careful not to double-count edges or triple-count corners
		for(int x = minx; x <= maxx; x++)
			for(int z = minz + 1; z <= maxz - 1; z++) {
				if(!dome)
					shape.add(x, miny, z, MODE_FIELD);
				shape.add(x, maxy, z, MODE_FIELD);
			}
		
		for(int y = dome ? miny : miny + 1; y <= maxy - 1; y++)
			for(int z = minz + 1; z <= maxz - 1; z++) {
				shape.add(minx, y, z, MODE_FIELD);
				shape.add(maxx, y, z, MODE_FIELD);
			}
	}

	@Override
	public int getBlockMode(int x, int y, int z) {
		if((x == minx || x == maxx) && y >= miny && y <= maxy && z >= minz && z <= maxz)
			return MODE_FIELD;
		if((z == minz || z == maxz) && y >= miny && y <= maxy && x >= minx && x <= maxx)
			return MODE_FIELD;
		if((y == maxy || (y == miny && !dome)) && z >= minz && z <= maxz && x >= minx && x <= maxx)
			return MODE_FIELD;
		if(x >= minx && x <= maxx && y >= miny && y <= maxy && z >= minz && z <= maxz)
			return inhibitor ? MODE_INHIBITOR : MODE_INSIDE;
		return 0;
	}
}
