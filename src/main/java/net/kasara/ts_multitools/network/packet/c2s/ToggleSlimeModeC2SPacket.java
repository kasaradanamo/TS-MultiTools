package net.kasara.ts_multitools.network.packet.c2s;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.server.handler.SlimeModeServerHandler;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.UUID;

/**
 * ToggleSlimeModeC2SPacket クラス
 * クライアントからサーバーへ送信するカスタムパケット。
 * スライムスタックの使用モード(useMode)を切り替えるために使用する
 *
 * @param stackUuid 対象スライムスタックの UUID
 * @param mode 切り替えるモード名（例: "bow", "tool" など）
 */
public record ToggleSlimeModeC2SPacket(UUID stackUuid, String mode) implements CustomPayload {

    /** パケットの識別子 */
    public static final CustomPayload.Id<ToggleSlimeModeC2SPacket> ID =
            new CustomPayload.Id<>(Identifier.of(TokorotenSlimeAPI.getModId(), "toggle_slime_mode"));

    /** パケットのシリアライズ・デシリアライズ用 Codec */
    public static final PacketCodec<RegistryByteBuf, ToggleSlimeModeC2SPacket> CODEC =
            PacketCodec.of(ToggleSlimeModeC2SPacket::write, ToggleSlimeModeC2SPacket::read);

    /**
     * パケットを書き込む（送信時に呼ばれる）
     */
    private static void write(ToggleSlimeModeC2SPacket packet, RegistryByteBuf buf) {
        buf.writeUuid(packet.stackUuid);
        buf.writeString(packet.mode);
    }

    private static ToggleSlimeModeC2SPacket read(RegistryByteBuf buf) {
        return new ToggleSlimeModeC2SPacket(buf.readUuid(), buf.readString());
    }

    /**
     * このパケットの識別子を返す
     */
    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    /**
     * クライアントからサーバーへパケットを送信するユーティリティ
     *
     * @param stackUuid 対象スライムスタックの UUID
     * @param mode 設定する使用モード
     */
    public static void send(UUID stackUuid, String mode) {
        ClientPlayNetworking.send(new ToggleSlimeModeC2SPacket(stackUuid, mode));
    }

    /**
     * サーバー側でパケットを受信した際の処理
     * 指定されたスライムスタックの使用モードを切り替える
     *
     * @param packet 受信したパケット
     * @param player 送信したプレイヤー
     */
    public static void receive(ToggleSlimeModeC2SPacket packet, ServerPlayerEntity player) {
        MinecraftServer server = player.getEntityWorld().getServer();
        server.execute(() -> SlimeModeServerHandler.toggleSlimeModeHandle(packet.stackUuid(), packet.mode(), player));
    }
}
