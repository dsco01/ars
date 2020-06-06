package mods.immibis.ars.beams;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import mods.immibis.ars.ARSMod;
import mods.immibis.core.BasicInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

public class TilePotionUpgrade extends TileBeamEmitter implements IInventory {
	private BasicInventory inv = new BasicInventory(9);
	
	@Override public boolean canUpdate() {return false;}

	private UpgradeData upgrade = new UpgradeData();
	public TilePotionUpgrade() {
		upgrade.potions = Arrays.asList(this);
	}
	
	@Override
	public Object getOutput() {
		return upgrade;
	}
	
	@Override
	public int getSizeInventory() {
		return inv.contents.length;
	}

	@Override
	public ItemStack getStackInSlot(int var1) {
		return inv.contents[var1];
	}

	@Override
	public ItemStack decrStackSize(int var1, int var2) {
		return inv.decrStackSize(var1, var2);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int var1) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int var1, ItemStack var2) {
		inv.contents[var1] = var2;
	}

	@Override
	public String getInventoryName() {
		return "Potion upgrade";
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer var1) {
		return var1.getDistanceSq(xCoord+0.5, yCoord+0.5, zCoord+0.5) <= 64;
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}
	
	@Override
	public boolean onBlockActivated(EntityPlayer pl) {
		if(!worldObj.isRemote)
			pl.openGui(ARSMod.instance, ARSMod.GUI_POTION_UPGRADE, worldObj, xCoord, yCoord, zCoord);
		return true;
	}
	
	@Override
	public int getBeamColour() {return 1;}
	
	@SuppressWarnings("unchecked")
	public List<PotionEffect> getEffect() {
		for(int k = 0; k < 9; k++) {
			ItemStack is = inv.contents[k];
			if(is == null || is.getItem() != Items.potionitem || !ItemPotion.isSplash(is.getItemDamage()))
				continue;
			
			is.stackSize--;
			if(is.stackSize == 0)
				inv.contents[k] = null;
			markDirty();
			
			return (List<PotionEffect>)Items.potionitem.getEffects(is);
		}
		return Collections.emptyList();
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack is) {
		if(is != null && (is.getItem() != Items.potionitem || !ItemPotion.isSplash(is.getItemDamage())))
			return false;
		return true;
	}
}
