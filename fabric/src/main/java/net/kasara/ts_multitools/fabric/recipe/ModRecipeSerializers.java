package net.kasara.ts_multitools.fabric.recipe;

import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.fabric.TSMultiTools;
import net.kasara.ts_multitools.recipe.SlimeRecipe;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class ModRecipeSerializers {

    // スライムのレシピシリアライザー
    public static RecipeSerializer<SlimeRecipe> CRAFTING_SLIME;

    public static void register() {
        CRAFTING_SLIME = registerRecipeSerializer("crafting_slime", SlimeRecipe.INSTANCE);

        // ログ出力
        TSMultiTools.LOGGER.info("Registering addon Mod Recipe Serializers for "+ TokorotenSlimeAPI.getModId() +" (from " + TSMultiTools.MOD_ID + ")");
    }

    private static <T extends Recipe<?>> RecipeSerializer<T> registerRecipeSerializer(String name, RecipeSerializer<T> instance) {
        return Registry.register(BuiltInRegistries.RECIPE_SERIALIZER,
                ResourceLocation.fromNamespaceAndPath(TokorotenSlimeAPI.getModId(), name),
                instance);
    }
}
