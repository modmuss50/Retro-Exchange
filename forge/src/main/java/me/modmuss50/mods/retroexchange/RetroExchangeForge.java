package me.modmuss50.mods.retroexchange;

import me.modmuss50.retroexchange.RetroExchange;
import me.shedaniel.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(RetroExchange.MOD_ID)
public class RetroExchangeForge {
	public RetroExchangeForge() {
		EventBuses.registerModEventBus(RetroExchange.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
		new RetroExchange();
	}
}
