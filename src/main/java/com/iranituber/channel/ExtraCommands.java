package com.iranituber.channel;

import com.iranituber.game.ComputerGame;
import com.iranituber.game.MoneyUtil;
import com.iranituber.world.HouseBuilder;
import com.iranituber.world.tehran.TehranBuilder;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

/**
 * /money                  موجودی پول (تومان)
 * /computer guess <n>     (از دکمه‌های چت صدا زده می‌شه) حدس عدد
 * /house                  ساخت خونه‌ی یوتوبر معروف و دوستش مقابل تو (فقط ادمین / چیت روشن)
 */
public final class ExtraCommands {
	private ExtraCommands() {}

	public static void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(CommandManager.literal("money").executes(context -> {
				ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
				long balance = MoneyUtil.balance(player);
				context.getSource().sendFeedback(() -> Text.translatable("command.irani_tuber.money", balance), false);
				return 1;
			}));

			dispatcher.register(CommandManager.literal("computer")
					.then(CommandManager.literal("guess")
							.then(CommandManager.argument("n", IntegerArgumentType.integer(1, ComputerGame.MAX_NUMBER))
									.executes(context -> {
										ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
										ComputerGame.guess(player, IntegerArgumentType.getInteger(context, "n"));
										return 1;
									}))));

			dispatcher.register(CommandManager.literal("tehran")
					.requires(source -> source.hasPermissionLevel(2))
					.executes(context -> {
						ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
						ServerWorld world = (ServerWorld) player.getWorld();
						int gy = player.getBlockPos().getY() - 1;
						if (gy + TehranBuilder.CLEAR_HEIGHT + 45 > world.getTopY() || gy - 8 < world.getBottomY()) {
							context.getSource().sendFeedback(() -> Text.translatable("message.irani_tuber.tehran_height"), false);
							return 0;
						}
						if (!TehranBuilder.start(world, player)) {
							context.getSource().sendFeedback(() -> Text.translatable("message.irani_tuber.tehran_busy"), false);
							return 0;
						}
						context.getSource().sendFeedback(() -> Text.translatable("message.irani_tuber.tehran_start"), false);
						return 1;
					}));

			dispatcher.register(CommandManager.literal("house")
					.requires(source -> source.hasPermissionLevel(2))
					.executes(context -> {
						ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
						ServerWorld world = (ServerWorld) player.getWorld();
						// کف خونه = زمینی که زیر پای بازیکنه؛ خونه ۳ بلوک شرق بازیکن شروع می‌شه
						BlockPos floor = player.getBlockPos().down().add(3, 0, -4);
						HouseBuilder.build(world, floor);
						context.getSource().sendFeedback(() -> Text.translatable("message.irani_tuber.house_built"), false);
						return 1;
					}));
		});
	}
}
