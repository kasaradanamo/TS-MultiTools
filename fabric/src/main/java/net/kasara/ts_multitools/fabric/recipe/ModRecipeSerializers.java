package net.kasara.ts_multitools.fabric.recipe;

import net.kasara.tokorotenslime.TokorotenSlimeCommon;
import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.TSMultiToolsCommon;
import net.kasara.ts_multitools.recipe.SlimeRecipe;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class ModRecipeSerializers {

    // スライムのレシピシリアライザー
    public static final RecipeSerializer<SlimeRecipe> CRAFTING_SLIME = Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER,
            new ResourceLocation(TokorotenSlimeCommon.MOD_ID, "crafting_slime"),
            SlimeRecipe.INSTANCE
    );

    public static void register() {
        // ログ出力
        TSMultiToolsCommon.LOGGER.info("Registering addon Mod Recipe Serializers for " + TokorotenSlimeAPI.getModId() + " (from " + TSMultiToolsCommon.MOD_ID + ")");
    }
}
