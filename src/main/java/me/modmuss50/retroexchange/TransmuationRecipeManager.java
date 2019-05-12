package me.modmuss50.retroexchange;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.crafting.ShapelessRecipe;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.commons.io.IOUtils;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;

import static net.minecraft.datafixers.fixes.BlockEntitySignTextStrictJsonFix.GSON;

public class TransmuationRecipeManager implements SimpleSynchronousResourceReloadListener {

	private static int count = 0;

	@Override
	public void apply(ResourceManager resourceManager) {
		count = 0;
		Collection<Identifier> resources = resourceManager.findResources("retroexchange_transmution", s -> s.endsWith(".json"));
		resources.forEach(resourceIdentifier -> {
			try {
				Resource resource = resourceManager.getResource(resourceIdentifier);
				String json = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
				resource.close();
				JsonArray jsonArray = GSON.fromJson(json, JsonArray.class);
				jsonArray.forEach(this::handle);


			} catch (Exception e) {
				System.out.println("Failed to read " + resourceIdentifier);
				e.printStackTrace();
			}
		});

		addAllSmelting();

		System.out.println("Loaded " + count + " transmutation recipes");
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
		addTransmuteRecipe(itemA, itemB, amount);
	}

	public void addTransmuteRecipe(Object object, Object input, int size) {
		ItemStack output = getStack(object);

		Object[] inputs = new Object[size + 1];
		for (int i = 1; i < size + 1; i++) {
			inputs[i] = input;
		}
		inputs[0] = getStoneStack();
		addShapelessRecipe(output, inputs);

		addShapelessRecipe(getStack(input, size), output, getStoneStack());

	}

	public void addAllSmelting() {
		getRecipeManager()
			.values().stream()
			.filter(recipe -> recipe.getType() == RecipeType.SMELTING)
			.forEach(recipe -> {
				ItemStack output = recipe.getOutput();
				Ingredient input = recipe.getPreviewInputs().get(0);
				if (output.isEmpty() || input.isEmpty()) {
					return;
				}

				ItemStack copy = output.copy();
				copy.setAmount(7);
				addShapelessRecipe(copy, getStoneStack(), new ItemStack(Items.COAL),
					input, input, input, input, input, input, input
				);

			});
	}

	public static ItemStack getStoneStack() {
		return new ItemStack(RetroExchange.transmutationStone, 1);
	}

	public static ItemStack getStack(Object object) {
		return getStack(object, 1);
	}

	public static ItemStack getStack(Object object, int size) {
		ItemStack stack = ItemStack.EMPTY;
		if (object instanceof ItemStack) {
			stack = ((ItemStack) object).copy();
		}
		if (object instanceof ItemConvertible) {
			stack = new ItemStack((ItemConvertible) object, size);
		}
		stack.setAmount(size);
		return stack;
	}

	private Identifier getNextID() {
		return new Identifier("retroexchange", "transmution_id_" + count++);
	}

	public void addShapelessRecipe(ItemStack output, Object... input) {
		ShapelessRecipe recipe = new ShapelessRecipe(getNextID(), "", output, buildInput(input));
		getRecipeManager().add(recipe);
	}

	private static RecipeManager getRecipeManager() {
		return RetroExchange.getServer().getRecipeManager();
	}

	private static DefaultedList<Ingredient> buildInput(Object[] input) {
		DefaultedList<Ingredient> list = DefaultedList.create();
		for (Object obj : input) {
			Ingredient ingredient = null;
			if (obj instanceof Ingredient) {
				ingredient = (Ingredient) obj;
			} else if (obj instanceof ItemStack) {
				ItemStack stack = (ItemStack) obj;
				System.out.println("called");
				ingredient = Ingredient.ofStacks(stack.copy());
				System.out.println("not called");
			} else if (obj instanceof ItemConvertible) {
				ingredient = Ingredient.ofStacks(new ItemStack((ItemConvertible) obj));
			}

			if (obj == null) {
				throw new JsonSyntaxException("Could not build ingredient for " + input.getClass().getName());
			}
			list.add(ingredient);
		}
		return list;
	}

	@Override
	public Identifier getFabricId() {
		return new Identifier("retroexchange", "transmutation");
	}

	//Forces this to load after the recipes, so we can add recipes based on other recipes :)
	@Override
	public Collection<Identifier> getFabricDependencies() {
		return Collections.singletonList(ResourceReloadListenerKeys.RECIPES);
	}
}
