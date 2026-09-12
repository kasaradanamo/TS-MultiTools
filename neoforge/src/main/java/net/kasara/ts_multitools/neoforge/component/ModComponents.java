package net.kasara.ts_multitools.neoforge.component;

import com.mojang.serialization.Codec;
import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.component.MiningEnchantLevelComponent;
import net.kasara.ts_multitools.component.SlimeModeComponent;
import net.kasara.ts_multitools.neoforge.TSMultiTools;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.UUID;

public class ModComponents {

    public static final DeferredRegister.DataComponents COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE,  TSMultiTools.MOD_ID);

    // 使用モード / 採掘モードを記録するコンポーネント
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SlimeModeComponent>> SLIME_MODE =
            COMPONENTS.registerComponentType("slime_mode", builder -> builder.persistent(SlimeModeComponent.CODEC));

    // fortune / silk_touch レベルを記録するコンポーネント
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<MiningEnchantLevelComponent>> MINING_ENCHANT_LEVEL =
            COMPONENTS.registerComponentType("mining_enchant_level", builder -> builder.persistent(MiningEnchantLevelComponent.CODEC));

    // スライムの「状態」（モデル切り替え用）を文字列で保持するコンポーネント
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> SLIME_STATE =
            COMPONENTS.registerComponentType("slime_state", builder -> builder.persistent(Codec.STRING));

    // スライムごとの一意な識別子（UUID）を保持するコンポーネント
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<UUID>> SLIME_UUID =
            COMPONENTS.registerComponentType("slime_uuid", builder -> builder.persistent(Codec.STRING.xmap(UUID::fromString, UUID::toString)));

    /**
     * コンポーネントの登録処理を初期化時に呼び出す
     */
    public static void register(IEventBus modEventBus) {
        COMPONENTS.register(modEventBus);

        TSMultiTools.LOGGER.info("Registering addon Mod Data Component Types for "+ TokorotenSlimeAPI.getModId() +" (from " + TSMultiTools.MOD_ID + ")");
    }
}
