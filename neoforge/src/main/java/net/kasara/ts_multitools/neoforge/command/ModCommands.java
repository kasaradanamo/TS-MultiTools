package net.kasara.ts_multitools.neoforge.command;

import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.command.ModCommandsCommon;
import net.kasara.ts_multitools.neoforge.TSMultiTools;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public class ModCommands {

    public static void register() {
        NeoForge.EVENT_BUS.register(ModCommands.class);

        // ログ出力
        TSMultiTools.LOGGER.info("Registering addon Mod Commands for "+ TokorotenSlimeAPI.getModId() +" (from " + TSMultiTools.MOD_ID + ")");
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        ModCommandsCommon.register(event.getDispatcher());
    }
}
