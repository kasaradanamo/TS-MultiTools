package net.kasara.ts_multitools.item;

import net.kasara.ts_multitools.component.ModComponentsCommon;
import net.kasara.ts_multitools.component.SlimeModeComponent;
import net.kasara.ts_multitools.constant.SlimeMode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * スライムで焚き火を消せるかどうかの判定
 */
public final class CampfireDouseRule {

    public static boolean blocksDouse(Player player, ItemStack stack, BlockState state) {
        if (stack.getItem() != ModItemsCommon.SLIME) return false;
        if (!CampfireBlock.isLitCampfire(state)) return false;
        if (player.totalExperience < 1) return true;

        SlimeModeComponent mode = stack.get(ModComponentsCommon.SLIME_MODE);
        return mode == null || !SlimeMode.UseMode.TOOL.equals(mode.useMode());
    }

    private CampfireDouseRule() {}
}
