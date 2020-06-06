package mods.immibis.ars.projectors;

import static mods.immibis.ars.projectors.TileProjector.*;
import mods.immibis.ars.CoordinateList;

public class FFShapeSphere extends FFShape {

	public FFShapeSphere(TileProjector projector, int radius) {
		super(projector);
		this.radius = radius;
		
		minx = centX - radius;
		maxx = centX + radius;
		miny = Math.max(0, dome ? centY : centY - radius);
		maxy = Math.min(255, centY + radius);
		minz = centZ - radius;
		maxz = centZ + radius;
		
		radiussq = radius*radius;
		radiusm1sq = (radius-1)*(radius-1);
	}
	
	public int radius, radiussq, radiusm1sq;
	public int minx, maxx, miny, maxy, minz, maxz;

	@Override
	public void getFieldBlocks(CoordinateList shape) {
		
		for(int x = minx; x <= maxx; x++) {
			int dx = x - centX;
			for(int z = minz; z <= maxz; z++) {
				int dz = z - centZ;
				if(dx * dx + dz * dz > radiussq)
					continue;
				int min = (int)Math.sqrt(radiussq - dx * dx - dz * dz) - radius/2;
				int max = min + radius;
				for(int k = min; k < max; k++) {
					if(!dome && k > 0)
						if(getBlockMode(x, centY - k, z) == MODE_FIELD)
							shape.add(x, centY - k, z, MODE_FIELD);
					if(k >= 0)
						if(getBlockMode(x, centY + k, z) == MODE_FIELD)
							shape.add(x, centY + k, z, MODE_FIELD);
				}
			}
		}
	}

	@Override
	public int getBlockMode(int x, int y, int z) {
		x -= centX; y -= centY; z -= centZ;
		
		if(y < 0 && dome)
			return 0;
		
		if(x < -radius || x > radius || y < -radius || y > radius || z < -radius || z > radius)
			return 0;
		
		int distsq = x*x + y*y + z*z;
		//System.out.println((x+projX)+","+(y+projY)+","+(z+projZ)+" "+distsq);
		if(distsq <= radiusm1sq)
			return inhibitor ? MODE_INHIBITOR : MODE_INSIDE;
		if(distsq <= radiussq)
			return MODE_FIELD;
			
		return 0;
	}

}
