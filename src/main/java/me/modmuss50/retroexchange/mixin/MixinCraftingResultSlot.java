package me.modmuss50.retroexchange.mixin;

import me.modmuss50.retroexchange.ExtendedRecipeRemainder;
import net.minecraft.container.CraftingResultSlot;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(CraftingResultSlot.class)
public abstract class MixinCraftingResultSlot {

	@Shadow
	@Final
	private CraftingInventory craftingInv;

	@ModifyVariable(method = "onTakeItem", at = @At(value = "INVOKE"), index = 3)
	private DefaultedList<ItemStack> defaultedList(DefaultedList<ItemStack> list) {
		for (int i = 0; i < craftingInv.getInvSize(); i++) {
			ItemStack invStack = craftingInv.getInvStack(i);
			if (invStack.getItem() instanceof ExtendedRecipeRemainder) {
				ItemStack remainder = ((ExtendedRecipeRemainder) invStack.getItem()).getRemainderStack(invStack.copy());
				if (!remainder.isEmpty()) {
					list.set(i, remainder);
				}
			}
		}
		return list;
	}

}
