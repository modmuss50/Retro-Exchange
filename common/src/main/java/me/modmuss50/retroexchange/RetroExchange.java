package me.modmuss50.retroexchange;

import me.shedaniel.architectury.event.events.EntityEvent;
import me.shedaniel.architectury.registry.CreativeTabs;
import me.shedaniel.architectury.registry.ReloadListeners;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class RetroExchange {
	public static final String MOD_ID = "retroexchange";

	public static CreativeModeTab tab;

	public static int dropChance = 25;

	public RetroExchange() {
		tab = CreativeTabs.create(new ResourceLocation("retroexchange", "item_group"), () -> new ItemStack(RetroExchangeItems.TRANSMUTATION_STONE.get()));
		RetroExchangeItems.init();
		ReloadListeners.registerReloadListener(PackType.SERVER_DATA, BlockExchangeManager.INSTANCE);
		EntityEvent.LIVING_DEATH.register(this::livingDeath);
	}

	private InteractionResult livingDeath(LivingEntity entity, DamageSource source) {
		if (entity instanceof Monster && !entity.level.isClientSide() && source.getEntity() instanceof Player && entity.level.random.nextInt(RetroExchange.dropChance) == 0) {
			entity.spawnAtLocation(RetroExchangeItems.TRANSMUTATION_SHARD.get(), 1);
		}

		return InteractionResult.PASS;
	}
}
