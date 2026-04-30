package net.kasara.ts_multitools.network.packet.s2c;

import net.kasara.ts_multitools.TSMultiTools;
import net.kasara.ts_multitools.client.data.SlimeUseCountClientCache;
import net.kasara.ts_multitools.network.ModPackets;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * プレイヤーのSlimeItem使用回数（SLIME_USE_COUNT）をクライアントに送信する
 */
public record SlimeUseCountS2CPacket(int slimeCount) implements CustomPacketPayload {

    public static final Type<SlimeUseCountS2CPacket> ID =
            new Type<>(Identifier.fromNamespaceAndPath(TSMultiTools.MOD_ID, "slime_use_count"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SlimeUseCountS2CPacket> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.INT, SlimeUseCountS2CPacket::slimeCount, SlimeUseCountS2CPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void send(ServerPlayer player, int count) {
        ModPackets.sendToPlayer(player, new SlimeUseCountS2CPacket(count));
    }

    public static void handle(SlimeUseCountS2CPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            SlimeUseCountClientCache.setSlimeUseCount(packet.slimeCount());
        });
    }
}