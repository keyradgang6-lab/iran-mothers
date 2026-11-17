package com.iranituber.world.tehran;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

/**
 * بوم نقاشی شهر: مختصات محلی (x شرق، z جنوب، y بالا؛ y=0 همون سطح زمین شهره)
 * رو به مختصات واقعی دنیا تبدیل می‌کنه و بلوک می‌ذاره.
 */
public final class Canvas {
	public final ServerWorld world;
	public final int ox;
	public final int gy;
	public final int oz;
	private final BlockPos.Mutable cursor = new BlockPos.Mutable();

	public Canvas(ServerWorld world, int ox, int gy, int oz) {
		this.world = world;
		this.ox = ox;
		this.gy = gy;
		this.oz = oz;
	}

	public void set(int x, int y, int z, BlockState state) {
		cursor.set(ox + x, gy + y, oz + z);
		world.setBlockState(cursor, state, Block.NOTIFY_LISTENERS);
	}

	public void set(int x, int y, int z, Block block) {
		set(x, y, z, block.getDefaultState());
	}

	public BlockState get(int x, int y, int z) {
		cursor.set(ox + x, gy + y, oz + z);
		return world.getBlockState(cursor);
	}

	public void fill(int x1, int y1, int z1, int x2, int y2, int z2, Block block) {
		BlockState state = block.getDefaultState();
		for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
			for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
				for (int z = Math.min(z1, z2); z <= Math.max(z1, z2); z++) {
					set(x, y, z, state);
				}
			}
		}
	}

	/** جعبه‌ی توخالی: فقط دیواره‌ها. */
	public void shell(int x1, int y1, int z1, int x2, int y2, int z2, Block block) {
		BlockState state = block.getDefaultState();
		for (int x = x1; x <= x2; x++) {
			for (int y = y1; y <= y2; y++) {
				for (int z = z1; z <= z2; z++) {
					if (x == x1 || x == x2 || z == z1 || z == z2) {
						set(x, y, z, state);
					}
				}
			}
		}
	}

	/** یه لایه‌ی دایره‌ای (پر). */
	public void disc(int cx, int y, int cz, double r, Block block) {
		BlockState state = block.getDefaultState();
		int ri = (int) Math.ceil(r);
		for (int x = cx - ri; x <= cx + ri; x++) {
			for (int z = cz - ri; z <= cz + ri; z++) {
				double dx = x - cx;
				double dz = z - cz;
				if (dx * dx + dz * dz <= r * r) {
					set(x, y, z, state);
				}
			}
		}
	}

	/** حلقه‌ی دایره‌ای (فقط لبه). */
	public void ring(int cx, int y, int cz, double r, Block block) {
		BlockState state = block.getDefaultState();
		int ri = (int) Math.ceil(r) + 1;
		for (int x = cx - ri; x <= cx + ri; x++) {
			for (int z = cz - ri; z <= cz + ri; z++) {
				double d = Math.sqrt((x - cx) * (x - cx) + (z - cz) * (z - cz));
				if (d <= r && d > r - 1.2) {
					set(x, y, z, state);
				}
			}
		}
	}

	/** گنبد نیم‌کره (توپر). */
	public void dome(int cx, int y0, int cz, int r, Block block) {
		for (int dy = 0; dy <= r; dy++) {
			double layer = Math.sqrt(Math.max(0, r * r - dy * dy));
			disc(cx, y0 + dy, cz, layer + 0.4, block);
		}
	}

	public void stairs(int x, int y, int z, Block block, Direction facing) {
		set(x, y, z, block.getDefaultState().with(StairsBlock.FACING, facing));
	}

	/** درخت چنار: تنه‌ی روشن و تاج پرپشت (برگ‌ها نمی‌ریزن). */
	public void tree(int x, int z, int trunk, Block log) {
		for (int y = 1; y <= trunk; y++) {
			set(x, y, z, log);
		}
		BlockState leaves = Blocks.OAK_LEAVES.getDefaultState().with(LeavesBlock.PERSISTENT, true);
		for (int dx = -2; dx <= 2; dx++) {
			for (int dz = -2; dz <= 2; dz++) {
				for (int dy = 0; dy <= 3; dy++) {
					double d = Math.sqrt(dx * dx + dz * dz + (dy - 1.2) * (dy - 1.2));
					if (d <= 2.6 && !(dx == 0 && dz == 0 && dy == 0)) {
						if (get(x + dx, trunk - 1 + dy, z + dz).isAir()) {
							set(x + dx, trunk - 1 + dy, z + dz, leaves);
						}
					}
				}
			}
		}
	}

	public void lamp(int x, int z) {
		fill(x, 1, z, x, 4, z, Blocks.COBBLESTONE_WALL);
		set(x, 5, z, Blocks.SEA_LANTERN);
	}

	public BlockPos worldPos(int x, int y, int z) {
		return new BlockPos(ox + x, gy + y, oz + z);
	}
}
