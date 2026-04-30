package net.kasara.ts_multitools.recipe;

import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.TSMultiTools;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipeSerializers {

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, TokorotenSlimeAPI.getModId());

    // スライムのレシピシリアライザー
    public static DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SlimeRecipe>> CRAFTING_SLIME =
            SERIALIZERS.register("crafting_slime", () -> SlimeRecipe.INSTANCE);

    public static void register(IEventBus modEventBus) {
        SERIALIZERS.register(modEventBus);

        // ログ出力
        TSMultiTools.LOGGER.info("Registering addon Mod Recipe Serializers for "+ TokorotenSlimeAPI.getModId() +" (from " + TSMultiTools.MOD_ID + ")");
    }
}