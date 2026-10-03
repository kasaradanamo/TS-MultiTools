package net.kasara.ts_multitools.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kasara.ts_multitools.item.ModItemsCommon;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;

import java.util.stream.Stream;

/**
 * 鍛冶台でスライムに武器・ツール・弓を合体させるレシピ
 */
public record SlimeFusionRecipe(Ingredient template, Ingredient base, Ingredient addition) implements SmithingRecipe {

    public static final RecipeSerializer<SlimeFusionRecipe> SERIALIZER = new Serializer();

    @Override
    public boolean matches(SmithingRecipeInput input, Level level) {
        return template.test(input.template()) && base.test(input.base()) && addition.test(input.addition());
    }

    @Override
    public ItemStack assemble(SmithingRecipeInput input, HolderLookup.Provider registries) {
        return SlimeFusionLogic.fuse(input.base(), input.addition(), !template.isEmpty());
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return new ItemStack(ModItemsCommon.SLIME);
    }

    @Override
    public boolean isTemplateIngredient(ItemStack stack) {
        return template.test(stack);
    }

    @Override
    public boolean isBaseIngredient(ItemStack stack) {
        return base.test(stack);
    }

    @Override
    public boolean isAdditionIngredient(ItemStack stack) {
        return addition.test(stack);
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public boolean isIncomplete() {
        return Stream.of(base, addition).anyMatch(ingredient -> ingredient.getItems().length == 0);
    }

    @Override
    public RecipeSerializer<SlimeFusionRecipe> getSerializer() {
        return SERIALIZER;
    }

    private static final class Serializer implements RecipeSerializer<SlimeFusionRecipe> {

        private static final MapCodec<SlimeFusionRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.optionalFieldOf("template", Ingredient.EMPTY).forGetter(SlimeFusionRecipe::template),
                Ingredient.CODEC_NONEMPTY.fieldOf("base").forGetter(SlimeFusionRecipe::base),
                Ingredient.CODEC_NONEMPTY.fieldOf("addition").forGetter(SlimeFusionRecipe::addition)
        ).apply(instance, SlimeFusionRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, SlimeFusionRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, SlimeFusionRecipe::template,
                Ingredient.CONTENTS_STREAM_CODEC, SlimeFusionRecipe::base,
                Ingredient.CONTENTS_STREAM_CODEC, SlimeFusionRecipe::addition,
                SlimeFusionRecipe::new);

        @Override
        public MapCodec<SlimeFusionRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SlimeFusionRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
