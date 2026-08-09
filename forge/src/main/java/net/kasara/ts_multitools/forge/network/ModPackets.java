package net.kasara.ts_multitools.forge.network;

import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.TSMultiToolsCommon;
import net.kasara.ts_multitools.network.ModPacketsCommon;
import net.kasara.ts_multitools.network.packet.c2s.SetSlimeUuidC2SPacket;
import net.kasara.ts_multitools.network.packet.c2s.SlimeStateC2SPacket;
import net.kasara.ts_multitools.network.packet.c2s.ToggleSlimeModeC2SPacket;
import net.kasara.ts_multitools.network.packet.s2c.SlimeUseCountS2CPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

/**
 * TS_MultiToolsのネットワーキング({@link SimpleChannel}のチャンネル登録とコンテキストアダプタ)。
 */
public class ModPackets {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(TSMultiToolsCommon.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    private static int nextId() {
        return packetId++;
    }

    public static void register() {
        CHANNEL.registerMessage(nextId(), ToggleSlimeModeC2SPacket.class,
                ToggleSlimeModeC2SPacket::encode, ToggleSlimeModeC2SPacket::decode, ModPackets::handleToggleSlimeMode);
        CHANNEL.registerMessage(nextId(), SetSlimeUuidC2SPacket.class,
                SetSlimeUuidC2SPacket::encode, SetSlimeUuidC2SPacket::decode, ModPackets::handleSetSlimeUuid);
        CHANNEL.registerMessage(nextId(), SlimeStateC2SPacket.class,
                SlimeStateC2SPacket::encode, SlimeStateC2SPacket::decode, ModPackets::handleSlimeState);
        CHANNEL.registerMessage(nextId(), SlimeUseCountS2CPacket.class,
                SlimeUseCountS2CPacket::encode, SlimeUseCountS2CPacket::decode, ModPackets::handleSlimeUseCount);

        // common側のsend()静的メソッドから呼ばれる送信ブリッジをセット
        ModPacketsCommon.SEND_TO_SERVER = ModPackets::sendToServer;
        ModPacketsCommon.SEND_TO_PLAYER = ModPackets::sendToPlayer;

        // ログ
        TSMultiToolsCommon.LOGGER.info("Registering addon Mod PayloadTypes for " + TokorotenSlimeAPI.getModId() + " (from " + TSMultiToolsCommon.MOD_ID + ")");
    }

    private static void handleToggleSlimeMode(ToggleSlimeModeC2SPacket packet, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> ToggleSlimeModeC2SPacket.handle(packet, ctx.getSender()));
        ctx.setPacketHandled(true);
    }

    private static void handleSetSlimeUuid(SetSlimeUuidC2SPacket packet, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> SetSlimeUuidC2SPacket.handle(packet, ctx.getSender()));
        ctx.setPacketHandled(true);
    }

    private static void handleSlimeState(SlimeStateC2SPacket packet, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> SlimeStateC2SPacket.handle(packet, ctx.getSender()));
        ctx.setPacketHandled(true);
    }

    private static void handleSlimeUseCount(SlimeUseCountS2CPacket packet, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> SlimeUseCountS2CPacket.handle(packet));
        ctx.setPacketHandled(true);
    }

    private static ServerPlayer senderOf(NetworkEvent.Context ctx) {
        return ctx.getSender();
    }

    public static void sendToPlayer(ServerPlayer player, Object message) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static void sendToServer(Object message) {
        CHANNEL.sendToServer(message);
    }
}
