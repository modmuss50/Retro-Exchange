package me.modmuss50.retroexchange;

import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;

public class RetroRxchangeClient {

	public static MinecraftServer getServer(){
		return MinecraftClient.getInstance().getServer();
	}

}
