package net.kasara.ts_multitools.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * スライムツールの幸運、シルクタッチエンチャントレベルを保持するコンポーネント
 */
public record MiningEnchantLevelComponent(int fortuneLevel, int silkTouchLevel) {

    public static final MiningEnchantLevelComponent DEFAULT = new MiningEnchantLevelComponent(3, 1);

    public static final Codec<MiningEnchantLevelComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("fortune").forGetter(MiningEnchantLevelComponent::fortuneLevel),
                    Codec.INT.fieldOf("silk_touch").forGetter(MiningEnchantLevelComponent::silkTouchLevel)
            ).apply(instance, MiningEnchantLevelComponent::new)
    );

    // fortune の値だけ変更した新しいインスタンスを返す
    public MiningEnchantLevelComponent withFortune(int newFortune) {
        return new MiningEnchantLevelComponent(newFortune, this.silkTouchLevel);
    }

    // silk_touch の値だけ変更した新しいインスタンスを返す
    public MiningEnchantLevelComponent withSilkTouch(int newSilkTouch) {
        return new MiningEnchantLevelComponent(this.fortuneLevel, newSilkTouch);
    }
}
