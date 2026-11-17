package com.iranituber.world.tehran;

import com.iranituber.entity.TuberEntity;
import com.iranituber.registry.ModEntities;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

/**
 * شهر تهران (ساده‌شده) رو کم‌کم و بین تیک‌ها می‌سازه تا سرور لگ نکنه.
 *
 * نقشه (محلی): x شرق ← 0..160، z جنوب ← 0..190، y=0 سطح زمین.
 * شمال (z کم) = کوه‌های البرز و دربند. ولیعصر یه خیابون بلنده شمال به جنوب (x=80)
 * از میدون تجریش تا راه‌آهن.
 */
public final class TehranBuilder {
	private TehranBuilder() {}

	public static final int W = 161;
	public static final int D = 191;
	public static final int VALIASR_X = 80;
	public static final int[] AVENUE_X = {40, 120};
	public static final int[] STREET_Z = {60, 110, 140, 168};
	public static final int EXPRESSWAY_Z = 78;
	public static final int ROAD_START_Z = 52;
	public static final int ROAD_END_Z = 174;
	public static final int CLEAR_HEIGHT = 56;

	/** کجای نقشه بازیکن وایساده (مختصات محلی). */
	public static final int PLAYER_LOCAL_X = 83;
	public static final int PLAYER_LOCAL_Z = 56;

	private static final long BUDGET_NANOS = 28_000_000L;

	private record Rect(int x1, int z1, int x2, int z2) {
		boolean overlaps(Rect o) {
			return x1 <= o.x2 && x2 >= o.x1 && z1 <= o.z2 && z2 >= o.z1;
		}

		Rect grow(int g) {
			return new Rect(x1 - g, z1 - g, x2 + g, z2 + g);
		}
	}

	private static final class Job {
		final ServerWorld world;
		final Canvas canvas;
		final ServerPlayerEntity owner;
		final Queue<Runnable> tasks = new ArrayDeque<>();
		int total;
		int lastPercent = -1;
		int tickCounter;

		Job(ServerWorld world, Canvas canvas, ServerPlayerEntity owner) {
			this.world = world;
			this.canvas = canvas;
			this.owner = owner;
		}
	}

	private static Job current;

	public static boolean isBusy() {
		return current != null;
	}

	// ------------------------------------------------------------------ شکل زمین

	/** ارتفاع کوه‌های البرز در ستون (x, z) محلی. صفر یعنی زمین صاف. */
	public static int mountainHeight(int x, int z) {
		if (z > 33) {
			return 0;
		}
		double h = (34 - z) * 1.15 + 3.0 * Math.sin(x * 0.13) + 2.0 * Math.cos(z * 0.4 + x * 0.09);
		return Math.max(0, (int) Math.round(h));
	}

	/** 0 هیچی، 1 آسفالت، 2 پیاده‌رو، 3 نوار سبز ولیعصر، 4 خط وسط. */
	public static int roadType(int x, int z) {
		int best = 0;

		// ولیعصر
		if (z >= ROAD_START_Z && z <= ROAD_END_Z) {
			int d = Math.abs(x - VALIASR_X);
			if (d <= 4) {
				best = Math.max(best, 1);
			} else if (d <= 6) {
				best = Math.max(best, 2);
			} else if (d == 7) {
				best = Math.max(best, 3);
			}
		}
		// خیابون‌های شمال به جنوب
		for (int c : AVENUE_X) {
			if (z >= 33 && z <= ROAD_END_Z) {
				int d = Math.abs(x - c);
				if (d <= 3) {
					best = Math.max(best, 1);
				} else if (d <= 5) {
					best = Math.max(best, 2);
				}
			}
		}
		// خیابون‌های شرق به غرب
		for (int c : STREET_Z) {
			int d = Math.abs(z - c);
			if (d <= 3) {
				best = Math.max(best, 1);
			} else if (d <= 5) {
				best = Math.max(best, 2);
			}
		}
		// بزرگراه
		int de = Math.abs(z - EXPRESSWAY_Z);
		if (de <= 4) {
			best = Math.max(best, 1);
		} else if (de <= 6) {
			best = Math.max(best, 2);
		}
		return best;
	}

	private static boolean isAsphalt(int x, int z) {
		return roadType(x, z) == 1;
	}

	/** آسفالت بودن در هر دو جهت (چهارراه). */
	private static boolean isIntersection(int x, int z) {
		boolean ns = false;
		boolean ew = false;
		if (z >= ROAD_START_Z && z <= ROAD_END_Z && Math.abs(x - VALIASR_X) <= 4) {
			ns = true;
		}
		for (int c : AVENUE_X) {
			if (z >= 33 && z <= ROAD_END_Z && Math.abs(x - c) <= 3) {
				ns = true;
			}
		}
		for (int c : STREET_Z) {
			if (Math.abs(z - c) <= 3) {
				ew = true;
			}
		}
		if (Math.abs(z - EXPRESSWAY_Z) <= 4) {
			ew = true;
		}
		return ns && ew;
	}

	// ------------------------------------------------------------------ شروع

	public static boolean start(ServerWorld world, ServerPlayerEntity player) {
		if (current != null) {
			return false;
		}
		int ox = player.getBlockPos().getX() - PLAYER_LOCAL_X;
		int oz = player.getBlockPos().getZ() - PLAYER_LOCAL_Z;
		int gy = player.getBlockPos().getY() - 1;
		Canvas canvas = new Canvas(world, ox, gy, oz);
		Job job = new Job(world, canvas, player);
		Random rnd = new Random(1357L);

		// ۱) زمین، کوه، آسفالت و پیاده‌رو (ردیف به ردیف)
		for (int z = 0; z < D; z++) {
			final int row = z;
			job.tasks.add(() -> groundRow(canvas, row));
		}

		// ۲) فلکه‌ها و میدون‌ها
		job.tasks.add(() -> Landmarks.tajrishPlaza(canvas, 80, 42, 10));
		job.tasks.add(() -> Landmarks.roundabout(canvas, VALIASR_X, 60, 3, true));
		job.tasks.add(() -> Landmarks.roundabout(canvas, VALIASR_X, 110, 4, false));

		// ۳) پارک‌ها و باغ
		job.tasks.add(() -> Landmarks.park(canvas, 47, 86, 71, 100, 59, 94, new Random(11)));
		job.tasks.add(() -> Landmarks.park(canvas, 89, 86, 113, 104, 101, 95, new Random(12)));
		job.tasks.add(() -> Landmarks.golestan(canvas, 127, 147, 158, 161, new Random(13)));

		// ۴) نشانه‌ها
		job.tasks.add(() -> Landmarks.miladTower(canvas, 22, 96));
		job.tasks.add(() -> Landmarks.azadiTower(canvas, 22, 154));
		job.tasks.add(() -> Landmarks.tabiatBridge(canvas, 58));
		job.tasks.add(() -> Landmarks.bazaar(canvas, 89, 147, 113, 161));
		job.tasks.add(() -> Landmarks.bazaar(canvas, 92, 38, 110, 50));
		job.tasks.add(() -> Landmarks.mosque(canvas, 126, 116, 158, 134));
		job.tasks.add(() -> Landmarks.station(canvas, 56, 176, 104, 189));
		job.tasks.add(() -> Landmarks.cafe(canvas, 92, 117));
		job.tasks.add(() -> Landmarks.darband(canvas, 20, 32, 8));
		job.tasks.add(() -> Landmarks.metro(canvas, 90, 66));
		job.tasks.add(() -> Landmarks.metro(canvas, 90, 128));
		job.tasks.add(() -> Landmarks.metro(canvas, 64, 150));
		job.tasks.add(() -> Landmarks.metro(canvas, 110, 176));

		// ۵) ساختمان‌ها
		List<Rect> reserved = reservedRects();
		List<Runnable> buildingTasks = new ArrayList<>();
		int[][] xIntervals = {{2, 34}, {47, 71}, {89, 113}, {127, 158}};
		int[][] zIntervals = {{34, 54}, {86, 104}, {116, 134}, {146, 162}, {176, 189}};
		for (int[] xi : xIntervals) {
			for (int[] zi : zIntervals) {
				int z = zi[0];
				while (z + 7 <= zi[1]) {
					int d = Math.min(8 + rnd.nextInt(3), zi[1] - z + 1);
					int x = xi[0];
					while (x + 7 <= xi[1]) {
						int w = Math.min(8 + rnd.nextInt(3), xi[1] - x + 1);
						Rect lot = new Rect(x, z, x + w - 1, z + d - 1);
						boolean blocked = false;
						for (Rect r : reserved) {
							if (lot.grow(1).overlaps(r)) {
								blocked = true;
								break;
							}
						}
						if (!blocked) {
							final int fx = x;
							final int fz = z;
							final int fw = w;
							final int fd = d;
							final long seed = rnd.nextLong();
							buildingTasks.add(() -> buildLot(canvas, fx, fz, fw, fd, new Random(seed)));
						}
						x += w + 2;
					}
					z += d + 2;
				}
			}
		}
		job.tasks.addAll(buildingTasks);

		// ۶) درخت‌های چنار و چراغ و تاکسی و گل
		job.tasks.add(() -> valiasrTrees(canvas));
		job.tasks.add(() -> streetLamps(canvas));
		job.tasks.add(() -> taxis(canvas, new Random(77)));

		// ۷) آدم‌های شهر
		job.tasks.add(() -> spawnPeople(job, new Random(99)));
		job.tasks.add(() -> finish(job));

		job.total = job.tasks.size();
		current = job;
		return true;
	}

	private static List<Rect> reservedRects() {
		List<Rect> list = new ArrayList<>();
		list.add(new Rect(68, 32, 92, 52));      // میدون تجریش
		list.add(new Rect(92, 38, 110, 50));     // بازار تجریش
		list.add(new Rect(10, 86, 34, 104));     // برج میلاد
		list.add(new Rect(10, 146, 34, 162));    // برج آزادی
		list.add(new Rect(47, 64, 71, 100));     // پارک و پل طبیعت
		list.add(new Rect(89, 86, 113, 104));    // پارک ملت
		list.add(new Rect(91, 116, 101, 126));   // کافی‌نت
		list.add(new Rect(126, 115, 159, 135));  // مسجد
		list.add(new Rect(89, 146, 113, 162));   // بازار بزرگ
		list.add(new Rect(127, 146, 159, 162));  // باغ گلستان
		list.add(new Rect(56, 175, 104, 190));   // راه‌آهن
		list.add(new Rect(89, 65, 95, 71));      // مترو
		list.add(new Rect(89, 127, 95, 133));
		list.add(new Rect(63, 149, 69, 155));
		list.add(new Rect(109, 175, 115, 181));
		return list;
	}

	// ------------------------------------------------------------------ اجرای کارها

	public static void tick(MinecraftServer server) {
		Job job = current;
		if (job == null) {
			return;
		}
		long start = System.nanoTime();
		try {
			while (!job.tasks.isEmpty() && System.nanoTime() - start < BUDGET_NANOS) {
				job.tasks.poll().run();
			}
		} catch (RuntimeException e) {
			job.owner.sendMessage(Text.translatable("message.irani_tuber.tehran_failed").formatted(Formatting.RED), false);
			com.iranituber.IraniTuberMod.LOGGER.error("Tehran build failed", e);
			current = null;
			return;
		}
		job.tickCounter++;
		int percent = job.total == 0 ? 100 : (int) (100L * (job.total - job.tasks.size()) / job.total);
		if (percent != job.lastPercent && job.tickCounter % 10 == 0 && !job.tasks.isEmpty()) {
			job.lastPercent = percent;
			job.owner.sendMessage(Text.translatable("message.irani_tuber.tehran_progress", percent).formatted(Formatting.AQUA), true);
		}
		if (job.tasks.isEmpty()) {
			current = null;
		}
	}

	// ------------------------------------------------------------------ ردیف زمین

	private static void groundRow(Canvas c, int z) {
		for (int x = 0; x < W; x++) {
			// پر کردن زیر زمین و پاک کردن بالا
			for (int y = -6; y <= -1; y++) {
				if (c.get(x, y, z).isAir() || c.get(x, y, z).isReplaceable() || !c.get(x, y, z).getFluidState().isEmpty()) {
					c.set(x, y, z, Blocks.STONE);
				}
			}
			for (int y = 1; y <= CLEAR_HEIGHT; y++) {
				if (!c.get(x, y, z).isAir()) {
					c.set(x, y, z, Blocks.AIR);
				}
			}

			int type = roadType(x, z);
			Block ground;
			if (type == 1) {
				ground = Blocks.BLACK_CONCRETE;
				boolean inter = isIntersection(x, z);
				if (!inter) {
					if (z <= 174 && z >= ROAD_START_Z && Math.abs(x - VALIASR_X) <= 4 && x == VALIASR_X && z % 4 < 2) {
						ground = Blocks.WHITE_CONCRETE;
					}
					for (int ax : AVENUE_X) {
						if (x == ax && z >= 33 && z % 4 < 2) {
							ground = Blocks.WHITE_CONCRETE;
						}
					}
					for (int sz : STREET_Z) {
						if (z == sz && x % 4 < 2) {
							ground = Blocks.WHITE_CONCRETE;
						}
					}
					if (z == EXPRESSWAY_Z) {
						ground = Blocks.GRAY_CONCRETE; // جدول وسط بزرگراه
					}
				}
			} else if (type == 2) {
				ground = Blocks.SMOOTH_STONE;
			} else if (type == 3) {
				ground = Blocks.GRASS_BLOCK;
			} else {
				ground = z > 130 ? Blocks.COARSE_DIRT : Blocks.GRASS_BLOCK;
			}
			c.set(x, 0, z, ground);

			// کوه‌های البرز
			int h = mountainHeight(x, z);
			for (int y = 1; y <= h; y++) {
				Block b;
				if (y > 27) {
					b = Blocks.SNOW_BLOCK;
				} else if (y > 14) {
					b = ((x + z + y) % 5 == 0) ? Blocks.ANDESITE : Blocks.STONE;
				} else if (y == h && h <= 9) {
					b = Blocks.GRASS_BLOCK;
				} else {
					b = y > h - 3 ? Blocks.COARSE_DIRT : Blocks.STONE;
				}
				c.set(x, y, z, b);
			}
		}
	}

	// ------------------------------------------------------------------ ساختمان‌ها

	private static void buildLot(Canvas c, int x, int z, int w, int d, Random rnd) {
		if (z < 90) {
			// شمال: آپارتمان‌های بلند و سفید
			Block[] walls = {Blocks.WHITE_CONCRETE, Blocks.LIGHT_GRAY_CONCRETE, Blocks.SMOOTH_QUARTZ};
			Block[] trims = {Blocks.GRAY_CONCRETE, Blocks.LIGHT_BLUE_TERRACOTTA, Blocks.STONE_BRICKS};
			Buildings.apartment(c, x, z, w, d, 5 + rnd.nextInt(7),
					walls[rnd.nextInt(walls.length)], Blocks.LIGHT_BLUE_STAINED_GLASS, trims[rnd.nextInt(trims.length)]);
		} else if (z < 140) {
			// مرکز: میان‌مرتبه، آجری و کرم
			Block[] walls = {Blocks.SMOOTH_SANDSTONE, Blocks.BRICKS, Blocks.WHITE_TERRACOTTA};
			Buildings.apartment(c, x, z, w, d, 3 + rnd.nextInt(5),
					walls[rnd.nextInt(walls.length)], Blocks.GLASS, Blocks.STONE_BRICKS);
		} else {
			// جنوب: خونه‌های کم‌ارتفاع آجری و خشتی
			Block[] walls = {Blocks.BRICKS, Blocks.TERRACOTTA, Blocks.STONE_BRICKS, Blocks.SMOOTH_SANDSTONE};
			Buildings.brickHouse(c, x, z, w, d, 1 + rnd.nextInt(3), walls[rnd.nextInt(walls.length)]);
		}
	}

	// ------------------------------------------------------------------ جزئیات خیابون

	private static void valiasrTrees(Canvas c) {
		for (int z = ROAD_START_Z + 2; z <= ROAD_END_Z; z += 4) {
			if (Math.abs(z - EXPRESSWAY_Z) <= 6 || isStreetBand(z)) {
				continue;
			}
			c.tree(VALIASR_X - 7, z, 4, Blocks.BIRCH_LOG);
			c.tree(VALIASR_X + 7, z, 4, Blocks.BIRCH_LOG);
			if (c.get(VALIASR_X - 7, 1, z + 2).isAir()) {
				c.set(VALIASR_X - 7, 1, z + 2, Blocks.POPPY);
			}
			if (c.get(VALIASR_X + 7, 1, z + 2).isAir()) {
				c.set(VALIASR_X + 7, 1, z + 2, Blocks.POPPY);
			}
		}
	}

	private static boolean isStreetBand(int z) {
		for (int s : STREET_Z) {
			if (Math.abs(z - s) <= 5) {
				return true;
			}
		}
		return false;
	}

	private static void streetLamps(Canvas c) {
		for (int z = ROAD_START_Z + 6; z <= ROAD_END_Z; z += 12) {
			if (isStreetBand(z) || Math.abs(z - EXPRESSWAY_Z) <= 6) {
				continue;
			}
			c.lamp(VALIASR_X - 5, z);
			c.lamp(VALIASR_X + 5, z);
			for (int ax : AVENUE_X) {
				c.lamp(ax - 4, z + 3);
				c.lamp(ax + 4, z + 3);
			}
		}
		for (int sz : STREET_Z) {
			for (int x = 6; x < W; x += 14) {
				if (roadType(x, sz - 4) == 2 && !isAsphalt(x, sz - 4)) {
					c.lamp(x, sz - 4);
				}
			}
		}
	}

	private static void taxis(Canvas c, Random rnd) {
		int placed = 0;
		int attempts = 0;
		while (placed < 26 && attempts < 400) {
			attempts++;
			int x = 4 + rnd.nextInt(W - 12);
			int z = 36 + rnd.nextInt(ROAD_END_Z - 36);
			boolean alongX = rnd.nextBoolean();
			int lx = alongX ? 4 : 2;
			int lz = alongX ? 2 : 4;
			boolean ok = true;
			for (int dx = 0; dx < lx && ok; dx++) {
				for (int dz = 0; dz < lz; dz++) {
					if (!isAsphalt(x + dx, z + dz) || isIntersection(x + dx, z + dz)
							|| (x + dx == VALIASR_X) || (z + dz == EXPRESSWAY_Z)) {
						ok = false;
						break;
					}
				}
			}
			if (ok && c.get(x, 1, z).isAir()) {
				Landmarks.taxi(c, x, z, alongX);
				placed++;
			}
		}
	}

	// ------------------------------------------------------------------ آدم‌ها

	private static void spawnPeople(Job job, Random rnd) {
		Canvas c = job.canvas;
		ServerWorld world = job.world;
		int spawned = 0;
		int attempts = 0;
		while (spawned < 22 && attempts < 300) {
			attempts++;
			int x = 4 + rnd.nextInt(W - 8);
			int z = 36 + rnd.nextInt(ROAD_END_Z - 36);
			if (roadType(x, z) != 2) {
				continue;
			}
			if (!c.get(x, 1, z).isAir() || !c.get(x, 2, z).isAir()) {
				continue;
			}
			TuberEntity entity = ModEntities.TUBER.create(world);
			if (entity == null) {
				return;
			}
			net.minecraft.util.math.BlockPos p = c.worldPos(x, 1, z);
			entity.refreshPositionAndAngles(p.getX() + 0.5, p.getY(), p.getZ() + 0.5, rnd.nextFloat() * 360.0f, 0.0f);
			entity.setVariant(rnd.nextInt(TuberEntity.VARIANT_COUNT));
			entity.setPersistent();
			world.spawnEntity(entity);
			spawned++;
		}
	}

	// ------------------------------------------------------------------ پایان

	private static void finish(Job job) {
		Canvas c = job.canvas;
		ServerPlayerEntity p = job.owner;
		p.sendMessage(Text.translatable("message.irani_tuber.tehran_done").formatted(Formatting.GREEN, Formatting.BOLD), false);
		String[][] places = {
				{"message.irani_tuber.place.milad", "22", "96"},
				{"message.irani_tuber.place.azadi", "22", "154"},
				{"message.irani_tuber.place.tabiat", "58", "78"},
				{"message.irani_tuber.place.bazaar", "101", "154"},
				{"message.irani_tuber.place.mosque", "142", "122"},
				{"message.irani_tuber.place.golestan", "142", "154"},
				{"message.irani_tuber.place.station", "80", "180"},
				{"message.irani_tuber.place.park", "101", "95"},
				{"message.irani_tuber.place.tajrish", "80", "42"},
				{"message.irani_tuber.place.darband", "20", "20"},
				{"message.irani_tuber.place.cafe", "96", "120"},
		};
		for (String[] pl : places) {
			net.minecraft.util.math.BlockPos pos = c.worldPos(Integer.parseInt(pl[1]), 1, Integer.parseInt(pl[2]));
			p.sendMessage(Text.translatable(pl[0], pos.getX(), pos.getZ()).formatted(Formatting.YELLOW), false);
		}
	}
}
