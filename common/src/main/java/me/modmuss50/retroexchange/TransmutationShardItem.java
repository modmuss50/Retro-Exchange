package me.modmuss50.retroexchange;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

public class TransmutationShardItem extends Item {

	public TransmutationShardItem() {
		super(new Properties().tab(RetroExchange.tab));
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.RARE;
	}
}
