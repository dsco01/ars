package mods.immibis.ars.beams;

public class TileUpgradeCombiner extends TileBeamEmitter {
	@Override public boolean canUpdate() {return false;}

	@Override
	public Object getOutput() {
		UpgradeData rv = new UpgradeData();
		for(int k = 0; k < 6; k++) {
			Object o = getInput(k);
			if(o instanceof UpgradeData) {
				UpgradeData u = (UpgradeData)o;
				rv.combine(u);
			}
		}
		return rv;
	}
	
	@Override
	public int getBeamColour() {return 1;}
}
