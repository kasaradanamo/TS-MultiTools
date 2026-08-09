package net.kasara.ts_multitools.network.packet.c2s;

import net.kasara.ts_multitools.network.ModPacketsCommon;
import net.kasara.ts_multitools.server.SlimeUuidServerManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

/**
 * サーバー側にuuidをセットさせる
 */
public record SetSlimeUuidC2SPacket(UUID uuid, int slot) {

    public static void encode(SetSlimeUuidC2SPacket packet, FriendlyByteBuf buf) {
        buf.writeUUID(packet.uuid());
        buf.writeInt(packet.slot());
    }

    public static SetSlimeUuidC2SPacket decode(FriendlyByteBuf buf) {
        return new SetSlimeUuidC2SPacket(buf.readUUID(), buf.readInt());
    }

    public static void send(UUID uuid, int slot) {
        ModPacketsCommon.SEND_TO_SERVER.accept(new SetSlimeUuidC2SPacket(uuid, slot));
    }

    public static void handle(SetSlimeUuidC2SPacket packet, ServerPlayer sender) {
        if (sender != null) {
            SlimeUuidServerManager.setSlimeUuid(sender, packet.uuid(), packet.slot());
        }
    }
}
