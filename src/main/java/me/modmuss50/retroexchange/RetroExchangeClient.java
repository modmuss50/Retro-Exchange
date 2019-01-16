package me.modmuss50.retroexchange;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(Side.CLIENT)
@SideOnly(Side.CLIENT)
public class RetroExchangeClient {

	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event){
		ModelLoader.setCustomModelResourceLocation(RetroExchange.transmutationShard, 0, new ModelResourceLocation(RetroExchange.transmutationShard.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(RetroExchange.transmutationStone, 0, new ModelResourceLocation(RetroExchange.transmutationStone.getRegistryName(), "inventory"));
	}

}
