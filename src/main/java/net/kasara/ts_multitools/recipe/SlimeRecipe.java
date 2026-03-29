package net.kasara.ts_multitools.recipe;

import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.component.MiningEnchantLevelComponent;
import net.kasara.ts_multitools.component.ModComponents;
import net.kasara.ts_multitools.item.ModItems;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.recipe.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.recipe.display.SlotDisplay;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;

/**
 * スライムのレシピ
 */
public class SlimeRecipe extends SpecialCraftingRecipe {

    public static final RecipeSerializer<SlimeRecipe> INSTANCE =
            new SpecialRecipeSerializer<>(category -> new SlimeRecipe());

    public SlimeRecipe() {
        super(CraftingRecipeCategory.EQUIPMENT);    // クラフトカテゴリ：装備
    }

    /**
     * スライムのレシピ判定
     */
    @Override
    public boolean matches(CraftingRecipeInput input, World world) {
        int slimeCount = 0;
        boolean hasSword = false, hasPickaxe = false, hasAxe = false,
                hasShovel = false, hasHoe = false, hasBow = false, hasStar = false;

        boolean hasMultitool = false;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getStackInSlot(i);
            if (stack.isEmpty()) continue;

            // 必要なアイテムを判定
            var item = stack.getItem();
            if (item == Items.SLIME_BALL) slimeCount++;
            else if (item == Items.NETHERITE_SWORD) hasSword = true;
            else if (item == Items.NETHERITE_PICKAXE) hasPickaxe = true;
            else if (item == Items.NETHERITE_AXE) hasAxe = true;
            else if (item == Items.NETHERITE_SHOVEL) hasShovel = true;
            else if (item == Items.NETHERITE_HOE) hasHoe = true;
            else if (item == Items.BOW) hasBow = true;
            else if (item == Items.NETHER_STAR) hasStar = true;

            // レシピ2用
            else if (item == ModItems.NETHERITE_MULTITOOL) hasMultitool = true;

            else return false;  // 不要なアイテムがあればレシピ不一致
        }

        //　レシピ1（ネザライトツール単体）
        boolean recipe1 = slimeCount == 2 && hasSword && hasPickaxe && hasAxe&&
                            hasShovel && hasHoe && hasBow && hasStar;

        // レシピ2（マルチツール）
        boolean recipe2 = slimeCount == 2 && hasMultitool && hasBow && hasStar &&
                            !(hasSword || hasPickaxe || hasAxe || hasShovel || hasHoe);

        return recipe1 || recipe2;
    }

    /**
     * リザルトアイテムの処理
     */
    @Override
    public ItemStack craft(CraftingRecipeInput input, RegistryWrapper.WrapperLookup registries) {
        ItemStack result = new ItemStack(ModItems.SLIME);

        // エンチャントのビルダー初期化
        ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);

        // マイニングエンチャントのコンポーネントを取得
        MiningEnchantLevelComponent comp = result.get(ModComponents.MINING_ENCHANT_LEVEL);
        if (comp == null) return ItemStack.EMPTY;

        boolean hasSilkTouch = false;
        boolean hasFortune = false;

        // スロット全部確認
        for (ItemStack stack : input.getStacks()) {
            if (stack.isEmpty() || !stack.hasEnchantments()) continue;

            // 元アイテムのエンチャントを取得
            ItemEnchantmentsComponent enchants = stack.get(DataComponentTypes.ENCHANTMENTS);

            for(Map.Entry<RegistryEntry<Enchantment>, Integer> entry : enchants.getEnchantmentEntries()) {
                RegistryEntry<Enchantment> enchant = entry.getKey();

                // 除外エンチャントは無視
                if (enchant.matchesKey(Enchantments.UNBREAKING) ||
                        enchant.matchesKey(Enchantments.MENDING) ||
                        enchant.matchesKey((Enchantments.INFINITY))) continue;

                // エンチャントのレベル
                int level = entry.getValue();

                if (enchant.matchesKey(Enchantments.SILK_TOUCH)) {
                    comp = comp.withSilkTouch(level >= 2 ? level : 1);
                    if (!hasFortune) {
                        builder.set(enchant, comp.silkTouchLevel());
                        hasSilkTouch = true;
                    }
                }
                // エンチャントが幸運だった場合
                else if (enchant.matchesKey(Enchantments.FORTUNE)) {
                    comp = comp.withFortune(level >= 4 ? level : 3);
                    if (!hasSilkTouch) {
                        builder.set(enchant, comp.fortuneLevel());
                        hasFortune = true;
                    }
                }
                // 上記以外の場合
                else {
                    int current = builder.getLevel(enchant);
                    if (level > current) {
                        builder.set(enchant, level);
                    }
                }
            }
        }

        // エンチャントとマイニング情報を結果アイテムにセット
        result.set(ModComponents.MINING_ENCHANT_LEVEL, comp);
        result.set(DataComponentTypes.ENCHANTMENTS, builder.build());

        return result;
    }

    @Override
    public RecipeSerializer<? extends SpecialCraftingRecipe> getSerializer() {
        return INSTANCE;
    }

    // レシピ開放時、右上に表示されるかどうか
    @Override
    public boolean showNotification() {
        return true;
    }

    @Override
    public String getGroup() {
        return Identifier.of(TokorotenSlimeAPI.getModId(), "slime").toString();
    }

    // falseにしないとレシピ本に載らない
    @Override
    public boolean isIgnoredInRecipeBook() {
        return false;
    }

    /**
     * レシピ本アイテム判定
     */
    @Override
    public IngredientPlacement getIngredientPlacement() {
        return IngredientPlacement.forShapeless(List.of(
                Ingredient.ofItem(Items.NETHERITE_SHOVEL),
                Ingredient.ofItem(Items.NETHERITE_PICKAXE),
                Ingredient.ofItem(Items.NETHERITE_AXE),
                Ingredient.ofItem(Items.NETHERITE_HOE),
                Ingredient.ofItem(Items.NETHERITE_SWORD),
                Ingredient.ofItem(Items.BOW),
                Ingredient.ofItem(Items.NETHER_STAR),
                Ingredient.ofItem(Items.SLIME_BALL),
                Ingredient.ofItem(Items.SLIME_BALL),
                Ingredient.ofItem(ModItems.NETHERITE_MULTITOOL)
        ));
    }

    /**
     * レシピ表示
     */
    @Override
    public List<RecipeDisplay> getDisplays() {
        return List.of(
                new ShapelessCraftingRecipeDisplay(
                        List.of(
                                Ingredient.ofItem(Items.NETHERITE_SHOVEL).toDisplay(),
                                Ingredient.ofItem(Items.NETHERITE_PICKAXE).toDisplay(),
                                Ingredient.ofItem(Items.NETHERITE_AXE).toDisplay(),
                                Ingredient.ofItem(Items.NETHERITE_HOE).toDisplay(),
                                Ingredient.ofItem(Items.NETHERITE_SWORD).toDisplay(),
                                Ingredient.ofItem(Items.BOW).toDisplay(),
                                Ingredient.ofItem(Items.NETHER_STAR).toDisplay(),
                                Ingredient.ofItem(Items.SLIME_BALL).toDisplay(),
                                Ingredient.ofItem(Items.SLIME_BALL).toDisplay()
                        ),
                        new SlotDisplay.ItemSlotDisplay(ModItems.SLIME),
                        new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)
                ),
                new ShapelessCraftingRecipeDisplay(
                        List.of(
                                Ingredient.ofItem(ModItems.NETHERITE_MULTITOOL).toDisplay(),
                                Ingredient.ofItem(Items.BOW).toDisplay(),
                                Ingredient.ofItem(Items.NETHER_STAR).toDisplay(),
                                Ingredient.ofItem(Items.SLIME_BALL).toDisplay(),
                                Ingredient.ofItem(Items.SLIME_BALL).toDisplay()
                        ),
                        new SlotDisplay.ItemSlotDisplay(ModItems.SLIME),
                        new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)
                )
        );
    }
}
