package net.kasara.ts_multitools.item;

import net.kasara.ts_multitools.server.ToolRightClickHandler;
import net.kasara.ts_multitools.util.ModTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.context.UseOnContext;

import static net.kasara.ts_multitools.util.MultiToolUtil.applyMultiToolProperties;

/**
 * マルチツールアイテム
 * 複数のツール（ピッケル/斧/シャベルなど）の機能を持つ
 */
public class MultitoolItem extends Item {

    public MultitoolItem(ToolMaterial material, Properties pros) {
        super(applyMultiToolProperties(
                material,
                pros.stacksTo(1),               // スタック不可
                ModTags.Blocks.MULTITOOL_MINEABLE,  // 採掘できるブロックタグ
                3,                                  // 攻撃力 (バニラ剣と同じ)
                -2.4F                               // 攻撃速度（バニラ剣と同じ）
        ));
    }

    /**
     * ブロックに向かって右クリックしたときの処理
     */
    @Override
    public InteractionResult useOn(UseOnContext context) {
        return ToolRightClickHandler.handleRightClick(context) != ToolRightClickHandler.ToolAction.NONE
                ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }
}