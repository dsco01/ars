package mods.immibis.ars;


import java.util.Hashtable;
import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;

import mods.immibis.ars.projectors.TileProjector;
import net.minecraft.world.World;

public final class Linkgrid {

	private static Map<World, Worldlinknet> WorldpowernetMap = new WeakHashMap<World, Worldlinknet>();

	public static class Worldlinknet {

		private Map<Integer, TileProjector> Projektor = new Hashtable<Integer, TileProjector>();
		private Map<Integer, TileEntityGeneratorCore> Generator = new Hashtable<Integer, TileEntityGeneratorCore>();

		public Map<Integer, String> RMonitorCount = new Hashtable<Integer, String>();

		public Map<Integer, TileProjector> getProjektor() {
			return Projektor;
		}

		public Map<Integer, TileEntityGeneratorCore> getGenerator() {
			return Generator;
		}

		public int newGenerator_ID(TileEntityGeneratorCore tileEntityGeneratorCore) {

			Random random = new Random();
			int tempGenerator_ID = random.nextInt();

			while (Generator.get(tempGenerator_ID) != null) {
				tempGenerator_ID = random.nextInt();
			}
			Generator.put(tempGenerator_ID, tileEntityGeneratorCore);
			return tempGenerator_ID;
		}

		public static int myRandom(int low, int high) {
			return (int) (Math.random() * (high - low) + low);
		}

		public int conProjektors(int Generator_ID, int xCoordr, int yCoordr, int zCoordr, short Transmitrange) {
			int counter = 0;
			for (TileProjector tileentity : Projektor.values()) {
				if (tileentity.getLinkGenerator_ID() == Generator_ID) {
					int dx = tileentity.xCoord - xCoordr;
					int dy = tileentity.yCoord - yCoordr;
					int dz = tileentity.zCoord - zCoordr;

					if (Transmitrange >= Math.sqrt(dx * dx + dy * dy + dz * dz)) {
						counter++;
					}

				}
			}
			return counter;
		}

	}

	public static Worldlinknet getWorldMap(World world) {
		if (world != null) {

			if (!WorldpowernetMap.containsKey(world)) {
				WorldpowernetMap.put(world, new Worldlinknet());
			}
			return (Worldlinknet) WorldpowernetMap.get(world);
		}

		return null;
	}

}