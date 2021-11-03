package me.modmuss50.retroexchange;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.ReloadListenerRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RetroExchange {
	public static final String MOD_ID = "retroexchange";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	public static CreativeModeTab tab;

	public static final int dropChance = 25;

	public static void init() {
		tab = CreativeTabRegistry.create(new ResourceLocation(MOD_ID, "item_group"), () -> new ItemStack(RetroExchangeItems.TRANSMUTATION_STONE.get()));
		RetroExchangeItems.init();
		ReloadListenerRegistry.register(PackType.SERVER_DATA, BlockExchangeManager.INSTANCE);
		EntityEvent.LIVING_DEATH.register(RetroExchange::livingDeath);
	}

	private static EventResult livingDeath(LivingEntity entity, DamageSource source) {
		if (entity instanceof Monster && !entity.level.isClientSide() && source.getEntity() instanceof Player && entity.level.random.nextInt(RetroExchange.dropChance) == 0) {
			entity.spawnAtLocation(RetroExchangeItems.TRANSMUTATION_SHARD.get(), 1);
		}

		return EventResult.pass();
	}
}
