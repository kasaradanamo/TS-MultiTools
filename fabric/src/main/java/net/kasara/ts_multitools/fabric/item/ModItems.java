package net.kasara.ts_multitools.fabric.item;

import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.fabric.TSMultiTools;
import net.kasara.ts_multitools.item.ModToolMaterials;
import net.kasara.ts_multitools.item.MultitoolItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;

import java.util.function.Function;

public class ModItems {

    // 4ツール、剣、弓の機能が使えるツール
    public static final Item SLIME = registerItemAndAddToTab("slime",
            pros -> new SlimeItem(ModToolMaterials.SLIME, pros));

    // 木製マルチツール
    public static final Item WOODEN_MULTITOOL = registerItemAndAddToTab("wooden_multitool",
            pros -> new MultitoolItem(ToolMaterial.WOOD, pros));
    // 石製マルチツール
    public static final Item STONE_MULTITOOL = registerItemAndAddToTab("stone_multitool",
            pros -> new MultitoolItem(ToolMaterial.STONE, pros));
    // 銅製マルチツール
    public static final Item COPPER_MULTITOOL = registerItemAndAddToTab("copper_multitool",
            pros -> new MultitoolItem(ToolMaterial.COPPER, pros));
    // 鉄製マルチツール
    public static final Item IRON_MULTITOOL = registerItemAndAddToTab("iron_multitool",
            pros -> new MultitoolItem(ToolMaterial.IRON, pros));
    // 金製マルチツール
    public static final Item GOLDEN_MULTITOOL = registerItemAndAddToTab("golden_multitool",
            pros -> new MultitoolItem(ToolMaterial.GOLD, pros));
    // ダイヤ製マルチツール
    public static final Item DIAMOND_MULTITOOL = registerItemAndAddToTab("diamond_multitool",
            pros -> new MultitoolItem(ToolMaterial.DIAMOND, pros));
    // ネザライト製マルチツール(耐火設定付き)
    public static final Item NETHERITE_MULTITOOL = registerItemAndAddToTab("netherite_multitool",
            pros -> new MultitoolItem(ToolMaterial.NETHERITE, pros.fireResistant()));

    // スライムソードのアイコン用アイテム（グループ追加対象外）
    public static final Item SLIME_SWORD_ICON = registerItemAndAddToTab("slime_sword_icon", Item::new);

    private static Item registerItemAndAddToTab(String name, Function<Item.Properties, Item> factory) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(TokorotenSlimeAPI.getModId(), name));
        Item item = Registry.register(
                BuiltInRegistries.ITEM,
                key,
                factory.apply(new Item.Properties().setId(key))
        );
        // 特定のアイテム（アイコン用アイテム）は除外して、API経由でグループに追加
        if (!name.equals("slime_sword_icon")) TokorotenSlimeAPI.addItemToTab(() -> item);
        return item;
    }

    /**
     * 登録確認用のログを出力するメソッド
     */
    public static void register() {
        TSMultiTools.LOGGER.info("Registering addon Mod Items for "+ TokorotenSlimeAPI.getModId() +" (from " + TSMultiTools.MOD_ID + ")");
    }
}
