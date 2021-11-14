package me.modmuss50.retroexchange;

import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.datafix.fixes.BlockEntitySignTextStrictJsonFix;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.apache.commons.io.IOUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static me.modmuss50.retroexchange.RetroExchange.MOD_ID;

public class BlockExchangeManager implements ResourceManagerReloadListener {

	public static final BlockExchangeManager INSTANCE = new BlockExchangeManager();

	public final Map<Block, Block> blockConversionMap = new HashMap<>();

	@Override
	public void onResourceManagerReload(ResourceManager resourceManager) {
		blockConversionMap.clear();

		for (var resourceIdentifier : resourceManager.listResources(MOD_ID, s -> s.equals("block_conversion.json"))) {
			try (Resource resource = resourceManager.getResource(resourceIdentifier)) {
				String json = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
				JsonObject jsonObject = BlockEntitySignTextStrictJsonFix.GSON.fromJson(json, JsonObject.class);
				jsonObject.entrySet().forEach(entry -> {
					String input = entry.getKey();
					String output = entry.getValue().getAsString();

					Block inputBlock = Registry.BLOCK.get(new ResourceLocation(input));
					Block outputBlock = Registry.BLOCK.get(new ResourceLocation(output));

					if (inputBlock == Blocks.AIR) {
						RetroExchange.LOGGER.error("{} is an unknown block", input);
						return;
					}

					if (outputBlock == Blocks.AIR) {
						RetroExchange.LOGGER.error("{} is an unknown block", output);
						return;
					}

					blockConversionMap.put(inputBlock, outputBlock);
				});
			} catch (Exception e) {
				RetroExchange.LOGGER.error(e);
			}
		}
	}
}
