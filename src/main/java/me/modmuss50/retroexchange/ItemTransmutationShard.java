package me.modmuss50.retroexchange;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class ItemTransmutationShard extends Item {

	public ItemTransmutationShard() {
		setCreativeTab(RetroExchange.CREATIVE_TAB);
		setRegistryName(new ResourceLocation("retroexchange", "transmutation_shard"));
		setUnlocalizedName("retroexchange.transmutation_shard");
	}
}