package net.kasara.ts_multitools.item;

import net.kasara.ts_multitools.util.ModTags;
import net.minecraft.item.ToolMaterial;

/**
 * ModToolMaterials クラス
 * カスタムツール素材を定義するクラス
 * SlimeItemで使用されるツール素材のパラメータを保持する
 */
public class ModToolMaterials {

    /**
     * スライム素材のツール素材設定
     * このツール素材はスライム用のアイテム・ツールで使用される
     */
    public static ToolMaterial SLIME = new ToolMaterial(
            ModTags.Blocks.INCORRECT_FOR_SLIME, // 採掘可能ブロックのタグ
            1,                                  // 耐久値（スライムにはないため1にしてる）
            10.0F,                              // 採掘速度
            5.0F,                               // 攻撃力ボーナス
            15,                                 // エンチャント適性
            ModTags.Items.SLIME_MATERIALS       // 修理に使えるアイテムのタグ
    );
}
