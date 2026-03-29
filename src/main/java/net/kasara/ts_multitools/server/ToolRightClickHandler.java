package net.kasara.ts_multitools.server;

import net.kasara.ts_multitools.server.multitool.AxeRightClickHandler;
import net.kasara.ts_multitools.server.multitool.HoeRightClickHandler;
import net.kasara.ts_multitools.server.multitool.ShovelRightClickHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;

/**
 * プレイヤーがツールで右クリックしたときの処理をまとめたクラス
 * シャベル、クワ、斧それぞれの右クリック挙動を管理
 */
public class ToolRightClickHandler {

    // 右クリック処理の結果としてのアクション種別
    public enum ToolAction {SHOVEL, HOE, AXE, NONE}

    /**
     * ツールの右クリック処理の統合エントリーポイント
     * プレイヤーの状態とブロックを確認して、適切な処理を実行
     */
    public static ToolAction handleRightClick(ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();

        // スニーク状態でクワ処理を優先
        if (player != null && player.isSneaking()) {
            ActionResult hoe = HoeRightClickHandler.tryHoeAction(context);
            if (hoe.isAccepted()) return ToolAction.HOE;
        }

        ActionResult shovel = ShovelRightClickHandler.tryShovelAction(context);
        if (shovel.isAccepted()) return ToolAction.SHOVEL;

        ActionResult axe = AxeRightClickHandler.tryAxeAction(context);
        if (axe.isAccepted()) return ToolAction.AXE;

        return ToolAction.NONE;
    }

    private ToolRightClickHandler() {}
}
