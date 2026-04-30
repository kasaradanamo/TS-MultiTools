package net.kasara.ts_multitools.client;

import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.TSMultiTools;
import net.kasara.ts_multitools.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * クライアントのイベントクラス
 */
public class ModClientEvents {

    public static void register() {
        NeoForge.EVENT_BUS.register(ModClientEvents.class);

        // ログ出力
        TSMultiTools.LOGGER.info("Registering addon Mod Client Events for "+ TokorotenSlimeAPI.getModId() +" (from " + TSMultiTools.MOD_ID + ")");
    }

    // ブロック攻撃時のイベント
    @SubscribeEvent
    public static void onAttackBlock(PlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getEntity();
        Level level = event.getLevel();
        InteractionHand hand = event.getHand();
        BlockPos pos = event.getPos();

        if (!level.isClientSide() || player.getItemInHand(hand).getItem() != ModItems.SLIME.get())
            return;

        // ブロック攻撃に応じてスライムの状態を変更
        SlimeStateClientHandler.onAttackBlock(player, level, hand, pos);
    }

    // 毎ティックのイベント
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        // tickごとのスライム処理
        SlimeTickEventHandler.slimeTickEventHandler(Minecraft.getInstance());
    }
}