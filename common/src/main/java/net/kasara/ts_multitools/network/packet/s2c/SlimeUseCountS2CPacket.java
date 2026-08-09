package net.kasara.ts_multitools.network.packet.s2c;

import net.kasara.ts_multitools.client.data.SlimeUseCountClientCache;
import net.kasara.ts_multitools.network.ModPacketsCommon;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

/**
 * プレイヤーのSlimeItem使用回数(SLIME_USE_COUNT)をクライアントに送信する
 */
public record SlimeUseCountS2CPacket(int slimeCount) {

    public static void encode(SlimeUseCountS2CPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.slimeCount());
    }

    public static SlimeUseCountS2CPacket decode(FriendlyByteBuf buf) {
        return new SlimeUseCountS2CPacket(buf.readInt());
    }

    public static void send(ServerPlayer player, int count) {
        ModPacketsCommon.SEND_TO_PLAYER.accept(player, new SlimeUseCountS2CPacket(count));
    }

    // クライアント側のみで処理するため受信コンテキストは不要
    public static void handle(SlimeUseCountS2CPacket packet) {
        SlimeUseCountClientCache.setSlimeUseCount(packet.slimeCount());
    }
}
