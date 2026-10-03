package net.kasara.ts_multitools.recipe;

import com.google.gson.JsonObject;
import net.kasara.ts_multitools.item.ModItemsCommon;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.level.Level;

import java.util.stream.Stream;

/**
 * 鍛冶台でスライムに武器・ツール・弓を合体させるレシピ
 */
public class SlimeFusionRecipe implements SmithingRecipe {

    public static final RecipeSerializer<SlimeFusionRecipe> SERIALIZER = new Serializer();

    private final ResourceLocation id;
    private final Ingredient template;
    private final Ingredient base;
    private final Ingredient addition;

    public SlimeFusionRecipe(ResourceLocation id, Ingredient template, Ingredient base, Ingredient addition) {
        this.id = id;
        this.template = template;
        this.base = base;
        this.addition = addition;
    }

    @Override
    public boolean matches(Container container, Level level) {
        return template.test(container.getItem(0)) && base.test(container.getItem(1)) && addition.test(container.getItem(2));
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registries) {
        return SlimeFusionLogic.fuse(container.getItem(1), container.getItem(2), !template.isEmpty());
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registries) {
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
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    private static final class Serializer implements RecipeSerializer<SlimeFusionRecipe> {

        @Override
        public SlimeFusionRecipe fromJson(ResourceLocation id, JsonObject json) {
            Ingredient template = GsonHelper.isValidNode(json, "template")
                    ? Ingredient.fromJson(GsonHelper.getNonNull(json, "template"))
                    : Ingredient.EMPTY;
            Ingredient base = Ingredient.fromJson(GsonHelper.getNonNull(json, "base"));
            Ingredient addition = Ingredient.fromJson(GsonHelper.getNonNull(json, "addition"));
            return new SlimeFusionRecipe(id, template, base, addition);
        }

        @Override
        public SlimeFusionRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            Ingredient template = Ingredient.fromNetwork(buf);
            Ingredient base = Ingredient.fromNetwork(buf);
            Ingredient addition = Ingredient.fromNetwork(buf);
            return new SlimeFusionRecipe(id, template, base, addition);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, SlimeFusionRecipe recipe) {
            recipe.template.toNetwork(buf);
            recipe.base.toNetwork(buf);
            recipe.addition.toNetwork(buf);
        }
    }
}
