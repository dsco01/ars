package mods.immibis.ars.beams;

public class TileSuppressorUpgrade extends TileBeamEmitter {
	@Override public boolean canUpdate() {return false;}
	
	private static UpgradeData upgrade;
	static {
		upgrade = new UpgradeData();
		upgrade.suppressor = true;
	}
	
	@Override
	public Object getOutput() {
		return upgrade;
	}
	
	@Override
	public int getBeamColour() {return 1;}
}
