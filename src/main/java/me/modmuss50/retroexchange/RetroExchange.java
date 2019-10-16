package me.modmuss50.retroexchange;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.Supplier;

public class RetroExchange implements ModInitializer {

	public static Item transmutationShard;
	public static Item transmutationStone;

	public static ItemGroup ITEM_GROUP;

	public static int dropChance = 25;

	@Override
	public void onInitialize() {
		ITEM_GROUP = FabricItemGroupBuilder.build(new Identifier("retroexchange", "item_group"), () -> new ItemStack(transmutationStone));

		transmutationShard = new ItemTransmutationShard();
		transmutationStone = new ItemTransmutationStone();

		Registry.register(Registry.ITEM, new Identifier("retroexchange", "transmutation_shard"), transmutationShard);
		Registry.register(Registry.ITEM, new Identifier("retroexchange", "transmutation_stone"), transmutationStone);

		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(BlockExchangeManager.INSTANCE);
	}

	public static MinecraftServer getServer(){
		if(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT){
			return RetroRxchangeClient.getServer();
		} else {
			return (MinecraftServer) FabricLoader.getInstance().getGameInstance();
		}
	}

}
