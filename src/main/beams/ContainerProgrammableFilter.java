package mods.immibis.ars.beams;

import mods.immibis.core.api.util.BaseContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContainerProgrammableFilter extends BaseContainer<TileProgrammableFilter> {
	
	public boolean inputsOnRight = true;

	public ContainerProgrammableFilter(EntityPlayer player, TileProgrammableFilter inv) {
		super(player, inv);
		
		for(int x = 0; x < 9; x++)
			addSlotToContainer(new Slot(player.inventory, x, 8 + 18*x, 198));
		
		for(int y = 0; y < 3; y++)
			for(int x = 0; x < 9; x++)
				addSlotToContainer(new Slot(player.inventory, x + y*9 + 9, 8 + 18*x, 140 + 18*y));
		
		for(int y = 0; y < 6; y++)
			addSlotToContainer(new Slot(inv, y + TileProgrammableFilter.SLOT_COL1, 12, 13 + 20*y));
		
		for(int y = 0; y < 3; y++)
			addSlotToContainer(new Slot(inv, y + TileProgrammableFilter.SLOT_COL2, 55, 23 + 40*y));
		
		addSlotToContainer(new Slot(inv, TileProgrammableFilter.SLOT_COL3, 109, 63));
		addSlotToContainer(new Slot(inv, TileProgrammableFilter.SLOT_FINAL, 151, 63));
	}
	
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		super.setProgressBar(0, inv.inputsOnRight ? 1 : 0);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int par1, int par2) {
		if(par1 == 0)
			inputsOnRight = (par2 != 0);
	}
	
	@Override
	public ItemStack transferStackInSlot(int slot) {
		return null;
	}
	
	@Override
	public void onButtonPressed(int id) {
		if(id == 0)
			inv.inputsOnRight = !inv.inputsOnRight;
		
		super.onButtonPressed(id);
	}

}
