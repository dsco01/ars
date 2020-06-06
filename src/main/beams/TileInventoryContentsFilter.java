package mods.immibis.ars.beams;


import java.util.HashSet;
import java.util.Set;

import mods.immibis.ars.ARSMod;
import mods.immibis.core.BasicInventory;
import mods.immibis.core.api.util.NBTType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileInventoryContentsFilter extends TileBeamEmitter implements IInventory {
	private BasicInventory inv = new BasicInventory(1);
	
	@Override public boolean canUpdate() {return false;}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		
		inv.readFromNBT(tag.getTagList("inv", NBTType.COMPOUND));
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		
		tag.setTag("inv", inv.writeToNBT());
	}
	
	private EntityFilter filter = new EntityFilter() {
		@Override
		public Set<Entity> filter(Set<Entity> in) {
			if(inv.contents[0] == null) {
				in.clear();
				return in;
			}
			
			Set<Entity> rv = new HashSet<Entity>();
			for(Entity e : in) {
				if(e instanceof EntityPlayer) {
					for(ItemStack is : ((EntityPlayer)e).inventory.mainInventory) {
						if(ARSMod.areItemsEqual(is, inv.contents[0])) {
							rv.add(e);
							break;
						}
					}
				} else if(e instanceof EntityItem) {
					if(ARSMod.areItemsEqual(((EntityItem)e).getEntityItem(), inv.contents[0])) {
						rv.add(e);
					}
				}
			}
			
			return rv;
		}
	};
	
	@Override
	public Object getOutput() {
		return filter;
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
		return "Loot collector";
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
			pl.openGui(ARSMod.instance, ARSMod.GUI_INVENTORY_CONTENTS_FILTER, worldObj, xCoord, yCoord, zCoord);
		return true;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return true;
	}
}
