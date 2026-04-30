package net.kasara.ts_multitools.network.packet.c2s;

import net.kasara.ts_multitools.TSMultiTools;
import net.kasara.ts_multitools.network.ModPackets;
import net.kasara.ts_multitools.server.SlimeUuidServerManager;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

/**
 * サーバー側にuuidをセットさせる
 */
public record SetSlimeUuidC2SPacket(UUID uuid, int slot) implements CustomPacketPayload {

    public static final Type<SetSlimeUuidC2SPacket> ID =
            new Type<>(Identifier.fromNamespaceAndPath(TSMultiTools.MOD_ID, "set_slime_uuid"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SetSlimeUuidC2SPacket> STREAM_CODEC =
            StreamCodec.composite(
                    UUIDUtil.STREAM_CODEC,
                    SetSlimeUuidC2SPacket::uuid,
                    ByteBufCodecs.INT,
                    SetSlimeUuidC2SPacket::slot,
                    SetSlimeUuidC2SPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void send(UUID uuid, int slot) {
        ModPackets.sendToServer(new SetSlimeUuidC2SPacket(uuid, slot));
    }

    public static void handle(SetSlimeUuidC2SPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            SlimeUuidServerManager.setSlimeUuid(player, packet.uuid(), packet.slot());
        });
    }
}
