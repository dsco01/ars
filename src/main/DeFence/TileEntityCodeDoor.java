package mods.immibis.ars.DeFence;

import java.util.List;

import mods.immibis.ars.ARSMod;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityCodeDoor extends TileEntity
{
	public boolean inited = false;
	public String code = "";
	public int timeLeft;
	public boolean open;
	public int facing = 0;
	public String owner = "";
	
	public static final int MAX_CODE_LENGTH = 4;
	
	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("o", owner);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, facing | (open ? 8 : 0), tag);
	}
	
	@Override
	public void onDataPacket(net.minecraft.network.NetworkManager net, S35PacketUpdateTileEntity pkt) {
		boolean oldO = open;
		int oldF = facing;
		
		open = (pkt.func_148853_f() & 8) != 0;
		facing = pkt.func_148853_f() & 7;
		owner = pkt.func_148857_g().getString("o");
		
		if(oldO != open || oldF != facing)
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	/**
     * Override this to receive events.
     * Args are:
     * Id = event id
     * Arg = event arguement
     * Player = the player that sent the event
     * Server = whether or not serverside
     */
    public void onDoorGuiAction(int id, String arg, EntityPlayer player)
    {
    	if(arg.length() > MAX_CODE_LENGTH || arg.length() == 0)
    		return;
    	
    	if(id == 9)
		{
    		if(arg.equals(code))
    		{
    			if(!open)
    				worldObj.playAuxSFXAtEntity(null, 1003, xCoord, yCoord, zCoord, 0);
    			open = true;
    			timeLeft = 100;
    			player.closeScreen();
    			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    		}
		} else if(id == 10) {
			if(isOwner(player))
				code = arg;
		}
    }
    
	public boolean onActivated(EntityPlayer entityplayer)
    {
		if(worldObj.getTileEntity(xCoord, yCoord - 1, zCoord) instanceof TileEntityCodeDoor)
		{
			return ((TileEntityCodeDoor)worldObj.getTileEntity(xCoord, yCoord - 1, zCoord)).onActivated(entityplayer);
		}else
		{
			if(entityplayer.isSneaking())
				return false;
			if(!worldObj.isRemote)
		    	entityplayer.openGui(ARSMod.instance, ARSMod.GUI_DEFENCE_CODE_DOOR, worldObj, xCoord, yCoord, zCoord);
		    return true;
		}
    }
    
    @Override
	public void updateEntity() 
    {
    	if(worldObj.isRemote)
    	{
    		return;
    	}
    	if(!worldObj.doesBlockHaveSolidTopSurface(worldObj, xCoord, yCoord - 1, zCoord) && !(worldObj.getTileEntity(xCoord, yCoord - 1, zCoord) instanceof TileEntityCodeDoor))
    	{
    		worldObj.setBlockToAir(xCoord, yCoord, zCoord);
    		return;
    	}
    	if(inited && !(worldObj.getTileEntity(xCoord, yCoord + 1, zCoord) instanceof TileEntityCodeDoor))
    	{
    		worldObj.setBlockToAir(xCoord, yCoord, zCoord);
    		return;
    	}
    	if(worldObj.getTileEntity(xCoord, yCoord - 1, zCoord) instanceof TileEntityCodeDoor)
    	{
    		boolean oldO = open;
    		int oldF = facing;
    		open = ((TileEntityCodeDoor)worldObj.getTileEntity(xCoord, yCoord - 1, zCoord)).open;
    		facing = ((TileEntityCodeDoor)worldObj.getTileEntity(xCoord, yCoord - 1, zCoord)).facing;
    		if(oldO != open || oldF != facing)
    			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    		return;
    	}
    	if(timeLeft > 0)
    	{
    		timeLeft--;
    		if(timeLeft == 0)
    		{
    			if(open)
    				worldObj.playAuxSFXAtEntity(null, 1003, xCoord, yCoord, zCoord, 0);
    			open = false;
    			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    		}
    	}
    }
	
    public AxisAlignedBB getCollisionBoundingBox()
    {
    	if(!open)
		{
			if(ForgeDirection.getOrientation(facing) == ForgeDirection.NORTH)
			{
				return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + .1875F);
			}
			if(ForgeDirection.getOrientation(facing) == ForgeDirection.SOUTH)
			{
				return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord + .8125F, xCoord + 1, yCoord + 1, zCoord + 1);
			}
			if(ForgeDirection.getOrientation(facing) == ForgeDirection.EAST)
			{
				return AxisAlignedBB.getBoundingBox(xCoord + .8125F, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
			}
			if(ForgeDirection.getOrientation(facing) == ForgeDirection.WEST)
			{
				return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + .1875F, yCoord + 1, zCoord + 1);
			}
		}else
		{
			if(ForgeDirection.getOrientation(facing) == ForgeDirection.NORTH)
			{
				return AxisAlignedBB.getBoundingBox(xCoord + .8125F, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
			}
			if(ForgeDirection.getOrientation(facing) == ForgeDirection.SOUTH)
			{
				return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + .1875F, yCoord + 1, zCoord + 1);
			}
			if(ForgeDirection.getOrientation(facing) == ForgeDirection.EAST)
			{
				return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord + .8125F, xCoord + 1, yCoord + 1, zCoord + 1);
			}
			if(ForgeDirection.getOrientation(facing) == ForgeDirection.WEST)
			{
				return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + .1875F);
			}
		}
    	return null;
    }
    
	public boolean isUseableByPlayer(EntityPlayer entityplayer)
    {
        if (worldObj.getTileEntity(xCoord, yCoord, zCoord) != this)
	    {
	        return false;
	    }
	    else
	    {
	        return entityplayer.getDistance((double)xCoord + 0.5D, (double)yCoord + 0.5D, (double)zCoord + 0.5D) <= 64D;
	    }
    }
	
	public void onAdded(EntityLivingBase player)
    {
		if(player == null)
	    {
	        setFacing((short)2);
	    }else
	    {
	        setFacing(determineOrientation(player));
	    }
		if(player instanceof EntityPlayer)
		{
			owner = ((EntityPlayer)player).getDisplayName(); // TODO allow for name changes
		}
		if(!(worldObj.getTileEntity(xCoord, yCoord - 1, zCoord) instanceof TileEntityCodeDoor))
		{
			worldObj.setBlock(xCoord, yCoord + 1, zCoord, DeFenceCore.codedoorblock, 0, 3);
			TileEntityCodeDoor top = ((TileEntityCodeDoor)worldObj.getTileEntity(xCoord, yCoord + 1, zCoord));
			top.facing = facing;
			top.owner = owner;
			inited = true;
		}
    }
	
	public float getHardnessFor(EntityPlayer player)
    {
		return (open || (!owner.equals("") && isOwner(player))) ? getHardness() : -1;
    }
	
	public float getHardness() {
		return 3.0f;
	}
	
	public AxisAlignedBB getBoundsWithinBlock() {
		ForgeDirection dir = ForgeDirection.getOrientation(facing);
		
		if(!open) {
			if(dir == ForgeDirection.NORTH)
				return AxisAlignedBB.getBoundingBox(0, 0, 0, 1, 1, .1875F);
			if(dir == ForgeDirection.SOUTH)
				return AxisAlignedBB.getBoundingBox(0, 0, .8125F, 1, 1, 1);
			if(dir == ForgeDirection.EAST)
				return AxisAlignedBB.getBoundingBox(.8125F, 0, 0, 1, 1, 1);
			if(dir == ForgeDirection.WEST)
				return AxisAlignedBB.getBoundingBox(0, 0, 0, .1875F, 1, 1);
		} else {
			if(dir == ForgeDirection.NORTH)
				return AxisAlignedBB.getBoundingBox(.8125F, 0, 0, 1, 1, 1);
			if(dir == ForgeDirection.SOUTH)
				return AxisAlignedBB.getBoundingBox(0, 0, 0, .1875F, 1, 1);
			if(dir == ForgeDirection.EAST)
				return AxisAlignedBB.getBoundingBox(0, 0, .8125F, 1, 1, 1);
			if(dir == ForgeDirection.WEST)
				return AxisAlignedBB.getBoundingBox(0, 0, 0, 1, 1, .1875F);
		}
		return AxisAlignedBB.getBoundingBox(0, 0, 0, 1, 1, 1);
	}

	@SideOnly(Side.CLIENT)
	public boolean renderWorldBlock(Block block, RenderBlocks render)
	{
		AxisAlignedBB bb = getBoundsWithinBlock();
		render.setRenderBounds(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
		render.renderStandardBlock(block, xCoord, yCoord, zCoord);
        return true;
	}

	public boolean isOwner(EntityPlayer player) {
		// TODO allow for name changes
		return player.getDisplayName().equals(owner) /*|| Platform.worldType() != Platform.WorldMode.MP*/;
	}

	/**
	 * If this block collides with the given aabb, add the aabb of the collision area to the list.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addCollidingBlockToList(AxisAlignedBB maskBB, List list, Entity entity) {
		AxisAlignedBB doorBB = getBoundsWithinBlock().offset(xCoord, yCoord, zCoord);
	    if(maskBB.intersectsWith(doorBB))
	    	list.add(doorBB);
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
	    super.readFromNBT(nbt);
	    
	    code = nbt.getString("code");
	    timeLeft = nbt.getInteger("timeLeft");
	    open = nbt.getBoolean("open");
	    facing = nbt.getInteger("facing");
	    owner = nbt.getString("owner");
	    
	    if(code.length() > MAX_CODE_LENGTH)
	    	code = code.substring(0, MAX_CODE_LENGTH);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
	    super.writeToNBT(nbt);
	    
	    nbt.setString("code", code);
	    nbt.setInteger("timeLeft", timeLeft);
	    nbt.setBoolean("open", open);
	    nbt.setInteger("facing", facing);
	    nbt.setString("owner", owner);
	}

	public short determineOrientation(Entity entity) {
		int rotation = Math.round(entity.rotationYaw / 90F) & 3;
	
	    switch (rotation)
	    {
	        case 0:
	            return 2;
	        case 1:
	            return 5;
	        case 2:
	            return 3;
	        case 3:
	            return 4;
	    }
	    //should never get to this point
	    return 2;
	}

	public short getFacing() {
		return (short)facing;
	}

	/**
	 * Sets the facing side.
	 */
	public void setFacing(short f) {
		facing = f;
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
}
