package mods.immibis.ars;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import mods.immibis.ars.projectors.FFShape;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class FFWorld {
	private static void removeOldWorlds() {
		Set<World> curWorlds = new HashSet<World>();
		for(World w : DimensionManager.getWorlds())
			curWorlds.add(w);
		
		for(World w : new ArrayList<World>(worlds.keySet()))
			if(!curWorlds.contains(w))
				worlds.remove(w);
	}
	private static Map<World, FFWorld> worlds = new HashMap<World, FFWorld>();
	
	public static FFWorld get(World w) {
		if(w == null)
			return null;
		FFWorld ffw = worlds.get(w);
		if(ffw == null)
			worlds.put(w, ffw = new FFWorld(w));
		return ffw;
	}
	
	private World w;
	private FFWorld(World w) {
		this.w = w;
	}
	
	private Map<String, FFBlock> blocks = new HashMap<String, FFBlock>();
	private StringBuilder hasher = new StringBuilder();
	private CoordinateList refreshQueue = null;
	
	// maps (hash of projector coordinates) -> (projector shape)
	private Map<String, FFShape> shapes = new HashMap<String, FFShape>();
	
	private String hash(int x, int y, int z) {
		hasher.setLength(0);
		hasher.append(x).append('/').append(y).append('/').append(z);
		return hasher.toString();
	}
	
	public FFBlock addOrGet(int x, int y, int z) {
		String hash = hash(x, y, z);
		
		FFBlock b = blocks.get(hash);
		if(b == null)
			blocks.put(hash, b = new FFBlock(x, y, z, w));
		
		return b;
	}
	
	public FFBlock get(int x, int y, int z) {
		return blocks.get(hash(x, y, z));
	}

	public void remove(int x, int y, int z) {
		blocks.remove(hash(x, y, z));
	}

	public void queueRefresh(int i, int j, int k) {
		if(refreshQueue == null)
			refreshQueue = new CoordinateList(8);
		refreshQueue.add(i, j, k, 0);
	}
	
	public void tick() {
		if(refreshQueue != null) {
			CoordinateList.CoordIterator it = refreshQueue.iterate();
			while(it.hasNext()) {
				it.next();
				FFBlock ffb = get(it.x, it.y, it.z);
				if(ffb != null) {
					ffb.refresh();
					ffb.refreshCamo();
				}
			}
			
			if(refreshQueue.getAllocatedSize() > 100)
				refreshQueue = null;
			else
				refreshQueue.clear();
		}
	}
	
	public Collection<FFShape> getShapesOverlapping(int x, int y, int z) {
		return shapes.values();
	}
	
	public static void tickAll() {
		removeOldWorlds();
		for(FFWorld w : worlds.values())
			w.tick();
	}

	public void removeShape(FFShape ffShape) {
		String hash = hash(ffShape.projX, ffShape.projY, ffShape.projZ);
		//if(shapes.get(hash) != ffShape)
			//throw new RuntimeException("shape not found at "+hash);
		shapes.remove(hash);
		//System.out.println("removed shape at "+hash);
	}
	
	public void addShape(FFShape ffShape) {
		String hash = hash(ffShape.projX, ffShape.projY, ffShape.projZ);
		shapes.put(hash, ffShape);
		//System.out.println("set shape at "+hash);
	}
}
