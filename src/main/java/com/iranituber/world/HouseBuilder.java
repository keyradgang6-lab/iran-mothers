package com.iranituber.world;

import com.iranituber.entity.TuberEntity;
import com.iranituber.registry.ModBlocks;
import com.iranituber.registry.ModEntities;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.enums.BedPart;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

/**
 * یه خونه‌ی کامل می‌سازه: دیوار آجری، پنجره، فرش، تخت، میز با کامپیوتر،
 * و دو شخصیت (یوتوبر معروف و دوستش) داخلش اسپاون می‌کنه.
 *
 * مختصات محلی: x از 0 تا 10، z از 0 تا 8. در روی دیوار x=0 (غرب) هست.
 */
public final class HouseBuilder {
	private HouseBuilder() {}

	private static final int SX = 10;
	private static final int SZ = 8;
	private static final int WALL_H = 4;

	/** floor = بلوکی که کف خونه می‌شه (زمینی که بازیکن روش وایساده). */
	public static void build(ServerWorld world, BlockPos floor) {
		int bx = floor.getX();
		int by = floor.getY();
		int bz = floor.getZ();

		// ۱) فضا رو خالی کن (درخت و علف و ...) تا خونه توی هوا گیر نکنه
		for (int x = -1; x <= SX + 1; x++) {
			for (int z = -1; z <= SZ + 1; z++) {
				for (int y = 1; y <= WALL_H + 4; y++) {
					set(world, bx + x, by + y, bz + z, Blocks.AIR.getDefaultState());
				}
			}
		}

		// ۲) پایه (اگه زمین گود بود پر می‌کنیم)
		for (int x = -1; x <= SX + 1; x++) {
			for (int z = -1; z <= SZ + 1; z++) {
				for (int d = 0; d <= 4; d++) {
					BlockPos p = new BlockPos(bx + x, by - d, bz + z);
					if (d == 0) {
						Block top = (x >= 0 && x <= SX && z >= 0 && z <= SZ) ? Blocks.SPRUCE_PLANKS : Blocks.STONE_BRICKS;
						world.setBlockState(p, top.getDefaultState());
					} else if (world.getBlockState(p).isReplaceable() || world.getBlockState(p).isAir()) {
						world.setBlockState(p, Blocks.COBBLESTONE.getDefaultState());
					}
				}
			}
		}

		// ۳) دیوارها
		for (int x = 0; x <= SX; x++) {
			for (int z = 0; z <= SZ; z++) {
				boolean edge = x == 0 || x == SX || z == 0 || z == SZ;
				if (!edge) {
					continue;
				}
				boolean corner = (x == 0 || x == SX) && (z == 0 || z == SZ);
				for (int y = 1; y <= WALL_H; y++) {
					Block block = corner ? Blocks.OAK_LOG : Blocks.STONE_BRICKS;
					if (!corner && (y == 2 || y == 3) && isWindow(x, z)) {
						block = Blocks.GLASS;
					}
					set(world, bx + x, by + y, bz + z, block.getDefaultState());
				}
			}
		}

		// ۴) در ورودی (دو بلوک خالی توی دیوار غربی)
		set(world, bx, by + 1, bz + 4, Blocks.AIR.getDefaultState());
		set(world, bx, by + 2, bz + 4, Blocks.AIR.getDefaultState());

		// ۵) سقف
		for (int x = -1; x <= SX + 1; x++) {
			for (int z = -1; z <= SZ + 1; z++) {
				boolean inside = x >= 0 && x <= SX && z >= 0 && z <= SZ;
				BlockState s = inside ? Blocks.SPRUCE_PLANKS.getDefaultState() : Blocks.SPRUCE_SLAB.getDefaultState();
				set(world, bx + x, by + WALL_H + 1, bz + z, s);
			}
		}
		// نورگیر
		set(world, bx + 3, by + WALL_H + 1, bz + 4, Blocks.SEA_LANTERN.getDefaultState());
		set(world, bx + 7, by + WALL_H + 1, bz + 4, Blocks.SEA_LANTERN.getDefaultState());
		set(world, bx + 5, by + WALL_H + 1, bz + 2, Blocks.SEA_LANTERN.getDefaultState());

		// ۶) فرش وسط خونه
		for (int x = 2; x <= 8; x++) {
			for (int z = 3; z <= 6; z++) {
				set(world, bx + x, by + 1, bz + z, Blocks.RED_CARPET.getDefaultState());
			}
		}

		// ۷) میز و کامپیوتر (کنار دیوار شمالی، صفحه رو به داخل خونه)
		set(world, bx + 5, by + 1, bz + 1, Blocks.SPRUCE_PLANKS.getDefaultState());
		set(world, bx + 4, by + 1, bz + 1, Blocks.SPRUCE_PLANKS.getDefaultState());
		set(world, bx + 5, by + 2, bz + 1,
				ModBlocks.COMPUTER.getDefaultState().with(HorizontalFacingBlock.FACING, Direction.SOUTH));
		set(world, bx + 4, by + 2, bz + 1, Blocks.FLOWER_POT.getDefaultState());
		// صندلی
		set(world, bx + 5, by + 1, bz + 2,
				Blocks.SPRUCE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.SOUTH));

		// ۸) کتابخونه
		for (int x = 1; x <= 3; x++) {
			set(world, bx + x, by + 1, bz + SZ - 1, Blocks.BOOKSHELF.getDefaultState());
			set(world, bx + x, by + 2, bz + SZ - 1, Blocks.BOOKSHELF.getDefaultState());
		}

		// ۹) تخت (پایین و بالا باید بدون آپدیت همسایه گذاشته بشن)
		BlockPos foot = new BlockPos(bx + 8, by + 1, bz + SZ - 1);
		BlockPos head = foot.offset(Direction.EAST);
		world.setBlockState(foot, Blocks.RED_BED.getDefaultState()
				.with(BedBlock.FACING, Direction.EAST).with(BedBlock.PART, BedPart.FOOT), Block.NOTIFY_LISTENERS);
		world.setBlockState(head, Blocks.RED_BED.getDefaultState()
				.with(BedBlock.FACING, Direction.EAST).with(BedBlock.PART, BedPart.HEAD), Block.NOTIFY_LISTENERS);

		// ۱۰) مسیر جلوی در
		for (int x = -2; x <= -1; x++) {
			set(world, bx + x, by, bz + 4, Blocks.STONE_BRICKS.getDefaultState());
		}

		// ۱۱) شخصیت‌ها
		spawn(world, bx + 4.5, by + 1, bz + 4.5, 3);
		spawn(world, bx + 6.5, by + 1, bz + 4.5, 4);
	}

	private static boolean isWindow(int x, int z) {
		if (z == 0 || z == SZ) {
			return x == 2 || x == 3 || x == 7 || x == 8;
		}
		if (x == SX) {
			return z == 3 || z == 5;
		}
		return false;
	}

	private static void set(ServerWorld world, int x, int y, int z, BlockState state) {
		world.setBlockState(new BlockPos(x, y, z), state);
	}

	private static void spawn(ServerWorld world, double x, double y, double z, int variant) {
		TuberEntity entity = ModEntities.TUBER.create(world);
		if (entity == null) {
			return;
		}
		entity.refreshPositionAndAngles(x, y, z, world.random.nextFloat() * 360.0f, 0.0f);
		entity.setVariant(variant);
		entity.setPersistent();
		world.spawnEntity(entity);
	}
}
