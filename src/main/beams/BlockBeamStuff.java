package mods.immibis.ars.beams;


import java.util.List;

import mods.immibis.core.BlockCombined;
import mods.immibis.core.RenderUtilsIC;
import mods.immibis.core.api.util.Dir;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockBeamStuff extends BlockCombined {
	
	public static int renderType;
	public static int renderPass;
	
	@Override
	public int getRenderType() {
		return renderType;
	}

	
	/* Metadata values */
	public static final int M_FILTER = 0;
	public static final int M_TESLA = 1;
	public static final int M_MULTIPLEXER = 2;
	public static final int M_RANGE = 3;
	public static final int M_EMP = 4;
	public static final int M_SPEED = 5;
	public static final int M_LOOT_COLLECTOR = 6;
	public static final int M_FIELD_FILTER = 7;
	public static final int M_INV_FILTER = 8;
	public static final int M_WAVELENGTH_SHIFT = 9;
	public static final int M_POTION_APPLICATOR = 10;
	
	
	
	public BlockBeamStuff() {
		super(Material.iron);
		
		GameRegistry.registerBlock(this, ItemBeamStuff.class, "teslaBlock");
	}
	
	private IIcon[][] iFilter, iMultiplexer, iRange, iEMP, iSpeed, iLoot, iFieldFilter;
	private IIcon[][] iInvFilter, iSuppressor, iApplicator;
	@SuppressWarnings("unused")
	private IIcon iTeslaOff, iTeslaOn;
	
	private IIcon iFilterOrangeVert, iFilterOrangeHorz, iFilterColourVert, iFilterColourHorz, iFilterOffColourVert, iFilterOffColourHorz;
	private IIcon iTransparent;
	
	private IIcon[][] iLoad(IIconRegister r, String prefix) {
		return new IIcon[][] {RenderUtilsIC.loadIconArray(r, "advrepsys:tesla!"+prefix+"0", 6), RenderUtilsIC.loadIconArray(r, "advrepsys:tesla!"+prefix+"1", 6)};
	}
	
	private IIcon[][] iLoad3H(IIconRegister r, String prefix) {
		return new IIcon[][] {RenderUtilsIC.loadIconArray(r, "advrepsys:tesla!"+prefix+"0", 3), RenderUtilsIC.loadIconArray(r, "advrepsys:tesla!"+prefix+"1", 3), RenderUtilsIC.loadIconArray(r, "advrepsys:tesla!"+prefix+"H", 3)};
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister r) {
		iFilter = iLoad(r, "filter");
		iMultiplexer = iLoad3H(r, "multiplexer");
		iRange = iLoad3H(r, "range");
		iEMP = iLoad3H(r, "emp");
		iSpeed = iLoad3H(r, "speed");
		iLoot = iLoad3H(r, "loot");
		iFieldFilter = iLoad3H(r, "field");
		iInvFilter = iLoad3H(r, "inv");
		iSuppressor = iLoad3H(r, "suppressor");
		iApplicator = iLoad3H(r, "potion");
		iTeslaOff = RenderUtilsIC.loadIcon(r, "advrepsys:tesla!tesla-off");
		iTeslaOn = RenderUtilsIC.loadIcon(r, "advrepsys:tesla!tesla-on");
		iFilterOrangeVert = RenderUtilsIC.loadIcon(r, "advrepsys:tesla!filter-orange-vert");
		iFilterOrangeHorz = RenderUtilsIC.loadIcon(r, "advrepsys:tesla!filter-orange-horz");
		iFilterColourVert = RenderUtilsIC.loadIcon(r, "advrepsys:tesla!filter-colour-vert");
		iFilterColourHorz = RenderUtilsIC.loadIcon(r, "advrepsys:tesla!filter-colour-horz");
		iFilterOffColourVert = RenderUtilsIC.loadIcon(r, "advrepsys:tesla!filter-off-colour-vert");
		iFilterOffColourHorz = RenderUtilsIC.loadIcon(r, "advrepsys:tesla!filter-off-colour-horz");
		iTransparent = RenderUtilsIC.loadIcon(r, "advrepsys:tesla!transparent");
	}
	
	private IIcon[][] getTextureBase(int meta) {
		switch(meta) {
		case 0: return iFilter; // filter
		case 1: return null; // tesla coil; special case that doesn't use this method
		case 2: return iMultiplexer; // upgrade combiner
		case 3: return iRange; // range upgrade
		case 4: return iEMP; // emp upgrade 
		case 5: return iSpeed; // speed upgrade
		case 6: return iLoot; // loot collector
		case 7: return iFieldFilter; // field filter
		case 8: return iInvFilter; // inventory contents filter
		case 9: return iSuppressor; // suppressor upgrade
		case 10: return iApplicator; // remote potion applicator
		default: return null;
		}
	}
	
	private final int[] sideColour = {
		0x0000FF, // blue
		0xFFFF00, // yellow
		0x00FF00, // green
		0xFF00FF, // purple
		0xFF0000, // red
		0x00FFFF  // cyan
	};
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getMixedBrightnessForBlock(IBlockAccess par1iBlockAccess, int par2, int par3, int par4) {
		//int meta = par1iBlockAccess.getBlockMetadata(par2, par3, par4);
		if(renderPass >= 1) {
			return 0x00F000F0;
		}
		return super.getMixedBrightnessForBlock(par1iBlockAccess, par2, par3, par4);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockAccess par1iBlockAccess, int par2, int par3, int par4) {
		int meta = par1iBlockAccess.getBlockMetadata(par2, par3, par4);
		TileEntity te = par1iBlockAccess.getTileEntity(par2, par3, par4);
		if(meta == M_FILTER) {
			if(renderPass >= 2 && renderPass <= 7)
				return sideColour[renderPass - 2];
		}
		if(renderPass == 2 && te instanceof TileBeamEmitter) {
			return sideColour[((TileBeamEmitter)te).outputFace];
		}
		return super.colorMultiplier(par1iBlockAccess, par2, par3, par4);
	}
	
	public static int getRenderPassesForMeta(int meta) {
		switch(meta) {
		case M_FILTER:
			// plain, orange highlight, colour on each face
			return 8;
		case M_MULTIPLEXER: case M_RANGE: case M_EMP: case M_FIELD_FILTER: case M_INV_FILTER: case M_LOOT_COLLECTOR:
		case M_POTION_APPLICATOR: case M_SPEED: case M_WAVELENGTH_SHIFT:
			// plain, colour on output face
			return 2;
		default:
			return 1;
		}
	}
	
	@Override
	public IIcon getIcon(IBlockAccess w, int x, int y, int z, int side) {
		TileEntity te = w.getTileEntity(x, y, z);
		int meta = w.getBlockMetadata(x, y, z);
		if(te instanceof TileTeslaCoil)
			return iTeslaOff;
		else if(te instanceof TileBeamEmitter) {
			int outputFace = ((TileBeamEmitter)te).outputFace;
			
			if(meta == M_FILTER && renderPass == 1) {
				if(outputFace == side)
					return side < 2 ? iFilterOrangeVert : iFilterOrangeHorz;
				else
					return iTransparent;
			}
			if(meta == M_FILTER && renderPass >= 2 && renderPass <= 7) {
				if(side != (renderPass - 2))
					return iTransparent;
				if(outputFace == side)
					return side < 2 ? iFilterColourVert : iFilterColourHorz;
				else
					return side < 2 ? iFilterOffColourVert : iFilterOffColourHorz;
			}
			IIcon[][] tex = getTextureBase(meta);

			if(tex == null)
				return null;
			
			int texSide;
			if(tex[0].length == 3)
				texSide = (side > 2 ? 2 : side);
			else
				texSide = side;
			if(renderPass == 1 && tex.length > 2) {
				if(side != outputFace)
					return iTransparent;
				return tex[2][texSide];
			}
			
			return tex[side == outputFace ? 1 : 0][texSide];
		}
		else
			return null;
	}

	@Override
	public IIcon getIcon(int side, int data) {
		if(data == 1)
			return iTeslaOff;
		IIcon[][] tex = getTextureBase(data);
		if(tex == null)
			return null;
		if(tex[0].length == 3)
			if(side > 2)
				side = 2;
		
		if(data == M_FILTER && renderPass == 1) {
			if(Dir.PY == side)
				return side < 2 ? iFilterOrangeVert : iFilterOrangeHorz;
			else
				return iTransparent;
		}
		if(data == M_FILTER && renderPass >= 2 && renderPass <= 7) {
			if(side != (renderPass - 2))
				return iTransparent;
			if(Dir.PY == side)
				return side < 2 ? iFilterColourVert : iFilterColourHorz;
			else
				return side < 2 ? iFilterOffColourVert : iFilterOffColourHorz;
		}
		
		if(renderPass == 1 && tex.length > 2) {
			if(side != Dir.PY)
				return iTransparent;
			return tex[2][side];
		}
		
		return tex[side == Dir.PY ? 1 : 0][side];
	}

	@Override
	public TileEntity getBlockEntity(int data) {
		switch(data) {
		case 0: return new TileProgrammableFilter();
		case 1: return new TileTeslaCoil();
		case 2: return new TileUpgradeCombiner();
		case 3: return new TileRangeUpgrade();
		case 4: return new TileEMPUpgrade();
		case 5: return new TileSpeedUpgrade();
		case 6: return new TileLootCollector();
		case 7: return new TileFieldFilter();
		case 8: return new TileInventoryContentsFilter();
		case 9: return new TileSuppressorUpgrade();
		case 10: return new TilePotionUpgrade();
		default: return null;
		}
	}

	@Override
	public void getCreativeItems(List<ItemStack> arraylist) {
		for(int k = 0; k < 11; k++)
			arraylist.add(new ItemStack(this, 1, k));
	}

}
