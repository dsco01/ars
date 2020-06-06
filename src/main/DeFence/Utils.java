package mods.immibis.ars.DeFence;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * 
 * Mostly internal class that contains misc. helper functions.
 * 
 * @author F4113nb34st
 *
 */
public class Utils
{
	/**
	 * Drops the specified item stack at the specified coords.
	 */
	public static void dropAsEntity(World world, int i, int j, int k, ItemStack itemstack)
    {
        if (itemstack == null)
        {
            return;
        }
        else
        {
            double d = 0.69999999999999996D;
            double d1 = (double)world.rand.nextFloat() * d + (1.0D - d) * 0.5D;
            double d2 = (double)world.rand.nextFloat() * d + (1.0D - d) * 0.5D;
            double d3 = (double)world.rand.nextFloat() * d + (1.0D - d) * 0.5D;
            EntityItem entityitem = new EntityItem(world, (double)i + d1, (double)j + d2, (double)k + d3, itemstack);
            entityitem.delayBeforeCanPickup = 10;
            world.spawnEntityInWorld(entityitem);
            return;
        }
    }
}
