package net.kasara.ts_multitools.network.packet.c2s;

import net.kasara.ts_multitools.TSMultiTools;
import net.kasara.ts_multitools.network.ModPackets;
import net.kasara.ts_multitools.server.SlimeModeServerHandler;
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
 * SLIMEのモードをサーバーに通知する
 */
public record ToggleSlimeModeC2SPacket(UUID uuid, String mode) implements CustomPacketPayload {

    public static final Type<ToggleSlimeModeC2SPacket> ID =
            new Type<>(Identifier.fromNamespaceAndPath(TSMultiTools.MOD_ID, "toggle_slime_mode"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ToggleSlimeModeC2SPacket> STREAM_CODEC =
            StreamCodec.composite(
                    UUIDUtil.STREAM_CODEC,
                    ToggleSlimeModeC2SPacket::uuid,
                    ByteBufCodecs.stringUtf8(16),
                    ToggleSlimeModeC2SPacket::mode,
                    ToggleSlimeModeC2SPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void send(UUID stackUuid, String mode) {
        ModPackets.sendToServer(new ToggleSlimeModeC2SPacket(stackUuid, mode));
    }

    public static void handle(ToggleSlimeModeC2SPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            SlimeModeServerHandler.toggleSlimeModeHandle(packet.uuid(), packet.mode(), player);
        });
    }
}