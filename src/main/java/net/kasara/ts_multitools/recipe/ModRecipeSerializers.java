package net.kasara.ts_multitools.recipe;

import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.TSMultitools;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

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
        return Registry.register(BuiltInRegistries.RECIPE_SERIALIZER,
                Identifier.fromNamespaceAndPath(TokorotenSlimeAPI.getModId(), name),
                instance);
    }
}