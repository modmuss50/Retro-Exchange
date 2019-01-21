package me.modmuss50.retroexchange;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import reborncore.common.registration.RebornRegistry;
import reborncore.common.registration.impl.ConfigRegistry;

import javax.annotation.Nullable;
import java.util.List;

@RebornRegistry(modID = "retroexchange")
public class ItemTransmutationStone extends Item {

	@ConfigRegistry(key = "stone_max_damage", comment = "The max damage of a shard")
	public static int maxDamage = 1500;

	public ItemTransmutationStone() {
		setCreativeTab(RetroExchange.CREATIVE_TAB);
		setRegistryName(new ResourceLocation("retroexchange", "transmutation_stone"));
		setUnlocalizedName("retroexchange.transmutation_stone");
		setMaxDamage(maxDamage);
		setMaxStackSize(1);
	}

	@Override
	public boolean hasContainerItem(ItemStack stack) {
		return stack.getItem() == this;
	}

	@Override
	public ItemStack getContainerItem(ItemStack itemStack) {
		ItemStack newStack = itemStack.copy();
		newStack.setItemDamage(itemStack.getItemDamage() + 1);
		return newStack;
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		IBlockState blockState = worldIn.getBlockState(pos);
		if(RetroExchange.blockConversionMap.containsKey(blockState.getBlock())){
			if(!worldIn.isRemote){
				worldIn.setBlockState(pos, RetroExchange.blockConversionMap.get(blockState.getBlock()).getDefaultState());

				ItemStack stack = player.getHeldItem(hand).copy();
				stack.setItemDamage(stack.getItemDamage() + 1);
				if(stack.getItemDamage() >= maxDamage){
					stack.setCount(0);
				}
				player.setHeldItem(hand, stack);
			}
			return EnumActionResult.SUCCESS;
		}
		return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack,
	                           @Nullable
		                           World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add("Uses Left: " + TextFormatting.GREEN + (maxDamage - stack.getItemDamage()));
		super.addInformation(stack, worldIn, tooltip, flagIn);
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.EPIC;
	}
}
