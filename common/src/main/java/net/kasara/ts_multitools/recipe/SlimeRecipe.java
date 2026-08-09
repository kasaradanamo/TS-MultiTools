package net.kasara.ts_multitools.recipe;

import net.kasara.ts_multitools.component.MiningEnchantLevelComponent;
import net.kasara.ts_multitools.data.SlimeItemData;
import net.kasara.ts_multitools.item.ModItemsCommon;
import net.kasara.ts_multitools.item.SlimeEnchantmentRules;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

import java.util.Map;

/**
 * スライムのレシピ。
 */
public class SlimeRecipe extends CustomRecipe {

    public static final RecipeSerializer<SlimeRecipe> INSTANCE =
            new SimpleCraftingRecipeSerializer<>(SlimeRecipe::new);

    public SlimeRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    /**
     * スライムのレシピ判定
     */
    @Override
    public boolean matches(CraftingContainer input, Level level) {
        int slimeCount = 0;
        boolean hasSword = false, hasPickaxe = false, hasAxe = false,
                hasShovel = false, hasHoe = false, hasBow = false, hasStar = false;

        boolean hasMultitool = false;

        for (int i = 0; i < input.getContainerSize(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;

            // 必要なアイテムを判定
            Item item = stack.getItem();
            if (item == Items.SLIME_BALL) slimeCount++;
            else if (item == Items.NETHERITE_SWORD) hasSword = true;
            else if (item == Items.NETHERITE_PICKAXE) hasPickaxe = true;
            else if (item == Items.NETHERITE_AXE) hasAxe = true;
            else if (item == Items.NETHERITE_SHOVEL) hasShovel = true;
            else if (item == Items.NETHERITE_HOE) hasHoe = true;
            else if (item == Items.BOW) hasBow = true;
            else if (item == Items.NETHER_STAR) hasStar = true;

            else if (item == ModItemsCommon.NETHERITE_MULTITOOL) hasMultitool = true;

            else return false;  // 不要なアイテムがあればレシピ不一致
        }

        // レシピ1(ネザライトツール)
        boolean recipe1 = slimeCount == 2 && hasSword && hasPickaxe && hasAxe &&
                hasShovel && hasHoe && hasBow && hasStar;

        // レシピ2(ネザライトマルチツール)
        boolean recipe2 = slimeCount == 2 && hasMultitool && hasBow && hasStar &&
                !(hasSword || hasPickaxe || hasAxe || hasShovel || hasHoe);

        return recipe1 || recipe2;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 9;
    }

    /**
     * リザルトアイテムの処理
     */
    @Override
    public ItemStack assemble(CraftingContainer input, RegistryAccess registryAccess) {
        ItemStack result = new ItemStack(ModItemsCommon.SLIME);

        // マイニングエンチャントの初期値を取得
        MiningEnchantLevelComponent comp = SlimeItemData.getMiningEnchantLevel(result);

        boolean hasSilkTouch = false;
        boolean hasFortune = false;

        Map<Enchantment, Integer> resultEnchantments = new java.util.HashMap<>();

        // スロット全部確認
        for (int i = 0; i < input.getContainerSize(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty() || !stack.isEnchanted()) continue;

            // 元アイテムのエンチャントを取得
            Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(stack);

            for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
                Enchantment enchantment = entry.getKey();
                int level = entry.getValue();

                // 除外エンチャントは無視
                if (SlimeEnchantmentRules.isBlacklisted(enchantment)) {
                    continue;
                }

                // エンチャントがシルクタッチだった場合
                if (enchantment == Enchantments.SILK_TOUCH) {
                    comp = comp.withSilkTouch(level >= 2 ? level : 1);
                    if (!hasFortune) {
                        resultEnchantments.put(enchantment, comp.silkTouchLevel());
                        hasSilkTouch = true;
                    }
                }
                // エンチャントが幸運だった場合
                else if (enchantment == Enchantments.BLOCK_FORTUNE) {
                    comp = comp.withFortune(level >= 4 ? level : 3);
                    if (!hasSilkTouch) {
                        resultEnchantments.put(enchantment, comp.fortuneLevel());
                        hasFortune = true;
                    }
                }
                // 上記以外の場合
                else {
                    int current = resultEnchantments.getOrDefault(enchantment, 0);
                    if (level > current) {
                        resultEnchantments.put(enchantment, level);
                    }
                }
            }
        }

        // エンチャントとマイニング情報を結果アイテムにセット
        SlimeItemData.setMiningEnchantLevel(result, comp);
        EnchantmentHelper.setEnchantments(resultEnchantments, result);

        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return INSTANCE;
    }

    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.EQUIPMENT;
    }

    // falseにしないとレシピ本に載らない(CustomRecipeのデフォルトはtrue)
    @Override
    public boolean isSpecial() {
        return false;
    }

    // CustomRecipeのデフォルトはItemStack.EMPTYのため、レシピ本アイコン用に明示的に返す
    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return new ItemStack(ModItemsCommon.SLIME);
    }

    /**
     * レシピ表示
     */
    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY,
                Ingredient.of(Items.NETHERITE_SHOVEL),
                Ingredient.of(Items.NETHERITE_PICKAXE),
                Ingredient.of(Items.NETHERITE_AXE),
                Ingredient.of(Items.NETHERITE_HOE),
                Ingredient.of(Items.NETHERITE_SWORD),
                Ingredient.of(Items.BOW),
                Ingredient.of(Items.NETHER_STAR),
                Ingredient.of(Items.SLIME_BALL),
                Ingredient.of(Items.SLIME_BALL)
        );
    }
}
