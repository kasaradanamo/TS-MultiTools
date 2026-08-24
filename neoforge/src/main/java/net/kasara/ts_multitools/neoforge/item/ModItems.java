package net.kasara.ts_multitools.neoforge.item;

import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.item.ModToolMaterials;
import net.kasara.ts_multitools.neoforge.TSMultiTools;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

public class ModItems {

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(TokorotenSlimeAPI.getModId());

    // 4ツール、剣、弓の機能が使えるツール
    public static final DeferredItem<Item> SLIME = registerItemAndAddToTab("slime",
            pros -> new SlimeItem(ModToolMaterials.SLIME, pros));

    // 木製マルチツール
    public static final DeferredItem<Item> WOODEN_MULTITOOL = registerItemAndAddToTab("wooden_multitool",
            pros -> new MultitoolItem(ToolMaterial.WOOD, pros));
    // 石製マルチツール
    public static final DeferredItem<Item> STONE_MULTITOOL = registerItemAndAddToTab("stone_multitool",
            pros -> new MultitoolItem(ToolMaterial.STONE, pros));
    // 銅製マルチツール
    public static final DeferredItem<Item> COPPER_MULTITOOL = registerItemAndAddToTab("copper_multitool",
            pros -> new MultitoolItem(ToolMaterial.COPPER, pros));
    // 鉄製マルチツール
    public static final DeferredItem<Item> IRON_MULTITOOL = registerItemAndAddToTab("iron_multitool",
            pros -> new MultitoolItem(ToolMaterial.IRON, pros));
    // 金製マルチツール
    public static final DeferredItem<Item> GOLDEN_MULTITOOL = registerItemAndAddToTab("golden_multitool",
            pros -> new MultitoolItem(ToolMaterial.GOLD, pros));
    // ダイヤ製マルチツール
    public static final DeferredItem<Item> DIAMOND_MULTITOOL = registerItemAndAddToTab("diamond_multitool",
            pros -> new MultitoolItem(ToolMaterial.DIAMOND, pros));
    // ネザライト製マルチツール(耐火設定付き)
    public static final DeferredItem<Item> NETHERITE_MULTITOOL = registerItemAndAddToTab("netherite_multitool",
            pros -> new MultitoolItem(ToolMaterial.NETHERITE, pros.fireResistant()));

    // スライムソードのアイコン用アイテム（グループ追加対象外）
    public static final DeferredItem<Item> SLIME_SWORD_ICON = registerItemAndAddToTab("slime_sword_icon", Item::new);

    private static DeferredItem<Item> registerItemAndAddToTab(String name, Function<Item.Properties, Item> factory) {
        DeferredItem<Item> item = ITEMS.registerItem(name, factory);

        // 特定のアイテム（アイコン用アイテム）は除外して、API経由でグループに追加
        if (!name.equals("slime_sword_icon")) TokorotenSlimeAPI.addItemToTab(item);
        return item;
    }

    /**
     * 登録確認用のログを出力するメソッド
     */
    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);

        // ログ出力
        TSMultiTools.LOGGER.info("Registering addon Mod Items for "+ TokorotenSlimeAPI.getModId() +" (from " + TSMultiTools.MOD_ID + ")");
    }
}
