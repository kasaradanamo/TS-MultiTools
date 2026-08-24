package net.kasara.ts_multitools.neoforge.server;

import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.kasara.ts_multitools.constant.SlimeState;
import net.kasara.ts_multitools.neoforge.TSMultiTools;
import net.kasara.ts_multitools.neoforge.component.ModComponents;
import net.kasara.ts_multitools.neoforge.item.ModItems;
import net.kasara.ts_multitools.network.packet.s2c.SlimeUseCountS2CPacket;
import net.kasara.ts_multitools.server.SlimeUseCountManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

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

        // 経験値0の場合は攻撃をキャンセルせず素手相当(攻撃力/速度)で扱う(onPlayerTickで属性を制御)。
        // 見た目も経験値0の間は素のスライムのまま(剣の見た目にしない)
        if (player.level().isClientSide()) {
            stack.set(ModComponents.SLIME_STATE.get(), player.totalExperience >= 1 ? SlimeState.SWORD : SlimeState.SLIME);
        }
    }

    // プレイヤーが経験値0でSlimeを持っている間、攻撃力/攻撃速度の補正を無効化して素手相当にする
    // (アイテム自体についているエンチャントの効果は無効化しない)
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return; // 属性値はサーバー側が真値

        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() != ModItems.SLIME.get()) return;

        boolean fistMode = player.totalExperience < 1;
        ItemAttributeModifiers modifiers = stack.getAttributeModifiers();
        for (ItemAttributeModifiers.Entry entry : modifiers.modifiers()) {
            if (!entry.slot().test(EquipmentSlot.MAINHAND)) continue;

            AttributeInstance instance = player.getAttribute(entry.attribute());
            if (instance == null) continue;

            boolean present = instance.getModifier(entry.modifier().id()) != null;
            if (fistMode && present) {
                instance.removeModifier(entry.modifier().id());   // 経験値0: 素手扱いにするため補正を除去
            } else if (!fistMode && !present) {
                instance.addOrUpdateTransientModifier(entry.modifier()); // 経験値ある: Slime本来の補正を再付与
            }
        }
    }

    // プレイヤーが経験値0でSlimeを持っている間、採掘速度を素手相当(1.0)にする
    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() != ModItems.SLIME.get() || player.totalExperience >= 1) return;

        event.setNewSpeed(1.0F);
    }

    // プレイヤーが経験値0でSlimeを持っている間、専用ツールを要求するブロックのドロップを素手相当(不可)にする
    @SubscribeEvent
    public static void onHarvestCheck(PlayerEvent.HarvestCheck event) {
        Player player = event.getEntity();
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() != ModItems.SLIME.get() || player.totalExperience >= 1) return;
        if (!event.getTargetBlock().requiresCorrectToolForDrops()) return; // 素手でも掘れるブロックはそのまま

        event.setCanHarvest(false);
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

}
