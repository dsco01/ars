package mods.immibis.ars.projectors;

import mods.immibis.ars.CoordinateList;

public abstract class FFShape {
	// Adds all "important" field blocks to the list.
	// A block is "important" if it needs to do something when the field
	// is activated.
	// Currently only MODE_FIELD and MODE_GAP are important.
	public abstract void getFieldBlocks(CoordinateList list);
	
	// Gets the field mode for any given block, which may or may not be
	// "important".
	public abstract int getBlockMode(int x, int y, int z);
	
	public long activeTime;
	public int projX, projY, projZ; // projector position
	public int centX, centY, centZ; // centre position (projector + offset)
	
	public boolean dome, breaker, subwater, inhibitor;
	public int ffmeta;
	public int genID;
	public int projID;
	public int camoBlock;
	
	public FFShape(TileProjector projector) {
		projX = projector.xCoord;
		projY = projector.yCoord;
		projZ = projector.zCoord;
		centX = projX + projector.offsetX;
		centY = projY + projector.offsetY;
		centZ = projZ + projector.offsetZ;
		activeTime = projector.activatedTime;
		dome = projector.isDome();
		breaker = projector.isBlockBreaker();
		subwater = projector.isSubwater();
		inhibitor = projector.isInhibitor();
		ffmeta = projector.getffmeta();
		projID = projector.getProjektor_ID();
		genID = projector.getLinkGenerator_ID();
		camoBlock = projector.getTextur();
	}
}
