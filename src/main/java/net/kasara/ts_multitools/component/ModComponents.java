package net.kasara.ts_multitools.component;

import com.mojang.serialization.Codec;
import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.TSMultitools;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.UUID;
import java.util.function.UnaryOperator;

public class ModComponents {

    // 使用モード / 採掘モードを記録するコンポーネント
    public static final ComponentType<SlimeModeComponent> SLIME_MODE =
            register("slime_mode", builder -> builder.codec(SlimeModeComponent.CODEC));

    // fortune / silk_touch レベルを記録するコンポーネント
    public static final ComponentType<MiningEnchantLevelComponent> MINING_ENCHANT_LEVEL =
            register("mining_enchant_level", builder -> builder.codec(MiningEnchantLevelComponent.CODEC));

    // スライムの「状態」（モデル切り替え用）を文字列で保持するコンポーネント
    public static final ComponentType<String> SLIME_STATE =
            register("slime_state", builder -> builder.codec(Codec.STRING));

    // スライムごとの一意な識別子（UUID）を保持するコンポーネント
    public static final ComponentType<UUID> SLIME_UUID =
            register("slime_uuid", builder -> builder.codec(Codec.STRING.xmap(UUID::fromString, UUID::toString)));

    private static <T> ComponentType<T> register(String name, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(TSMultitools.MOD_ID, name),
                builderOperator.apply(ComponentType.builder()).build());
    }

    /**
     * コンポーネントの登録処理を初期化時に呼び出す
     */
    public static void registerDataComponentTypes() {
        TSMultitools.LOGGER.info("Registering addon Data Component Types for "+ TokorotenSlimeAPI.getModId() +" (from " + TSMultitools.MOD_ID + ")");
    }
}