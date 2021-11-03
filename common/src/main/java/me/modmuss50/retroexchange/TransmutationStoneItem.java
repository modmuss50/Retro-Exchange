package me.modmuss50.retroexchange;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class TransmutationStoneItem extends Item implements ExtendedRecipeRemainder {

	public static final int MAX_DAMAGE = 1500;

	public TransmutationStoneItem() {
		super(new Properties()
				.tab(RetroExchange.tab)
				.durability(MAX_DAMAGE)
		);
	}

	// FIXME: not functional on Forge
	@Override
	public ItemStack getRemainderStack(ItemStack stack) {
		damage(stack);
		if (getDamage(stack) >= maxDamage) {
			return ItemStack.EMPTY;
		}
		return stack;
	}

	public static int getDamage(ItemStack stack) {
		checkTag(stack);
		return stack.getTag().getInt("Damage");
	}

	public static ItemStack damage(ItemStack stack) {
		checkTag(stack);
		stack.getTag().putInt("Damage", getDamage(stack) + 1);
		return stack;
	}

	private static void checkTag(ItemStack stack) {
		if (!stack.hasTag()) {
			stack.setTag(new CompoundTag());
		}
		if (!stack.getTag().contains("Damage")) {
			stack.getTag().putInt("Damage", 0);
		}
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		BlockState blockState = context.getLevel().getBlockState(context.getClickedPos());

		if (BlockExchangeManager.INSTANCE.blockConversionMap.containsKey(blockState.getBlock())) {
			if (!context.getLevel().isClientSide()) {
				context.getLevel().setBlock(context.getClickedPos(), BlockExchangeManager.INSTANCE.blockConversionMap.get(blockState.getBlock()).defaultBlockState(), 3);

				ItemStack stack = context.getPlayer().getItemInHand(context.getHand()).copy();
				damage(stack);

				if (getDamage(stack) >= MAX_DAMAGE) {
					stack.setCount(0);
				}

				context.getPlayer().setItemInHand(context.getHand(), stack);
			}

			return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag tooltipOptions) {
		tooltip.add(new TextComponent("Uses Left: ").append(new TextComponent(String.valueOf(MAX_DAMAGE - stack.getDamageValue()))).withStyle(ChatFormatting.GREEN));
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.EPIC;
	}
}
