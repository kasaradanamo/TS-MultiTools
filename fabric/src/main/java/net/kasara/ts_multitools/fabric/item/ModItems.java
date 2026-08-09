package net.kasara.ts_multitools.fabric.item;

import net.kasara.tokorotenslime.TokorotenSlimeCommon;
import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.TSMultiToolsCommon;
import net.kasara.ts_multitools.item.ModItemsCommon;
import net.kasara.ts_multitools.item.ModToolMaterials;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;

import java.util.function.Function;

public class ModItems {

    // 4ツール、剣、弓の機能が使えるツール
    public static final Item SLIME = registerItemAndAddToTab("slime",
            pros -> new SlimeItem(ModToolMaterials.SLIME, pros));

    // 木製マルチツール
    public static final Item WOODEN_MULTITOOL = registerItemAndAddToTab("wooden_multitool",
            pros -> new MultitoolItem(Tiers.WOOD, pros));
    // 石製マルチツール
    public static final Item STONE_MULTITOOL = registerItemAndAddToTab("stone_multitool",
            pros -> new MultitoolItem(Tiers.STONE, pros));
    // 鉄製マルチツール
    public static final Item IRON_MULTITOOL = registerItemAndAddToTab("iron_multitool",
            pros -> new MultitoolItem(Tiers.IRON, pros));
    // 金製マルチツール
    public static final Item GOLDEN_MULTITOOL = registerItemAndAddToTab("golden_multitool",
            pros -> new MultitoolItem(Tiers.GOLD, pros));
    // ダイヤ製マルチツール
    public static final Item DIAMOND_MULTITOOL = registerItemAndAddToTab("diamond_multitool",
            pros -> new MultitoolItem(Tiers.DIAMOND, pros));
    // ネザライト製マルチツール(耐火設定付き)
    public static final Item NETHERITE_MULTITOOL = registerItemAndAddToTab("netherite_multitool",
            pros -> new MultitoolItem(Tiers.NETHERITE, pros.fireResistant()));

    // スライムソードのアイコン用アイテム(グループ追加対象外)
    public static final Item SLIME_SWORD_ICON = registerItem("slime_sword_icon", Item::new);

    private static Item registerItem(String name, Function<Item.Properties, Item> factory) {
        ResourceLocation id = new ResourceLocation(TokorotenSlimeCommon.MOD_ID, name);
        return Registry.register(BuiltInRegistries.ITEM, id, factory.apply(new Item.Properties()));
    }

    private static Item registerItemAndAddToTab(String name, Function<Item.Properties, Item> factory) {
        Item item = registerItem(name, factory);
        // Supplierとして包んで渡す
        TokorotenSlimeAPI.addItemToTab(() -> item);
        return item;
    }

    /**
     * ModItemsの登録処理を呼び出す。
     */
    public static void register() {
        ModItemsCommon.SLIME = SLIME;
        ModItemsCommon.NETHERITE_MULTITOOL = NETHERITE_MULTITOOL;

        TSMultiToolsCommon.LOGGER.info("Registering addon Mod Items for " + TokorotenSlimeAPI.getModId() + " (from " + TSMultiToolsCommon.MOD_ID + ")");
    }
}
