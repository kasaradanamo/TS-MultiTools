package net.kasara.ts_multitools.neoforge.network;

import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.neoforge.TSMultiTools;
import net.kasara.ts_multitools.network.packet.c2s.SetSlimeUuidC2SPacket;
import net.kasara.ts_multitools.network.packet.c2s.SlimeStateC2SPacket;
import net.kasara.ts_multitools.network.packet.c2s.ToggleSlimeModeC2SPacket;
import net.kasara.ts_multitools.network.packet.s2c.SlimeUseCountS2CPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModPackets {

    private static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        // c2s (受信ハンドラのplayer()はサーバーバウンドではServerPlayerを返す)
        registrar.playToServer(ToggleSlimeModeC2SPacket.ID, ToggleSlimeModeC2SPacket.STREAM_CODEC,
                (packet, context) -> ToggleSlimeModeC2SPacket.receive(packet, (ServerPlayer) context.player()));
        registrar.playToServer(SetSlimeUuidC2SPacket.ID, SetSlimeUuidC2SPacket.STREAM_CODEC,
                (packet, context) -> SetSlimeUuidC2SPacket.receive(packet, (ServerPlayer) context.player()));
        registrar.playToServer(SlimeStateC2SPacket.ID, SlimeStateC2SPacket.STREAM_CODEC,
                (packet, context) -> SlimeStateC2SPacket.receive(packet, (ServerPlayer) context.player()));

        // s2c
        registrar.playToClient(SlimeUseCountS2CPacket.ID, SlimeUseCountS2CPacket.STREAM_CODEC,
                (packet, context) -> SlimeUseCountS2CPacket.receive(packet));
    }

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(ModPackets::onRegisterPayloads);

        // ログ
        TSMultiTools.LOGGER.info("Registering addon Mod PayloadTypes for "+ TokorotenSlimeAPI.getModId() +" (from " + TSMultiTools.MOD_ID + ")");
    }

    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        player.connection.send(new ClientboundCustomPayloadPacket(payload));
    }

    public static void sendToServer(CustomPacketPayload payload) {
        if (Minecraft.getInstance().getConnection() != null) {
            Minecraft.getInstance().getConnection().send(new ServerboundCustomPayloadPacket(payload));
        }
    }
}
