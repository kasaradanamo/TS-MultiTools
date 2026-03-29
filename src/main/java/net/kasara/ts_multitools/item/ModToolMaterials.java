package net.kasara.ts_multitools.item;

import net.kasara.ts_multitools.util.ModTags;
import net.minecraft.item.ToolMaterial;

/**
 * カスタムツール素材を定義するクラス
 */
public class ModToolMaterials {

    // スライム素材のツール素材設定
    public static ToolMaterial SLIME = new ToolMaterial(
            ModTags.Blocks.INCORRECT_FOR_SLIME, // 採掘可能ブロックのタグ
            1,                                  // 耐久値（スライムにはないため1にしてる）
            10.0F,                              // 採掘速度
            5.0F,                               // 攻撃力ボーナス
            15,                                 // エンチャント適性
            ModTags.Items.SLIME_MATERIALS       // 修理に使えるアイテムのタグ
    );
}
