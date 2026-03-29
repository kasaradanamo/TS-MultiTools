package net.kasara.ts_multitools.network.packet.c2s;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.kasara.ts_multitools.TSMultitools;
import net.kasara.ts_multitools.server.SlimeUuidServerManager;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

/**
 * サーバー側にuuidをセットさせる
 */
public record SetSlimeUuidC2SPacket(UUID uuid, int slot) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SetSlimeUuidC2SPacket> ID =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(TSMultitools.MOD_ID, "set_slime_uuid"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SetSlimeUuidC2SPacket> CODEC =
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
        ClientPlayNetworking.send(new SetSlimeUuidC2SPacket(uuid, slot));
    }

    public static void receive(SetSlimeUuidC2SPacket packet, ServerPlayer player) {
        SlimeUuidServerManager.setSlimeUuid(player, packet.uuid(), packet.slot());
    }
}
