package com.iranituber.world.tehran;

import com.iranituber.registry.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.RailBlock;
import net.minecraft.block.enums.RailShape;
import net.minecraft.util.math.Direction;

import java.util.Random;

/** نشانه‌های معروف تهران، به شکل ساده‌شده‌ی ماینکرفتی. */
public final class Landmarks {
	private Landmarks() {}

	// ------------------------------------------------------------------ برج میلاد
	public static void miladTower(Canvas c, int cx, int cz) {
		// میدون پایه
		c.fill(cx - 9, 0, cz - 9, cx + 9, 0, cz + 9, Blocks.SMOOTH_SANDSTONE);
		c.ring(cx, 0, cz, 8.5, Blocks.STONE_BRICKS);
		// چهار پایه
		for (int sx = -1; sx <= 1; sx += 2) {
			for (int sz = -1; sz <= 1; sz += 2) {
				c.fill(cx + sx * 3 - 1, 1, cz + sz * 3 - 1, cx + sx * 3 + 1, 16, cz + sz * 3 + 1, Blocks.WHITE_CONCRETE);
			}
		}
		c.fill(cx - 5, 17, cz - 5, cx + 5, 18, cz + 5, Blocks.LIGHT_GRAY_CONCRETE);
		// تنه‌ی باریک‌شونده
		for (int y = 19; y <= 54; y++) {
			int half = 3 - (int) Math.round((y - 19) * 2.0 / 35.0);
			c.fill(cx - half, y, cz - half, cx + half, y, cz + half, Blocks.WHITE_CONCRETE);
		}
		// کابین مشاهده (زیر مخروطی، حلقه‌ی شیشه‌ای، سقف)
		double[] under = {4, 5.5, 7, 8.5};
		for (int i = 0; i < under.length; i++) {
			c.disc(cx, 55 + i, cz, under[i], Blocks.LIGHT_GRAY_CONCRETE);
		}
		for (int y = 59; y <= 62; y++) {
			c.disc(cx, y, cz, 9.5, Blocks.AIR);
			c.ring(cx, y, cz, 9.5, y == 60 || y == 61 ? Blocks.LIGHT_BLUE_STAINED_GLASS : Blocks.WHITE_CONCRETE);
		}
		c.disc(cx, 59, cz, 9.5, Blocks.WHITE_CONCRETE);
		c.disc(cx, 63, cz, 9.5, Blocks.WHITE_CONCRETE);
		c.disc(cx, 64, cz, 7.0, Blocks.LIGHT_GRAY_CONCRETE);
		c.disc(cx, 65, cz, 4.5, Blocks.WHITE_CONCRETE);
		c.set(cx, 60, cz, Blocks.LIGHT);
		c.set(cx + 4, 60, cz, Blocks.LIGHT);
		c.set(cx - 4, 60, cz, Blocks.LIGHT);
		// آنتن
		for (int y = 66; y <= 74; y++) {
			c.set(cx, y, cz, Blocks.IRON_BLOCK);
		}
		for (int y = 75; y <= 92; y++) {
			c.set(cx, y, cz, Blocks.END_ROD);
		}
		c.set(cx, 93, cz, Blocks.SEA_LANTERN);
		c.set(cx, 70, cz + 1, Blocks.SEA_LANTERN);
	}

	// ------------------------------------------------------------------ برج آزادی
	public static void azadiTower(Canvas c, int cx, int cz) {
		// پایه‌ی پله‌ای
		c.fill(cx - 10, 1, cz - 7, cx + 10, 1, cz + 7, Blocks.QUARTZ_BLOCK);
		c.fill(cx - 9, 2, cz - 6, cx + 9, 2, cz + 6, Blocks.SMOOTH_QUARTZ);
		// بدنه‌ی اصلی
		c.fill(cx - 8, 3, cz - 5, cx + 8, 18, cz + 5, Blocks.SMOOTH_QUARTZ);
		// طاق بزرگ و نوک‌تیز (از شمال به جنوب)
		for (int y = 3; y <= 17; y++) {
			double t = (y - 3) / 14.0;
			int hw = (int) Math.round(4.6 * Math.sqrt(Math.max(0, 1 - t * t * t)));
			for (int x = cx - hw; x <= cx + hw; x++) {
				for (int z = cz - 5; z <= cz + 5; z++) {
					c.set(x, y, z, Blocks.AIR);
				}
			}
		}
		// طاق کوچیک‌تر از شرق به غرب
		for (int y = 3; y <= 11; y++) {
			double t = (y - 3) / 9.0;
			int hw = (int) Math.round(2.6 * Math.sqrt(Math.max(0, 1 - t * t)));
			for (int z = cz - hw; z <= cz + hw; z++) {
				for (int x = cx - 8; x <= cx + 8; x++) {
					c.set(x, y, z, Blocks.AIR);
				}
			}
		}
		// بخش بالایی و گنبد
		c.fill(cx - 5, 19, cz - 4, cx + 5, 25, cz + 4, Blocks.QUARTZ_BLOCK);
		c.fill(cx - 3, 26, cz - 3, cx + 3, 28, cz + 3, Blocks.SMOOTH_QUARTZ);
		c.dome(cx, 29, cz, 3, Blocks.QUARTZ_BLOCK);
		for (int y = 33; y <= 38; y++) {
			c.set(cx, y, cz, Blocks.END_ROD);
		}
		// چهار برجک گوشه‌ای
		for (int sx = -1; sx <= 1; sx += 2) {
			for (int sz = -1; sz <= 1; sz += 2) {
				c.fill(cx + sx * 8, 19, cz + sz * 5, cx + sx * 8, 22, cz + sz * 5, Blocks.QUARTZ_PILLAR);
				c.set(cx + sx * 8, 23, cz + sz * 5, Blocks.SEA_LANTERN);
			}
		}
		c.set(cx, 4, cz + 4, Blocks.LIGHT);
		c.set(cx, 4, cz - 4, Blocks.LIGHT);
	}

	// ------------------------------------------------------------------ پل طبیعت
	/** پل عابر پیاده روی بزرگراه: خط z=71 تا z=85 عرشه، رمپ‌ها دو طرف. */
	public static void tabiatBridge(Canvas c, int cx) {
		int deckY = 5;
		for (int z = 71; z <= 85; z++) {
			for (int x = cx - 2; x <= cx + 2; x++) {
				c.set(x, deckY, z, Blocks.SPRUCE_PLANKS);
			}
			c.set(cx - 2, deckY + 1, z, Blocks.SPRUCE_FENCE);
			c.set(cx + 2, deckY + 1, z, Blocks.SPRUCE_FENCE);
			if ((z - 71) % 5 == 0) {
				c.set(cx - 2, deckY + 2, z, Blocks.LANTERN);
				c.set(cx + 2, deckY + 2, z, Blocks.LANTERN);
			}
		}
		for (int i = 0; i < 5; i++) {
			int zn = 66 + i;
			int zs = 90 - i;
			for (int x = cx - 2; x <= cx + 2; x++) {
				for (int y = 1; y <= i; y++) {
					c.set(x, y, zn, Blocks.SPRUCE_PLANKS);
					c.set(x, y, zs, Blocks.SPRUCE_PLANKS);
				}
				c.stairs(x, i + 1, zn, Blocks.SPRUCE_STAIRS, Direction.SOUTH);
				c.stairs(x, i + 1, zs, Blocks.SPRUCE_STAIRS, Direction.NORTH);
			}
		}
		// پایه‌ها کنار بزرگراه
		for (int sx = -2; sx <= 2; sx += 4) {
			for (int z : new int[] {71, 85}) {
				c.fill(cx + sx, 1, z, cx + sx, deckY - 1, z, Blocks.STRIPPED_SPRUCE_LOG);
			}
		}
		// تیرهای مورب تزئینی زیر عرشه
		for (int x = cx - 2; x <= cx + 2; x += 4) {
			c.set(x, deckY - 1, 74, Blocks.SPRUCE_FENCE);
			c.set(x, deckY - 1, 78, Blocks.SPRUCE_FENCE);
			c.set(x, deckY - 1, 82, Blocks.SPRUCE_FENCE);
		}
	}

	// ------------------------------------------------------------------ بازار (طاق آجری)
	public static void bazaar(Canvas c, int x1, int z1, int x2, int z2) {
		int wallH = 5;
		int depth = z2 - z1 + 1;
		int kMax = (depth - 1) / 2;
		c.fill(x1, 0, z1, x2, 0, z2, Blocks.POLISHED_ANDESITE);
		// دیوارهای کناری
		for (int y = 1; y <= wallH; y++) {
			for (int x = x1; x <= x2; x++) {
				c.set(x, y, z1, Blocks.BRICKS);
				c.set(x, y, z2, Blocks.BRICKS);
			}
		}
		// طاق پله‌ای
		for (int k = 1; k <= kMax; k++) {
			int y = wallH + k;
			for (int x = x1; x <= x2; x++) {
				c.set(x, y, z1 + k, Blocks.BRICKS);
				c.set(x, y, z2 - k, Blocks.BRICKS);
			}
		}
		for (int x = x1; x <= x2; x++) {
			Block top = (x - x1) % 6 == 3 ? Blocks.SEA_LANTERN : Blocks.BRICKS;
			c.set(x, wallH + kMax, z1 + kMax, top);
		}
		// ورودی‌ها
		int midZ = (z1 + z2) / 2;
		for (int y = 1; y <= 4; y++) {
			for (int z = midZ - 1; z <= midZ + 1; z++) {
				c.set(x1, y, z, Blocks.AIR);
				c.set(x2, y, z, Blocks.AIR);
			}
		}
		int midX = (x1 + x2) / 2;
		for (int y = 1; y <= 3; y++) {
			for (int x = midX - 1; x <= midX + 1; x++) {
				c.set(x, y, z1, Blocks.AIR);
				c.set(x, y, z2, Blocks.AIR);
			}
		}
		// غرفه‌ها با فرش و پیشخوان
		Block[] carpets = {Blocks.RED_CARPET, Blocks.BLUE_CARPET, Blocks.ORANGE_CARPET, Blocks.GREEN_CARPET,
				Blocks.YELLOW_CARPET, Blocks.LIGHT_BLUE_CARPET};
		int n = 0;
		for (int x = x1 + 2; x + 3 <= x2 - 1; x += 4) {
			for (int side = 0; side < 2; side++) {
				int zWall = side == 0 ? z1 + 1 : z2 - 1;
				int zCounter = side == 0 ? z1 + 3 : z2 - 3;
				int zMid = side == 0 ? z1 + 2 : z2 - 2;
				if (Math.abs(x + 1 - midX) <= 2) {
					continue; // جلوی ورودی‌ها غرفه نمی‌ذاریم
				}
				for (int y = 1; y <= 3; y++) {
					c.set(x, y, zWall, Blocks.BRICKS);
					c.set(x, y, zMid, Blocks.BRICKS);
					c.set(x, y, zCounter, Blocks.BRICKS);
				}
				Block carpet = carpets[(n++) % carpets.length];
				for (int dx = 1; dx <= 2; dx++) {
					c.set(x + dx, 1, zWall, carpet);
					c.set(x + dx, 1, zMid, carpet);
					c.set(x + dx, 1, zCounter, Blocks.SPRUCE_SLAB);
					c.set(x + dx, 2, zWall, Blocks.WHITE_WOOL);
				}
			}
		}
		// فرش بلند وسط راهرو
		for (int x = x1 + 1; x <= x2 - 1; x++) {
			c.set(x, 1, midZ, Blocks.RED_CARPET);
		}
		for (int x = x1 + 1; x <= x2 - 1; x++) {
			c.set(x, 4, midZ, Blocks.LIGHT);
		}
	}

	// ------------------------------------------------------------------ مسجد
	public static void mosque(Canvas c, int x1, int z1, int x2, int z2) {
		int hallX1 = x1 + 8;
		int hallX2 = x2 - 8;
		int hallZ2 = z1 + 12;
		int cx = (hallX1 + hallX2) / 2;
		int cz = (z1 + hallZ2) / 2;

		// حیاط و کف
		c.fill(x1, 0, z1, x2, 0, z2, Blocks.CUT_SANDSTONE);
		// تالار اصلی
		c.shell(hallX1, 1, z1, hallX2, 9, hallZ2, Blocks.SMOOTH_SANDSTONE);
		for (int x = hallX1; x <= hallX2; x++) {
			c.set(x, 8, hallZ2, Blocks.BLUE_TERRACOTTA);
			c.set(x, 8, z1, Blocks.BLUE_TERRACOTTA);
		}
		c.fill(hallX1 + 1, 10, z1 + 1, hallX2 - 1, 10, hallZ2 - 1, Blocks.SMOOTH_SANDSTONE);
		c.fill(hallX1 + 1, 1, z1 + 1, hallX2 - 1, 1, hallZ2 - 1, Blocks.BLUE_CARPET);
		// ایوان نوک‌تیز رو به حیاط (جنوب)
		for (int y = 1; y <= 8; y++) {
			double t = (y - 1) / 8.0;
			int hw = (int) Math.round(4.4 * Math.sqrt(Math.max(0, 1 - t * t * t)));
			for (int x = cx - hw; x <= cx + hw; x++) {
				c.set(x, y, hallZ2, Blocks.AIR);
			}
		}
		// قاب آبی دور ایوان
		for (int y = 1; y <= 9; y++) {
			c.set(cx - 5, y, hallZ2, Blocks.BLUE_TERRACOTTA);
			c.set(cx + 5, y, hallZ2, Blocks.BLUE_TERRACOTTA);
		}
		// گنبد فیروزه‌ای
		c.fill(cx - 5, 11, cz - 5, cx + 5, 12, cz + 5, Blocks.SMOOTH_SANDSTONE);
		c.dome(cx, 13, cz, 6, Blocks.LIGHT_BLUE_CONCRETE);
		c.ring(cx, 13, cz, 6.4, Blocks.BLUE_TERRACOTTA);
		for (int y = 20; y <= 23; y++) {
			c.set(cx, y, cz, Blocks.END_ROD);
		}
		c.set(cx, 3, cz, Blocks.LIGHT);
		c.set(cx + 3, 3, cz, Blocks.LIGHT);
		c.set(cx - 3, 3, cz, Blocks.LIGHT);
		// دو مناره
		minaret(c, hallX1 - 2, hallZ2 + 1);
		minaret(c, hallX2 + 2, hallZ2 + 1);
		// حوض حیاط
		int px = cx;
		int pz = hallZ2 + 4;
		c.fill(px - 4, 0, pz - 2, px + 4, 0, pz + 2, Blocks.QUARTZ_BLOCK);
		c.fill(px - 3, 0, pz - 1, px + 3, 0, pz + 1, Blocks.WATER);
		// دیوار کوتاه حیاط (به‌جز دروازه)
		for (int x = x1; x <= x2; x++) {
			if (Math.abs(x - cx) > 2) {
				c.set(x, 1, z2, Blocks.SMOOTH_SANDSTONE);
				c.set(x, 2, z2, Blocks.SMOOTH_SANDSTONE_SLAB);
			}
		}
		for (int z = hallZ2 + 1; z <= z2; z++) {
			c.set(x1, 1, z, Blocks.SMOOTH_SANDSTONE);
			c.set(x2, 1, z, Blocks.SMOOTH_SANDSTONE);
		}
	}

	private static void minaret(Canvas c, int x, int z) {
		c.fill(x - 1, 1, z - 1, x + 1, 19, z + 1, Blocks.SMOOTH_SANDSTONE);
		for (int dx = -1; dx <= 1; dx++) {
			for (int dz = -1; dz <= 1; dz++) {
				c.set(x + dx, 12, z + dz, Blocks.BLUE_TERRACOTTA);
				c.set(x + dx, 16, z + dz, Blocks.BLUE_TERRACOTTA);
			}
		}
		c.fill(x - 2, 19, z - 2, x + 2, 19, z + 2, Blocks.QUARTZ_SLAB);
		c.fill(x - 1, 20, z - 1, x + 1, 21, z + 1, Blocks.SMOOTH_SANDSTONE);
		c.dome(x, 22, z, 2, Blocks.LIGHT_BLUE_CONCRETE);
		for (int y = 25; y <= 27; y++) {
			c.set(x, y, z, Blocks.END_ROD);
		}
		c.set(x, 20, z + 2, Blocks.LIGHT);
	}

	// ------------------------------------------------------------------ باغ گلستان
	public static void golestan(Canvas c, int x1, int z1, int x2, int z2, Random rnd) {
		c.fill(x1, 0, z1, x2, 0, z2, Blocks.GRASS_BLOCK);
		// دیوار آجری دور باغ و دروازه‌ی غربی
		for (int x = x1; x <= x2; x++) {
			c.set(x, 1, z1, Blocks.BRICKS);
			c.set(x, 1, z2, Blocks.BRICKS);
			c.set(x, 2, z1, Blocks.BRICK_SLAB);
			c.set(x, 2, z2, Blocks.BRICK_SLAB);
		}
		int zm = (z1 + z2) / 2;
		for (int z = z1; z <= z2; z++) {
			if (Math.abs(z - zm) > 1) {
				c.set(x1, 1, z, Blocks.BRICKS);
				c.set(x1, 2, z, Blocks.BRICK_SLAB);
			}
			c.set(x2, 1, z, Blocks.BRICKS);
			c.set(x2, 2, z, Blocks.BRICK_SLAB);
		}
		// راه سنگی اصلی و حوض بلند وسط
		for (int x = x1 + 1; x <= x2 - 1; x++) {
			c.set(x, 0, zm - 3, Blocks.SMOOTH_SANDSTONE);
			c.set(x, 0, zm + 3, Blocks.SMOOTH_SANDSTONE);
		}
		c.fill(x1 + 6, 0, zm - 1, x2 - 6, 0, zm + 1, Blocks.WATER);
		for (int x = x1 + 5; x <= x2 - 5; x++) {
			c.set(x, 0, zm - 2, Blocks.QUARTZ_BLOCK);
			c.set(x, 0, zm + 2, Blocks.QUARTZ_BLOCK);
		}
		c.set(x1 + 5, 0, zm, Blocks.QUARTZ_BLOCK);
		c.set(x2 - 5, 0, zm, Blocks.QUARTZ_BLOCK);
		// کوشک با ستون‌های مرمری و سقف فیروزه‌ای (ضلع شمالی)
		int px1 = x1 + 8;
		int px2 = x2 - 8;
		int pz1 = z1 + 2;
		int pz2 = z1 + 4;
		c.fill(px1, 1, pz1, px2, 1, pz2, Blocks.QUARTZ_BLOCK);
		for (int x = px1; x <= px2; x += 3) {
			c.fill(x, 2, pz1, x, 6, pz1, Blocks.QUARTZ_PILLAR);
			c.fill(x, 2, pz2, x, 6, pz2, Blocks.QUARTZ_PILLAR);
		}
		c.fill(px1 - 1, 7, pz1 - 1, px2 + 1, 7, pz2 + 1, Blocks.LIGHT_BLUE_CONCRETE);
		c.fill(px1, 8, pz1, px2, 8, pz2, Blocks.CYAN_TERRACOTTA);
		c.set((px1 + px2) / 2, 6, (pz1 + pz2) / 2, Blocks.LIGHT);
		// گل‌کاری و چنار
		for (int x = x1 + 2; x <= x2 - 2; x += 2) {
			c.set(x, 1, zm + 5, rnd.nextBoolean() ? Blocks.POPPY : Blocks.DANDELION);
			c.set(x, 1, zm + 6, rnd.nextBoolean() ? Blocks.AZURE_BLUET : Blocks.POPPY);
		}
		for (int x = x1 + 3; x <= x2 - 3; x += 7) {
			c.tree(x, z2 - 2, 5, Blocks.BIRCH_LOG);
		}
		c.tree(x1 + 3, z1 + 8, 5, Blocks.BIRCH_LOG);
		c.tree(x2 - 3, z1 + 8, 5, Blocks.BIRCH_LOG);
	}

	// ------------------------------------------------------------------ راه‌آهن
	public static void station(Canvas c, int x1, int z1, int x2, int z2) {
		c.fill(x1, 0, z1, x2, 0, z2, Blocks.STONE_BRICKS);
		int hz2 = z1 + 8;
		c.shell(x1 + 2, 1, z1, x2 - 2, 11, hz2, Blocks.BRICKS);
		c.fill(x1 + 3, 12, z1 + 1, x2 - 3, 12, hz2 - 1, Blocks.STONE_BRICK_SLAB);
		for (int x = x1 + 4; x <= x2 - 4; x += 4) {
			for (int y = 3; y <= 8; y++) {
				for (int dx = 0; dx <= 1; dx++) {
					c.set(x + dx, y, z1, Blocks.LIGHT_BLUE_STAINED_GLASS);
				}
			}
		}
		int cx = (x1 + x2) / 2;
		for (int y = 1; y <= 3; y++) {
			for (int x = cx - 2; x <= cx + 2; x++) {
				c.set(x, y, z1, Blocks.AIR);
			}
		}
		// برج ساعت
		c.fill(cx - 3, 12, z1, cx + 3, 24, z1 + 6, Blocks.BRICKS);
		for (int y = 18; y <= 21; y++) {
			c.set(cx - 1, y, z1 - 1, Blocks.WHITE_CONCRETE);
			c.set(cx, y, z1 - 1, Blocks.WHITE_CONCRETE);
			c.set(cx + 1, y, z1 - 1, Blocks.WHITE_CONCRETE);
		}
		c.set(cx, 19, z1 - 1, Blocks.BLACK_CONCRETE);
		c.set(cx, 20, z1 - 1, Blocks.BLACK_CONCRETE);
		c.set(cx + 1, 19, z1 - 1, Blocks.BLACK_CONCRETE);
		c.fill(cx - 2, 25, z1 + 1, cx + 2, 26, z1 + 5, Blocks.STONE_BRICK_SLAB);
		for (int y = 27; y <= 30; y++) {
			c.set(cx, y, z1 + 3, Blocks.END_ROD);
		}
		c.set(cx, 3, z1 + 3, Blocks.LIGHT);
		c.set(cx - 12, 3, z1 + 3, Blocks.LIGHT);
		c.set(cx + 12, 3, z1 + 3, Blocks.LIGHT);
		// سکوها و ریل
		int railZ1 = hz2 + 3;
		int railZ2 = hz2 + 6;
		c.fill(x1, 0, hz2 + 1, x2, 0, z2, Blocks.GRAVEL);
		c.fill(x1, 0, hz2 + 1, x2, 0, hz2 + 2, Blocks.SMOOTH_STONE);
		for (int x = x1; x <= x2; x++) {
			c.set(x, 1, railZ1, Blocks.RAIL.getDefaultState().with(RailBlock.SHAPE, RailShape.EAST_WEST));
			c.set(x, 1, railZ2, Blocks.RAIL.getDefaultState().with(RailBlock.SHAPE, RailShape.EAST_WEST));
		}
		// یه قطار روی ریل شمالی
		int tz = railZ1;
		c.fill(x1 + 6, 1, tz - 1, x1 + 30, 3, tz + 1, Blocks.BLUE_CONCRETE);
		for (int x = x1 + 7; x <= x1 + 29; x += 3) {
			for (int dx = 0; dx <= 1; dx++) {
				c.set(x + dx, 2, tz - 1, Blocks.LIGHT_BLUE_STAINED_GLASS);
				c.set(x + dx, 2, tz + 1, Blocks.LIGHT_BLUE_STAINED_GLASS);
			}
		}
		c.fill(x1 + 6, 4, tz - 1, x1 + 30, 4, tz + 1, Blocks.WHITE_CONCRETE);
	}

	// ------------------------------------------------------------------ پارک
	public static void park(Canvas c, int x1, int z1, int x2, int z2, int pondX, int pondZ, Random rnd) {
		c.fill(x1, 0, z1, x2, 0, z2, Blocks.GRASS_BLOCK);
		int cx = (x1 + x2) / 2;
		int cz = (z1 + z2) / 2;
		for (int x = x1; x <= x2; x++) {
			c.set(x, 0, cz, Blocks.DIRT_PATH);
		}
		for (int z = z1; z <= z2; z++) {
			c.set(cx, 0, z, Blocks.DIRT_PATH);
		}
		c.disc(pondX, 0, pondZ, 4.8, Blocks.STONE_BRICKS);
		c.disc(pondX, 0, pondZ, 3.6, Blocks.WATER);
		for (int i = 0; i < 14; i++) {
			int x = x1 + 2 + rnd.nextInt(Math.max(1, x2 - x1 - 3));
			int z = z1 + 2 + rnd.nextInt(Math.max(1, z2 - z1 - 3));
			if (Math.abs(x - cx) <= 1 || Math.abs(z - cz) <= 1) {
				continue;
			}
			if (Math.hypot(x - pondX, z - pondZ) < 7) {
				continue;
			}
			c.tree(x, z, 4 + rnd.nextInt(2), i % 2 == 0 ? Blocks.BIRCH_LOG : Blocks.OAK_LOG);
		}
		for (int i = 0; i < 18; i++) {
			int x = x1 + 1 + rnd.nextInt(Math.max(1, x2 - x1 - 1));
			int z = z1 + 1 + rnd.nextInt(Math.max(1, z2 - z1 - 1));
			if (c.get(x, 0, z).isOf(Blocks.GRASS_BLOCK) && c.get(x, 1, z).isAir()) {
				c.set(x, 1, z, rnd.nextBoolean() ? Blocks.POPPY : Blocks.DANDELION);
			}
		}
	}

	// ------------------------------------------------------------------ میدون و فلکه
	public static void roundabout(Canvas c, int cx, int cz, int r, boolean fountain) {
		c.disc(cx, 0, cz, r, Blocks.STONE_BRICKS);
		c.disc(cx, 0, cz, r - 1.3, Blocks.GRASS_BLOCK);
		if (fountain) {
			c.disc(cx, 0, cz, r - 1.8, Blocks.QUARTZ_BLOCK);
			c.disc(cx, 0, cz, r - 2.6, Blocks.WATER);
			c.fill(cx, 1, cz, cx, 4, cz, Blocks.QUARTZ_PILLAR);
			c.set(cx, 5, cz, Blocks.SEA_LANTERN);
		} else {
			c.fill(cx, 1, cz, cx, 7, cz, Blocks.QUARTZ_PILLAR);
			c.set(cx, 8, cz, Blocks.SEA_LANTERN);
			c.ring(cx, 1, cz, r - 1.5, Blocks.STONE_BRICK_SLAB);
		}
	}

	public static void tajrishPlaza(Canvas c, int cx, int cz, int r) {
		c.disc(cx, 0, cz, r, Blocks.STONE_BRICKS);
		c.ring(cx, 0, cz, r * 0.6, Blocks.SMOOTH_SANDSTONE);
		c.disc(cx, 0, cz, 4.5, Blocks.QUARTZ_BLOCK);
		c.disc(cx, 0, cz, 3.4, Blocks.WATER);
		c.fill(cx, 1, cz, cx, 5, cz, Blocks.QUARTZ_PILLAR);
		c.set(cx, 6, cz, Blocks.SEA_LANTERN);
	}

	// ------------------------------------------------------------------ دربند
	public static void darband(Canvas c, int pathX, int zFrom, int zTo) {
		for (int z = zFrom; z >= zTo; z--) {
			int top = TehranBuilder.mountainHeight(pathX, z);
			for (int dx = -1; dx <= 1; dx++) {
				for (int y = top + 1; y <= top + 3; y++) {
					c.set(pathX + dx, y, z, Blocks.AIR);
				}
				c.set(pathX + dx, top, z, Blocks.STONE_BRICKS);
				for (int y = TehranBuilder.mountainHeight(pathX + dx, z); y < top; y++) {
					c.set(pathX + dx, y, z, Blocks.STONE_BRICKS);
				}
			}
			c.set(pathX - 2, top + 1, z, Blocks.SPRUCE_FENCE);
			c.set(pathX + 2, top + 1, z, Blocks.SPRUCE_FENCE);
			if (z % 6 == 0) {
				c.set(pathX + 2, top + 2, z, Blocks.LANTERN);
			}
		}
		// کافه‌ی چوبی روی پایه‌ها
		int hx = pathX + 4;
		int hz = zFrom - 8;
		int floor = TehranBuilder.mountainHeight(hx, hz) + 2;
		for (int x = hx; x <= hx + 6; x++) {
			for (int z = hz; z <= hz + 5; z++) {
				c.set(x, floor, z, Blocks.SPRUCE_PLANKS);
			}
		}
		for (int[] p : new int[][] {{hx, hz}, {hx + 6, hz}, {hx, hz + 5}, {hx + 6, hz + 5}}) {
			c.fill(p[0], 1, p[1], p[0], floor - 1, p[1], Blocks.SPRUCE_LOG);
			c.fill(p[0], floor + 1, p[1], p[0], floor + 3, p[1], Blocks.SPRUCE_LOG);
		}
		for (int x = hx; x <= hx + 6; x++) {
			c.set(x, floor + 1, hz + 5, Blocks.SPRUCE_FENCE);
			c.set(x, floor + 1, hz, Blocks.SPRUCE_FENCE);
		}
		c.fill(hx - 1, floor + 4, hz - 1, hx + 7, floor + 4, hz + 6, Blocks.SPRUCE_SLAB);
		for (int t = 0; t < 3; t++) {
			c.set(hx + 1 + t * 2, floor + 1, hz + 2, Blocks.SPRUCE_FENCE);
			c.set(hx + 1 + t * 2, floor + 2, hz + 2, Blocks.SPRUCE_SLAB);
		}
		c.set(hx + 3, floor + 3, hz + 3, Blocks.LANTERN);
		c.set(hx + 3, floor + 2, hz + 2, Blocks.LIGHT);
	}

	// ------------------------------------------------------------------ ورودی مترو
	public static void metro(Canvas c, int x, int z) {
		c.shell(x, 1, z, x + 4, 4, z + 4, Blocks.LIGHT_BLUE_CONCRETE);
		c.fill(x, 5, z, x + 4, 5, z + 4, Blocks.WHITE_CONCRETE);
		c.fill(x + 1, 1, z + 1, x + 3, 4, z + 3, Blocks.AIR);
		c.set(x + 2, 1, z + 4, Blocks.AIR);
		c.set(x + 2, 2, z + 4, Blocks.AIR);
		c.set(x + 2, 3, z + 4, Blocks.SEA_LANTERN);
		c.set(x + 2, 2, z + 2, Blocks.LIGHT);
		// پله‌های رو به پایین
		for (int i = 0; i < 3; i++) {
			c.stairs(x + 2, -i, z + 2 + i - 1, Blocks.STONE_BRICK_STAIRS, Direction.NORTH);
		}
	}

	// ------------------------------------------------------------------ کافی‌نت با کامپیوتر
	public static void cafe(Canvas c, int x, int z) {
		int w = 9;
		int d = 7;
		int x2 = x + w - 1;
		int z2 = z + d - 1;
		c.fill(x, 0, z, x2, 0, z2, Blocks.SPRUCE_PLANKS);
		c.shell(x, 1, z, x2, 4, z2, Blocks.WHITE_CONCRETE);
		c.fill(x + 1, 5, z + 1, x2 - 1, 5, z2 - 1, Blocks.WHITE_CONCRETE);
		c.shell(x, 5, z, x2, 5, z2, Blocks.LIGHT_GRAY_CONCRETE);
		for (int i = x + 1; i < x2; i++) {
			c.set(i, 2, z2, Blocks.LIGHT_BLUE_STAINED_GLASS);
			c.set(i, 3, z2, Blocks.LIGHT_BLUE_STAINED_GLASS);
		}
		c.set(x + w / 2, 1, z2, Blocks.AIR);
		c.set(x + w / 2, 2, z2, Blocks.AIR);
		c.set(x + w / 2, 3, z2, Blocks.AIR);
		for (int i = 0; i < 4; i++) {
			int px = x + 1 + i * 2;
			c.set(px, 1, z + 1, Blocks.SPRUCE_PLANKS);
			c.set(px, 2, z + 1, ModBlocks.COMPUTER.getDefaultState().with(HorizontalFacingBlock.FACING, Direction.SOUTH));
			c.stairs(px, 1, z + 2, Blocks.SPRUCE_STAIRS, Direction.SOUTH);
		}
		c.set(x + 2, 2, z + 4, Blocks.LIGHT);
		c.set(x + 6, 2, z + 4, Blocks.LIGHT);
		c.set(x + w / 2, 4, z2 - 1, Blocks.SEA_LANTERN);
	}

	// ------------------------------------------------------------------ تاکسی نارنجی
	public static void taxi(Canvas c, int x, int z, boolean alongX) {
		int lx = alongX ? 4 : 2;
		int lz = alongX ? 2 : 4;
		c.fill(x, 1, z, x + lx - 1, 1, z + lz - 1, Blocks.ORANGE_CONCRETE);
		int cx1 = alongX ? x + 1 : x;
		int cx2 = alongX ? x + 2 : x + 1;
		int cz1 = alongX ? z : z + 1;
		int cz2 = alongX ? z + 1 : z + 2;
		c.fill(cx1, 2, cz1, cx2, 2, cz2, Blocks.LIGHT_BLUE_STAINED_GLASS);
		c.fill(cx1, 3, cz1, cx2, 3, cz2, Blocks.WHITE_CONCRETE);
		c.set(cx1, 4, cz1, Blocks.SEA_LANTERN);
	}
}
