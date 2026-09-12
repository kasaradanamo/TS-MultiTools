package net.kasara.ts_multitools.server;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShovelItem;
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
            if (hoe().useOn(context).consumesAction()) return ToolAction.HOE;
        }

        if (shovel().useOn(context).consumesAction()) return ToolAction.SHOVEL;

        if (axe().useOn(context).consumesAction()) return ToolAction.AXE;

        return ToolAction.NONE;
    }

    private static AxeItem axe() {
        return (AxeItem) Items.IRON_AXE;
    }

    private static ShovelItem shovel() {
        return (ShovelItem) Items.IRON_SHOVEL;
    }

    private static HoeItem hoe() {
        return (HoeItem) Items.IRON_HOE;
    }

    private ToolRightClickHandler() {}
}
