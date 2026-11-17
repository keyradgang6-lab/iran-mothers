package com.iranituber.game;

import com.iranituber.registry.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * پول بازی به‌صورت آیتم توی اینونتوری هست:
 * سکه = ۱٬۰۰۰ تومان، اسکناس = ۱۰٬۰۰۰ تومان، اسکناس بزرگ = ۱۰۰٬۰۰۰ تومان.
 */
public final class MoneyUtil {
	private MoneyUtil() {}

	public static final long COIN = 1_000L;
	public static final long NOTE = 10_000L;
	public static final long BIG_NOTE = 100_000L;

	public static long valueOf(Item item) {
		if (item == ModItems.TOMAN_COIN) {
			return COIN;
		}
		if (item == ModItems.TOMAN_NOTE) {
			return NOTE;
		}
		if (item == ModItems.TOMAN_BIG_NOTE) {
			return BIG_NOTE;
		}
		return 0L;
	}

	/** مجموع پول داخل اینونتوری (تومان). */
	public static long balance(PlayerEntity player) {
		PlayerInventory inventory = player.getInventory();
		long total = 0L;
		for (int i = 0; i < inventory.size(); i++) {
			ItemStack stack = inventory.getStack(i);
			total += valueOf(stack.getItem()) * stack.getCount();
		}
		return total;
	}

	/** پول رو به شکل اسکناس و سکه می‌ذاره توی دست بازیکن (کمتر از ۱۰۰۰ تومان گرد می‌شه). */
	public static void give(ServerPlayerEntity player, long amount) {
		if (amount <= 0) {
			return;
		}
		long big = amount / BIG_NOTE;
		amount %= BIG_NOTE;
		long notes = amount / NOTE;
		amount %= NOTE;
		long coins = amount / COIN;
		giveStacks(player, ModItems.TOMAN_BIG_NOTE, big);
		giveStacks(player, ModItems.TOMAN_NOTE, notes);
		giveStacks(player, ModItems.TOMAN_COIN, coins);
	}

	/** مبلغ رو از اینونتوری کم می‌کنه و بقیه رو خرد برمی‌گردونه. اگه پول کافی نبود false. */
	public static boolean take(ServerPlayerEntity player, long amount) {
		long balance = balance(player);
		if (balance < amount) {
			return false;
		}
		PlayerInventory inventory = player.getInventory();
		for (int i = 0; i < inventory.size(); i++) {
			if (valueOf(inventory.getStack(i).getItem()) > 0) {
				inventory.setStack(i, ItemStack.EMPTY);
			}
		}
		give(player, balance - amount);
		return true;
	}

	private static void giveStacks(ServerPlayerEntity player, Item item, long count) {
		while (count > 0) {
			int n = (int) Math.min(count, item.getMaxCount());
			player.getInventory().offerOrDrop(new ItemStack(item, n));
			count -= n;
		}
	}
}
