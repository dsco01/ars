package mods.immibis.ars;

import static mods.immibis.ars.projectors.TileProjector.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mods.immibis.ars.projectors.FFShape;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class FFBlock {
	
	public static class Entry {

		public Entry(int ffmeta, int genID, int projID, int mode, boolean blockBreaker, int camoBlock, long time) {
			this.ffmeta = (byte)ffmeta;
			this.genID = genID;
			this.projID = projID;
			this.mode = (byte)mode;
			this.blockBreaker = blockBreaker;
			this.camoBlock = camoBlock;
			this.time = time;
		}
		
		public int genID;
		public int projID;
		public byte mode;
		public byte ffmeta;
		public boolean blockBreaker;
		public int camoBlock;
		public long time;
	}
	
	public Entry activeEntry;
	public List<Entry> entries = new ArrayList<Entry>(2);
	public final int x, y, z;
	public final World w;

	public FFBlock(int x, int y, int z, World w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public int getFieldType() {
		return activeEntry == null ? 0 : activeEntry.ffmeta;
	}

	public boolean shouldBeActive() {
		return activeEntry != null && activeEntry.mode == MODE_FIELD;
	}
	
	public void usePower(int multiplier) {
		if(activeEntry == null)
			return;
		
		TileEntity genTE = Linkgrid.getWorldMap(w).getGenerator().get(activeEntry.genID);
		if (genTE instanceof TileEntityGeneratorCore) {
			TileEntityGeneratorCore gen = (TileEntityGeneratorCore)genTE;
			
			int cost = ARSMod.forcefieldblockcost * multiplier;
			if(BlockForceField.isZapper(getFieldType()))
				cost *= ARSMod.forcefieldblockzappermodifier;
			
			gen.Energylost(cost);
		}
	}

	// returns true if anything changed
	public boolean refresh() {
		boolean didStuff = false;
		
		//System.out.println("FF refresh, shouldBeActive "+shouldBeActive());
		
		if(!shouldBeActive()) {
			if(w.getBlock(x, y, z) == ARSMod.MFFSFieldblock) {
				w.setBlockToAir(x, y, z);
				didStuff = true;
			}
		}
		else
		{
			Block oldBlock = w.getBlock(x, y, z);
			if(oldBlock != Blocks.bedrock && (oldBlock != ARSMod.MFFSFieldblock || w.getBlockMetadata(x, y, z) != getFieldType())) {
				if(oldBlock.isAir(w, x, y, z) || oldBlock == ARSMod.MFFSFieldblock || activeEntry.blockBreaker || !oldBlock.getMaterial().isSolid())
				{
					int oldMeta = w.getBlockMetadata(x, y, z);
					if(!oldBlock.isAir(w, x, y, z) && oldBlock != ARSMod.MFFSFieldblock)
					{
						if(w.getTileEntity(x, y, z) != null)
							return false;

						Block b = oldBlock;
						w.setBlockToAir(x, y, z);
						
						ArrayList<ItemStack> drops = b.getDrops(w, x, y, z, oldMeta, 0);
						
						//System.out.println("Dropping "+drops);
						
						for(ItemStack is : drops)
							w.spawnEntityInWorld(new EntityItem(w, x + 0.5, y + 0.5, z + 0.5, is));
					}
					if(oldBlock != ARSMod.MFFSFieldblock || oldMeta != getFieldType()) {
						w.setBlockToAir(x, y, z);
						w.setBlock(x, y, z, ARSMod.MFFSFieldblock, getFieldType(), 2);
					}

					refreshCamo();
				
					usePower(ARSMod.forcefieldblockcreatemodifier);
					
					didStuff = true;
				}
			}
		}
		
		if(activeEntry == null) {
			refreshActiveEntry();
			if(activeEntry == null) {
				FFWorld.get(w).remove(x, y, z);
			}
			didStuff = true;
		}
		
		return didStuff;
	}
	
	public void removeEntry(int projID) {
		Iterator<Entry> it = entries.iterator();
		while(it.hasNext())
			if(it.next().projID == projID) {
				it.remove();
				refreshActiveEntry();
				return;
			}
		//System.out.println("removeEntry("+projID+") failed");
	}

	public void addEntry(Entry entry) {
		
		// Remove any existing entry from this projector
		Iterator<Entry> it = entries.iterator();
		while(it.hasNext())
			if(it.next().projID == entry.projID) {
				it.remove();
				break;
			}
		
		// Insert entry into entries array, sorted by time.
		for(int k = 0; k < entries.size(); k++) {
			if(entries.get(k).time > entry.time) {
				entries.add(k, entry);
				refreshActiveEntry();
				return;
			}
		}
		entries.add(entry);
		refreshActiveEntry();
	}
	
	private void refreshActiveEntry() {
		
		List<Entry> entries2 = new ArrayList<Entry>(entries);
		
		for(FFShape s : FFWorld.get(w).getShapesOverlapping(x, y, z)) {
			int mode = s.getBlockMode(x, y, z);
			if(mode != 0) {
				//System.out.println("shape entry "+x+","+y+","+z+" "+mode+" "+s.projID);
				entries2.add(new Entry(s.ffmeta, s.genID, s.projID, mode, s.breaker, s.camoBlock, s.activeTime));
			}
		}
		
		
		// Most common case: one or zero entries
		if(entries2.size() < 2) {
			Entry e = (entries2.size() == 0 ? null : entries2.get(0));
			
			if(activeEntry != e) {
				activeEntry = e;
				refresh();
				refreshCamo();
			}
			return;
		}
		
		/*
		 * A gap entry on any generator cancels ALL field entries on that generator.
		 * Forcefield is off if there are no non-cancelled field entries.
		 * If multiple fields from the same generator, first created is used.
		 * If multiple fields from different generators, first created is used.
		 * 
		 * Inhibitors override fields created after them, except from the same generator.
		 * If multiple inhibitors, only the most recent is used.
		 * 
		 * An inhibitor entry on any generator cancels ALL field entries on OTHER generators created later.
		 */
		
		long inhibitTime = Long.MIN_VALUE;
		Entry inhibitor = null;
		
		Map<Integer, Entry> genMap = new HashMap<Integer, Entry>();
		for(Entry e : entries2) {
			if(e.mode == MODE_INHIBITOR) {
				if(e.time > inhibitTime) {
					inhibitor = e;
					inhibitTime = e.time;
				}
				continue;
			}
			Entry oe = genMap.get(e.genID);
			if(oe == null)
				genMap.put(e.genID, e);
			else if(oe.mode == MODE_FIELD && e.mode == MODE_GAP)
				genMap.put(e.genID, e);
		}
		
		Entry oldAE = activeEntry;
		
		activeEntry = null;
		for(Entry e : genMap.values()) {
			if(inhibitor != null && inhibitor.genID != e.genID && inhibitTime < e.time)
				continue;
			if(activeEntry == null) {
				activeEntry = e;
				continue;
			}
			//if(e.mode == MODE_GAP && activeEntry == null)
			//	activeEntry = e;
			if(e.mode == MODE_FIELD && activeEntry.mode == MODE_GAP)
				// From different generators, fields override gaps
				activeEntry = e;
		}
		
		/*if(inhibitor != null) {
			if(activeEntry != null) {
				System.out.println(x+","+y+","+z+" inh "+inhibitTime+"/"+inhibitor.genID);
				System.out.println("act "+activeEntry.time+"/"+activeEntry.genID);
				System.out.println();
			}
		}*/
		
		if(oldAE != activeEntry) {
			refresh();
			refreshCamo();
		}
	}

	public void useEnergyFor(int projektor_ID) {
		if(shouldBeActive() && activeEntry.projID == projektor_ID) {
			usePower(1);
		}
	}

	public void refreshCamo() {
		if(shouldBeActive() && BlockForceField.isCamo(getFieldType()) && w.getBlock(x, y, z) == ARSMod.MFFSFieldblock) {
			TileCamouflagedField tcf = ((TileCamouflagedField)w.getTileEntity(x, y, z));
			if(tcf == null)
				return;
			if(tcf.camoBlockId == activeEntry.camoBlock)
				return;
			tcf.camoBlockId = activeEntry.camoBlock;
			w.markBlockForUpdate(x, y, z);
		}
	}

	public String getDebugInfo() {
		String s = "";
		s += "entries.size: " + entries.size();
		s += "\nactive mode: " + (activeEntry == null ? -1 : activeEntry.mode);
		return s;
	}

}
