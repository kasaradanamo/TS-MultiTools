package net.kasara.ts_multitools.network.packet.s2c;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.client.data.SlimeUseCountClientCache;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;


/**
 * サーバー → クライアント（S2C）パケット
 * プレイヤーのSlimeItem使用回数（SLIME_USE_COUNT）をクライアントに送信する
 */
public record SlimeUseCountS2CPacket(int slimeCount) implements CustomPayload {

    /** このパケットの識別ID */
    public static final Id<SlimeUseCountS2CPacket> ID =
            new Id<>(Identifier.of(TokorotenSlimeAPI.getModId(), "slime_use_count"));

    /** このパケットのエンコード・デコード方法 */
    public static final PacketCodec<RegistryByteBuf, SlimeUseCountS2CPacket> CODEC =
            PacketCodec.of(SlimeUseCountS2CPacket::write, SlimeUseCountS2CPacket::read);

    /** パケットの書き込み処理 */
    private static void write(SlimeUseCountS2CPacket packet, RegistryByteBuf buf) {
        buf.writeInt(packet.slimeCount());
    }

    private static SlimeUseCountS2CPacket read(RegistryByteBuf buf) {
        return new SlimeUseCountS2CPacket(buf.readInt());
    }

    /** パケットIDの取得 */
    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    /**
     * 指定したプレイヤーにこのパケットを送信する
     * @param player 対象プレイヤー
     * @param count SlimeItem使用回数
     */
    public static void send(ServerPlayerEntity player, int count) {
        ServerPlayNetworking.send(player, new SlimeUseCountS2CPacket(count));
    }

    /**
     * クライアント側でパケットを受信したときに呼ばれる
     * クライアントのSlimeItem使用回数データを更新する
     * @param packet 受信したパケット
     */
    public static void receive(SlimeUseCountS2CPacket packet) {
        MinecraftClient client = MinecraftClient.getInstance();
        client.execute(() -> SlimeUseCountClientCache.setSlimeUseCount(packet.slimeCount()));
    }
}
