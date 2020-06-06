package mods.immibis.ars.beams;

import net.minecraft.block.Block;
import mods.immibis.core.ItemCombined;

public class ItemBeamStuff extends ItemCombined {

	public ItemBeamStuff(Block id) {
		super(id, "advrepsys", new String[] {
			"tesla.filter.base",
			"tesla",
			"tesla.upgrade.multiplexer",
			"tesla.upgrade.range",
			"tesla.upgrade.emp",
			"tesla.upgrade.speed",
			"tesla.upgrade.lootcollector",
			"tesla.filter.field",
			"tesla.filter.item",
			"tesla.upgrade.invisible",
			"tesla.upgrade.potion"
		});
	}

}
