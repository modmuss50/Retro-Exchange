package me.modmuss50.mods.retroexchange;

import me.modmuss50.retroexchange.RetroExchange;
import net.fabricmc.api.ModInitializer;

public class RetroExchangeFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		new RetroExchange();
	}
}
