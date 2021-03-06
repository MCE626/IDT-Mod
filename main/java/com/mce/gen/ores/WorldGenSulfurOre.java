package com.mce.gen.ores;

import java.util.Random;

import com.mce.common.mod_IDT;

import cpw.mods.fml.common.IWorldGenerator;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;

public class WorldGenSulfurOre implements IWorldGenerator {
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
			IChunkProvider chunkProvider) {
		switch (world.provider.dimensionId) {
		case 1:
			generateNether(world, random, chunkX * 16, chunkZ * 16);
		case 0:
			generateSurface(world, random, chunkX * 16, chunkZ * 16);
		case -1:
			generateEnd(world, random, chunkX * 16, chunkZ * 16);
		}
	}

	private void generateEnd(World world, Random random, int chunkX, int chunkZ) {
	}

	private void generateSurface(World world, Random random, int chunkX, int chunkZ) {
		for (int i = 0; i < 8; i++) {
			int xCoord = chunkX + random.nextInt(16);
			int yCoord = random.nextInt(60);
			int zCoord = chunkZ + random.nextInt(16);
			new WorldGenMinable(mod_IDT.SulfurOre, 3).generate(world, random, xCoord, yCoord, zCoord);
		}
	}

	private void generateNether(World world, Random random, int chunkX, int chunkZ) {
	}
}
