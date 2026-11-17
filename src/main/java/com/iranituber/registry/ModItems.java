package com.iranituber.registry;

import com.iranituber.IraniTuberMod;
import com.iranituber.item.CameraItem;
import com.iranituber.item.DrinkItem;
import net.minecraft.component.type.FoodComponent;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class ModItems {
	private ModItems() {}

	public static final Item CAMERA = register("camera", new CameraItem(new Item.Settings().maxCount(1)));

	public static final Item TUBER_SPAWN_EGG = register("tuber_spawn_egg",
			new SpawnEggItem(ModEntities.TUBER, 0xC62828, 0xFFD54F, new Item.Settings()));

	// ---------- پول ----------
	public static final Item TOMAN_COIN = register("toman_coin", new Item(new Item.Settings()));
	public static final Item TOMAN_NOTE = register("toman_note", new Item(new Item.Settings()));
	public static final Item TOMAN_BIG_NOTE = register("toman_big_note", new Item(new Item.Settings()));

	// ---------- غذاهای ایرانی ----------
	public static final Item BARBARI = register("barbari", new Item(new Item.Settings().food(
			new FoodComponent.Builder().nutrition(6).saturationModifier(0.7f).build())));
	public static final Item SANGAK = register("sangak", new Item(new Item.Settings().food(
			new FoodComponent.Builder().nutrition(5).saturationModifier(0.6f).build())));
	public static final Item KEBAB = register("kebab", new Item(new Item.Settings().food(
			new FoodComponent.Builder().nutrition(9).saturationModifier(0.9f).build())));
	public static final Item DOOGH = register("doogh", new DrinkItem(new Item.Settings().food(
			new FoodComponent.Builder().nutrition(3).saturationModifier(0.4f).build())));
	public static final Item CHAI = register("chai", new DrinkItem(new Item.Settings().food(
			new FoodComponent.Builder().nutrition(2).saturationModifier(0.5f).build())));

	public static final ItemGroup GROUP = Registry.register(
			Registries.ITEM_GROUP,
			Identifier.of(IraniTuberMod.MOD_ID, "main"),
			FabricItemGroup.builder()
					.icon(() -> new ItemStack(CAMERA))
					.displayName(Text.translatable("itemGroup.irani_tuber.main"))
					.entries((context, entries) -> {
						entries.add(CAMERA);
						entries.add(TUBER_SPAWN_EGG);
						entries.add(ModBlocks.COMPUTER_ITEM);
						entries.add(TOMAN_COIN);
						entries.add(TOMAN_NOTE);
						entries.add(TOMAN_BIG_NOTE);
						entries.add(BARBARI);
						entries.add(SANGAK);
						entries.add(KEBAB);
						entries.add(DOOGH);
						entries.add(CHAI);
					})
					.build());

	private static Item register(String name, Item item) {
		return Registry.register(Registries.ITEM, Identifier.of(IraniTuberMod.MOD_ID, name), item);
	}

	public static void register() {}
}
