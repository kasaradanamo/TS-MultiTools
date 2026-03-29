package net.kasara.ts_multitools.item;

import net.kasara.ts_multitools.server.ToolRightClickHandler;
import net.kasara.ts_multitools.util.ModTags;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.ActionResult;

import static net.kasara.ts_multitools.util.MultiToolUtil.applyMultiToolSettings;

/**
 * マルチツールアイテム
 * 複数のツール（ピッケル/斧/シャベルなど）の機能を持つ
 */
public class MultitoolItem extends Item {

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
     * ブロックに向かって右クリックしたときの処理
     */
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        return ToolRightClickHandler.handleRightClick(context) != ToolRightClickHandler.ToolAction.NONE
                ? ActionResult.SUCCESS : ActionResult.PASS;
    }
}