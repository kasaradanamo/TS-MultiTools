package net.kasara.ts_multitools.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kasara.ts_multitools.constant.SlimeMode;

/**
 * スライムツールの「使用モード」と「採掘モード」を保持するコンポーネント
 */
public record SlimeModeComponent(String useMode, String miningMode) {

    public static final SlimeModeComponent DEFAULT = new SlimeModeComponent(SlimeMode.UseMode.BOW, SlimeMode.MiningMode.DEFAULT);

    public static final Codec<SlimeModeComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf(SlimeMode.Type.USE).forGetter(SlimeModeComponent::useMode),
                    Codec.STRING.fieldOf(SlimeMode.Type.MINING).forGetter(SlimeModeComponent::miningMode)
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
