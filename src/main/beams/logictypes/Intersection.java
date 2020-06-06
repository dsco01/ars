package mods.immibis.ars.beams.logictypes;


import java.util.Set;

import mods.immibis.ars.beams.EntityFilter;
import mods.immibis.ars.beams.LogicType;
import mods.immibis.ars.beams.TileProgrammableFilter;
import net.minecraft.entity.Entity;

public class Intersection extends LogicType {
	@Override
	public EntityFilter createFilter(TileProgrammableFilter container, final EntityFilter[] subfilters) {
		return new EntityFilter() {
			@Override
			public Set<Entity> filter(Set<Entity> in) {
				for(EntityFilter ef : subfilters)
					if(ef != null)
						in = ef.filter(in);
				return in;
			}
		};
	}

}
