package me.modmuss50.retroexchange;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.datafix.fixes.BlockEntitySignTextStrictJsonFix;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.ItemLike;
import org.apache.commons.io.IOUtils;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;

public class TransmutationRecipeManager {
	private int count = 0;

	private final Map<RecipeType<?>, ImmutableMap.Builder<ResourceLocation, Recipe<?>>> recipeMap;

	public TransmutationRecipeManager(Map<RecipeType<?>, ImmutableMap.Builder<ResourceLocation, Recipe<?>>> recipeMap) {
		this.recipeMap = recipeMap;
	}

	public void apply(ResourceManager resourceManager) {
		count = 0;
		Collection<ResourceLocation> resources = resourceManager.listResources("retroexchange_transmutation", s -> s.endsWith(".json"));
		resources.forEach(resourceIdentifier -> {
			try {
				Resource resource = resourceManager.getResource(resourceIdentifier);
				String json = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
				resource.close();
				JsonArray jsonArray = BlockEntitySignTextStrictJsonFix.GSON.fromJson(json, JsonArray.class);
				try {
					jsonArray.forEach(this::handle);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				System.out.println("Failed to read " + resourceIdentifier);
				e.printStackTrace();
			}
		});

		addAllSmelting();
	}

	private void handle(JsonElement jsonElement) {
		if (!jsonElement.isJsonObject()) {
			throw new JsonSyntaxException("Not a json object");
		}
		JsonObject object = jsonElement.getAsJsonObject();

		String type = "transmute";
		if (object.has("type")) {
			type = object.get("type").getAsString();
		}

		if (type.equals("transmute")) {
			handleTransmute(object);
		} else {
			throw new JsonSyntaxException("Type not found");
		}

	}

	private void handleTransmute(JsonObject jsonObject) {
		JsonArray itemsArray = jsonObject.get("items").getAsJsonArray();
		if (itemsArray.size() != 2) {
			throw new JsonSyntaxException("items must have a size of 2");
		}

		ResourceLocation idA = new ResourceLocation(itemsArray.get(0).getAsString());
		ResourceLocation idB = new ResourceLocation(itemsArray.get(1).getAsString());
		Item itemA = Registry.ITEM.get(idA);
		if (itemA == Items.AIR) {
			throw new JsonSyntaxException("invalid item: " + idA);
		}
		Item itemB = Registry.ITEM.get(idB);
		if (itemB == Items.AIR) {
			throw new JsonSyntaxException("invalid item: " + idB);
		}

		int amount = jsonObject.get("amount").getAsInt();
		boolean reversible = true;
		if (jsonObject.has("reversible")) {
			reversible = jsonObject.get("reversible").getAsBoolean();
		}

		addTransmutationRecipe(itemA, itemB, amount, reversible);
	}

	public void addTransmutationRecipe(Item item, Object input, int size, boolean reversible) {
		Object[] inputs = new Object[size + 1];
		for (int i = 1; i < size + 1; i++) {
			inputs[i] = input;
		}
		inputs[0] = RetroExchangeItems.TRANSMUTATION_STONE.get();
		addShapelessRecipe(new ItemStack(item), inputs);

		if (!reversible) {
			return;
		}
		addShapelessRecipe(getStack(input, size), item, RetroExchangeItems.TRANSMUTATION_STONE.get());
	}

	public void addAllSmelting() {
		recipeMap.get(RecipeType.SMELTING).build()
				.values().stream()
				.filter(recipe -> recipe.getType() == RecipeType.SMELTING)
				.forEach(recipe -> {
					ItemStack output = recipe.getResultItem();

					for (Ingredient input : recipe.getIngredients()) {
						if (output.isEmpty() || input.isEmpty()) {
							return;
						}

						ItemStack copy = output.copy();
						copy.setCount(7);
						addShapelessRecipe(copy, RetroExchangeItems.TRANSMUTATION_STONE.get(), Items.COAL,
								input, input, input, input, input, input, input
						);
					}
				});
	}

	public ItemStack getStack(Object object, int size) {
		ItemStack stack = ItemStack.EMPTY;
		if (object instanceof ItemStack) {
			stack = ((ItemStack) object).copy();
		}
		if (object instanceof ItemLike) {
			stack = new ItemStack((ItemLike) object, size);
		}
		stack.setCount(size);
		return stack;
	}

	private ResourceLocation getNextID() {
		return new ResourceLocation("retroexchange", "transmutation/id_" + count++);
	}

	public void addShapelessRecipe(ItemStack stack, Object... input) {
		ShapelessRecipe recipe = new ShapelessRecipe(getNextID(), "", stack, buildInput(input));
		recipeMap.get(recipe.getType()).put(recipe.getId(), recipe);
	}

	private NonNullList<Ingredient> buildInput(Object[] input) {
		NonNullList<Ingredient> list = NonNullList.create();
		for (Object obj : input) {
			Ingredient ingredient = null;
			if (obj instanceof Ingredient) {
				ingredient = (Ingredient) obj;
			} else if (obj instanceof ItemStack) {
				throw new UnsupportedOperationException("thanks mojang");
			} else if (obj instanceof ItemLike) {
				ingredient = Ingredient.of((ItemLike) obj);
			}

			if (obj == null) {
				throw new JsonSyntaxException("Could not build ingredient for " + input.getClass().getName());
			}
			list.add(ingredient);
		}
		return list;
	}
}
