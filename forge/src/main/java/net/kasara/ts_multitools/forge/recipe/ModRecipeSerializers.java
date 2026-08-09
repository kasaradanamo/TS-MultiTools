package net.kasara.ts_multitools.forge.recipe;

import net.kasara.tokorotenslime.TokorotenSlimeCommon;
import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.TSMultiToolsCommon;
import net.kasara.ts_multitools.recipe.SlimeRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeSerializers {

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, TokorotenSlimeCommon.MOD_ID);

    // スライムのレシピシリアライザー
    public static final RegistryObject<RecipeSerializer<SlimeRecipe>> CRAFTING_SLIME =
            SERIALIZERS.register("crafting_slime", () -> SlimeRecipe.INSTANCE);

    public static void register(IEventBus modEventBus) {
        SERIALIZERS.register(modEventBus);

        // ログ出力
        TSMultiToolsCommon.LOGGER.info("Registering addon Mod Recipe Serializers for " + TokorotenSlimeAPI.getModId() + " (from " + TSMultiToolsCommon.MOD_ID + ")");
    }
}
