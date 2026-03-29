package net.kasara.ts_multitools.network.packet.c2s;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.kasara.ts_multitools.TSMultitools;
import net.kasara.ts_multitools.server.SlimeUuidServerManager;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;

import java.util.UUID;

/**
 * サーバー側にuuidをセットさせる
 */
public record SetSlimeUuidC2SPacket(UUID uuid, int slot) implements CustomPayload{

    public static final Id<SetSlimeUuidC2SPacket> ID =
            new Id<>(Identifier.of(TSMultitools.MOD_ID, "set_slime_uuid"));

    public static final PacketCodec<RegistryByteBuf, SetSlimeUuidC2SPacket> CODEC =
            PacketCodec.tuple(
                    Uuids.PACKET_CODEC,
                    SetSlimeUuidC2SPacket::uuid,
                    PacketCodecs.INTEGER,
                    SetSlimeUuidC2SPacket::slot,
                    SetSlimeUuidC2SPacket::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void send(UUID uuid, int slot) {
        ClientPlayNetworking.send(new SetSlimeUuidC2SPacket(uuid, slot));
    }

    public static void receive(SetSlimeUuidC2SPacket packet, ServerPlayerEntity player) {
        SlimeUuidServerManager.setSlimeUuid(player, packet.uuid(), packet.slot());
    }
}
