package me.modmuss50.retroexchange.mixin;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import me.modmuss50.retroexchange.TransmutationRecipeManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@Mixin(RecipeManager.class)
public class MixinRecipeManager {
	@Inject(method = "apply", at = @At(value = "INVOKE", target = "Ljava/util/Map;entrySet()Ljava/util/Set;", ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD)
	protected void applyRE(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profilerFiller, CallbackInfo ci, Map<RecipeType<?>, ImmutableMap.Builder<ResourceLocation, Recipe<?>>> recipeMap) {
		new TransmutationRecipeManager(recipeMap).apply(resourceManager);
	}
}
