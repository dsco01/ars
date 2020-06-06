package mods.immibis.ars.beams.logictypes;


import java.util.Set;

import mods.immibis.ars.beams.EntityFilter;
import mods.immibis.ars.beams.LogicType;
import mods.immibis.ars.beams.TileProgrammableFilter;
import net.minecraft.entity.Entity;

public class RedstoneInput extends LogicType {

	@Override
	public EntityFilter createFilter(final TileProgrammableFilter container, EntityFilter[] in) {
		return new EntityFilter() {
			@Override
			public Set<Entity> filter(Set<Entity> in) {
				if(!container.getWorldObj().isBlockIndirectlyGettingPowered(container.xCoord, container.yCoord, container.zCoord))
					in.clear();
				return in;
			}
		};
	}

}
