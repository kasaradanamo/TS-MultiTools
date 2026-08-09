package net.kasara.ts_multitools.network.packet.c2s;

import net.kasara.ts_multitools.network.ModPacketsCommon;
import net.kasara.ts_multitools.server.SlimeStateServerHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

/**
 * スライムの状態(state)をサーバーに通知する
 */
public record SlimeStateC2SPacket(UUID uuid, String state) {

    public static void encode(SlimeStateC2SPacket packet, FriendlyByteBuf buf) {
        buf.writeUUID(packet.uuid());
        buf.writeUtf(packet.state(), 16);
    }

    public static SlimeStateC2SPacket decode(FriendlyByteBuf buf) {
        return new SlimeStateC2SPacket(buf.readUUID(), buf.readUtf(16));
    }

    public static void send(UUID uuid, String state) {
        ModPacketsCommon.SEND_TO_SERVER.accept(new SlimeStateC2SPacket(uuid, state));
    }

    public static void handle(SlimeStateC2SPacket packet, ServerPlayer sender) {
        if (sender != null) {
            SlimeStateServerHandler.onSlimeStateUpdate(packet.uuid(), packet.state(), sender);
        }
    }
}
