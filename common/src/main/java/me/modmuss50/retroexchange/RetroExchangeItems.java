package me.modmuss50.retroexchange;

import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.Registries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public interface RetroExchangeItems {
	Registrar<Item> REGISTRY = Registries.get(RetroExchange.MOD_ID).get(Registry.ITEM_REGISTRY);

	Supplier<Item> TRANSMUTATION_SHARD = REGISTRY.register(new ResourceLocation(RetroExchange.MOD_ID, "transmutation_shard"), TransmutationShardItem::new);
	Supplier<Item> TRANSMUTATION_STONE = REGISTRY.register(new ResourceLocation(RetroExchange.MOD_ID, "transmutation_stone"), TransmutationStoneItem::new);

	static void init() {
	}
}
