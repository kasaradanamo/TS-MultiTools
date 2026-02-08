package net.kasara.ts_multitools.item;

import net.kasara.ts_multitools.util.ModTags;
import net.kasara.ts_multitools.server.handler.ToolRightClickHandler;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;

import static net.kasara.ts_multitools.util.MultiToolUtil.applyMultiToolSettings;

/**
 * カスタムマルチツールアイテム。
 * - 1本で複数のツール（ピッケル/斧/シャベルなど）の機能を持つ。
 * - バニラ剣と同等の攻撃力・攻撃速度を持つ。
 * - 右クリック動作は外部のハンドラに処理を委譲する。
 */
public class MultitoolItem extends Item {

    /**
     * コンストラクタ。
     *
     * @param material 使用するツール素材（耐久値や効率に影響）
     * @param settings アイテムの基本設定
     */
    public MultitoolItem(ToolMaterial material, Settings settings) {
        super(applyMultiToolSettings(
                material,
                settings.maxCount(1),               // スタック不可
                ModTags.Blocks.MULTITOOL_MINEABLE,  // 採掘できるブロックタグ
                3,                                  // 攻撃力 (バニラ剣と同じ)
                -2.4F                               // 攻撃速度（バニラ剣と同じ）
        ));
    }

    /**
     * ブロックに向かって右クリックしたときの処理。
     * - ToolRightClickHandler に処理を委譲。
     * - 何らかのツールアクションが実行された場合は SUCCESS を返す。
     * - それ以外は PASS を返してバニラの処理にフォールバック。
     *
     * @param context 使用状況（プレイヤー・ワールド・ブロック位置など）
     * @return ActionResult（SUCCESS or PASS）
     */
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        return ToolRightClickHandler.handleRightClick(context) != ToolRightClickHandler.ToolAction.NONE
                ? ActionResult.SUCCESS : ActionResult.PASS;
    }
}