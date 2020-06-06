package mods.immibis.ars;

/*
 *  Typ 1: Generator
 *  Typ 2: Projektor
 * 
 */

import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileUpgradePassive extends TileEntityMaschines {

	private short connectet_typID;
	private int conectet_ID;
	
	@Override
	public Packet getDescriptionPacket() {
		int actionType = (conectet_ID != 0 ? 1 : 0) | (getActive() ? 2 : 0) | (getFacing() << 2);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, actionType, null);
	}
	
	@Override
	public void onDataPacket(S35PacketUpdateTileEntity p) {
		int actionType = p.func_148853_f();
		setconectet_ID(actionType & 1);
		setActive((actionType & 2) != 0);
		setFacing((short)(actionType >> 2));
	}

	public TileUpgradePassive() {

		conectet_ID = 0;
		connectet_typID = 0;
	}

	public int getconectet_ID() {
		return conectet_ID;
	}

	public void setconectet_ID(int conectet_ID) {
		this.conectet_ID = conectet_ID;
		if((this.conectet_ID != 0) != (conectet_ID != 0))
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public short getConnectet_typID() {
		return connectet_typID;
	}

	public void setConnectet_typID(short i) {
		this.connectet_typID = i;
	}

	@Override
	public void updateEntity() {

		if(!worldObj.isRemote) {

			if (getActive() && getWrenchDropRate() != -1.0F) {
				setWrenchRate(-1.0F);
			}
			if (!getActive() && getWrenchDropRate() != 1.0F) {
				setWrenchRate(1.0F);
			}
		}
	}

	public void updatecheck() {

		if(!worldObj.isRemote) {

			if (conectet_ID != 0) {
				switch (connectet_typID) {
				case 1:
					TileEntity Generator = Linkgrid.getWorldMap(worldObj).getGenerator().get(conectet_ID);
					if (Generator == null) {
						setconectet_ID(0);
						setActive(false);
						updateEntity();
					}
					break;
				case 2:
					TileEntity Projektor = Linkgrid.getWorldMap(worldObj).getProjektor().get(conectet_ID);
					if (Projektor == null) {
						setconectet_ID(0);
						setActive(false);
						updateEntity();
					}
					break;
				}

			}

		}
	}
}
