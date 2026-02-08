package net.kasara.ts_multitools.recipe;

import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.TSMultitools;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * MOD 専用のレシピ登録クラス
 * SlimeRecipe の RecipeType と Serializer を登録する
 */
public class ModRecipes {

    // SlimeRecipe 専用の RecipeType を作成・登録
    public static final RecipeType<SpecialCraftingRecipe> SLIME_RECIPE =
            Registry.register(Registries.RECIPE_TYPE,
                    Identifier.of(TokorotenSlimeAPI.getModId(), "crafting_slime"), // レシピタイプのID
                    new RecipeType<>() {}); // 匿名クラスで空の RecipeType

    /**
     * レシピシリアライザの登録
     */
    public static void register() {
        // SlimeRecipe を扱う Serializer を登録
        Registry.register(Registries.RECIPE_SERIALIZER,
                Identifier.of(TokorotenSlimeAPI.getModId(), "crafting_slime"),
                SlimeRecipeSerializer.INSTANCE);

        // ログに出力（MODロード時の確認用）
        TSMultitools.LOGGER.info("Registering addon Recipes for "+ TokorotenSlimeAPI.getModId() +" (from " + TSMultitools.MOD_ID + ")");
    }
}
