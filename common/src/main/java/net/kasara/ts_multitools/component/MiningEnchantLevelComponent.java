package net.kasara.ts_multitools.component;

/**
 * スライムツールの幸運、シルクタッチエンチャントレベルを保持するレコード
 */
public record MiningEnchantLevelComponent(int fortuneLevel, int silkTouchLevel) {

    public static final MiningEnchantLevelComponent DEFAULT = new MiningEnchantLevelComponent(3, 1);

    // fortune の値だけ変更した新しいインスタンスを返す
    public MiningEnchantLevelComponent withFortune(int newFortune) {
        return new MiningEnchantLevelComponent(newFortune, this.silkTouchLevel);
    }

    // silk_touch の値だけ変更した新しいインスタンスを返す
    public MiningEnchantLevelComponent withSilkTouch(int newSilkTouch) {
        return new MiningEnchantLevelComponent(this.fortuneLevel, newSilkTouch);
    }
}
