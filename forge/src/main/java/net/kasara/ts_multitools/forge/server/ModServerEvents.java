package net.kasara.ts_multitools.forge.server;

import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.TSMultiToolsCommon;
import net.kasara.ts_multitools.item.ModItemsCommon;
import net.kasara.ts_multitools.network.packet.s2c.SlimeUseCountS2CPacket;
import net.kasara.ts_multitools.server.ModServerEventsCommon;
import net.kasara.ts_multitools.server.SlimeUseCountManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * サーバー側イベントハンドラ(判定ロジックは{@link ModServerEventsCommon}を共有)。
 */
public class ModServerEvents {

    public static void register() {
        MinecraftForge.EVENT_BUS.register(ModServerEvents.class);

        // 登録完了ログを出力
        TSMultiToolsCommon.LOGGER.info("Registering addon Mod Server Events for " + TokorotenSlimeAPI.getModId() + " (from " + TSMultiToolsCommon.MOD_ID + ")");
    }

    // プレイヤーがエンティティを攻撃した時のイベント
    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        Player player = event.getEntity();
        ModServerEventsCommon.onAttackEntity(player);
    }

    // プレイヤーが経験値0でSlimeを持っている間、攻撃力/攻撃速度の補正を無効化して素手相当にする
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Player player = event.player;
        if (player.level().isClientSide()) return; // 属性値はサーバー側が真値

        ModServerEventsCommon.onPlayerTick(player);
    }

    // プレイヤーが経験値0でSlimeを持っている間、採掘速度を素手相当(1.0)にする
    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        Float speed = ModServerEventsCommon.onBreakSpeed(player);
        if (speed != null) event.setNewSpeed(speed);
    }

    // プレイヤーが経験値0でSlimeを持っている間、専用ツールを要求するブロックのドロップを素手相当(不可)にする
    @SubscribeEvent
    public static void onHarvestCheck(PlayerEvent.HarvestCheck event) {
        Player player = event.getEntity();
        Boolean canHarvest = ModServerEventsCommon.onHarvestCheck(player, event.getTargetBlock());
        if (canHarvest != null) event.setCanHarvest(canHarvest);
    }

    // プレイヤーがワールドに入った時のイベント
    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        // サーバー側で管理しているSlimeItemの使用回数をクライアントに送信
        SlimeUseCountS2CPacket.send(player, SlimeUseCountManager.get(player));
    }

    // プレイヤーが別プレイヤーインスタンスにコピーされる時のイベント
    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        SlimeUseCountManager.copyFrom(event.getOriginal(), event.getEntity());
    }

    // 金床使用時のイベント
    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        boolean hasBlacklisted = ModServerEventsCommon.hasBlacklistedAnvilEnchant(event.getPlayer(), event.getLeft(), event.getRight());

        if (hasBlacklisted) {
            event.setOutput(ItemStack.EMPTY);
        }
    }
}
