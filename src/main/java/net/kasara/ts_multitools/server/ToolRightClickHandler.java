package net.kasara.ts_multitools.server;

import net.kasara.ts_multitools.server.multitool.AxeRightClickHandler;
import net.kasara.ts_multitools.server.multitool.HoeRightClickHandler;
import net.kasara.ts_multitools.server.multitool.ShovelRightClickHandler;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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
            InteractionResult hoe = HoeRightClickHandler.tryHoeAction(context);
            if (hoe.consumesAction()) return ToolAction.HOE;
        }

        InteractionResult shovel = ShovelRightClickHandler.tryShovelAction(context);
        if (shovel.consumesAction()) return ToolAction.SHOVEL;

        InteractionResult axe = AxeRightClickHandler.tryAxeAction(context);
        if (axe.consumesAction()) return ToolAction.AXE;

        return ToolAction.NONE;
    }

    private ToolRightClickHandler() {}
}