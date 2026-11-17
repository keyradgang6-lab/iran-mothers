package com.iranituber.registry;

import com.iranituber.IraniTuberMod;
import com.iranituber.block.ComputerBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public final class ModBlocks {
	private ModBlocks() {}

	public static final Block COMPUTER = registerBlock("computer",
			new ComputerBlock(AbstractBlock.Settings.create().strength(1.5f).sounds(BlockSoundGroup.METAL)));

	public static final Item COMPUTER_ITEM = Registry.register(
			Registries.ITEM,
			Identifier.of(IraniTuberMod.MOD_ID, "computer"),
			new BlockItem(COMPUTER, new Item.Settings()));

	private static Block registerBlock(String name, Block block) {
		return Registry.register(Registries.BLOCK, Identifier.of(IraniTuberMod.MOD_ID, name), block);
	}

	public static void register() {}
}
