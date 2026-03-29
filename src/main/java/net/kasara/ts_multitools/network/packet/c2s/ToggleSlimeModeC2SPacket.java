package net.kasara.ts_multitools.network.packet.c2s;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.kasara.ts_multitools.TSMultitools;
import net.kasara.ts_multitools.server.SlimeModeServerHandler;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;

import java.util.UUID;

/**
 * SLIMEのモードをサーバーに通知する
 */
public record ToggleSlimeModeC2SPacket(UUID uuid, String mode) implements CustomPayload {

    public static final Id<ToggleSlimeModeC2SPacket> ID =
            new Id<>(Identifier.of(TSMultitools.MOD_ID, "toggle_slime_mode"));

    public static final PacketCodec<RegistryByteBuf, ToggleSlimeModeC2SPacket> CODEC =
            PacketCodec.tuple(
                    Uuids.PACKET_CODEC,
                    ToggleSlimeModeC2SPacket::uuid,
                    PacketCodecs.string(16),
                    ToggleSlimeModeC2SPacket::mode,
                    ToggleSlimeModeC2SPacket::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void send(UUID stackUuid, String mode) {
        ClientPlayNetworking.send(new ToggleSlimeModeC2SPacket(stackUuid, mode));
    }

    public static void receive(ToggleSlimeModeC2SPacket packet, ServerPlayerEntity player) {
        SlimeModeServerHandler.toggleSlimeModeHandle(packet.uuid(), packet.mode(), player);
    }
}
