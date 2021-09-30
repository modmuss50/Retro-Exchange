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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BlockExchangeManager implements ResourceManagerReloadListener {

	public static final BlockExchangeManager INSTANCE = new BlockExchangeManager();

	public Map<Block, Block> blockConversionMap = new HashMap<>();

	@Override
	public void onResourceManagerReload(ResourceManager resourceManager) {
		blockConversionMap.clear();

		Collection<ResourceLocation> resources = resourceManager.listResources("retroexchange", s -> s.equals("block_conversion.json"));
		resources.forEach(resourceIdentifier -> {
			try {
				Resource resource = resourceManager.getResource(resourceIdentifier);
				String json = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
				JsonObject jsonObject = BlockEntitySignTextStrictJsonFix.GSON.fromJson(json, JsonObject.class);
				jsonObject.entrySet().forEach(entry -> {
					String input = entry.getKey();
					String output = entry.getValue().getAsString();

					Block inputBlock = Registry.BLOCK.get(new ResourceLocation(input));
					Block outputBlock = Registry.BLOCK.get(new ResourceLocation(output));

					if (inputBlock == Blocks.AIR) {
						System.out.println(input + " is an unknown block");
						return;
					}

					if (inputBlock == Blocks.AIR) {
						System.out.println(output + " is an unknown block");
						return;
					}

					blockConversionMap.put(inputBlock, outputBlock);
				});

				resource.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}
