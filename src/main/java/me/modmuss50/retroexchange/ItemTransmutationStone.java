package me.modmuss50.retroexchange;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipOptions;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;

import java.util.List;

public class ItemTransmutationStone extends Item implements ExtendedRecipeRemainder {

	public static int maxDamage = 1500;

	public ItemTransmutationStone() {
		super(new Item.Settings()
			.itemGroup(RetroExchange.ITEM_GROUP)
			.stackSize(1)
			.durability(maxDamage)
		);
	}

	@Override
	public ItemStack getRemainderStack(ItemStack stack) {
		damage(stack);
		if(getDamage(stack) >= maxDamage){
			return ItemStack.EMPTY;
		}
		return stack;
	}


	public static int getDamage(ItemStack stack){
		checkTag(stack);
		return stack.getTag().getInt("Damage");
	}

	public static ItemStack damage(ItemStack stack){
		checkTag(stack);
		stack.getTag().putInt("Damage", getDamage(stack) + 1);
		return stack;
	}

	private static void checkTag(ItemStack stack){
		if(!stack.hasTag()){
			System.out.println("new tag");
			stack.setTag(new CompoundTag());
		}
		if(!stack.getTag().containsKey("Damage")){
			System.out.println("new damage");
			stack.getTag().putInt("Damage", 0);
		}
	}

	public ActionResult useOnBlock(ItemUsageContext context) {
		BlockState blockState = context.getWorld().getBlockState(context.getBlockPos());
		if(BlockExchangeManager.INSTANCE.blockConversionMap.containsKey(blockState.getBlock())){
			if(!context.getWorld().isClient){
				context.getWorld().setBlockState(context.getBlockPos(), BlockExchangeManager.INSTANCE.blockConversionMap.get(blockState.getBlock()).getDefaultState());

				ItemStack stack = context.getPlayer().getStackInHand(Hand.MAIN).copy();
				damage(stack);
				if(getDamage(stack) >= maxDamage){
					stack.setAmount(0);
				}
				context.getPlayer().setStackInHand(Hand.MAIN, stack);
			}
			return ActionResult.SUCCESS;
		}
		return ActionResult.PASS;
	}

	@Environment(EnvType.CLIENT)
	public void buildTooltip(ItemStack stack, World world, List<TextComponent> tooltip, TooltipOptions tooltipOptions) {
		tooltip.add(new StringTextComponent("Uses Left: " + TextFormat.GREEN + (maxDamage - getDamage(stack))));
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.EPIC;
	}
}
