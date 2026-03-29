package net.kasara.ts_multitools.network.packet.c2s;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.kasara.ts_multitools.TSMultitools;
import net.kasara.ts_multitools.server.SlimeStateServerHandler;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

/**
 * スライムの状態(state)をサーバーに通知する
 */
public record SlimeStateC2SPacket(UUID uuid, String state) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SlimeStateC2SPacket> ID =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(TSMultitools.MOD_ID, "slime_state"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SlimeStateC2SPacket> CODEC =
            StreamCodec.composite(
                    UUIDUtil.STREAM_CODEC,
                    SlimeStateC2SPacket::uuid,
                    ByteBufCodecs.stringUtf8(16),
                    SlimeStateC2SPacket::state,
                    SlimeStateC2SPacket::new
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void send(UUID uuid, String state) {
        ClientPlayNetworking.send(new SlimeStateC2SPacket(uuid, state));
    }

    public static void receive(SlimeStateC2SPacket packet, ServerPlayer player) {
        SlimeStateServerHandler.onSlimeStateUpdate(packet.uuid(), packet.state(), player);
    }
}
