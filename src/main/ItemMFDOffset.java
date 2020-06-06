package mods.immibis.ars;

import mods.immibis.ars.projectors.TileProjector;
import mods.immibis.ars.projectors.TileProjectorExtender;
import mods.immibis.core.api.util.Dir;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ItemMFDOffset extends ItemMFD {

	public ItemMFDOffset() {
		super(2);
		setUnlocalizedName("advrepsys.mfd.offset");
		setTextureName("advrepsys:mfd-offset");
	}
	
	private int clamp(int i) {
		if(i < -TileProjector.maxOffset)
			return -TileProjector.maxOffset;
		if(i > TileProjector.maxOffset)
			return TileProjector.maxOffset;
		return i;
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int x, int y, int z, int side, float x1, float x2, float x3) {
		
		TileEntity te = world.getTileEntity(x, y, z);
		if(te instanceof TileProjector && !(te instanceof TileProjectorExtender)) {
			if(world.isRemote)
				return true;
			
			TileProjector p = (TileProjector)te;
			if(p.getActive()) {
				Functions.ChattoPlayer(entityplayer, "Cannot move an active forcefield.");
				return true;
			}
			int step = entityplayer.isSneaking() ? -1 : 1;
			switch(side) {
			case Dir.NX: p.offsetX = clamp(p.offsetX + step); break;
			case Dir.NY: p.offsetY = clamp(p.offsetY + step); break;
			case Dir.NZ: p.offsetZ = clamp(p.offsetZ + step); break;
			case Dir.PX: p.offsetX = clamp(p.offsetX - step); break;
			case Dir.PY: p.offsetY = clamp(p.offsetY - step); break;
			case Dir.PZ: p.offsetZ = clamp(p.offsetZ - step); break;
			}
			Functions.ChattoPlayer(entityplayer, "New field offset: "+p.offsetX+","+p.offsetY+","+p.offsetZ);
			return true;
		}
		return false;
	}
}
