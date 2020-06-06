package mods.immibis.ars.beams;

import java.util.ArrayList;
import java.util.Collection;

public class UpgradeData {
	public int range = 0;
	public TileEMPUpgrade emp = null;
	public double speed = 1;
	public Collection<TileLootCollector> lootCollectors = null;
	public Collection<TilePotionUpgrade> potions = null;
	public boolean suppressor = false;

	public void combine(UpgradeData u) {
		range += u.range;
		speed *= u.speed;
		suppressor |= u.suppressor;
		
		if(u.lootCollectors != null)
			if(lootCollectors == null)
				lootCollectors = new ArrayList<TileLootCollector>(u.lootCollectors);
			else
				lootCollectors.addAll(u.lootCollectors);
		
		if(u.potions != null)
			if(potions == null)
				potions = new ArrayList<TilePotionUpgrade>(u.potions);
			else
				potions.addAll(u.potions);
		
		if(emp == null)
			emp = u.emp;
		else if(u.emp != null && u.emp.getStoredEnergy() > emp.getStoredEnergy())
			// pick whichever EMP source has the most stored energy
			emp = u.emp;
	}
}
