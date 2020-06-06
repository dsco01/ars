package mods.immibis.ars.beams.logictypes;


import java.util.HashSet;
import java.util.Set;

import mods.immibis.ars.beams.EntityFilter;
import mods.immibis.ars.beams.LogicType;
import mods.immibis.ars.beams.TileProgrammableFilter;
import net.minecraft.entity.Entity;

public class ClassDetector extends LogicType {
	
	public ClassDetector(Class<?> clazz) {
		this.clazz = clazz;
	}
	
	public Class<?> clazz;

	@Override
	public EntityFilter createFilter(TileProgrammableFilter container, EntityFilter[] in) {
		return new EntityFilter() {
			@Override
			public Set<Entity> filter(Set<Entity> in) {
				Set<Entity> rv = new HashSet<Entity>();
				for(Entity e : in)
					if(clazz.isAssignableFrom(e.getClass()))
						rv.add(e);
				return rv;
			}
		};
	}
}
