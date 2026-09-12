package net.kasara.ts_multitools.recipe;

import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.component.MiningEnchantLevelComponent;
import net.kasara.ts_multitools.component.ModComponentsCommon;
import net.kasara.ts_multitools.item.ModItemsCommon;
import net.kasara.ts_multitools.item.SlimeEnchantmentRules;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

/**
 * スライムのレシピ
 */
public class SlimeRecipe extends CustomRecipe {

    public static final RecipeSerializer<SlimeRecipe> INSTANCE =
            new SimpleCraftingRecipeSerializer<>(SlimeRecipe::new);

    public SlimeRecipe(CraftingBookCategory category) {
        super(category);
    }

    /**
     * スライムのレシピ判定
     */
    @Override
    public boolean matches(CraftingInput input, Level level) {
        int slimeCount = 0;
        boolean hasSword = false, hasPickaxe = false, hasAxe = false,
                hasShovel = false, hasHoe = false, hasBow = false, hasStar = false;

        boolean hasMultitool = false;

        for (int i = 0; i < input.size(); i++) {
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

        //　レシピ1（ネザライトツール）
        boolean recipe1 = slimeCount == 2 && hasSword && hasPickaxe && hasAxe &&
                            hasShovel && hasHoe && hasBow && hasStar;

        // レシピ2(ネザライトマルチツール)
        boolean recipe2 = slimeCount == 2 && hasMultitool && hasBow && hasStar &&
                            !(hasSword || hasPickaxe || hasAxe || hasShovel || hasHoe);

        return recipe1 || recipe2;
    }

    /**
     * リザルトアイテムの処理
     */
    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack result = new ItemStack(ModItemsCommon.SLIME);

        // エンチャントボックス初期化
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);

        // マイニングエンチャントのコンポーネントを取得
        MiningEnchantLevelComponent comp = result.get(ModComponentsCommon.MINING_ENCHANT_LEVEL);
        if (comp == null) return ItemStack.EMPTY;

        boolean hasSilkTouch = false;
        boolean hasFortune = false;

        // スロット全部確認
        for (ItemStack stack : input.items()) {
            if (stack.isEmpty() || !stack.isEnchanted()) continue;

            // 元アイテムのエンチャントを取得
            ItemEnchantments enchants = stack.getEnchantments();

            for (Holder<Enchantment> holder : enchants.keySet()) {
                // 除外エンチャントは無視
                if (SlimeEnchantmentRules.isBlacklisted(holder)) {
                    continue;
                }

                // エンチャントのレベル
                int level = enchants.getLevel(holder);

                // エンチャントがシルクタッチだった場合
                if (holder.is(Enchantments.SILK_TOUCH)) {
                    comp = comp.withSilkTouch(level >= 2 ? level : 1);
                    if (!hasFortune) {
                        mutable.set(holder, comp.silkTouchLevel());
                        hasSilkTouch = true;
                    }
                }
                // エンチャントが幸運だった場合
                else if (holder.is(Enchantments.FORTUNE)) {
                    comp = comp.withFortune(level >= 4 ? level : 3);
                    if (!hasSilkTouch) {
                        mutable.set(holder, comp.fortuneLevel());
                        hasFortune = true;
                    }
                }
                // 上記以外の場合
                else {
                    int current = mutable.getLevel(holder);
                    if (level > current) {
                        mutable.set(holder, level);
                    }
                }
            }
        }

        // エンチャントとマイニング情報を結果アイテムにセット
        result.set(ModComponentsCommon.MINING_ENCHANT_LEVEL, comp);
        result.set(DataComponents.ENCHANTMENTS, mutable.toImmutable());

        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 9;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return INSTANCE;
    }

    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.EQUIPMENT;
    }

    // レシピ開放時、右上に表示されるかどうか
    @Override
    public boolean showNotification() {
        return true;
    }

    @Override
    public String getGroup() {
        return ResourceLocation.fromNamespaceAndPath(TokorotenSlimeAPI.getModId(), "slime").toString();
    }

    // falseにしないとレシピ本に載らない
    @Override
    public boolean isSpecial() {
        return false;
    }

    // CustomRecipeのデフォルトはItemStack.EMPTYのため、レシピ本アイコン用に明示的に返す
    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return new ItemStack(ModItemsCommon.SLIME);
    }

    /**
     * レシピ本アイテム判定
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
