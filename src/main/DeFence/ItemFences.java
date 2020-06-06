// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package mods.immibis.ars.DeFence;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemFences extends ItemBlock
{
    public ItemFences(Block i) 
    {
    	super(i);
		setMaxDamage(0);
        setHasSubtypes(true);
        setFull3D();
	}
    
    /**
     * Gets an icon index based on an item's damage value
     */
    @Override
	public IIcon getIconFromDamage(int par1)
    {
        return field_150939_a.getIcon(2, par1);
    }

	/**
     * Returns the metadata of the block which this Item (ItemBlock) can place
     */
    @Override
	public int getMetadata(int i)
    {
        return i;
    }
    
    public String getItemNameIS(ItemStack itemstack)
    {
    	int meta = itemstack.getItemDamage();
    	switch(meta)
    	{
    	case 0:
    		return "tile.advrepsys.defence.chainlink";
    	case 1:
    		return "tile.advrepsys.defence.barbedwire";
    	}
    	return "";
    }
}
