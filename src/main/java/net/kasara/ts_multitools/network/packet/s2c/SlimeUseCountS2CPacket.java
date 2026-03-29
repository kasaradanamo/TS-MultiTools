package net.kasara.ts_multitools.network.packet.s2c;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.kasara.ts_multitools.TSMultitools;
import net.kasara.ts_multitools.client.data.SlimeUseCountClientCache;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

/**
 * プレイヤーのSlimeItem使用回数（SLIME_USE_COUNT）をクライアントに送信する
 */
public record SlimeUseCountS2CPacket(int slimeCount) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SlimeUseCountS2CPacket> ID =
            new Type<>(Identifier.fromNamespaceAndPath(TSMultitools.MOD_ID, "slime_use_count"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SlimeUseCountS2CPacket> CODEC =
            StreamCodec.composite(ByteBufCodecs.INT, SlimeUseCountS2CPacket::slimeCount, SlimeUseCountS2CPacket::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void send(ServerPlayer player, int count) {
        ServerPlayNetworking.send(player, new SlimeUseCountS2CPacket(count));
    }

    public static void receive(SlimeUseCountS2CPacket packet) {
        SlimeUseCountClientCache.setSlimeUseCount(packet.slimeCount());
    }
}