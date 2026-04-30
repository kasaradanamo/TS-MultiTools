package net.kasara.ts_multitools.server;

import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.TSMultiTools;
import net.kasara.ts_multitools.component.ModComponents;
import net.kasara.ts_multitools.constant.SlimeState;
import net.kasara.ts_multitools.item.ModItems;
import net.kasara.ts_multitools.item.SlimeEnchantmentRules;
import net.kasara.ts_multitools.network.packet.s2c.SlimeUseCountS2CPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class ModServerEvents {

    public static void register() {
        NeoForge.EVENT_BUS.register(ModServerEvents.class);

        // 登録完了ログを出力
        TSMultiTools.LOGGER.info("Registering addon Mod Server Events for "+ TokorotenSlimeAPI.getModId() +" (from " + TSMultiTools.MOD_ID + ")");
    }

    // プレイヤーがエンティティを攻撃した時のイベント
    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        Player player = event.getEntity();

        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() != ModItems.SLIME.get()) return;

        // 経験値がない場合何もできないようにする
        if (player.totalExperience <1) {
            event.setCanceled(true);
            return;
        }

        // 攻撃した際常にswordになるように
        if (!player.level().isClientSide()) {
            stack.set(ModComponents.SLIME_STATE.get(), SlimeState.SWORD);
        }
    }

    // プレイヤーがブロックを攻撃した時のイベント
    @SubscribeEvent
    public static void onAttackBlock(PlayerInteractEvent.LeftClickBlock event) {
        // 条件に応じて攻撃をキャンセルするか判定
        if (SlimeAttackHandler.shouldCancelAttack(event.getEntity(), event.getHand())) {
            event.setCanceled(true);   // 攻撃をキャンセル
        }
    }

    // プレイヤーがワールドに入った時のイベント
    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();

        //旧データがあって、新データがなかった場合コピー
        SlimeUseCountManager.migrateIfNeeded(player);

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
        if (event.getPlayer().isCreative() || event.getLeft().getItem() != ModItems.SLIME.get()) return;

        var enchantments = EnchantmentHelper.getEnchantmentsForCrafting(event.getRight());

        // 右側のスロットにブラックリストのエンチャントがついたアイテムがあったらtrue(付与不可)
        boolean hasBlacklisted = enchantments.entrySet().stream()
                .anyMatch(e -> SlimeEnchantmentRules.isBlacklisted(e.getKey()));

        if (hasBlacklisted) {
            event.setOutput(ItemStack.EMPTY);
        }
    }
}
