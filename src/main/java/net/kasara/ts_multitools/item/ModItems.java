package net.kasara.ts_multitools.item;

import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.TSMultitools;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.function.Function;

/**
 * ModItems クラス
 * このクラスでは、TSMultitool アドオンで追加するアイテムを定義・登録している。
 * アイテムはすべて registerAndAddToTab() を通して登録され、
 * TokorotenSlimeAPI を介してアイテムグループに追加される。
 */
public class ModItems {

    /** スライム素材のアイテム */
    public static final Item SLIME = registerAndAddToTab("slime",
            settings -> new SlimeItem(ModToolMaterials.SLIME, settings));

    /** 木製マルチツール */
    public static final Item WOODEN_MULTITOOL = registerAndAddToTab("wooden_multitool",
            settings -> new MultitoolItem(ToolMaterial.WOOD, settings));
    /** 石製マルチツール */
    public static final Item STONE_MULTITOOL = registerAndAddToTab("stone_multitool",
            settings -> new MultitoolItem(ToolMaterial.STONE, settings));
    /** 銅製マルチツール */
    public static final Item COPPER_MULTITOOL = registerAndAddToTab("copper_multitool",
            settings -> new MultitoolItem(ToolMaterial.COPPER, settings));
    /** 鉄製マルチツール */
    public static final Item IRON_MULTITOOL = registerAndAddToTab("iron_multitool",
            settings -> new MultitoolItem(ToolMaterial.IRON, settings));
    /** 金製マルチツール */
    public static final Item GOLDEN_MULTITOOL = registerAndAddToTab("golden_multitool",
            settings -> new MultitoolItem(ToolMaterial.GOLD, settings));
    /** ダイヤ製マルチツール */
    public static final Item DIAMOND_MULTITOOL = registerAndAddToTab("diamond_multitool",
            settings -> new MultitoolItem(ToolMaterial.DIAMOND, settings));
    /** ネザライト製マルチツール（耐火設定付き） */
    public static final Item NETHERITE_MULTITOOL = registerAndAddToTab("netherite_multitool",
            settings -> new MultitoolItem(ToolMaterial.NETHERITE, settings.fireproof()));

    /** スライムソードのアイコン用アイテム（グループ追加対象外） */
    public static final Item SLIME_SWORD_ICON = registerAndAddToTab("slime_sword_icon", Item::new);


    /**
     * アイテムを登録し、必要ならアイテムタブにも追加する共通メソッド
     *
     * @param name     アイテムIDの名前部分
     * @param function Item.Settings から Item を生成するファクトリ
     * @return 登録済みの Item インスタンス
     */
    private static Item registerAndAddToTab(String name, Function<Item.Settings, Item> function) {
        // アイテムの登録処理
        Item item = Registry.register(
                Registries.ITEM,
                Identifier.of(TokorotenSlimeAPI.getModId(), name),
                function.apply(new Item.Settings().registryKey(
                        RegistryKey.of(RegistryKeys.ITEM, Identifier.of(TokorotenSlimeAPI.getModId(), name)))
                )
        );
        // 特定のアイテム（ここではアイコン用）は除外して、API経由でグループに追加
        if (!name.equals("slime_sword_icon")) TokorotenSlimeAPI.addItemToTab(item);
        return item;
    }

    /**
     * 登録確認用のログを出力するメソッド
     * 実際の登録処理は static 初期化子で行われるため、ここではログのみ。
     */
    public static void registerModItems() {
        TSMultitools.LOGGER.info("Registering addon items for "+ TokorotenSlimeAPI.getModId() +" (from " + TSMultitools.MOD_ID + ")");
    }
}
