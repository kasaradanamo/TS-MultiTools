package net.kasara.ts_multitools.recipe;

import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.TSMultitools;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * レシピシリアライザー登録クラス
 */
public class ModRecipeSerializers {

    // スライムのレシピシリアライザー
    public static RecipeSerializer<SlimeRecipe> CRAFTING_SLIME;

    public static void register() {
        CRAFTING_SLIME = registerRecipeSerializer("crafting_slime", SlimeRecipe.INSTANCE);

        // ログ出力
        TSMultitools.LOGGER.info("Registering addon Recipe Serializers for "+ TokorotenSlimeAPI.getModId() +" (from " + TSMultitools.MOD_ID + ")");
    }

    private static <T extends Recipe<?>> RecipeSerializer<T> registerRecipeSerializer(String name, RecipeSerializer<T> instance) {
        return Registry.register(Registries.RECIPE_SERIALIZER,
                Identifier.of(TokorotenSlimeAPI.getModId(), name),
                instance);
    }
}
