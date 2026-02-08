package net.kasara.ts_multitools.component;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * スライムツールの「使用モード」と「採掘モード」を保持するコンポーネント。
 * - useMode: アイテムの使用方法（例: 通常モード/特殊モードなど）
 * - miningMode: 採掘時の動作（例: 通常/範囲採掘 など）
 */
public record SlimeModeComponent(String useMode, String miningMode) {

    public static final SlimeModeComponent DEFAULT = new SlimeModeComponent("bow", "default");


    // データのシリアライズ/デシリアライズ用 Codec
    public static final Codec<SlimeModeComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("use_mode").forGetter(SlimeModeComponent::useMode),
                    Codec.STRING.fieldOf("mining_mode").forGetter(SlimeModeComponent::miningMode)
            ).apply(instance, SlimeModeComponent::new)
    );

    // useMode のみ変更した新しいインスタンスを返す
    public SlimeModeComponent withUseMode(String newUseMode) {
        return new SlimeModeComponent(newUseMode, this.miningMode);
    }

    // miningMode のみ変更した新しいインスタンスを返す
    public SlimeModeComponent withMiningMode(String newMiningMode) {
        return new SlimeModeComponent(this.useMode, newMiningMode);
    }
}
