package mods.immibis.ars.projectors;

import mods.immibis.ars.CoordinateList;

public class FFShapeDeflector extends FFShape {

	public FFShapeDeflector(TileProjectorDeflector projector) {
		super(projector);
		lengthx = projector.getlengthx();
		lengthz = projector.getlengthz();
		distance = projector.getDistance();
		facing = projector.getFacing();
	}
	
	public int lengthx, lengthz, distance, facing;

	@Override
	public void getFieldBlocks(CoordinateList list) {
		int tempx = 0;
		int tempy = 0;
		int tempz = 0;

		for (int x1 = -lengthx; x1 <= lengthx; x1++) {
			for (int z1 = -lengthz; z1 <= lengthz; z1++) {

				if (facing == 0) {
					tempy = 0 - distance - 1;
					tempx = x1;
					tempz = z1;
				}

				if (facing == 1) {
					tempy = 0 + distance + 1;
					tempx = x1;
					tempz = z1;
				}

				if (facing == 2) {
					tempz = 0 - distance - 1;
					tempy = x1;
					tempx = z1;
				}

				if (facing == 3) {
					tempz = 0 + distance + 1;
					tempy = x1;
					tempx = z1;
				}

				if (facing == 4) {
					tempx = 0 - distance - 1;
					tempy = x1;
					tempz = z1;
				}
				if (facing == 5) {
					tempx = 0 + distance + 1;
					tempy = x1;
					tempz = z1;
				}
				
				list.add(centX + tempx, centY + tempy, centZ + tempz, TileProjector.MODE_FIELD);
			}
		}
	}

	@Override
	public int getBlockMode(int x, int y, int z) {
		x -= centX; y -= centY; z -= centZ;
		switch(facing) {
		case 0:
			if(y != -distance - 1) return 0;
			break;
		case 1:
			if(y != distance + 1) return 0;
			break;
		case 2:
			if(z != -distance - 1) return 0;
			z = x;
			x = y;
			break;
		case 3:
			if(z != distance + 1) return 0;
			z = x;
			x = y;
			break;
		case 4:
			if(x != -distance - 1) return 0;
			x = y;
			break;
		case 5:
			if(x != distance + 1) return 0;
			x = y;
			break;
		}
		if(x >= -lengthx && x <= lengthx && z >= -lengthz && z <= lengthz)
			return TileProjector.MODE_FIELD;
		return 0;
	}

}
