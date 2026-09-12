package net.kasara.ts_multitools.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * パケット送信をローダー側実装へ橋渡しする内部ブリッジ
 */
public class ModPacketsCommon {

    public static Consumer<CustomPacketPayload> SEND_TO_SERVER;
    public static BiConsumer<ServerPlayer, CustomPacketPayload> SEND_TO_PLAYER;

    public static void sendToServer(CustomPacketPayload payload) {
        SEND_TO_SERVER.accept(payload);
    }

    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        SEND_TO_PLAYER.accept(player, payload);
    }

    private ModPacketsCommon() {}
}
