package mods.immibis.ars.beams;

public class TileRangeUpgrade extends TileBeamEmitter {
	@Override public boolean canUpdate() {return false;}
	
	private static UpgradeData upgrade;
	static {
		upgrade = new UpgradeData();
		upgrade.range = 10;
	}
	
	@Override
	public Object getOutput() {
		return upgrade;
	}
	
	@Override
	public int getBeamColour() {return 1;}
}
