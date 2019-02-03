package me.modmuss50.retroexchange;

import net.minecraft.item.ItemStack;

public interface ExtendedRecipeRemainder {

	default ItemStack getRemainderStack(ItemStack stack){
		return ItemStack.EMPTY;
	}

}
