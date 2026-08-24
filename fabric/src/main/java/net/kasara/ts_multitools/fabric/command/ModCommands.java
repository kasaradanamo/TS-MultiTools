package net.kasara.ts_multitools.fabric.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.fabric.TSMultiTools;
import net.kasara.ts_multitools.server.SlimeUseCountManager;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.PermissionCheck;
import net.minecraft.server.permissions.Permissions;

public class ModCommands {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {

            // "slimecount" コマンドのルート定義
            // /slimecount reset - 使用回数をリセット
            // /slimecount set <数値> - 使用回数を指定した値に設定
            dispatcher.register(
                    Commands.literal("slimecount")
                            // ゲームマスターレベルに制限
                            .requires(Commands.hasPermission(
                                    new PermissionCheck.Require(Permissions.COMMANDS_GAMEMASTER)
                            ))

                            // "reset" サブコマンド
                            .then(Commands.literal("reset")
                                    .executes(context -> {
                                        // コマンド実行者のプレイヤー取得
                                        ServerPlayer player = context.getSource().getPlayer();
                                        if (player == null) return 0;

                                        // 使用回数をリセット
                                        SlimeUseCountManager.reset(player);

                                        // サーバーに通知メッセージ送信
                                        context.getSource().sendSuccess(() -> Component.translatable("command.tokorotenslime.reset_slime_count"), true);
                                        return 1; // 成功を返す
                                    })
                            )

                            // "set" サブコマンド
                            .then(Commands.literal("set")
                                    .then(Commands.argument("count", IntegerArgumentType.integer(0))
                                            .executes(context -> {
                                                // 引数の取得
                                                int count = IntegerArgumentType.getInteger(context, "count");

                                                // コマンド実行者のプレイヤー取得
                                                ServerPlayer player = context.getSource().getPlayer();
                                                if (player == null) return 0;

                                                // 使用回数を指定値に設定
                                                SlimeUseCountManager.set(player, count);

                                                // サーバーに通知メッセージ送信
                                                context.getSource().sendSuccess(() -> Component.translatable("command.tokorotenslime.set_slime_count", count), true);
                                                return 1; // 成功を返す
                                            })
                                    )
                            )
            );
        });

        // ログ出力
        TSMultiTools.LOGGER.info("Registering addon Mod Commands for "+ TokorotenSlimeAPI.getModId() +" (from " + TSMultiTools.MOD_ID + ")");
    }
}
