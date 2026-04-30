package net.kasara.ts_multitools.network.packet.c2s;

import net.kasara.ts_multitools.TSMultiTools;
import net.kasara.ts_multitools.network.ModPackets;
import net.kasara.ts_multitools.server.SlimeStateServerHandler;
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
 * スライムの状態(state)をサーバーに通知する
 */
public record SlimeStateC2SPacket(UUID uuid, String state) implements CustomPacketPayload {

    public static final Type<SlimeStateC2SPacket> ID =
            new Type<>(Identifier.fromNamespaceAndPath(TSMultiTools.MOD_ID, "slime_state"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SlimeStateC2SPacket> STREAM_CODEC =
            StreamCodec.composite(
                    UUIDUtil.STREAM_CODEC,
                    SlimeStateC2SPacket::uuid,
                    ByteBufCodecs.stringUtf8(16),
                    SlimeStateC2SPacket::state,
                    SlimeStateC2SPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void send(UUID uuid, String state) {
        ModPackets.sendToServer(new SlimeStateC2SPacket(uuid, state));
    }

    public static void handle(SlimeStateC2SPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            SlimeStateServerHandler.onSlimeStateUpdate(packet.uuid(), packet.state(), player);
        });
    }
}
