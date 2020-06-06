package mods.immibis.ars.beams;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mods.immibis.core.api.APILocator;
import mods.immibis.core.api.crossmod.ICrossModIC2;
import mods.immibis.core.api.net.IPacket;
import mods.immibis.core.api.traits.IEnergyConsumerTrait;
import mods.immibis.core.api.traits.IEnergyConsumerTrait.EnergyUnit;
import mods.immibis.core.api.traits.IEnergyConsumerTraitUser;
import mods.immibis.core.api.traits.TraitField;
import mods.immibis.core.api.traits.UsesTraits;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;

@UsesTraits
public class TileTeslaCoil extends TileBeamEmitter implements IEnergyConsumerTraitUser {
	
	@TraitField
	private IEnergyConsumerTrait energyConsumer;
	
	@Override public double EnergyConsumer_getPreferredBufferSize() {return MAX_STORAGE;}
	@Override public EnergyUnit EnergyConsumer_getPreferredUnit() {return EnergyUnit.EU;}
	@Override public boolean EnergyConsumer_isBufferingPreferred() {return true;}
	
	@Override protected void setupBeams() {}
	@Override public Object getOutput(int side) {return null;}
	
	/* Notes:
	 * At 32 eu/t
	 * 	skeletons die in 2 hits
	 *  zombies in 3
	 *  endermen in 4
	 * At 128 eu/t:
	 *  skeletons in 1
	 *  zombies in 2
	 *  endermen in 2
	 */
	
	private int filter_side = -1;
	
	@Override
	public void onBlockNeighbourChange() {
		super.onBlockNeighbourChange();
		filter_side = -1;
	}
	
	private static final int MAX_STORAGE = 5000;
	private static final int MIN_SHOT_EU = 500;
	private static final int MIN_INTERVAL = 1; // ticks
	private static final int PROJECTILE_EU = 500; // EU cost to destroy a single projectile
	private static final int DEFAULT_INTERVAL = 20; // ticks
	private static final int DEFAULT_ITEM_INTERVAL = 3; // ticks
	private static final int DEFAULT_PROJECTILE_INTERVAL = 10; // ticks
	private static final double DEFAULT_RANGE = 10;
	
	private Set<Entity> currentTargets = null;
	private int ticksToNextShot = 0;
	
	private UpgradeData upgrades;
	
	private double RANGE; // updated each shot from DEFAULT_RANGE and upgrades
	
	private ICrossModIC2 ic2 = APILocator.getCrossModIC2();
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		energyConsumer.writeToNBT(tag);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		energyConsumer.readFromNBT(tag);
	}
	
	@Override
	public void updateEntity() {
		if(worldObj.isRemote)
			return;
		
		if(filter_side == -1) {
			for(int k = 0; k < 6; k++) {
				Object input = getInput(k);
				if(input instanceof EntityFilter) {
					filter_side = k;
					break;
				}
			}
		}
		
		if(ticksToNextShot <= 0) {
			if(energyConsumer.getStoredEnergy() >= MIN_SHOT_EU) {
				EntityFilter filter;
				if(filter_side == -1)
					filter = null;
				else {
					Object filter_raw = getInput(filter_side);
					if(!(filter_raw instanceof EntityFilter))
						filter = null;
					else
						filter = (EntityFilter)filter_raw;
				}
				
				upgrades = new UpgradeData();
				for(int k = 0; k < 6; k++) {
					Object data = getInput(k);
					if(data instanceof UpgradeData) {
						upgrades.combine((UpgradeData)data);
					}
				}
				
				RANGE = Math.min(BeamsMain.maxTeslaRange, DEFAULT_RANGE + upgrades.range);
				
				Set<Entity> targets = findAllPotentialTargets();
				if(filter != null)
					targets = filter.filter(targets);
				
				currentTargets = targets;
				
				if(targets != null && targets.size() > 0) {
					// Pick closest target
					Entity best = null;
					double best_dsq = Double.MAX_VALUE;
					for(Entity e : currentTargets) {
						double dsq = e.getDistanceSq(xCoord+0.5, yCoord+0.5, zCoord+0.5);
						if(dsq < best_dsq) {
							best_dsq = dsq;
							best = e;
						}
					}
					
					if(best != null)
						fireShot(best, energyConsumer.getStoredEnergy());
					
				}
			}

		}
		
		ticksToNextShot--;
	}
	
	@Override
	public void validate() {
		super.validate();
		energyConsumer.onValidate();
	}
	
	private static DamageSource damageSource = new DamageSource("ARSteslacoil") {};
	
	private static final int[] EMP_ARMOUR_PRIORITY = {
		2, // chestplate
		1, // legs
		3, // helmet
		0, // boots
	};
	
	private int getItemCollectionCost(EntityItem item) {
		double dx = item.posX - (xCoord + 0.5);
		double dy = item.posY - (yCoord + 0.5);
		double dz = item.posZ - (zCoord + 0.5);
		double dist_sq = dx*dx + dy*dy + dz*dz;
		
		// An item at 50 blocks will take 1000 EU
		return (int)(dist_sq * 0.4); 
	}
	
	private void fireShot(Entity at, double eu) {
		ticksToNextShot = Math.max(MIN_INTERVAL, (int)(DEFAULT_INTERVAL / upgrades.speed));
		
		if(at instanceof EntityItem || at instanceof EntityBoat || at instanceof EntityMinecart) {
			if(eu > 384)
				eu = 384;
			ticksToNextShot = Math.min(ticksToNextShot, DEFAULT_ITEM_INTERVAL);
		}
		
		boolean isProjectile = at instanceof EntityArrow || at instanceof EntityThrowable || at instanceof EntityFireball;
		
		if(isProjectile) {
			if(eu >= PROJECTILE_EU)
				eu = PROJECTILE_EU;
			else
				return; // not enough EU
			ticksToNextShot = Math.min(ticksToNextShot, DEFAULT_PROJECTILE_INTERVAL);
		}
		
		if(energyConsumer.getStoredEnergy() < eu) {
			ticksToNextShot = 0;
			return;
		}

		energyConsumer.setStoredEnergy(energyConsumer.getStoredEnergy() - eu);
		
		
		
		if(at instanceof EntityPlayer && upgrades.emp != null && upgrades.emp.getStoredEnergy() > 0) {
			int emp_eu = (int)upgrades.emp.getStoredEnergy();
			
			InventoryPlayer inv = ((EntityPlayer)at).inventory;
			for(int k = 0; k < 4 && emp_eu > 0; k++) {
				ItemStack item = inv.armorInventory[EMP_ARMOUR_PRIORITY[k]];
				if(item != null && ic2.isElectricItem(item))
					emp_eu -= ic2.dischargeElectricItem(item, emp_eu, 9001, true, false);
			}
			
			upgrades.emp.setStoredEnergy(emp_eu);
		}
		
		
		
		if(isProjectile) {
			at.setDead();
		} else {
			int damage = (int)(Math.sqrt(eu / 5000.0) * 30);
			at.attackEntityFrom(damageSource, damage);
		}
		
		if(upgrades.lootCollectors != null && at instanceof EntityItem && at.isDead) {
			EntityItem ei = (EntityItem)at;
			int origStackSize = ei.getEntityItem().stackSize;
			for(TileLootCollector tlc : upgrades.lootCollectors)
				if(tlc.collectItem(ei))
					break;
			
			// may cause negative stored EU
			// since we don't check if there's enough first
			// (which we can't because we don't know whether the item
			// will be collected or not)
			if(ei.getEntityItem().stackSize < origStackSize)
				energyConsumer.setStoredEnergy(energyConsumer.getStoredEnergy() - getItemCollectionCost(ei));
		}
		
		
		
		if(upgrades.potions != null && upgrades.potions.size() >= 1 && at instanceof EntityLivingBase) {
			TilePotionUpgrade tpu = upgrades.potions.iterator().next();
			for(PotionEffect pe : tpu.getEffect())
				((EntityLivingBase)at).addPotionEffect(new PotionEffect(pe));
		}
		
		
		
		if(!upgrades.suppressor) {
			// create bolt fx on clients
			AxisAlignedBB atBB = at.boundingBox;
			IPacket packet = new PacketBoltFX(
					xCoord + 0.5, yCoord + 0.5, zCoord + 0.5,
					(atBB.maxX + atBB.minX) / 2, (atBB.maxY + atBB.minY) / 2, (atBB.maxZ + atBB.minZ) / 2
				);
			@SuppressWarnings("unchecked")
			List<EntityPlayer> nearbyPlayers = worldObj.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord).offset(0.5, 0.5, 0.5).expand(100, 100, 100));
			for(EntityPlayer ep : nearbyPlayers)
				APILocator.getNetManager().sendToClient(packet, ep);
		}
	}
	
	@SuppressWarnings("unchecked")
	private Set<Entity> findAllPotentialTargets() {
		double x = xCoord + 0.5;
		double y = yCoord + 0.5;
		double z = zCoord + 0.5;
		
		double rangesq = RANGE*RANGE;
		
		AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(
			x - RANGE,
			y - RANGE,
			z - RANGE,
			x + RANGE,
			y + RANGE,
			z + RANGE
		);
		
		Set<Entity> out = new HashSet<Entity>();
		for(Entity e : (List<Entity>)worldObj.getEntitiesWithinAABB(Entity.class, aabb)) {
			if(e instanceof EntityLivingBase && ((EntityLivingBase)e).deathTime > 0)
				continue; // never target dead mobs
			
			//if(e instanceof EntityHanging || e instanceof EntityFallingSand || e instanceof EntityTNTPrimed)
			//	continue;
			
			//if(e.getClass().getName().startsWith("buildcraft.") || e.getClass().getName().startsWith("codechicken."))
			//	continue;
			
			if(!(e instanceof EntityArrow || e instanceof EntityBoat || e instanceof EntityFireball || e instanceof EntityItem || e instanceof EntityLivingBase || e instanceof EntityMinecart || e instanceof EntityThrowable || e instanceof EntityXPOrb))
				continue;
			
			if(e.getDistanceSq(x, y, z) <= rangesq)
				out.add(e);
		}
		
		return out;
	}
	
	@Override
	public void invalidate() {
		super.invalidate();
		energyConsumer.onInvalidate();
	}
	
	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		energyConsumer.onChunkUnload();
	}
}
