package net.kasara.ts_multitools.network.packet.c2s;

import net.kasara.ts_multitools.network.ModPacketsCommon;
import net.kasara.ts_multitools.server.SlimeModeServerHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

/**
 * SLIMEのモードをサーバーに通知する
 */
public record ToggleSlimeModeC2SPacket(UUID uuid, String mode) {

    public static void encode(ToggleSlimeModeC2SPacket packet, FriendlyByteBuf buf) {
        buf.writeUUID(packet.uuid());
        buf.writeUtf(packet.mode(), 16);
    }

    public static ToggleSlimeModeC2SPacket decode(FriendlyByteBuf buf) {
        return new ToggleSlimeModeC2SPacket(buf.readUUID(), buf.readUtf(16));
    }

    public static void send(UUID stackUuid, String mode) {
        ModPacketsCommon.SEND_TO_SERVER.accept(new ToggleSlimeModeC2SPacket(stackUuid, mode));
    }

    public static void handle(ToggleSlimeModeC2SPacket packet, ServerPlayer sender) {
        if (sender != null) {
            SlimeModeServerHandler.toggleSlimeModeHandle(packet.uuid(), packet.mode(), sender);
        }
    }
}
