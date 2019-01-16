package me.modmuss50.retroexchange;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import reborncore.common.registration.RebornRegistry;
import reborncore.common.registration.impl.ConfigRegistry;

@RebornRegistry(modID = "retroexchange")
public class ItemTransmutationStone extends Item {

	@ConfigRegistry(key = "stone_max_damage", comment = "The max damage of a shard")
	public static int maxDamage = 1000;

	public ItemTransmutationStone() {
		setCreativeTab(RetroExchange.CREATIVE_TAB);
		setRegistryName(new ResourceLocation("retroexchange", "transmutation_stone"));
		setUnlocalizedName("retroexchange.transmutation_stone");
		setMaxDamage(maxDamage);
	}

	@Override
	public boolean hasContainerItem(ItemStack stack) {
		return true;
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
}