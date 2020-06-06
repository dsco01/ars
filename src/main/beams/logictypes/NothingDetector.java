package mods.immibis.ars.beams.logictypes;


import java.util.Set;

import mods.immibis.ars.beams.EntityFilter;
import mods.immibis.ars.beams.LogicType;
import mods.immibis.ars.beams.TileProgrammableFilter;
import net.minecraft.entity.Entity;

public class NothingDetector extends LogicType {

	@Override
	public EntityFilter createFilter(TileProgrammableFilter container, EntityFilter[] in) {
		return new EntityFilter() {
			@Override
			public Set<Entity> filter(Set<Entity> in) {
				in.clear();
				return in;
			}
		};
	}

}
