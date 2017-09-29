package com.mce.handlers.dimensions.chunks.managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.mce.handlers.dimensions.layers.FrostGenLayer;
import com.mce.handlers.registers.BiomeRegistry;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeCache;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.WorldTypeEvent;

public class WorldChunkManagerFrost extends WorldChunkManager {
	public static ArrayList<BiomeGenBase> allowBiomes = new ArrayList<BiomeGenBase>(
			Arrays.asList(BiomeRegistry.FrostBiome, BiomeRegistry.FrostBarren, BiomeRegistry.FrostForest));
	private GenLayer genBiomes;
	private GenLayer biomeIndexLayer;
	private BiomeCache biomeCache;
	private List<BiomeGenBase> biomesToSpawnIn;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected WorldChunkManagerFrost() {
		this.biomeCache = new BiomeCache(this);
		this.biomesToSpawnIn = new ArrayList();
		this.biomesToSpawnIn.addAll(allowBiomes);
	}

	public WorldChunkManagerFrost(long seed, WorldType worldType) {
		this();
		GenLayer[] agenlayer = FrostGenLayer.makeTheWorld(seed, worldType);
		agenlayer = getModdedBiomeGenerators(worldType, seed, agenlayer);
		this.genBiomes = agenlayer[0];
		this.biomeIndexLayer = agenlayer[1];
	}

	public WorldChunkManagerFrost(World world) {
		this(world.getSeed(), world.getWorldInfo().getTerrainType());
	}

	/**
	 * Gets the list of valid biomes for the player to spawn in.
	 */
	@Override
	public List<BiomeGenBase> getBiomesToSpawnIn() {
		return this.biomesToSpawnIn;
	}

	/**
	 * Returns the BiomeGenBase related to the x, z position on the world.
	 */
	@Override
	public BiomeGenBase getBiomeGenAt(int x, int z) {
		return this.biomeCache.getBiomeGenAt(x, z);
	}

	/**
	 * Returns a list of rainfall values for the specified blocks. Args:
	 * listToReuse, x, z, width, length.
	 */
	@Override
	public float[] getRainfall(float[] listToReuse, int x, int z, int width, int length) {
		IntCache.resetIntCache();

		if (listToReuse == null || listToReuse.length < width * length) {
			listToReuse = new float[width * length];
		}

		int[] aint = this.biomeIndexLayer.getInts(x, z, width, length);

		for (int i1 = 0; i1 < width * length; ++i1) {
			try {
				float f = (float) BiomeGenBase.getBiome(aint[i1]).getIntRainfall() / 65536.0F;

				if (f > 1.0F) {
					f = 1.0F;
				}

				listToReuse[i1] = f;
			} catch (Throwable throwable) {
				CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Invalid Biome id");
				CrashReportCategory crashreportcategory = crashreport.makeCategory("DownfallBlock");
				crashreportcategory.addCrashSection("biome id", Integer.valueOf(i1));
				crashreportcategory.addCrashSection("downfalls[] size", Integer.valueOf(listToReuse.length));
				crashreportcategory.addCrashSection("x", Integer.valueOf(x));
				crashreportcategory.addCrashSection("z", Integer.valueOf(z));
				crashreportcategory.addCrashSection("w", Integer.valueOf(width));
				crashreportcategory.addCrashSection("h", Integer.valueOf(length));
				throw new ReportedException(crashreport);
			}
		}

		return listToReuse;
	}

	/**
	 * Return an adjusted version of a given temperature based on the y height
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public float getTemperatureAtHeight(float par1, int par2) {
		return par1;
	}

	/**
	 * Returns an array of biomes for the location input.
	 */
	@Override
	public BiomeGenBase[] getBiomesForGeneration(BiomeGenBase[] biomes, int x, int z, int w, int h) {
		IntCache.resetIntCache();

		if (biomes == null || biomes.length < w * h) {
			biomes = new BiomeGenBase[w * h];
		}

		int[] aint = this.genBiomes.getInts(x, z, w, h);

		try {
			for (int i1 = 0; i1 < w * h; ++i1) {
				biomes[i1] = BiomeGenBase.getBiome(aint[i1]);
			}

			return biomes;
		} catch (Throwable throwable) {
			CrashReport cr = CrashReport.makeCrashReport(throwable, "Invalid Biome id");
			CrashReportCategory crc = cr.makeCategory("RawBiomeBlock");
			crc.addCrashSection("biomes[] size", Integer.valueOf(biomes.length));
			crc.addCrashSection("x", Integer.valueOf(x));
			crc.addCrashSection("z", Integer.valueOf(z));
			crc.addCrashSection("w", Integer.valueOf(w));
			crc.addCrashSection("h", Integer.valueOf(h));
			throw new ReportedException(cr);
		}
	}

	/**
	 * Returns biomes to use for the blocks and loads the other data like
	 * temperature and humidity onto the WorldChunkManager Args: oldBiomeList,
	 * x, z, width, depth
	 */
	public BiomeGenBase[] loadBlockGeneratorData(BiomeGenBase[] oldBiomeList, int x, int z, int width, int depth) {
		return this.getBiomeGenAt(oldBiomeList, x, z, width, depth, true);
	}

	/**
	 * Return a list of biomes for the specified blocks. Args: listToReuse, x,
	 * y, width, length, cacheFlag (if false, don't check biomeCache to avoid
	 * infinite loop in BiomeCacheBlock)
	 */
	@Override
	public BiomeGenBase[] getBiomeGenAt(BiomeGenBase[] listToReuse, int x, int z, int w, int h, boolean cacheFlag) {
		IntCache.resetIntCache();

		if (listToReuse == null || listToReuse.length < w * h) {
			listToReuse = new BiomeGenBase[w * h];
		}

		if (cacheFlag && w == 16 && h == 16 && (x & 15) == 0 && (z & 15) == 0) {
			BiomeGenBase[] biomeBase = this.biomeCache.getCachedBiomes(x, z);
			System.arraycopy(biomeBase, 0, listToReuse, 0, w * h);
			return listToReuse;
		} else {
			int[] aint = this.biomeIndexLayer.getInts(x, z, w, h);

			for (int i = 0; i < w * h; ++i) {
				listToReuse[i] = BiomeGenBase.getBiome(aint[i]);
			}
			return listToReuse;
		}
	}

	/**
	 * checks given Chunk's Biomes against List of allowed ones
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public boolean areBiomesViable(int x, int z, int radius, List list) {
		IntCache.resetIntCache();
		int l = x - radius >> 2;
		int i1 = z - radius >> 2;
		int j1 = x + radius >> 2;
		int k1 = z + radius >> 2;
		int l1 = j1 - l + 1;
		int i2 = k1 - i1 + 1;
		int[] aint = this.genBiomes.getInts(l, i1, l1, i2);

		try {
			for (int j2 = 0; j2 < l1 * i2; ++j2) {
				BiomeGenBase biomegenbase = BiomeGenBase.getBiome(aint[j2]);

				if (!list.contains(biomegenbase)) {
					return false;
				}
			}

			return true;
		} catch (Throwable throwable) {
			CrashReport cr = CrashReport.makeCrashReport(throwable, "Invalid Biome id");
			CrashReportCategory crc = cr.makeCategory("Layer");
			crc.addCrashSection("Layer", this.genBiomes.toString());
			crc.addCrashSection("x", Integer.valueOf(x));
			crc.addCrashSection("z", Integer.valueOf(z));
			crc.addCrashSection("radius", Integer.valueOf(radius));
			crc.addCrashSection("allowed", list);
			throw new ReportedException(cr);
		}
	}

	/**
	 * Finds a valid position within a range, that is in one of the listed
	 * biomes. Searches {par1,par2} +-par3 blocks. Strongly favors positive y
	 * positions.
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public ChunkPosition findBiomePosition(int x, int z, int radius, List list, Random random) {
		IntCache.resetIntCache();
		int l = x - radius >> 2;
		int i1 = z - radius >> 2;
		int j1 = x + radius >> 2;
		int k1 = z + radius >> 2;
		int l1 = j1 - l + 1;
		int i2 = k1 - i1 + 1;
		int[] aint = this.genBiomes.getInts(l, i1, l1, i2);
		ChunkPosition chunkposition = null;
		int j2 = 0;

		for (int k2 = 0; k2 < l1 * i2; ++k2) {
			int l2 = l + k2 % l1 << 2;
			int i3 = i1 + k2 / l1 << 2;
			BiomeGenBase biomegenbase = BiomeGenBase.getBiome(aint[k2]);

			if (list.contains(biomegenbase) && (chunkposition == null || random.nextInt(j2 + 1) == 0)) {
				chunkposition = new ChunkPosition(l2, 0, i3);
				++j2;
			}
		}

		return chunkposition;
	}

	/**
	 * Calls the WorldChunkManager's biomeCache.cleanupCache()
	 */
	@Override
	public void cleanupCache() {
		this.biomeCache.cleanupCache();
	}

	public GenLayer[] getModdedBiomeGenerators(WorldType worldType, long seed, GenLayer[] original) {
		WorldTypeEvent.InitBiomeGens event = new WorldTypeEvent.InitBiomeGens(worldType, seed, original);
		MinecraftForge.TERRAIN_GEN_BUS.post(event);
		return event.newBiomeGens;
	}
}