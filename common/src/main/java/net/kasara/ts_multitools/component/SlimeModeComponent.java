package net.kasara.ts_multitools.component;

import net.kasara.ts_multitools.constant.SlimeMode;

/**
 * スライムツールの「使用モード」と「採掘モード」を保持するレコード
 */
public record SlimeModeComponent(String useMode, String miningMode) {

    public static final SlimeModeComponent DEFAULT = new SlimeModeComponent(SlimeMode.UseMode.BOW, SlimeMode.MiningMode.DEFAULT);

    // useMode のみ変更した新しいインスタンスを返す
    public SlimeModeComponent withUseMode(String newUseMode) {
        return new SlimeModeComponent(newUseMode, this.miningMode);
    }

    // miningMode のみ変更した新しいインスタンスを返す
    public SlimeModeComponent withMiningMode(String newMiningMode) {
        return new SlimeModeComponent(this.useMode, newMiningMode);
    }
}
