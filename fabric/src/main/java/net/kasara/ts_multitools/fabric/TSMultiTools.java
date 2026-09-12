package net.kasara.ts_multitools.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.kasara.ts_multitools.TSMultiToolsCommon;
import net.kasara.ts_multitools.component.ModComponentsCommon;
import net.kasara.ts_multitools.entity.ModEntitiesCommon;
import net.kasara.ts_multitools.item.ModItemsCommon;
import net.kasara.ts_multitools.network.ModPacketsCommon;
import net.kasara.ts_multitools.fabric.command.ModCommands;
import net.kasara.ts_multitools.fabric.component.ModComponents;
import net.kasara.ts_multitools.fabric.entity.ModEntities;
import net.kasara.ts_multitools.fabric.item.ModItems;
import net.kasara.ts_multitools.fabric.network.ModPackets;
import net.kasara.ts_multitools.fabric.recipe.ModRecipeSerializers;
import net.kasara.ts_multitools.fabric.server.ModServerEvents;
import org.slf4j.Logger;

public class TSMultiTools implements ModInitializer {

    public static final String MOD_ID = TSMultiToolsCommon.MOD_ID;
    public static final Logger LOGGER = TSMultiToolsCommon.LOGGER;

    @Override
    public void onInitialize() {
        // カスタムコンポーネント登録
        ModComponents.register();
        ModComponentsCommon.SLIME_MODE = ModComponents.SLIME_MODE;
        ModComponentsCommon.MINING_ENCHANT_LEVEL = ModComponents.MINING_ENCHANT_LEVEL;
        ModComponentsCommon.SLIME_STATE = ModComponents.SLIME_STATE;
        ModComponentsCommon.SLIME_UUID = ModComponents.SLIME_UUID;

        // アイテム登録
        ModItems.register();
        ModItemsCommon.SLIME = ModItems.SLIME;
        ModItemsCommon.NETHERITE_MULTITOOL = ModItems.NETHERITE_MULTITOOL;

        // エンティティ登録
        ModEntities.register();
        ModEntitiesCommon.SLIME_ARROW = ModEntities.SLIME_ARROW;

        // レシピシリアライザー登録
        ModRecipeSerializers.register();

        // C2Sパケット登録
        ModPackets.registerPayloadTypes();
        ModPackets.registerC2SPackets();
        // サーバー側からクライアントへの送信ブリッジ(共通エントリポイントで代入)
        ModPacketsCommon.SEND_TO_PLAYER = ServerPlayNetworking::send;

        // コマンドイベント登録
        ModCommands.register();

        // サーバーイベント登録
        ModServerEvents.register();
    }
}
