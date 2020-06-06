package mods.immibis.ars.DeFence;

import ic2.core.IC2;
import ic2.core.audio.AudioPosition;
import ic2.core.audio.PositionSpec;
import ic2.core.item.tool.ItemToolCutter;
import net.minecraft.block.BlockPane;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class BlockFences extends BlockPane
{
    public BlockFences(String texname)
    {
        super(texname, texname+"_top", Material.iron, true);
        setCreativeTab(CreativeTabs.tabDecorations);
        setBlockUnbreakable();
        setResistance(150F);
    }
    
    /**
	 * Called when the block is clicked by a player. Args: x, y, z, entityPlayer
	 */
	@Override
	public void onBlockClicked(World world, int i, int j, int k, EntityPlayer ep)
	{
		if(ep.getCurrentEquippedItem() != null && ep.getCurrentEquippedItem().getItem() instanceof ItemToolCutter)
		{
			if(!world.isRemote)
			{
				final int USES = 4;
				
				ItemStack c = ep.getCurrentEquippedItem();
				if(c.getItemDamage() > c.getMaxDamage() - USES) {
					ep.inventory.mainInventory[ep.inventory.currentItem] = null;
					return;
				}
				ItemToolCutter.damageCutter(c, USES);
				if(c.getItemDamage() >= c.getMaxDamage())
					ep.inventory.mainInventory[ep.inventory.currentItem] = null;
				Utils.dropAsEntity(world, i, j, k, new ItemStack(this));
				world.setBlockToAir(i, j, k);
			}
			
			if(IC2.platform.isRendering())
			{
				AudioPosition var10001 = new AudioPosition(world, (float)i + 0.5F, (float)j + 0.5F, (float)k + 0.5F);
				IC2.audioManager.playOnce(var10001, PositionSpec.Center, "Tools/InsulationCutters.ogg", true, IC2.audioManager.getDefaultVolume());
			}
		}
	}
    
    /**
     * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
     */
    @Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity)
    {
    	if(this == DeFenceCore.fenceBW) {
    		// barbed wire
    		entity.attackEntityFrom(DamageSource.cactus, 1);
    		entity.setInWeb();
    	}
    }
}
