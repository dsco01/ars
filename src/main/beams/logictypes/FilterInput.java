package mods.immibis.ars.beams.logictypes;

import java.util.Set;

import mods.immibis.ars.beams.EntityFilter;
import mods.immibis.ars.beams.LogicType;
import mods.immibis.ars.beams.TileProgrammableFilter;
import net.minecraft.entity.Entity;

public class FilterInput extends LogicType {

	private final int side;
	
	public FilterInput(int side) {
		this.side = side;
	}

	@Override
	public EntityFilter createFilter(final TileProgrammableFilter container, EntityFilter[] in) {
		return new EntityFilter() {
			@Override
			public Set<Entity> filter(Set<Entity> in) {
				Object input = container.getInput(side);
				if(input instanceof EntityFilter) {
					return ((EntityFilter)input).filter(in);
				} else {
					in.clear();
					return in;
				}
			}
		};
	}

}
