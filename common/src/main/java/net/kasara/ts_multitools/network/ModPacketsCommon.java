package net.kasara.ts_multitools.network;

import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * ローダー固有のModPacketsが初期化時にセットする。
 */
public final class ModPacketsCommon {

    public static Consumer<Object> SEND_TO_SERVER;
    public static BiConsumer<ServerPlayer, Object> SEND_TO_PLAYER;

    private ModPacketsCommon() {}
}
