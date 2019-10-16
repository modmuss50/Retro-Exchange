package me.modmuss50.retroexchange.mixin;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import me.modmuss50.retroexchange.TransmuationRecipeManager;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@Mixin(RecipeManager.class)
public class MixinRecipeManager {

	@Inject(method = "method_20705", at = @At(value = "INVOKE", target = "Ljava/util/Map;entrySet()Ljava/util/Set;", ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD)
	protected void method_20705(Map<Identifier, JsonObject> map, ResourceManager resourceManager, Profiler profiler, CallbackInfo info, Map<RecipeType<?>, ImmutableMap.Builder<Identifier, Recipe<?>>> recipeMap) {
		new TransmuationRecipeManager((RecipeManager) (Object)this, recipeMap).apply(resourceManager);
	}
}
