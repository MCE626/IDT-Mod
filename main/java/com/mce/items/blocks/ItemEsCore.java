package com.mce.items.blocks;

import com.mce.common.mod_IDT;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemEsCore extends Item {
	public ItemEsCore() {
	}

	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int meta, float dx,
			float dy, float dz) {
		Block block = null;
		block = mod_IDT.EinsteiniumCoreReactor;

		if (player.canPlayerEdit(x, y, z, meta, stack) && player.canPlayerEdit(x, y + 1, z, meta, stack)) {
			if (!block.canPlaceBlockAt(world, x, y, z)) {
				return false;
			} else {
				int i1 = MathHelper.floor_double((double) ((player.rotationYaw + 180.0F) * 4.0F / 360.0F) - 0.5D) & 3;
				placeBlock(world, x, y, z, i1, block);
				--stack.stackSize;
				return true;
			}
		} else {
			return false;
		}
	}

	public static void placeBlock(World world, int x, int y, int z, int meta, Block block) {
		byte b0 = 0;
		byte b1 = 0;

		if (meta == 0) {
			b1 = 1;
		}

		if (meta == 1) {
			b0 = -1;
		}

		if (meta == 2) {
			b1 = -1;
		}

		if (meta == 3) {
			b0 = 1;
		}

		int i1 = (world.getBlock(x - b0, y, z - b1).isNormalCube() ? 1 : 0)
				+ (world.getBlock(x - b0, y + 1, z - b1).isNormalCube() ? 1 : 0);
		int j1 = (world.getBlock(x + b0, y, z + b1).isNormalCube() ? 1 : 0)
				+ (world.getBlock(x + b0, y + 1, z + b1).isNormalCube() ? 1 : 0);
		boolean flag = world.getBlock(x - b0, y, z - b1) == block || world.getBlock(x - b0, y + 1, z - b1) == block;
		boolean flag1 = world.getBlock(x + b0, y, z + b1) == block || world.getBlock(x + b0, y + 1, z + b1) == block;
		boolean flag2 = false;

		if (flag && !flag1) {
			flag2 = true;
		} else if (j1 > i1) {
			flag2 = true;
		}

		world.setBlock(x, y, z, block, meta, 2);
		world.setBlock(x, y + 1, z, block, 8 | (flag2 ? 1 : 0), 2);
		world.notifyBlocksOfNeighborChange(x, y, z, block);
		world.notifyBlocksOfNeighborChange(x, y + 1, z, block);
	}
}
