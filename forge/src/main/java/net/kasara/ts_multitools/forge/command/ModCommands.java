package net.kasara.ts_multitools.forge.command;

import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.TSMultiToolsCommon;
import net.kasara.ts_multitools.command.ModCommandsCommon;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ModCommands {

    public static void register() {
        MinecraftForge.EVENT_BUS.register(ModCommands.class);

        // ログ出力
        TSMultiToolsCommon.LOGGER.info("Registering addon Mod Commands for " + TokorotenSlimeAPI.getModId() + " (from " + TSMultiToolsCommon.MOD_ID + ")");
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        ModCommandsCommon.register(event.getDispatcher());
    }
}
