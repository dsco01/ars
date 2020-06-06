package mods.immibis.ars;

import mods.immibis.ars.packet.PacketGenericUpdate;
import mods.immibis.core.api.net.IPacket;
import mods.immibis.core.api.util.BaseContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ContainerMFFS extends BaseContainer<TileEntityMFFS> {

	private TileEntityMFFS entity;
	private int updateCount = -1, baseUpdateCount = -1;

	public ContainerMFFS(EntityPlayer player, TileEntityMFFS tileentity) {
		super(player, tileentity);
		entity = tileentity;
	}
	
	@Override
	public void onButtonPressed(int id) {
		entity.handleButton(id);
	}

	@Override
	public ItemStack transferStackInSlot(int i) {
		return null;
	}
	
	@Override
	public void onUpdatePacket(IPacket p) {
		if(p instanceof PacketGenericUpdate) {
			PacketGenericUpdate pgu = (PacketGenericUpdate)p;
			try {
				if(pgu.data.length > 0)
					entity.handleUpdate(pgu.data);
			} catch(ArrayIndexOutOfBoundsException e) {
			}
			try {
				if(pgu.baseData.length > 0)
					entity.handleBaseUpdate(pgu.baseData);
			} catch(ArrayIndexOutOfBoundsException e) {
			}
		}
	}
	
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if(!entity.getWorldObj().isRemote)
		if(entity.updateCount != updateCount || entity.baseUpdateCount != baseUpdateCount) {
			PacketGenericUpdate p = new PacketGenericUpdate();
			if(entity.updateCount != updateCount)
				p.data = entity.getUpdate();
			if(entity.baseUpdateCount != baseUpdateCount)
				p.baseData = entity.getBaseUpdate();
			sendUpdatePacket(p);
			updateCount = entity.updateCount;
			baseUpdateCount = entity.baseUpdateCount;
		}
	}

}