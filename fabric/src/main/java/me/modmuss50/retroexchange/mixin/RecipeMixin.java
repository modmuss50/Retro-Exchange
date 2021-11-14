package me.modmuss50.retroexchange.mixin;

import me.modmuss50.retroexchange.ExtendedRecipeRemainder;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Recipe.class)
public interface RecipeMixin<C extends Container> {
	@Inject(method = "getRemainingItems(Lnet/minecraft/world/Container;)Lnet/minecraft/core/NonNullList;", at = @At("RETURN"))
	default void modifyRemainder(C container, CallbackInfoReturnable<NonNullList<ItemStack>> cir) {
		NonNullList<ItemStack> remainders = cir.getReturnValue();

		for (int i = 0; i < container.getContainerSize(); i++) {
			ItemStack stack = container.getItem(i);
			Item item = stack.getItem();

			if (item instanceof ExtendedRecipeRemainder extendedItem) {
				// note: ResultSlot.onTake() will consume `stack`, thus we pass a copy here
				remainders.set(i, extendedItem.getRemainderStack(stack.copy()));
			}
		}
	}
}
