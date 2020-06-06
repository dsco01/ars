package mods.immibis.ars.beams.logictypes;


import java.util.HashSet;
import java.util.Set;

import mods.immibis.ars.beams.EntityFilter;
import mods.immibis.ars.beams.LogicType;
import mods.immibis.ars.beams.TileProgrammableFilter;
import net.minecraft.entity.Entity;

public class Union extends LogicType {

	@Override
	public EntityFilter createFilter(TileProgrammableFilter container, final EntityFilter[] subfilters) {
		return new EntityFilter() {
			@Override
			public Set<Entity> filter(Set<Entity> in) {
				Set<Entity> ret = null;
				
				boolean foundAny = false;
				for(EntityFilter ef : subfilters) {
					if(ef == null)
						continue;

					if(!foundAny) {
						ret = ef.filter(new HashSet<Entity>(in));
						foundAny = true;
				
					} else {
						ret.addAll(ef.filter(new HashSet<Entity>(in)));
					}
				}
				
				if(!foundAny)
					// If no subfilters, don't affect the input list
					return in;
				
				return ret;
			}
		};
	}

}
