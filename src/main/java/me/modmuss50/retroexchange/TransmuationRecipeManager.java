package me.modmuss50.retroexchange;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.*;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.commons.io.IOUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static net.minecraft.datafixer.fix.BlockEntitySignTextStrictJsonFix.GSON;

public class TransmuationRecipeManager {

	private int count = 0;

	private final RecipeManager recipeManager;
	private final Map<RecipeType<?>, ImmutableMap.Builder<Identifier, Recipe<?>>> recipeMap;

	public TransmuationRecipeManager(RecipeManager recipeManager, Map<RecipeType<?>, ImmutableMap.Builder<Identifier, Recipe<?>>> recipeMap) {
		this.recipeManager = recipeManager;
		this.recipeMap = recipeMap;
	}

	public void apply(ResourceManager resourceManager) {
		count = 0;
		Collection<Identifier> resources = resourceManager.findResources("retroexchange_transmution", s -> s.endsWith(".json"));
		resources.forEach(resourceIdentifier -> {
			try {
				Resource resource = resourceManager.getResource(resourceIdentifier);
				String json = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
				resource.close();
				JsonArray jsonArray = GSON.fromJson(json, JsonArray.class);
				try {
					jsonArray.forEach(this::handle);
				} catch (Throwable e){
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

		Identifier idA = new Identifier(itemsArray.get(0).getAsString());
		Identifier idB = new Identifier(itemsArray.get(1).getAsString());
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
		if(jsonObject.has("reversible")){
			reversible = jsonObject.get("reversible").getAsBoolean();
		}

		addTransmuteRecipe(itemA, itemB, amount, reversible);
	}

	public void addTransmuteRecipe(Item item, Object input, int size, boolean reversible) {
		Object[] inputs = new Object[size + 1];
		for (int i = 1; i < size + 1; i++) {
			inputs[i] = input;
		}
		inputs[0] = RetroExchange.transmutationStone;
		addShapelessRecipe(new ItemStack(item), inputs);

		if(!reversible) return;
		addShapelessRecipe(getStack(input, size), item, RetroExchange.transmutationStone);
	}

	public void addAllSmelting() {
		recipeMap.get(RecipeType.SMELTING).build()
			.values().stream()
			.filter(recipe -> recipe.getType() == RecipeType.SMELTING)
			.forEach(recipe -> {
				ItemStack output = recipe.getOutput();

				for(Ingredient input : recipe.getPreviewInputs()){
					if (output.isEmpty() || input.isEmpty()) {
						return;
					}

					ItemStack copy = output.copy();
					copy.setCount(7);
					addShapelessRecipe(copy, RetroExchange.transmutationStone, Items.COAL,
					                   input, input, input, input, input, input, input
					);
				}
			});
	}

	public ItemStack getStack(Object object) {
		return getStack(object, 1);
	}

	public ItemStack getStack(Object object, int size) {
		ItemStack stack = ItemStack.EMPTY;
		if (object instanceof ItemStack) {
			stack = ((ItemStack) object).copy();
		}
		if (object instanceof ItemConvertible) {
			stack = new ItemStack((ItemConvertible) object, size);
		}
		stack.setCount(size);
		return stack;
	}

	private Identifier getNextID() {
		return new Identifier("retroexchange", "transmution_id_" + count++);
	}

	public void addShapelessRecipe(ItemStack stack, Object... input) {
		ShapelessRecipe recipe = new ShapelessRecipe(getNextID(), "", stack, buildInput(input));
		recipeMap.get(recipe.getType()).put(recipe.getId(), recipe);
	}

	private DefaultedList<Ingredient> buildInput(Object[] input) {
		DefaultedList<Ingredient> list = DefaultedList.of();
		for (Object obj : input) {
			Ingredient ingredient = null;
			if (obj instanceof Ingredient) {
				ingredient = (Ingredient) obj;
			} else if (obj instanceof ItemStack) {
				throw new UnsupportedOperationException("thanks mojang");
			} else if (obj instanceof ItemConvertible) {
				ingredient = Ingredient.ofItems((ItemConvertible) obj);
			}

			if (obj == null) {
				throw new JsonSyntaxException("Could not build ingredient for " + input.getClass().getName());
			}
			list.add(ingredient);
		}
		return list;
	}
}
