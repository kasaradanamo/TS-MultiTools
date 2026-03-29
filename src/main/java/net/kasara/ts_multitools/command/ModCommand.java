package net.kasara.ts_multitools.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.server.SlimeUseCountManager;
import net.kasara.ts_multitools.TSMultitools;
import net.minecraft.command.DefaultPermissions;
import net.minecraft.command.permission.PermissionCheck;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class ModCommand {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {

            // "slimecount" コマンドのルート定義
            // /slimecount reset - 使用回数をリセット
            // /slimecount set <数値> - 使用回数を指定した値に設定
            dispatcher.register(
                    CommandManager.literal("slimecount")
                            // ゲームマスターレベルに制限
                            .requires(CommandManager.requirePermissionLevel(
                                    new PermissionCheck.Require(DefaultPermissions.GAMEMASTERS)
                            ))

                            // "reset" サブコマンド
                            .then(CommandManager.literal("reset")
                                    .executes(context -> {
                                        // コマンド実行者のプレイヤー取得
                                        ServerPlayerEntity player = context.getSource().getPlayer();
                                        if (player == null) return 0;

                                        // 使用回数をリセット
                                        SlimeUseCountManager.reset(player);

                                        // サーバーに通知メッセージ送信
                                        context.getSource().sendFeedback(() -> Text.translatable("command.tokorotenslime.reset_slime_count"), true);
                                        return 1; // 成功を返す
                                    })
                            )

                            // "set" サブコマンド
                            .then(CommandManager.literal("set")
                                    .then(CommandManager.argument("count", IntegerArgumentType.integer(0))
                                            .executes(context -> {
                                                // 引数の取得
                                                int count = IntegerArgumentType.getInteger(context, "count");

                                                // コマンド実行者のプレイヤー取得
                                                ServerPlayerEntity player = context.getSource().getPlayer();
                                                if (player == null) return 0;

                                                // 使用回数を指定値に設定
                                                SlimeUseCountManager.set(player, count);

                                                // サーバーに通知メッセージ送信
                                                context.getSource().sendFeedback(() -> Text.translatable("command.tokorotenslime.set_slime_count", count), true);
                                                return 1; // 成功を返す
                                            })
                                    )
                            )
            );
        });

        // ログ出力
        TSMultitools.LOGGER.info("Registering addon Commands for "+ TokorotenSlimeAPI.getModId() +" (from " + TSMultitools.MOD_ID + ")");
    }
}
