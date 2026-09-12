package net.kasara.ts_multitools.network.packet.c2s;

import net.kasara.ts_multitools.TSMultiToolsCommon;
import net.kasara.ts_multitools.network.ModPacketsCommon;
import net.kasara.ts_multitools.server.SlimeStateServerHandler;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public record SlimeStateC2SPacket(UUID uuid, String state) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SlimeStateC2SPacket> ID =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(TSMultiToolsCommon.MOD_ID, "slime_state"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SlimeStateC2SPacket> STREAM_CODEC =
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
        ModPacketsCommon.sendToServer(new SlimeStateC2SPacket(uuid, state));
    }

    public static void receive(SlimeStateC2SPacket packet, ServerPlayer player) {
        SlimeStateServerHandler.onSlimeStateUpdate(packet.uuid(), packet.state(), player);
    }
}
