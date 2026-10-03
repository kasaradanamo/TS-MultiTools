package net.kasara.ts_multitools.fabric.command;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.command.ModCommandsCommon;
import net.kasara.ts_multitools.fabric.TSMultiTools;

public class ModCommands {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                ModCommandsCommon.register(dispatcher));

        // ログ出力
        TSMultiTools.LOGGER.info("Registering addon Mod Commands for "+ TokorotenSlimeAPI.getModId() +" (from " + TSMultiTools.MOD_ID + ")");
    }
}
