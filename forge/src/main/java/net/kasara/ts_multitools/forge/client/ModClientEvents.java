package net.kasara.ts_multitools.forge.client;

import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.TSMultiToolsCommon;
import net.kasara.ts_multitools.client.SlimeStateClientHandler;
import net.kasara.ts_multitools.client.SlimeTickEventHandler;
import net.kasara.ts_multitools.item.ModItemsCommon;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * クライアントのイベントクラス。
 */
public class ModClientEvents {

    public static void register() {
        MinecraftForge.EVENT_BUS.register(ModClientEvents.class);

        // ログ出力
        TSMultiToolsCommon.LOGGER.info("Registering addon Mod Client Events for " + TokorotenSlimeAPI.getModId() + " (from " + TSMultiToolsCommon.MOD_ID + ")");
    }

    // ブロック攻撃時のイベント
    @SubscribeEvent
    public static void onAttackBlock(PlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getEntity();
        Level level = event.getLevel();
        InteractionHand hand = event.getHand();
        BlockPos pos = event.getPos();

        if (!level.isClientSide() || player.getItemInHand(hand).getItem() != ModItemsCommon.SLIME)
            return;

        // ブロック攻撃に応じてスライムの状態を変更
        SlimeStateClientHandler.onAttackBlock(player, level, hand, pos);
    }

    // 毎ティックのイベント
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        // tickごとのスライム処理
        SlimeTickEventHandler.slimeTickEventHandler(Minecraft.getInstance());
    }
}
