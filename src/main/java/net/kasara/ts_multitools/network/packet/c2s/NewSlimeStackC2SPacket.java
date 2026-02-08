package net.kasara.ts_multitools.network.packet.c2s;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.server.SlimeUUIDManager;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

/**
 * NewSlimeStackC2SPacket クラス
 * クライアントからサーバーへ送信するカスタムパケット。
 * 新しいスライムスタックを生成する際に、サーバー側で UUID を割り当てるイベントを呼び出す
 */
public record NewSlimeStackC2SPacket() implements CustomPayload{

    /** パケットの識別子 */
    public static final CustomPayload.Id<NewSlimeStackC2SPacket> ID =
            new CustomPayload.Id<>(Identifier.of(TokorotenSlimeAPI.getModId(), "new_slime_stack"));

    /** パケットのシリアライズ・デシリアライズ用 Codec */
    public static final PacketCodec<RegistryByteBuf, NewSlimeStackC2SPacket> CODEC =
            PacketCodec.of((packet, buf) ->{}, NewSlimeStackC2SPacket::read);

    private static NewSlimeStackC2SPacket read(RegistryByteBuf buf) {
        return new NewSlimeStackC2SPacket();
    }

    /**
     * このパケットの識別子を返す
     */
    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    /**
     * クライアントからサーバーへ送信するためのユーティリティメソッド
     */
    public static void send() {
        ClientPlayNetworking.send(new NewSlimeStackC2SPacket());
    }

    /**
     * サーバー側でパケットを受信した際の処理
     * 新しいスライムスタックに UUID を割り当てる
     *
     * @param player 対象のプレイヤー
     */
    public void receive(ServerPlayerEntity player) {
        MinecraftServer server = player.getEntityWorld().getServer();
        server.execute(() -> SlimeUUIDManager.ensureSlimeUUIDs(player));
    }
}
