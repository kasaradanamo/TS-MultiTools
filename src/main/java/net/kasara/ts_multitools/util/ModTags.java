package net.kasara.ts_multitools.util;

import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

/**
 * ModTagsクラス
 * ブロックやアイテムに関連するタグを管理するユーティリティクラス。
 * タグを使うことで、特定のブロックやアイテムをまとめて扱えるようになる。
 */
public class ModTags {

    /**
     * ブロックに関連するタグをまとめた内部クラス
     */
    public static class Blocks {

        /** スライムに適さないブロック。ツールマテリアル作成用（現状は空） */
        public static final TagKey<Block> INCORRECT_FOR_SLIME = createTag("incorrect_for_slime");

        /** スライムで採掘可能なブロック。jsonで登録済み */
        public static final TagKey<Block> SLIME_MINEABLE = createTag("mineable/slime");

        /** マルチツールで採掘可能なブロック */
        public static final TagKey<Block> MULTITOOL_MINEABLE = createTag("mineable/multitool");

        /**
         * タグを作成するヘルパーメソッド
         * @param name タグ名
         * @return Block用のTagKey
         */
        private static TagKey<Block> createTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK, Identifier.of(TokorotenSlimeAPI.getModId(), name));
        }
    }

    /**
     * アイテムに関連するタグをまとめた内部クラス
     */
    public static class Items {

        /** スライム系の素材。ツールマテリアル作成用（現状は空） */
        public static final TagKey<Item> SLIME_MATERIALS = createTag("slime_materials");

        /**
         * タグを作成するヘルパーメソッド
         * @param name タグ名
         * @return Item用のTagKey
         */
        private static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, Identifier.of(TokorotenSlimeAPI.getModId(), name));
        }
    }
}
