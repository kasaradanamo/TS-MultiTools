package net.kasara.ts_multitools.fabric.client.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.kasara.ts_multitools.fabric.network.ModPackets;
import net.kasara.ts_multitools.network.packet.c2s.SetSlimeUuidC2SPacket;
import net.kasara.ts_multitools.network.packet.c2s.SlimeStateC2SPacket;
import net.kasara.ts_multitools.network.packet.c2s.ToggleSlimeModeC2SPacket;
import net.kasara.ts_multitools.network.packet.s2c.SlimeUseCountS2CPacket;
import net.minecraft.network.FriendlyByteBuf;

public final class ClientModPackets {

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.SLIME_USE_COUNT, (client, handler, buf, responseSender) -> {
            SlimeUseCountS2CPacket packet = SlimeUseCountS2CPacket.decode(buf);
            client.execute(() -> SlimeUseCountS2CPacket.handle(packet));
        });
    }

    public static void send(Object message) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        if (message instanceof ToggleSlimeModeC2SPacket packet) {
            ToggleSlimeModeC2SPacket.encode(packet, buf);
            ClientPlayNetworking.send(ModPackets.TOGGLE_SLIME_MODE, buf);
        } else if (message instanceof SetSlimeUuidC2SPacket packet) {
            SetSlimeUuidC2SPacket.encode(packet, buf);
            ClientPlayNetworking.send(ModPackets.SET_SLIME_UUID, buf);
        } else if (message instanceof SlimeStateC2SPacket packet) {
            SlimeStateC2SPacket.encode(packet, buf);
            ClientPlayNetworking.send(ModPackets.SLIME_STATE, buf);
        }
    }

    private ClientModPackets() {}
}
