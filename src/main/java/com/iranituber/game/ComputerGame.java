package com.iranituber.game;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * بازی حدس عدد روی کامپیوتر: یه عدد بین ۱ تا ۱۰ انتخاب می‌شه، ۳ بار فرصت داری.
 * دکمه‌های داخل چت (قابل کلیک) دستور /computer guess N رو اجرا می‌کنن.
 */
public final class ComputerGame {
	private ComputerGame() {}

	public static final int MAX_NUMBER = 10;
	public static final int MAX_TRIES = 3;

	private static final Random RANDOM = new Random();

	private static final class Round {
		final int target = 1 + RANDOM.nextInt(MAX_NUMBER);
		int triesLeft = MAX_TRIES;
	}

	private static final Map<UUID, Round> ROUNDS = new HashMap<>();

	public static void forget(ServerPlayerEntity player) {
		ROUNDS.remove(player.getUuid());
	}

	/** شروع یه دور جدید و نشون دادن دکمه‌ها. */
	public static void start(ServerPlayerEntity player) {
		ROUNDS.put(player.getUuid(), new Round());
		player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.8f, 1.4f);
		player.sendMessage(Text.translatable("computer.irani_tuber.title", MAX_NUMBER, MAX_TRIES).formatted(Formatting.AQUA, Formatting.BOLD), false);
		showButtons(player);
	}

	private static void showButtons(ServerPlayerEntity player) {
		MutableText row = Text.empty();
		for (int i = 1; i <= MAX_NUMBER; i++) {
			final int n = i;
			row.append(Text.literal("[" + n + "] ").styled(style -> style
					.withColor(Formatting.GREEN)
					.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/computer guess " + n))));
		}
		player.sendMessage(row, false);
	}

	public static void guess(ServerPlayerEntity player, int number) {
		Round round = ROUNDS.get(player.getUuid());
		if (round == null) {
			start(player);
			return;
		}

		if (number == round.target) {
			ROUNDS.remove(player.getUuid());
			// ۲ تا ۶ هزار تومان + هزار تومان برای هر فرصت باقی‌مونده
			long reward = (2 + RANDOM.nextInt(5) + round.triesLeft) * MoneyUtil.COIN;
			MoneyUtil.give(player, reward);
			player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.PLAYERS, 1.0f, 1.0f);
			player.sendMessage(Text.translatable("computer.irani_tuber.win", reward, MoneyUtil.balance(player)).formatted(Formatting.GOLD, Formatting.BOLD), false);
			return;
		}

		round.triesLeft--;
		if (round.triesLeft <= 0) {
			ROUNDS.remove(player.getUuid());
			player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.BLOCK_DISPENSER_FAIL, SoundCategory.PLAYERS, 1.0f, 0.8f);
			player.sendMessage(Text.translatable("computer.irani_tuber.lose", round.target).formatted(Formatting.RED), false);
			return;
		}

		String hintKey = number < round.target ? "computer.irani_tuber.higher" : "computer.irani_tuber.lower";
		player.sendMessage(Text.translatable(hintKey, number, round.triesLeft).formatted(Formatting.YELLOW), false);
		showButtons(player);
	}
}
