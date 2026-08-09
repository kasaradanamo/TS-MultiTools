package net.kasara.ts_multitools.fabric.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.TSMultiToolsCommon;
import net.kasara.ts_multitools.network.ModPacketsCommon;
import net.kasara.ts_multitools.network.packet.c2s.SetSlimeUuidC2SPacket;
import net.kasara.ts_multitools.network.packet.c2s.SlimeStateC2SPacket;
import net.kasara.ts_multitools.network.packet.c2s.ToggleSlimeModeC2SPacket;
import net.kasara.ts_multitools.network.packet.s2c.SlimeUseCountS2CPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/**
 * TS_MultiToolsのネットワーキング。
 */
public class ModPackets {

    public static final ResourceLocation TOGGLE_SLIME_MODE = id("toggle_slime_mode");
    public static final ResourceLocation SET_SLIME_UUID = id("set_slime_uuid");
    public static final ResourceLocation SLIME_STATE = id("slime_state");
    public static final ResourceLocation SLIME_USE_COUNT = id("slime_use_count");

    private static ResourceLocation id(String path) {
        return new ResourceLocation(TSMultiToolsCommon.MOD_ID, path);
    }

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(TOGGLE_SLIME_MODE, (server, player, handler, buf, responseSender) -> {
            ToggleSlimeModeC2SPacket packet = ToggleSlimeModeC2SPacket.decode(buf);
            server.execute(() -> ToggleSlimeModeC2SPacket.handle(packet, player));
        });
        ServerPlayNetworking.registerGlobalReceiver(SET_SLIME_UUID, (server, player, handler, buf, responseSender) -> {
            SetSlimeUuidC2SPacket packet = SetSlimeUuidC2SPacket.decode(buf);
            server.execute(() -> SetSlimeUuidC2SPacket.handle(packet, player));
        });
        ServerPlayNetworking.registerGlobalReceiver(SLIME_STATE, (server, player, handler, buf, responseSender) -> {
            SlimeStateC2SPacket packet = SlimeStateC2SPacket.decode(buf);
            server.execute(() -> SlimeStateC2SPacket.handle(packet, player));
        });

        // common側のsend()静的メソッドから呼ばれる送信ブリッジをセット
        ModPacketsCommon.SEND_TO_SERVER = ModPackets::sendToServer;
        ModPacketsCommon.SEND_TO_PLAYER = ModPackets::sendToPlayer;

        TSMultiToolsCommon.LOGGER.info("Registering addon Mod PayloadTypes for " + TokorotenSlimeAPI.getModId() + " (from " + TSMultiToolsCommon.MOD_ID + ")");
    }

    public static void sendToPlayer(ServerPlayer player, Object message) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        if (message instanceof SlimeUseCountS2CPacket packet) {
            SlimeUseCountS2CPacket.encode(packet, buf);
            ServerPlayNetworking.send(player, SLIME_USE_COUNT, buf);
        }
    }

    // クライアント側からのsendToServer()はfabricモジュールのクライアント専用クラス(ClientModPackets)が担当する
    private static void sendToServer(Object message) {
        net.kasara.ts_multitools.fabric.client.network.ClientModPackets.send(message);
    }
}
