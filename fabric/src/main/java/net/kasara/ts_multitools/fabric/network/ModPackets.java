package net.kasara.ts_multitools.fabric.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.fabric.TSMultiTools;
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
        registerPTC2S(ToggleSlimeModeC2SPacket.ID, ToggleSlimeModeC2SPacket.STREAM_CODEC);
        registerPTC2S(SetSlimeUuidC2SPacket.ID, SetSlimeUuidC2SPacket.STREAM_CODEC);
        registerPTC2S(SlimeStateC2SPacket.ID, SlimeStateC2SPacket.STREAM_CODEC);

        registerPTS2C(SlimeUseCountS2CPacket.ID, SlimeUseCountS2CPacket.STREAM_CODEC);

        // ログ
        TSMultiTools.LOGGER.info("Registering addon Mod PayloadTypes for "+ TokorotenSlimeAPI.getModId() +" (from " + TSMultiTools.MOD_ID + ")");
    }

    // C2Sの登録
    public static void registerC2SPackets() {
        registerC2S(ToggleSlimeModeC2SPacket.ID, ToggleSlimeModeC2SPacket::receive);
        registerC2S(SetSlimeUuidC2SPacket.ID, SetSlimeUuidC2SPacket::receive);
        registerC2S(SlimeStateC2SPacket.ID, SlimeStateC2SPacket::receive);

        // ログ
        TSMultiTools.LOGGER.info("Registering addon Mod C2SPackets for "+ TokorotenSlimeAPI.getModId() +" (from " + TSMultiTools.MOD_ID + ")");
    }

    // S2Cの登録
    public static void registerS2CPackets() {
        registerS2C(SlimeUseCountS2CPacket.ID, SlimeUseCountS2CPacket::receive);

        // ログ
        TSMultiTools.LOGGER.info("Registering addon Mod S2CPackets for "+ TokorotenSlimeAPI.getModId() +" (from " + TSMultiTools.MOD_ID + ")");
    }

    private static <T extends CustomPacketPayload> void registerPTC2S(CustomPacketPayload.Type<T> id, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        PayloadTypeRegistry.serverboundPlay().register(id, streamCodec);
    }

    private static <T extends CustomPacketPayload> void registerPTS2C(CustomPacketPayload.Type<T> id, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        PayloadTypeRegistry.clientboundPlay().register(id, streamCodec);
    }

    private static <T extends CustomPacketPayload> void registerC2S(CustomPacketPayload.Type<T> id, BiConsumer<T, ServerPlayer> handler) {
        ServerPlayNetworking.registerGlobalReceiver(id, (packet, context) -> handler.accept(packet, context.player()));
    }

    private static <T extends CustomPacketPayload> void registerS2C(CustomPacketPayload.Type<T> id, Consumer<T> handler) {
        ClientPlayNetworking.registerGlobalReceiver(id, (packet, context) -> handler.accept(packet));
    }
}
