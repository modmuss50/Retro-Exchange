package me.modmuss50.retroexchange;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.commons.io.IOUtils;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static net.minecraft.datafixers.fixes.BlockEntitySignTextStrictJsonFix.GSON;

public class BlockExchangeManager implements SimpleSynchronousResourceReloadListener {

	public static final BlockExchangeManager INSTANCE = new BlockExchangeManager();

	public Map<Block, Block> blockConversionMap = new HashMap<>();

	@Override
	public void apply(ResourceManager resourceManager) {
		blockConversionMap.clear();

		Collection<Identifier> resources = resourceManager.findResources("retroexchange", s -> s.equals("block_conversion.json"));
		resources.forEach(resourceIdentifier -> {
			try {
				Resource resource = resourceManager.getResource(resourceIdentifier);
				String json = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
				JsonObject jsonObject = GSON.fromJson(json, JsonObject.class);
				jsonObject.entrySet().forEach(entry -> {
					String input = entry.getKey();
					String output = entry.getValue().getAsString();

					Block inputBlock = Registry.BLOCK.get(new Identifier(input));
					Block outputBlock = Registry.BLOCK.get(new Identifier(output));

					if (inputBlock == Blocks.AIR){
						System.out.println(input + " is an unknown block");
						return;
					}

					if (inputBlock == Blocks.AIR){
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

	@Override
	public Identifier getFabricId() {
		return new Identifier("retroexchange", "block_exchange");
	}

}
