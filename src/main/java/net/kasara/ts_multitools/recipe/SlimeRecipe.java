package net.kasara.ts_multitools.recipe;

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
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Map;

/**
 * SlimeRecipe クラス
 * 特殊クラフティングレシピを実装
 * 特定の材料（スライムボール×2 + ネザライト装備 + BOW + ネザースター）で
 * SlimeItem を作成する
 */
public class SlimeRecipe extends SpecialCraftingRecipe {

    private final Identifier id;    // このレシピの識別子

    public SlimeRecipe(Identifier id) {
        super(CraftingRecipeCategory.EQUIPMENT);    // クラフトカテゴリ：装備
        this.id = id;
    }

    /**
     * 入力アイテムがレシピ条件に合致するかを判定
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
        boolean recipe2 = slimeCount == 2 && hasMultitool && hasBow && hasStar;

        // 必要な素材が全て揃っているか判定
        return recipe1 || recipe2;
    }

    /**
     * レシピにマッチした場合に作成されるアイテム
     * 元の装備からエンチャントを引き継ぐ処理もここで行う
     */
    @Override
    public ItemStack craft(CraftingRecipeInput input, RegistryWrapper.WrapperLookup registries) {
        ItemStack result = new ItemStack(ModItems.SLIME);

        // エンチャントのビルダー初期化
        ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);

        // マイニングエンチャントのコンポーネントを取得
        var comp = result.get(ModComponents.MINING_ENCHANT_LEVEL);
        if (comp == null) return null;

        boolean hasSilkTouch = false;
        boolean hasFortune = false;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getStackInSlot(i);
            if (!stack.isEmpty() && !stack.hasEnchantments()) continue;

            // 元アイテムのエンチャントを取得
            ItemEnchantmentsComponent ench = stack.get(DataComponentTypes.ENCHANTMENTS);
            if (ench == null) continue;

            // エンチャントを SlimeItem に適用
            for(Map.Entry<RegistryEntry<Enchantment>, Integer> entry : ench.getEnchantmentEntries()) {
                RegistryEntry<Enchantment> enchant = entry.getKey();
                int level = entry.getValue();

                // 除外エンチャントは無視
                if (enchant.matchesKey(Enchantments.UNBREAKING) ||
                        enchant.matchesKey(Enchantments.MENDING) ||
                        enchant.matchesKey((Enchantments.INFINITY))) continue;

                if (enchant.matchesKey(Enchantments.SILK_TOUCH)) {
                    if (level >=2) comp = comp.withSilkTouch(level);
                    if (!hasFortune) {
                        builder.set(enchant, level);
                        hasSilkTouch = true;
                    }
                } else if (enchant.matchesKey(Enchantments.FORTUNE)) {
                    if (level >= 4) comp = comp.withFortune(level);
                    else level = 3;
                    if (!hasSilkTouch) {
                        builder.set(enchant, level);
                        hasFortune = true;
                    }
                } else {
                    int current = builder.getLevel(enchant);
                    if (level > current) builder.set(enchant, level);
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
        return SlimeRecipeSerializer.INSTANCE;
    }

    public Identifier getId() {
        return id;
    }
}
