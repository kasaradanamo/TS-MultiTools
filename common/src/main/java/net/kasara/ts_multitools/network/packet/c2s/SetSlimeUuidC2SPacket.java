package net.kasara.ts_multitools.network.packet.c2s;

import net.kasara.ts_multitools.TSMultiToolsCommon;
import net.kasara.ts_multitools.network.ModPacketsCommon;
import net.kasara.ts_multitools.server.SlimeUuidServerManager;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public record SetSlimeUuidC2SPacket(UUID uuid, int slot) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SetSlimeUuidC2SPacket> ID =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(TSMultiToolsCommon.MOD_ID, "set_slime_uuid"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SetSlimeUuidC2SPacket> STREAM_CODEC =
            StreamCodec.composite(
                    UUIDUtil.STREAM_CODEC,
                    SetSlimeUuidC2SPacket::uuid,
                    ByteBufCodecs.INT,
                    SetSlimeUuidC2SPacket::slot,
                    SetSlimeUuidC2SPacket::new
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void send(UUID uuid, int slot) {
        ModPacketsCommon.sendToServer(new SetSlimeUuidC2SPacket(uuid, slot));
    }

    public static void receive(SetSlimeUuidC2SPacket packet, ServerPlayer player) {
        SlimeUuidServerManager.setSlimeUuid(player, packet.uuid(), packet.slot());
    }
}
