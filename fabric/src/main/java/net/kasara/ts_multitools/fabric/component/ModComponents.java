package net.kasara.ts_multitools.fabric.component;

import com.mojang.serialization.Codec;
import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.component.MiningEnchantLevelComponent;
import net.kasara.ts_multitools.component.SlimeModeComponent;
import net.kasara.ts_multitools.fabric.TSMultiTools;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

import java.util.UUID;
import java.util.function.UnaryOperator;

public class ModComponents {

    // 使用モード / 採掘モードを記録するコンポーネント
    public static final DataComponentType<SlimeModeComponent> SLIME_MODE =
            registerDataComponentType("slime_mode", builder -> builder.persistent(SlimeModeComponent.CODEC));

    // fortune / silk_touch レベルを記録するコンポーネント
    public static final DataComponentType<MiningEnchantLevelComponent> MINING_ENCHANT_LEVEL =
            registerDataComponentType("mining_enchant_level", builder -> builder.persistent(MiningEnchantLevelComponent.CODEC));

    // スライムの「状態」（モデル切り替え用）を文字列で保持するコンポーネント
    public static final DataComponentType<String> SLIME_STATE =
            registerDataComponentType("slime_state", builder -> builder.persistent(Codec.STRING));

    // スライムごとの一意な識別子（UUID）を保持するコンポーネント
    public static final DataComponentType<UUID> SLIME_UUID =
            registerDataComponentType("slime_uuid", builder -> builder.persistent(Codec.STRING.xmap(UUID::fromString, UUID::toString)));

    private static <T> DataComponentType<T> registerDataComponentType(String name, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Identifier.fromNamespaceAndPath(TSMultiTools.MOD_ID, name),
                builderOperator.apply(DataComponentType.builder()).build());
    }

    /**
     * コンポーネントの登録処理を初期化時に呼び出す
     */
    public static void register() {
        TSMultiTools.LOGGER.info("Registering addon Mod Data Component Types for "+ TokorotenSlimeAPI.getModId() +" (from " + TSMultiTools.MOD_ID + ")");
    }
}
