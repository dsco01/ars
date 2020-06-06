package mods.immibis.ars.beams;

import java.util.Set;

import net.minecraft.entity.Entity;

public abstract class EntityFilter {
	// may modify "in" or return a new set
	public abstract Set<Entity> filter(Set<Entity> in);
}
