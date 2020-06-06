package mods.immibis.ars.DeFence;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemCodeDoor extends Item
{
    public ItemCodeDoor()
    {
        setTextureName("advrepsys:DeFence_codedoor");
        setCreativeTab(CreativeTabs.tabAllSearch);
        setUnlocalizedName("advrepsys.defence.codedoor");
        setMaxStackSize(1);
    }
    
    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    @Override
	public boolean onItemUse(ItemStack item, EntityPlayer player, World world, int x, int y, int z, int side, float par8, float par9, float par10)
    {
        Block id = world.getBlock(x, y, z);

        if (id == Blocks.snow_layer)
        {
        	side = 1;
        }else 
        if (id != Blocks.vine && id != Blocks.tallgrass && id != Blocks.deadbush && !id.isReplaceable(world, x, y, z))
        {
            if (side == 0)
            {
                --y;
            }

            if (side == 1)
            {
                ++y;
            }

            if (side == 2)
            {
                --z;
            }

            if (side == 3)
            {
                ++z;
            }

            if (side == 4)
            {
                --x;
            }

            if (side == 5)
            {
                ++x;
            }
        }

        Block blockid = DeFenceCore.codedoorblock;
        
        if (item.stackSize == 0)
        {
            return false;
        }else 
        if (!player.canPlayerEdit(x, y, z, side, item))
        {
            return false;
        }else 
        if (y == 255 && blockid.getMaterial().isSolid())
        {
            return false;
        }
        else if (world.canPlaceEntityOnSide(blockid, x, y, z, false, side, player, item))
        {
            Block block = blockid;
            int meta = getMetadata(item.getItemDamage());
            int var14 = blockid.onBlockPlaced(world, x, y, z, side, par8, par9, par10, meta);

            if (placeBlockAt(item, player, world, x, y, z, side, par8, par9, par10, var14))
            {
            	world.playSoundEffect((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), block.stepSound.func_150496_b(), (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);
                item.stackSize--;
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns true if the given ItemBlock can be placed on the given side of the given block position.
     */
    public boolean canPlaceItemBlockOnSide(World world, int x, int y, int z, int side, EntityPlayer player, ItemStack item)
    {
        Block id = world.getBlock(x, y, z);

        if (id == Blocks.snow_layer)
        {
        	side = 1;
        }else 
        if (id != Blocks.vine && id != Blocks.tallgrass && id != Blocks.deadbush && !id.isReplaceable(world, x, y, z))
        {
            if (side == 0)
            {
                --y;
            }

            if (side == 1)
            {
                ++y;
            }

            if (side == 2)
            {
                --z;
            }

            if (side == 3)
            {
                ++z;
            }

            if (side == 4)
            {
                --x;
            }

            if (side == 5)
            {
                ++x;
            }
        }

        return world.canPlaceEntityOnSide(DeFenceCore.codedoorblock, x, y, z, false, side, (Entity)null, item);
    }

    public String getItemNameIS(ItemStack par1ItemStack)
    {
        return "Code Door";
    }

    public String getItemName()
    {
        return "Code Door";
    }

    /**
     * Called to actually place the block, after the location is determined
     * and all permission checks have been made.
     *
     * @param stack The item stack that was used to place the block. This can be changed inside the method.
     * @param player The player who is placing the block. Can be null if the block is not being placed by a player.
     * @param side The side the player (or machine) right-clicked on.
     */
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
    {
       Block blockid = DeFenceCore.codedoorblock;
       if (!world.setBlock(x, y, z, blockid, metadata, 3))
       {
           return false;
       }

       if (world.getBlock(x, y, z) == blockid)
       {
           blockid.onBlockPlacedBy(world, x, y, z, player, stack);
           blockid.onPostBlockPlaced(world, x, y, z, metadata);
       }

       return true;
    }
}
