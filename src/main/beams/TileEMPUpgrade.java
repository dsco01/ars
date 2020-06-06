package mods.immibis.ars.beams;

import mods.immibis.core.api.traits.IEnergyConsumerTrait;
import mods.immibis.core.api.traits.IEnergyConsumerTrait.EnergyUnit;
import mods.immibis.core.api.traits.IEnergyConsumerTraitUser;
import mods.immibis.core.api.traits.TraitField;
import mods.immibis.core.api.traits.UsesTraits;
import net.minecraft.nbt.NBTTagCompound;

@UsesTraits
public class TileEMPUpgrade extends TileBeamEmitter implements IEnergyConsumerTraitUser {
	@TraitField
	public IEnergyConsumerTrait energyConsumerTrait;
	
	@Override
	public double EnergyConsumer_getPreferredBufferSize() {
		return 1000000;
	}
	@Override
	public EnergyUnit EnergyConsumer_getPreferredUnit() {
		return EnergyUnit.EU;
	}
	@Override
	public boolean EnergyConsumer_isBufferingPreferred() {
		return true;
	}
	
	//private ICrossModIC2 ic2 = APILocator.getCrossModIC2();
	
	private UpgradeData upg_data;
	public TileEMPUpgrade() {
		upg_data = new UpgradeData();
		upg_data.emp = this;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		energyConsumerTrait.readFromNBT(tag);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		energyConsumerTrait.writeToNBT(tag);
	}
	
	@Override
	public void invalidate() {
		energyConsumerTrait.onInvalidate();
		super.invalidate();
	}
	
	@Override
	public void validate() {
		super.validate();
		energyConsumerTrait.onValidate();
	}
	
	@Override
	public void onChunkUnload() {
		energyConsumerTrait.onChunkUnload();
		super.onChunkUnload();
	}
	
	@Override
	public Object getOutput() {
		return upg_data;
	}
	
	@Override
	public void updateEntity() {
		if(worldObj.isRemote)
			return;
		
		/*if(!added_to_enet) {
			
			added_to_enet = true;
		}*/
	}
	
	@Override
	public int getBeamColour() {return 1;}
	
	public double getStoredEnergy() {
		return energyConsumerTrait.getStoredEnergy();
	}
	public void setStoredEnergy(double target) {
		energyConsumerTrait.setStoredEnergy(target);
	}
}
