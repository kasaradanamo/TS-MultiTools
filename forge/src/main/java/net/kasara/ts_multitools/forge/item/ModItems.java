package net.kasara.ts_multitools.forge.item;

import net.kasara.tokorotenslime.TokorotenSlimeCommon;
import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.TSMultiToolsCommon;
import net.kasara.ts_multitools.item.ModItemsCommon;
import net.kasara.ts_multitools.item.ModToolMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, TokorotenSlimeCommon.MOD_ID);

    // 4ツール、剣、弓の機能が使えるツール
    public static final RegistryObject<Item> SLIME = registerItemAndAddToTab("slime",
            pros -> new SlimeItem(ModToolMaterials.SLIME, pros));

    // 木製マルチツール
    public static final RegistryObject<Item> WOODEN_MULTITOOL = registerItemAndAddToTab("wooden_multitool",
            pros -> new MultitoolItem(Tiers.WOOD, pros));
    // 石製マルチツール
    public static final RegistryObject<Item> STONE_MULTITOOL = registerItemAndAddToTab("stone_multitool",
            pros -> new MultitoolItem(Tiers.STONE, pros));
    // 鉄製マルチツール
    public static final RegistryObject<Item> IRON_MULTITOOL = registerItemAndAddToTab("iron_multitool",
            pros -> new MultitoolItem(Tiers.IRON, pros));
    // 金製マルチツール
    public static final RegistryObject<Item> GOLDEN_MULTITOOL = registerItemAndAddToTab("golden_multitool",
            pros -> new MultitoolItem(Tiers.GOLD, pros));
    // ダイヤ製マルチツール
    public static final RegistryObject<Item> DIAMOND_MULTITOOL = registerItemAndAddToTab("diamond_multitool",
            pros -> new MultitoolItem(Tiers.DIAMOND, pros));
    // ネザライト製マルチツール(耐火設定付き)
    public static final RegistryObject<Item> NETHERITE_MULTITOOL = registerItemAndAddToTab("netherite_multitool",
            pros -> new MultitoolItem(Tiers.NETHERITE, pros.fireResistant()));

    // スライムソードのアイコン用アイテム(グループ追加対象外)
    public static final RegistryObject<Item> SLIME_SWORD_ICON = registerItemAndAddToTab("slime_sword_icon", Item::new);

    private static RegistryObject<Item> registerItemAndAddToTab(String name, Function<Item.Properties, Item> factory) {
        RegistryObject<Item> item = ITEMS.register(name, () -> factory.apply(new Item.Properties()));

        // 特定のアイテム(アイコン用アイテム)は除外して、API経由でグループに追加
        if (!name.equals("slime_sword_icon")) TokorotenSlimeAPI.addItemToTab(item);
        return item;
    }

    /**
     * 登録確認用のログを出力するメソッド
     */
    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);

        // ログ出力
        TSMultiToolsCommon.LOGGER.info("Registering addon Mod Items for " + TokorotenSlimeAPI.getModId() + " (from " + TSMultiToolsCommon.MOD_ID + ")");
    }

    /**
     * common側の共有ホルダー(ModItemsCommon)へItem参照を反映する。
     */
    public static void initCommonHolder() {
        ModItemsCommon.SLIME = SLIME.get();
        ModItemsCommon.NETHERITE_MULTITOOL = NETHERITE_MULTITOOL.get();
    }
}
