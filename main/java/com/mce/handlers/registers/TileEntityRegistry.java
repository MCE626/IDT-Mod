package com.mce.handlers.registers;

import com.mce.entity.tile.TEAH;
import com.mce.entity.tile.TECT;
import com.mce.entity.tile.TileEntityECR;
import com.mce.entity.tile.TileEntityNCR;
import com.mce.entity.tile.TileEntitySR;
import com.mce.entity.tile.TileEntityTF;
import com.mce.entity.tile.TileEntityUCR;
import com.mce.entity.tile.TileEntityZimWire;
import com.mce.entity.tile.bombs.TileEntityDM;
import com.mce.entity.tile.bombs.TileEntityEB;
import com.mce.entity.tile.bombs.TileEntityNB;
import com.mce.entity.tile.bombs.TileEntityUB;
import com.mce.entity.tile.bombs.TileEntityUN;
import com.mce.entity.tile.chest.TileEntityAcaciaChest;
import com.mce.entity.tile.chest.TileEntityBirchChest;
import com.mce.entity.tile.chest.TileEntityDarkMatterChest;
import com.mce.entity.tile.chest.TileEntityDarkOakChest;
import com.mce.entity.tile.chest.TileEntityEbonyChest;
import com.mce.entity.tile.chest.TileEntityGelidChest;
import com.mce.entity.tile.chest.TileEntityJungleChest;
import com.mce.entity.tile.chest.TileEntitySilkwoodChest;
import com.mce.entity.tile.chest.TileEntitySpruceChest;
import com.mce.entity.tile.chest.TileEntityWillowChest;
import com.mce.entity.tile.tech.TileEntityBFE;
import com.mce.entity.tile.tech.TileEntityBHG;
import com.mce.entity.tile.tech.TileEntityEnricher;
import com.mce.entity.tile.tech.TileEntityIndustrialCutter;
import com.mce.entity.tile.tech.TileEntityMagnetizer;
import com.mce.entity.tile.tech.TileEntityMatterCondenser;
import com.mce.entity.tile.tech.TileEntitySmelter;
import com.mce.entity.tile.tech.TileEntityTankCompressor;
import com.mce.entity.tile.tech.TileEntityWelder;
import com.mce.entity.tile.tech.TileEntityZNG;
import com.mce.entity.tile.wall_parts.TileEntityWPAcacia;
import com.mce.entity.tile.wall_parts.TileEntityWPBirch;
import com.mce.entity.tile.wall_parts.TileEntityWPBrick;
import com.mce.entity.tile.wall_parts.TileEntityWPCement;
import com.mce.entity.tile.wall_parts.TileEntityWPCementBrick;
import com.mce.entity.tile.wall_parts.TileEntityWPCoal;
import com.mce.entity.tile.wall_parts.TileEntityWPDarkOak;
import com.mce.entity.tile.wall_parts.TileEntityWPEbony;
import com.mce.entity.tile.wall_parts.TileEntityWPGelid;
import com.mce.entity.tile.wall_parts.TileEntityWPJungle;
import com.mce.entity.tile.wall_parts.TileEntityWPMossyCobblestone;
import com.mce.entity.tile.wall_parts.TileEntityWPNetherBrick;
import com.mce.entity.tile.wall_parts.TileEntityWPOak;
import com.mce.entity.tile.wall_parts.TileEntityWPObsidian;
import com.mce.entity.tile.wall_parts.TileEntityWPQuartz;
import com.mce.entity.tile.wall_parts.TileEntityWPSandstone;
import com.mce.entity.tile.wall_parts.TileEntityWPSilkwood;
import com.mce.entity.tile.wall_parts.TileEntityWPSnow;
import com.mce.entity.tile.wall_parts.TileEntityWPSpruce;
import com.mce.entity.tile.wall_parts.TileEntityWPStone;
import com.mce.entity.tile.wall_parts.TileEntityWPStonebrick;
import com.mce.entity.tile.wall_parts.TileEntityWPWillow;

import cpw.mods.fml.common.registry.GameRegistry;

public class TileEntityRegistry {
	public static void tileEntityRegistry() {
		// Bombs
		GameRegistry.registerTileEntity(TileEntityEB.class, "EB");
		GameRegistry.registerTileEntity(TileEntityNB.class, "NB");
		GameRegistry.registerTileEntity(TileEntityUB.class, "UB");
		GameRegistry.registerTileEntity(TileEntityUN.class, "UN");
		GameRegistry.registerTileEntity(TileEntityDM.class, "DM");

		// Tech
		GameRegistry.registerTileEntity(TileEntitySmelter.class, "te_smelter");
		GameRegistry.registerTileEntity(TileEntityIndustrialCutter.class, "te_ic");
		GameRegistry.registerTileEntity(TileEntityMatterCondenser.class, "te_mc");
		GameRegistry.registerTileEntity(TileEntityZNG.class, "te_zng");
		GameRegistry.registerTileEntity(TileEntityBFE.class, "te_bfe");
		GameRegistry.registerTileEntity(TileEntityTankCompressor.class, "te_tc");
		GameRegistry.registerTileEntity(TEAH.class, "te_ah");
		GameRegistry.registerTileEntity(TileEntityBHG.class, "te_bhg");
		GameRegistry.registerTileEntity(TileEntityWelder.class, "te_welder");
		GameRegistry.registerTileEntity(TileEntityEnricher.class, "te_enricher");
		GameRegistry.registerTileEntity(TileEntityMagnetizer.class, "te_magnetizer");

		// Chests
		GameRegistry.registerTileEntity(TileEntityBirchChest.class, "te_birchchest");
		GameRegistry.registerTileEntity(TileEntitySpruceChest.class, "te_sprucechest");
		GameRegistry.registerTileEntity(TileEntityJungleChest.class, "te_junglechest");
		GameRegistry.registerTileEntity(TileEntityWillowChest.class, "te_willowchest");
		GameRegistry.registerTileEntity(TileEntityEbonyChest.class, "te_ebonychest");
		GameRegistry.registerTileEntity(TileEntitySilkwoodChest.class, "te_silkwoodchest");
		GameRegistry.registerTileEntity(TileEntityAcaciaChest.class, "te_acaciachest");
		GameRegistry.registerTileEntity(TileEntityDarkOakChest.class, "te_darkoakchest");
		GameRegistry.registerTileEntity(TileEntityGelidChest.class, "te_gelidchest");
		GameRegistry.registerTileEntity(TileEntityDarkMatterChest.class, "te_darkmatterchest");

		// Other
		GameRegistry.registerTileEntity(TECT.class, "te_craft");
		GameRegistry.registerTileEntity(TileEntityTF.class, "te_tf");
		GameRegistry.registerTileEntity(TileEntityECR.class, "te_ecr");
		GameRegistry.registerTileEntity(TileEntityUCR.class, "te_ucr");
		GameRegistry.registerTileEntity(TileEntityNCR.class, "te_ncr");
		GameRegistry.registerTileEntity(TileEntitySR.class, "te_sr");
		GameRegistry.registerTileEntity(TileEntityZimWire.class, "te_zw");

		// Wall Parts
		GameRegistry.registerTileEntity(TileEntityWPOak.class, "te_wp-oak");
		GameRegistry.registerTileEntity(TileEntityWPBirch.class, "te_wp-birch");
		GameRegistry.registerTileEntity(TileEntityWPSpruce.class, "te_wp-spruce");
		GameRegistry.registerTileEntity(TileEntityWPJungle.class, "te_wp-jungle");
		GameRegistry.registerTileEntity(TileEntityWPDarkOak.class, "te_wp-darkoak");
		GameRegistry.registerTileEntity(TileEntityWPAcacia.class, "te_wp-acacia");
		GameRegistry.registerTileEntity(TileEntityWPWillow.class, "te_wp-willow");
		GameRegistry.registerTileEntity(TileEntityWPEbony.class, "te_wp-ebony");
		GameRegistry.registerTileEntity(TileEntityWPSilkwood.class, "te_wp-silkwood");
		GameRegistry.registerTileEntity(TileEntityWPGelid.class, "te_wp-gelid");
		GameRegistry.registerTileEntity(TileEntityWPCement.class, "te_wp-cement");
		GameRegistry.registerTileEntity(TileEntityWPCementBrick.class, "te_wp-cementbrick");
		GameRegistry.registerTileEntity(TileEntityWPStone.class, "te_wp-stone");
		GameRegistry.registerTileEntity(TileEntityWPStonebrick.class, "te_wp-stonebirck");
		GameRegistry.registerTileEntity(TileEntityWPBrick.class, "te_wp-brick");
		GameRegistry.registerTileEntity(TileEntityWPNetherBrick.class, "te_wp-netherbrick");
		GameRegistry.registerTileEntity(TileEntityWPSandstone.class, "te_wp-sandstone");
		GameRegistry.registerTileEntity(TileEntityWPObsidian.class, "te_wp-obsidian");
		GameRegistry.registerTileEntity(TileEntityWPSnow.class, "te_wp-snow");
		GameRegistry.registerTileEntity(TileEntityWPMossyCobblestone.class, "te_wp-mossycobble");
		GameRegistry.registerTileEntity(TileEntityWPCoal.class, "te_wp-coal");
		GameRegistry.registerTileEntity(TileEntityWPQuartz.class, "te_wp-quartz");
	}
}