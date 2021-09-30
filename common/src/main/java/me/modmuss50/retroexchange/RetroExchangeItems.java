package me.modmuss50.retroexchange;

import me.shedaniel.architectury.registry.Registries;
import me.shedaniel.architectury.registry.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public interface RetroExchangeItems {
	Registry<Item> REGISTRY = Registries.get(RetroExchange.MOD_ID).get(net.minecraft.core.Registry.ITEM_REGISTRY);

	Supplier<Item> TRANSMUTATION_SHARD = REGISTRY.register(new ResourceLocation(RetroExchange.MOD_ID, "transmutation_shard"), TransmutationShardItem::new);
	Supplier<Item> TRANSMUTATION_STONE = REGISTRY.register(new ResourceLocation(RetroExchange.MOD_ID, "transmutation_stone"), TransmutationStoneItem::new);

	static void init() {
	}
}
