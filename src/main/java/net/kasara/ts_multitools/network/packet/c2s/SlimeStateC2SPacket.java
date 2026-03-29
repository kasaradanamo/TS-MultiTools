package net.kasara.ts_multitools.network.packet.c2s;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.kasara.ts_multitools.TSMultitools;
import net.kasara.ts_multitools.server.SlimeStateServerHandler;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;

import java.util.UUID;

/**
 * スライムの状態(state)をサーバーに通知する
 */
public record SlimeStateC2SPacket(UUID uuid, String state) implements CustomPayload {

    public static final Id<SlimeStateC2SPacket> ID =
            new Id<>(Identifier.of(TSMultitools.MOD_ID, "slime_state"));

    public static final PacketCodec<RegistryByteBuf, SlimeStateC2SPacket> CODEC =
            PacketCodec.tuple(
                    Uuids.PACKET_CODEC,
                    SlimeStateC2SPacket::uuid,
                    PacketCodecs.string(16),
                    SlimeStateC2SPacket::state,
                    SlimeStateC2SPacket::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void send(UUID uuid, String state) {
        ClientPlayNetworking.send(new SlimeStateC2SPacket(uuid, state));
    }

    public static void receive(SlimeStateC2SPacket packet, ServerPlayerEntity player) {
        SlimeStateServerHandler.onSlimeStateUpdate(packet.uuid(), packet.state(), player);
    }
}
