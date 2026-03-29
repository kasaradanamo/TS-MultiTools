package net.kasara.ts_multitools.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.TSMultitools;
import net.kasara.ts_multitools.network.packet.c2s.SetSlimeUuidC2SPacket;
import net.kasara.ts_multitools.network.packet.c2s.SlimeStateC2SPacket;
import net.kasara.ts_multitools.network.packet.c2s.ToggleSlimeModeC2SPacket;
import net.kasara.ts_multitools.network.packet.s2c.SlimeUseCountS2CPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * カスタムパケットの登録用クラス
 * C2S (Client → Server) / S2C (Server → Client) の両方を管理する
 */
public class ModPackets {

    // PayloadTypesの登録
    public static void registerPayloadTypes() {
        registerPTC2S(ToggleSlimeModeC2SPacket.ID, ToggleSlimeModeC2SPacket.CODEC);
        registerPTC2S(SetSlimeUuidC2SPacket.ID, SetSlimeUuidC2SPacket.CODEC);
        registerPTC2S(SlimeStateC2SPacket.ID, SlimeStateC2SPacket.CODEC);

        registerPTS2C(SlimeUseCountS2CPacket.ID, SlimeUseCountS2CPacket.CODEC);

        // ログ
        TSMultitools.LOGGER.info("Registering addon PayloadTypes for "+ TokorotenSlimeAPI.getModId() +" (from " + TSMultitools.MOD_ID + ")");
    }

    // C2Sの登録
    public static void registerC2SPackets() {
        registerC2S(ToggleSlimeModeC2SPacket.ID, ToggleSlimeModeC2SPacket::receive);
        registerC2S(SetSlimeUuidC2SPacket.ID, SetSlimeUuidC2SPacket::receive);
        registerC2S(SlimeStateC2SPacket.ID, SlimeStateC2SPacket::receive);

        // ログ
        TSMultitools.LOGGER.info("Registering addon C2SPackets for "+ TokorotenSlimeAPI.getModId() +" (from " + TSMultitools.MOD_ID + ")");
    }

    // S2Cの登録
    public static void registerS2CPackets() {
        registerS2C(SlimeUseCountS2CPacket.ID, SlimeUseCountS2CPacket::receive);

        // ログ
        TSMultitools.LOGGER.info("Registering addon S2CPackets for "+ TokorotenSlimeAPI.getModId() +" (from " + TSMultitools.MOD_ID + ")");
    }

    private static <T extends CustomPacketPayload> void registerPTC2S(CustomPacketPayload.Type<T> id, StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        PayloadTypeRegistry.serverboundPlay().register(id, codec);
    }

    private static <T extends CustomPacketPayload> void registerPTS2C(CustomPacketPayload.Type<T> id, StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        PayloadTypeRegistry.clientboundPlay().register(id, codec);
    }

    private static <T extends CustomPacketPayload> void registerC2S(CustomPacketPayload.Type<T> id, BiConsumer<T, ServerPlayer> handler) {
        ServerPlayNetworking.registerGlobalReceiver(id, (packet, context) -> handler.accept(packet, context.player()));
    }

    private static <T extends CustomPacketPayload> void registerS2C(CustomPacketPayload.Type<T> id, Consumer<T> handler) {
        ClientPlayNetworking.registerGlobalReceiver(id, (packet, context) -> handler.accept(packet));
    }
}