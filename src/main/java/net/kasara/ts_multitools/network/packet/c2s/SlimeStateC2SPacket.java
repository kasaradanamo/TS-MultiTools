package net.kasara.ts_multitools.network.packet.c2s;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.server.handler.SlimeStateServerHandler;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.UUID;

/**
 * SlimeStateC2SPacket クラス
 * クライアントからサーバーへ送信するカスタムパケット。
 * スライムの状態(state)をサーバーに通知する
 *
 * @param uuid 対象スライムの UUID
 * @param state 変更するスライムの状態
 */
public record SlimeStateC2SPacket(UUID uuid, String state) implements CustomPayload {

    /** パケットの識別子 */
    public static final CustomPayload.Id<SlimeStateC2SPacket> ID =
            new CustomPayload.Id<>(Identifier.of(TokorotenSlimeAPI.getModId(), "slime_state"));

    /** パケットのシリアライズ・デシリアライズ用 Codec */
    public static final PacketCodec<RegistryByteBuf, SlimeStateC2SPacket> CODEC =
            PacketCodec.of(SlimeStateC2SPacket::write, SlimeStateC2SPacket::read);

    /**
     * パケットを書き込む（送信時に呼ばれる）
     */
    private static void write(SlimeStateC2SPacket packet, RegistryByteBuf buf) {
        buf.writeUuid(packet.uuid);
        buf.writeString(packet.state);
    }

    private static SlimeStateC2SPacket read(RegistryByteBuf buf) {
        return new SlimeStateC2SPacket(buf.readUuid(), buf.readString());
    }

    /**
     * このパケットの識別子を返す
     */
    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    /**
     * クライアントからサーバーへパケットを送信する
     *
     * @param uuid 対象スライムの UUID
     * @param state 設定する状態
     */
    public static void send(UUID uuid, String state) {
        ClientPlayNetworking.send(new SlimeStateC2SPacket(uuid, state));
    }

    /**
     * サーバー側でパケットを受信した際の処理
     * スライムの状態を更新するイベントを呼び出す
     *
     * @param packet 受信したパケット
     * @param player 送信したプレイヤー
     */
    public static void receive(SlimeStateC2SPacket packet, ServerPlayerEntity player) {
        MinecraftServer server = player.getEntityWorld().getServer();
        server.execute(() -> SlimeStateServerHandler.onSlimeStateUpdate(packet.uuid(), packet.state(), player));
    }
}
