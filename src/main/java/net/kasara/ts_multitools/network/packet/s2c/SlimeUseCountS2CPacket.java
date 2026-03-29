package net.kasara.ts_multitools.network.packet.s2c;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.kasara.ts_multitools.TSMultitools;
import net.kasara.ts_multitools.client.data.SlimeUseCountClientCache;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

/**
 * プレイヤーのSlimeItem使用回数（SLIME_USE_COUNT）をクライアントに送信する
 */
public record SlimeUseCountS2CPacket(int slimeCount) implements CustomPayload {

    public static final Id<SlimeUseCountS2CPacket> ID =
            new Id<>(Identifier.of(TSMultitools.MOD_ID, "slime_use_count"));

    public static final PacketCodec<RegistryByteBuf, SlimeUseCountS2CPacket> CODEC =
            PacketCodec.tuple(PacketCodecs.INTEGER, SlimeUseCountS2CPacket::slimeCount, SlimeUseCountS2CPacket::new);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void send(ServerPlayerEntity player, int count) {
        ServerPlayNetworking.send(player, new SlimeUseCountS2CPacket(count));
    }

    public static void receive(SlimeUseCountS2CPacket packet) {
        SlimeUseCountClientCache.setSlimeUseCount(packet.slimeCount());
    }
}