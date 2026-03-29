package net.kasara.ts_multitools.recipe;

import com.mojang.serialization.MapCodec;
import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.component.MiningEnchantLevelComponent;
import net.kasara.ts_multitools.component.ModComponents;
import net.kasara.ts_multitools.item.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * スライムのレシピ
 */
public class SlimeRecipe extends CustomRecipe {

    private static final MapCodec<SlimeRecipe> MAP_CODEC = MapCodec.unit(new SlimeRecipe());

    private static final StreamCodec<RegistryFriendlyByteBuf, SlimeRecipe> STREAM_CODEC =
            StreamCodec.unit(new SlimeRecipe());

    public static final RecipeSerializer<SlimeRecipe> INSTANCE =
            new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

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

            else if (item == ModItems.NETHERITE_MULTITOOL) hasMultitool = true;

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
    public ItemStack assemble(CraftingInput input) {
        ItemStack result = new ItemStack(ModItems.SLIME);

        // エンチャントボックス初期化
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);

        // マイニングエンチャントのコンポーネントを取得
        MiningEnchantLevelComponent comp = result.get(ModComponents.MINING_ENCHANT_LEVEL);
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
                if (holder.is(Enchantments.UNBREAKING) || holder.is(Enchantments.MENDING) || holder.is((Enchantments.INFINITY))) {
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
        result.set(ModComponents.MINING_ENCHANT_LEVEL, comp);
        result.set(DataComponents.ENCHANTMENTS, mutable.toImmutable());

        return result;
    }

    @Override
    public RecipeSerializer<? extends CustomRecipe> getSerializer() {
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
    public String group() {
        return Identifier.fromNamespaceAndPath(TokorotenSlimeAPI.getModId(), "slime").toString();
    }

    // falseにしないとレシピ本に載らない
    @Override
    public boolean isSpecial() {
        return false;
    }

    /**
     * レシピ本アイテム判定
     */
    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.create(List.of(
                Ingredient.of(Items.NETHERITE_SHOVEL),
                Ingredient.of(Items.NETHERITE_PICKAXE),
                Ingredient.of(Items.NETHERITE_AXE),
                Ingredient.of(Items.NETHERITE_HOE),
                Ingredient.of(Items.NETHERITE_SWORD),
                Ingredient.of(Items.BOW),
                Ingredient.of(Items.NETHER_STAR),
                Ingredient.of(Items.SLIME_BALL),
                Ingredient.of(Items.SLIME_BALL),
                Ingredient.of(ModItems.NETHERITE_MULTITOOL)
        ));
    }

    /**
     * レシピ表示
     */
    @Override
    public List<RecipeDisplay> display() {
        return List.of(
                new ShapelessCraftingRecipeDisplay(
                        List.of(
                                Ingredient.of(Items.NETHERITE_SHOVEL).display(),
                                Ingredient.of(Items.NETHERITE_PICKAXE).display(),
                                Ingredient.of(Items.NETHERITE_AXE).display(),
                                Ingredient.of(Items.NETHERITE_HOE).display(),
                                Ingredient.of(Items.NETHERITE_SWORD).display(),
                                Ingredient.of(Items.BOW).display(),
                                Ingredient.of(Items.NETHER_STAR).display(),
                                Ingredient.of(Items.SLIME_BALL).display(),
                                Ingredient.of(Items.SLIME_BALL).display()
                        ),
                        new SlotDisplay.ItemStackSlotDisplay(new ItemStackTemplate(ModItems.SLIME)),
                        new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)
                ),
                new ShapelessCraftingRecipeDisplay(
                        List.of(
                                Ingredient.of(ModItems.NETHERITE_MULTITOOL).display(),
                                Ingredient.of(Items.BOW).display(),
                                Ingredient.of(Items.NETHER_STAR).display(),
                                Ingredient.of(Items.SLIME_BALL).display(),
                                Ingredient.of(Items.SLIME_BALL).display()
                        ),
                        new SlotDisplay.ItemStackSlotDisplay(new ItemStackTemplate(ModItems.SLIME)),
                        new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)
                )
        );
    }
}