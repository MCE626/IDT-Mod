package com.mce.handlers.dimensions.teleporters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.mce.common.mod_IDT;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Direction;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class TeleportFrost extends Teleporter{
	private final WorldServer worldServerInstance;
	/** A private Random() function in Teleporter */
	private final Random random;
	/** Stores successful portal placement locations for rapid lookup. */
	private final LongHashMap destinationCoordinateCache = new LongHashMap();
	/**
	 * A list of valid keys for the destinationCoordainteCache. These are based
	 * on the X & Z of the players initial location.
	 */
	private final List destinationCoordinateKeys = new ArrayList();
	private static final String __OBFID = "CL_00000153";

	public TeleportFrost(WorldServer server) {
		super(server);
		this.worldServerInstance = server;
		this.random = new Random(server.getSeed());
	}

	/**
	 * Place an entity in a nearby portal, creating one if necessary.
	 */
	public void placeInPortal(Entity entity, double x, double y, double z, float f) {
		if (this.worldServerInstance.provider.dimensionId != 1) {
			if (!this.placeInExistingPortal(entity, x, y, z, f)) {
				this.makePortal(entity);
				this.placeInExistingPortal(entity, x, y, z, f);
			}
		} else {
			int i = MathHelper.floor_double(entity.posX);
			int j = MathHelper.floor_double(entity.posY) - 1;
			int k = MathHelper.floor_double(entity.posZ);
			byte b0 = 1;
			byte b1 = 0;

			for (int l = -2; l <= 2; ++l) {
				for (int i1 = -2; i1 <= 2; ++i1) {
					for (int j1 = -1; j1 < 3; ++j1) {
						int k1 = i + i1 * b0 + l * b1;
						int l1 = j + j1;
						int i2 = k + i1 * b1 - l * b0;
						boolean flag = j1 < 0;
						this.worldServerInstance.setBlock(k1, l1, i2, flag ? Blocks.snow : Blocks.air);
					}
				}
			}

			entity.setLocationAndAngles((double) i, (double) j, (double) k, entity.rotationYaw, 0.0F);
			entity.motionX = entity.motionY = entity.motionZ = 0.0D;
		}
	}

	/**
	 * Place an entity in a nearby portal which already exists.
	 */
	public boolean placeInExistingPortal(Entity entity, double x, double y, double z, float f) {
		short short1 = 128;
		double d3 = -1.0D;
		int i = 0;
		int j = 0;
		int k = 0;
		int l = MathHelper.floor_double(entity.posX);
		int i1 = MathHelper.floor_double(entity.posZ);
		long j1 = ChunkCoordIntPair.chunkXZ2Int(l, i1);
		boolean flag = true;
		double d7;
		int l3;

		if (this.destinationCoordinateCache.containsItem(j1)) {
			TeleportFrost.PortalPosition portalposition = (TeleportFrost.PortalPosition) this.destinationCoordinateCache
					.getValueByKey(j1);
			d3 = 0.0D;
			i = portalposition.posX;
			j = portalposition.posY;
			k = portalposition.posZ;
			portalposition.lastUpdateTime = this.worldServerInstance.getTotalWorldTime();
			flag = false;
		} else {
			for (l3 = l - short1; l3 <= l + short1; ++l3) {
				double d4 = (double) l3 + 0.5D - entity.posX;

				for (int l1 = i1 - short1; l1 <= i1 + short1; ++l1) {
					double d5 = (double) l1 + 0.5D - entity.posZ;

					for (int i2 = this.worldServerInstance.getActualHeight() - 1; i2 >= 0; --i2) {
						if (this.worldServerInstance.getBlock(l3, i2, l1) == mod_IDT.FrozenPortal) {
							while (this.worldServerInstance.getBlock(l3, i2 - 1, l1) == mod_IDT.FrozenPortal) {
								--i2;
							}

							d7 = (double) i2 + 0.5D - entity.posY;
							double d8 = d4 * d4 + d7 * d7 + d5 * d5;

							if (d3 < 0.0D || d8 < d3) {
								d3 = d8;
								i = l3;
								j = i2;
								k = l1;
							}
						}
					}
				}
			}
		}

		if (d3 >= 0.0D) {
			if (flag) {
				this.destinationCoordinateCache.add(j1,
						new TeleportFrost.PortalPosition(i, j, k, this.worldServerInstance.getTotalWorldTime()));
				this.destinationCoordinateKeys.add(Long.valueOf(j1));
			}

			double d11 = (double) i + 0.5D;
			double d6 = (double) j + 0.5D;
			d7 = (double) k + 0.5D;
			int i4 = -1;

			if (this.worldServerInstance.getBlock(i - 1, j, k) == mod_IDT.FrozenPortal) {
				i4 = 2;
			}

			if (this.worldServerInstance.getBlock(i + 1, j, k) == mod_IDT.FrozenPortal) {
				i4 = 0;
			}

			if (this.worldServerInstance.getBlock(i, j, k - 1) == mod_IDT.FrozenPortal) {
				i4 = 3;
			}

			if (this.worldServerInstance.getBlock(i, j, k + 1) == mod_IDT.FrozenPortal) {
				i4 = 1;
			}

			int j2 = entity.getTeleportDirection();

			if (i4 > -1) {
				int k2 = Direction.rotateLeft[i4];
				int l2 = Direction.offsetX[i4];
				int i3 = Direction.offsetZ[i4];
				int j3 = Direction.offsetX[k2];
				int k3 = Direction.offsetZ[k2];
				boolean flag1 = !this.worldServerInstance.isAirBlock(i + l2 + j3, j, k + i3 + k3)
						|| !this.worldServerInstance.isAirBlock(i + l2 + j3, j + 1, k + i3 + k3);
				boolean flag2 = !this.worldServerInstance.isAirBlock(i + l2, j, k + i3)
						|| !this.worldServerInstance.isAirBlock(i + l2, j + 1, k + i3);

				if (flag1 && flag2) {
					i4 = Direction.rotateOpposite[i4];
					k2 = Direction.rotateOpposite[k2];
					l2 = Direction.offsetX[i4];
					i3 = Direction.offsetZ[i4];
					j3 = Direction.offsetX[k2];
					k3 = Direction.offsetZ[k2];
					l3 = i - j3;
					d11 -= (double) j3;
					int k1 = k - k3;
					d7 -= (double) k3;
					flag1 = !this.worldServerInstance.isAirBlock(l3 + l2 + j3, j, k1 + i3 + k3)
							|| !this.worldServerInstance.isAirBlock(l3 + l2 + j3, j + 1, k1 + i3 + k3);
					flag2 = !this.worldServerInstance.isAirBlock(l3 + l2, j, k1 + i3)
							|| !this.worldServerInstance.isAirBlock(l3 + l2, j + 1, k1 + i3);
				}

				float f1 = 0.5F;
				float f2 = 0.5F;

				if (!flag1 && flag2) {
					f1 = 1.0F;
				} else if (flag1 && !flag2) {
					f1 = 0.0F;
				} else if (flag1 && flag2) {
					f2 = 0.0F;
				}

				d11 += (double) ((float) j3 * f1 + f2 * (float) l2);
				d7 += (double) ((float) k3 * f1 + f2 * (float) i3);
				float f3 = 0.0F;
				float f4 = 0.0F;
				float f5 = 0.0F;
				float f6 = 0.0F;

				if (i4 == j2) {
					f3 = 1.0F;
					f4 = 1.0F;
				} else if (i4 == Direction.rotateOpposite[j2]) {
					f3 = -1.0F;
					f4 = -1.0F;
				} else if (i4 == Direction.rotateRight[j2]) {
					f5 = 1.0F;
					f6 = -1.0F;
				} else {
					f5 = -1.0F;
					f6 = 1.0F;
				}

				double d9 = entity.motionX;
				double d10 = entity.motionZ;
				entity.motionX = d9 * (double) f3 + d10 * (double) f6;
				entity.motionZ = d9 * (double) f5 + d10 * (double) f4;
				entity.rotationYaw = f - (float) (j2 * 90) + (float) (i4 * 90);
			} else {
				entity.motionX = entity.motionY = entity.motionZ = 0.0D;
			}

			entity.setLocationAndAngles(d11, d6, d7, entity.rotationYaw, entity.rotationPitch);
			return true;
		} else {
			return false;
		}
	}

	public boolean makePortal(Entity entity) {
		byte b0 = 16;
		double d0 = -1.0D;
		int i = MathHelper.floor_double(entity.posX);
		int j = MathHelper.floor_double(entity.posY);
		int k = MathHelper.floor_double(entity.posZ);
		int l = i;
		int i1 = j;
		int j1 = k;
		int k1 = 0;
		int l1 = this.random.nextInt(4);
		int i2, i3, j3, k3, l3, i4, j4, k4, l4, i5, k2;
		double d1, d2, d3, d4;

		for (i2 = i - b0; i2 <= i + b0; ++i2) {
			d1 = (double) i2 + 0.5D - entity.posX;

			for (k2 = k - b0; k2 <= k + b0; ++k2) {
				d2 = (double) k2 + 0.5D - entity.posZ;
				label274:

				for (i3 = this.worldServerInstance.getActualHeight() - 1; i3 >= 0; --i3) {
					if (this.worldServerInstance.isAirBlock(i2, i3, k2)) {
						while (i3 > 0 && this.worldServerInstance.isAirBlock(i2, i3 - 1, k2)) {
							--i3;
						}

						for (j3 = l1; j3 < l1 + 4; ++j3) {
							k3 = j3 % 2;
							l3 = 1 - k3;

							if (j3 % 4 >= 2) {
								k3 = -k3;
								l3 = -l3;
							}

							for (i4 = 0; i4 < 3; ++i4) {
								for (j4 = 0; j4 < 4; ++j4) {
									for (k4 = -1; k4 < 4; ++k4) {
										l4 = i2 + (j4 - 1) * k3 + i4 * l3;
										i5 = i3 + k4;
										int j5 = k2 + (j4 - 1) * l3 - i4 * k3;

										if (k4 < 0
												&& !this.worldServerInstance.getBlock(l4, i5, j5).getMaterial()
														.isSolid()
												|| k4 >= 0 && !this.worldServerInstance.isAirBlock(l4, i5, j5)) {
											continue label274;
										}
									}
								}
							}

							d3 = (double) i3 + 0.5D - entity.posY;
							d4 = d1 * d1 + d3 * d3 + d2 * d2;

							if (d0 < 0.0D || d4 < d0) {
								d0 = d4;
								l = i2;
								i1 = i3;
								j1 = k2;
								k1 = j3 % 4;
							}
						}
					}
				}
			}
		}

		if (d0 < 0.0D) {
			for (i2 = i - b0; i2 <= i + b0; ++i2) {
				d1 = (double) i2 + 0.5D - entity.posX;

				for (k2 = k - b0; k2 <= k + b0; ++k2) {
					d2 = (double) k2 + 0.5D - entity.posZ;
					label222:

					for (i3 = this.worldServerInstance.getActualHeight() - 1; i3 >= 0; --i3) {
						if (this.worldServerInstance.isAirBlock(i2, i3, k2)) {
							while (i3 > 0 && this.worldServerInstance.isAirBlock(i2, i3 - 1, k2)) {
								--i3;
							}

							for (j3 = l1; j3 < l1 + 2; ++j3) {
								k3 = j3 % 2;
								l3 = 1 - k3;

								for (i4 = 0; i4 < 4; ++i4) {
									for (j4 = -1; j4 < 4; ++j4) {
										k4 = i2 + (i4 - 1) * k3;
										l4 = i3 + j4;
										i5 = k2 + (i4 - 1) * l3;

										if (j4 < 0
												&& !this.worldServerInstance.getBlock(k4, l4, i5).getMaterial()
														.isSolid()
												|| j4 >= 0 && !this.worldServerInstance.isAirBlock(k4, l4, i5)) {
											continue label222;
										}
									}
								}

								d3 = (double) i3 + 0.5D - entity.posY;
								d4 = d1 * d1 + d3 * d3 + d2 * d2;

								if (d0 < 0.0D || d4 < d0) {
									d0 = d4;
									l = i2;
									i1 = i3;
									j1 = k2;
									k1 = j3 % 2;
								}
							}
						}
					}
				}
			}
		}

		int k5 = l;
		int j2 = i1;
		k2 = j1;
		int l5 = k1 % 2;
		int l2 = 1 - l5;

		if (k1 % 4 >= 2) {
			l5 = -l5;
			l2 = -l2;
		}

		boolean flag;

		if (d0 < 0.0D) {
			if (i1 < 70) {
				i1 = 70;
			}

			if (i1 > this.worldServerInstance.getActualHeight() - 10) {
				i1 = this.worldServerInstance.getActualHeight() - 10;
			}

			j2 = i1;

			for (i3 = -1; i3 <= 1; ++i3) {
				for (j3 = 1; j3 < 3; ++j3) {
					for (k3 = -1; k3 < 3; ++k3) {
						l3 = k5 + (j3 - 1) * l5 + i3 * l2;
						i4 = j2 + k3;
						j4 = k2 + (j3 - 1) * l2 - i3 * l5;
						flag = k3 < 0;
						this.worldServerInstance.setBlock(l3, i4, j4, flag ? Blocks.snow : Blocks.air);
					}
				}
			}
		}

		for (i3 = 0; i3 < 4; ++i3) {
			for (j3 = 0; j3 < 4; ++j3) {
				for (k3 = -1; k3 < 4; ++k3) {
					l3 = k5 + (j3 - 1) * l5;
					i4 = j2 + k3;
					j4 = k2 + (j3 - 1) * l2;
					flag = j3 == 0 || j3 == 3 || k3 == -1 || k3 == 3;
					this.worldServerInstance.setBlock(l3, i4, j4, (Block) (flag ? Blocks.snow : mod_IDT.FrozenPortal),
							0, 2);
				}
			}

			for (j3 = 0; j3 < 4; ++j3) {
				for (k3 = -1; k3 < 4; ++k3) {
					l3 = k5 + (j3 - 1) * l5;
					i4 = j2 + k3;
					j4 = k2 + (j3 - 1) * l2;
					this.worldServerInstance.notifyBlocksOfNeighborChange(l3, i4, j4,
							this.worldServerInstance.getBlock(l3, i4, j4));
				}
			}
		}

		return true;
	}

	/**
	 * called periodically to remove out-of-date portal locations from the cache
	 * list. Argument par1 is a WorldServer.getTotalWorldTime() value.
	 */
	public void removeStalePortalLocations(long l) {
		if (l % 100L == 0L) {
			Iterator iterator = this.destinationCoordinateKeys.iterator();
			long j = l - 600L;

			while (iterator.hasNext()) {
				Long olong = (Long) iterator.next();
				TeleportFrost.PortalPosition portalposition = (TeleportFrost.PortalPosition) this.destinationCoordinateCache
						.getValueByKey(olong.longValue());

				if (portalposition == null || portalposition.lastUpdateTime < j) {
					iterator.remove();
					this.destinationCoordinateCache.remove(olong.longValue());
				}
			}
		}
	}

	public class PortalPosition extends ChunkCoordinates {
		/** The worldtime at which this PortalPosition was last verified */
		public long lastUpdateTime;
		private static final String __OBFID = "CL_00000154";

		public PortalPosition(int x, int y, int z, long l) {
			super(x, y, z);
			this.lastUpdateTime = l;
		}
	}
}