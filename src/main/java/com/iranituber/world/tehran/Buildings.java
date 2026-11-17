package com.iranituber.world.tehran;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

/** ساختمان‌های معمولی شهر: آپارتمان‌های بلند شمال، میان‌مرتبه‌ها و خونه‌های آجری جنوب. */
public final class Buildings {
	private Buildings() {}

	/**
	 * یه ساختمون توخالی با پنجره‌های طبقه‌ای و پشت‌بوم.
	 * (x, z) گوشه‌ی شمال‌غربی، w در راستای x و d در راستای z. هر طبقه ۳ بلوکه.
	 */
	public static void apartment(Canvas c, int x, int z, int w, int d, int floors, Block wall, Block glass, Block trim) {
		int h = floors * 3;
		int x2 = x + w - 1;
		int z2 = z + d - 1;

		// دیوارها
		c.shell(x, 1, z, x2, h, z2, wall);

		// ستون‌های گوشه با رنگ تزئینی
		for (int y = 1; y <= h; y++) {
			c.set(x, y, z, trim);
			c.set(x2, y, z, trim);
			c.set(x, y, z2, trim);
			c.set(x2, y, z2, trim);
		}

		// پنجره‌ها: دو ردیف توی هر طبقه، هر سه بلوک یه ستون پر می‌ذاریم
		for (int f = 0; f < floors; f++) {
			if (f == 0) {
				continue; // طبقه‌ی همکف در و ویترین داره
			}
			int y0 = f * 3 + 1;
			for (int i = x + 1; i < x2; i++) {
				if ((i - x) % 3 != 0) {
					c.set(i, y0, z, glass);
					c.set(i, y0 + 1, z, glass);
					c.set(i, y0, z2, glass);
					c.set(i, y0 + 1, z2, glass);
				}
			}
			for (int k = z + 1; k < z2; k++) {
				if ((k - z) % 3 != 0) {
					c.set(x, y0, k, glass);
					c.set(x, y0 + 1, k, glass);
					c.set(x2, y0, k, glass);
					c.set(x2, y0 + 1, k, glass);
				}
			}
		}

		// همکف: ویترین جلو (جنوب) و در وسط
		int mid = x + w / 2;
		for (int i = x + 1; i < x2; i++) {
			if (Math.abs(i - mid) > 0 && (i - x) % 3 != 0) {
				c.set(i, 1, z2, glass);
				c.set(i, 2, z2, glass);
			}
		}
		c.set(mid, 1, z2, Blocks.AIR);
		c.set(mid, 2, z2, Blocks.AIR);

		// کف همکف و سقف
		c.fill(x + 1, h + 1, z + 1, x2 - 1, h + 1, z2 - 1, wall);
		c.shell(x, h + 1, z, x2, h + 2, z2, trim);

		// نور داخل (بلوک نامرئی تا هیولا اسپاون نشه)
		for (int f = 0; f < floors; f++) {
			c.set(x + w / 2, f * 3 + 2, z + d / 2, Blocks.LIGHT);
		}

		// پشت‌بوم: مخزن آب و کولر
		if (w >= 7 && d >= 7) {
			c.fill(x + 2, h + 2, z + 2, x + 3, h + 3, z + 3, Blocks.IRON_BLOCK);
			c.set(x2 - 2, h + 2, z2 - 2, Blocks.LIGHT_GRAY_CONCRETE);
			c.set(x2 - 3, h + 2, z2 - 2, Blocks.LIGHT_GRAY_CONCRETE);
		}
	}

	/** خونه‌ی آجری کوچیک جنوب شهر (یک یا دو طبقه). */
	public static void brickHouse(Canvas c, int x, int z, int w, int d, int floors, Block wall) {
		apartment(c, x, z, w, d, floors, wall, Blocks.GLASS, Blocks.STONE_BRICKS);
	}
}
