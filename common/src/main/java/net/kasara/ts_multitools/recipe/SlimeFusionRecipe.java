package net.kasara.ts_multitools.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;

import java.util.List;
import java.util.Optional;

/**
 * 鍛冶台でスライムに武器・ツール・弓を合体させるレシピ
 */
public record SlimeFusionRecipe(Optional<Ingredient> template, Ingredient base, Ingredient addition) implements SmithingRecipe {

    private static final MapCodec<SlimeFusionRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.optionalFieldOf("template").forGetter(SlimeFusionRecipe::template),
            Ingredient.CODEC.fieldOf("base").forGetter(SlimeFusionRecipe::base),
            Ingredient.CODEC.fieldOf("addition").forGetter(SlimeFusionRecipe::addition)
    ).apply(instance, SlimeFusionRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, SlimeFusionRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC, SlimeFusionRecipe::template,
            Ingredient.CONTENTS_STREAM_CODEC, SlimeFusionRecipe::base,
            Ingredient.CONTENTS_STREAM_CODEC, SlimeFusionRecipe::addition,
            SlimeFusionRecipe::new);

    public static final RecipeSerializer<SlimeFusionRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    @Override
    public ItemStack assemble(SmithingRecipeInput input) {
        return SlimeFusionLogic.fuse(input.base(), input.addition(), template.isPresent());
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.createFromOptionals(List.of(template, Optional.of(base), Optional.of(addition)));
    }

    @Override
    public RecipeSerializer<SlimeFusionRecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public Optional<Ingredient> templateIngredient() {
        return template;
    }

    @Override
    public Ingredient baseIngredient() {
        return base;
    }

    @Override
    public Optional<Ingredient> additionIngredient() {
        return Optional.of(addition);
    }
}
