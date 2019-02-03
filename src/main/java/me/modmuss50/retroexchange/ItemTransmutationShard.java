package me.modmuss50.retroexchange;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Rarity;

public class ItemTransmutationShard extends Item {

	public ItemTransmutationShard() {
		super(new Item.Settings().itemGroup(RetroExchange.ITEM_GROUP));
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.RARE;
	}
}
