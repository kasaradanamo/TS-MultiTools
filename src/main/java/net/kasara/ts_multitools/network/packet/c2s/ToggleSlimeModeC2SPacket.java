package net.kasara.ts_multitools.network.packet.c2s;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.kasara.ts_multitools.TSMultitools;
import net.kasara.ts_multitools.server.SlimeModeServerHandler;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

/**
 * SLIMEのモードをサーバーに通知する
 */
public record ToggleSlimeModeC2SPacket(UUID uuid, String mode) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ToggleSlimeModeC2SPacket> ID =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(TSMultitools.MOD_ID, "toggle_slime_mode"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ToggleSlimeModeC2SPacket> CODEC =
            StreamCodec.composite(
                    UUIDUtil.STREAM_CODEC,
                    ToggleSlimeModeC2SPacket::uuid,
                    ByteBufCodecs.stringUtf8(16),
                    ToggleSlimeModeC2SPacket::mode,
                    ToggleSlimeModeC2SPacket::new
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void send(UUID stackUuid, String mode) {
        ClientPlayNetworking.send(new ToggleSlimeModeC2SPacket(stackUuid, mode));
    }

    public static void receive(ToggleSlimeModeC2SPacket packet, ServerPlayer player) {
        SlimeModeServerHandler.toggleSlimeModeHandle(packet.uuid(), packet.mode(), player);
    }
}