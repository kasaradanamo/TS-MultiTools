package net.kasara.ts_multitools.server;

import net.minecraft.core.Holder;
import net.minecraft.core.component.BlockTransformer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.BlockTransformers;
import net.minecraft.world.item.context.UseOnContext;

/**
 * プレイヤーがツールで右クリックしたときの処理をまとめたクラス
 * シャベル、クワ、斧それぞれの右クリック挙動を管理
 */
public final class ToolRightClickHandler {

    // 右クリック処理の結果としてのアクション種別
    public enum ToolAction {SHOVEL, HOE, AXE, NONE}

    /**
     * ツールの右クリック処理の統合エントリーポイント
     * プレイヤーの状態とブロックを確認して、適切な処理を実行
     */
    public static ToolAction handleRightClick(UseOnContext context) {
        Player player = context.getPlayer();

        // スニーク状態でクワ処理を優先
        if (player != null && player.isShiftKeyDown()) {
            if (transform(context, BlockTransformers.HOE).consumesAction()) return ToolAction.HOE;
        }

        if (transform(context, BlockTransformers.SHOVEL).consumesAction()) return ToolAction.SHOVEL;

        if (transform(context, BlockTransformers.AXE).consumesAction()) return ToolAction.AXE;

        return ToolAction.NONE;
    }

    /**
     * バニラのBlockTransformerレジストリから該当ルールを引いて直接適用する
     */
    private static InteractionResult transform(UseOnContext context, ResourceKey<BlockTransformer> key) {
        Holder<BlockTransformer> transformer = context.getLevel().registryAccess()
                .lookupOrThrow(Registries.BLOCK_TRANSFORMER).getOrThrow(key);
        return transformer.value().transformBlock(context);
    }

    private ToolRightClickHandler() {}
}
