package mods.immibis.ars.projectors;


import java.util.HashSet;
import java.util.Set;

import mods.immibis.ars.CoordinateList;
import mods.immibis.core.api.util.Dir;

public class FFShapeExtender extends FFShape {
	
	private final short facing, ausrichtungx, ausrichtungy, ausrichtungz;
	private final int wide, length, distance;

	public FFShapeExtender(TileProjector tile,
			short facing, int wide, int length, int distance,
			short ausrichtungx, short ausrichtungy, short ausrichtungz) {
		super(tile);
		
		this.facing = facing;
		this.ausrichtungx = ausrichtungx;
		this.ausrichtungy = ausrichtungy;
		this.ausrichtungz = ausrichtungz;
		this.wide = wide;
		this.length = length;
		this.distance = distance;
	}
	
	private Set<String> blocks = new HashSet<String>();

	@Override
	public void getFieldBlocks(CoordinateList list) {
		for (int x1 = 0; x1 <= wide; x1++) {
			int tempx = ausrichtungx * x1;
			int tempy = ausrichtungy * x1;
			int tempz = ausrichtungz * x1;

			for (int y1 = 1; y1 <= length; y1++) {
				
				switch(facing) {
				case Dir.NY: tempy = -y1 - distance; break;
				case Dir.PY: tempy = y1 + distance; break;
				case Dir.NZ: tempz = -y1 - distance; break;
				case Dir.PZ: tempz = y1 + distance; break;
				case Dir.NX: tempx = -y1 - distance; break;
				case Dir.PX: tempx = y1 + distance; break;
				}
				
				list.add(centX + tempx, centY + tempy, centZ + tempz, TileProjector.MODE_FIELD);
				blocks.add(tempx+"/"+tempy+"/"+tempz);
			}
		}
	}

	@Override
	public int getBlockMode(int x, int y, int z) {
		x -= centX; y -= centY; z -= centZ;

		if(blocks.contains(x+"/"+y+"/"+z))
			return TileProjector.MODE_FIELD;
		else
			return 0;
	}

}
