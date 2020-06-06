package mods.immibis.ars;

import mods.immibis.ars.projectors.TileProjector;
import mods.immibis.ars.projectors.TileProjectorExtender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ItemMFDDebugger extends ItemMFD {

	public ItemMFDDebugger() {
		super(3);
		setUnlocalizedName("advrepsys.debugger");
		setTextureName("advrepsys:mfd-debugger");
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l, float x1, float x2, float x3) {
	
		TileEntity tileEntity = world.getTileEntity(i, j, k);

		if (tileEntity instanceof TileProjector) {
			info.setLength(0);
			info.append("linkGenerator_ID: ").append(((TileProjector) tileEntity).getLinkGenerator_ID());
			Functions.ChattoPlayer(entityplayer, info.toString());

		}

		if (tileEntity instanceof TileEntityGeneratorCore) {
			info.setLength(0);
			info.append("Generator ID: ").append(((TileEntityGeneratorCore) tileEntity).getGenerator_ID());
			Functions.ChattoPlayer(entityplayer, info.toString());
		}

		if (tileEntity instanceof TileProjectorExtender) {
			info.setLength(0);
			info.append("is create: ").append(((TileProjectorExtender) tileEntity).isCreate());
			Functions.ChattoPlayer(entityplayer, info.toString());
			info.setLength(0);
			info.append("preactive: ").append(((TileProjectorExtender) tileEntity).isPreactive());
			Functions.ChattoPlayer(entityplayer, info.toString());
			info.setLength(0);
			info.append("active: ").append(((TileProjectorExtender) tileEntity).getActive());
			Functions.ChattoPlayer(entityplayer, info.toString());
			info.setLength(0);
			info.append("con_to_projektor: ").append(((TileProjectorExtender) tileEntity).isCon_to_projektor());
			Functions.ChattoPlayer(entityplayer, info.toString());
			info.setLength(0);
			info.append("remProjektor_ID: ").append(((TileProjectorExtender) tileEntity).getRemProjektor_ID());
			Functions.ChattoPlayer(entityplayer, info.toString());
			info.setLength(0);
			info.append("remGenerator_ID: ").append(((TileProjectorExtender) tileEntity).getRemGenerator_ID());
			Functions.ChattoPlayer(entityplayer, info.toString());
			return true;
		}

		//

		if (world.getBlock(i, j, k) == ARSMod.MFFSFieldblock) {
			FFBlock ffb = FFWorld.get(world).get(i, j, k);
			if (ffb != null) {
				for(String s : ffb.getDebugInfo().split("\n"))
					Functions.ChattoPlayer(entityplayer, s);
				return true;
			}

		}

		return false;
	}

}
