package me.modmuss50.retroexchange;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

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
		stack.setDamageValue(stack.getDamageValue() + 1);

		if (stack.getDamageValue() >= stack.getMaxDamage()) {
			return ItemStack.EMPTY;
		}

		return stack;
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		Block block = level.getBlockState(context.getClickedPos()).getBlock();

		if (BlockExchangeManager.INSTANCE.blockConversionMap.containsKey(block)) {
			if (!level.isClientSide) {
				level.setBlockAndUpdate(context.getClickedPos(), BlockExchangeManager.INSTANCE.blockConversionMap.get(block).defaultBlockState());
				context.getItemInHand().hurtAndBreak(1, context.getPlayer(), player -> player.broadcastBreakEvent(context.getHand()));
			}

			return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag tooltipOptions) {
		tooltip.add(new TextComponent("Uses Left: ").append(new TextComponent(String.valueOf(stack.getMaxDamage() - stack.getDamageValue()))).withStyle(ChatFormatting.GREEN));
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.EPIC;
	}
}
