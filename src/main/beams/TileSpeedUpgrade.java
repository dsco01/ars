package mods.immibis.ars.beams;

public class TileSpeedUpgrade extends TileBeamEmitter {
	@Override public boolean canUpdate() {return false;}
	
	private static UpgradeData upgrade;
	static {
		upgrade = new UpgradeData();
		upgrade.speed = 1.1;
	}
	
	@Override
	public Object getOutput() {
		return upgrade;
	}
	
	@Override
	public int getBeamColour() {return 1;}
}
