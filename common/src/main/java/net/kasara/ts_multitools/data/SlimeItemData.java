package net.kasara.ts_multitools.data;

import net.kasara.ts_multitools.component.MiningEnchantLevelComponent;
import net.kasara.ts_multitools.component.SlimeModeComponent;
import net.kasara.ts_multitools.constant.SlimeState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

/**
 * SlimeItemの永続状態(使用モード/採掘モード/幸運・シルクタッチ到達レベル/表示状態/UUID)を
 * ItemStackのNBTタグに読み書きするユーティリティ。
 */
public final class SlimeItemData {

    private static final String KEY_USE_MODE = "UseMode";
    private static final String KEY_MINING_MODE = "MiningMode";
    private static final String KEY_FORTUNE_LEVEL = "FortuneLevel";
    private static final String KEY_SILK_TOUCH_LEVEL = "SilkTouchLevel";
    private static final String KEY_STATE = "SlimeState";
    private static final String KEY_UUID = "SlimeUuid";

    public static SlimeModeComponent getMode(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(KEY_USE_MODE)) return SlimeModeComponent.DEFAULT;
        return new SlimeModeComponent(tag.getString(KEY_USE_MODE), tag.getString(KEY_MINING_MODE));
    }

    public static void setMode(ItemStack stack, SlimeModeComponent mode) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString(KEY_USE_MODE, mode.useMode());
        tag.putString(KEY_MINING_MODE, mode.miningMode());
    }

    public static MiningEnchantLevelComponent getMiningEnchantLevel(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(KEY_FORTUNE_LEVEL)) return MiningEnchantLevelComponent.DEFAULT;
        return new MiningEnchantLevelComponent(tag.getInt(KEY_FORTUNE_LEVEL), tag.getInt(KEY_SILK_TOUCH_LEVEL));
    }

    public static void setMiningEnchantLevel(ItemStack stack, MiningEnchantLevelComponent level) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt(KEY_FORTUNE_LEVEL, level.fortuneLevel());
        tag.putInt(KEY_SILK_TOUCH_LEVEL, level.silkTouchLevel());
    }

    public static String getState(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(KEY_STATE)) return SlimeState.SLIME;
        return tag.getString(KEY_STATE);
    }

    /**
     * 見た目切替を行うために数字でもデータ保持
     */
    public static void setState(ItemStack stack, String state) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString(KEY_STATE, state);
        tag.putInt("CustomModelData", customModelDataFor(state));
    }

    private static int customModelDataFor(String state) {
        return switch (state) {
            case SlimeState.SWORD -> 1;
            case SlimeState.PICKAXE -> 2;
            case SlimeState.AXE -> 3;
            case SlimeState.SHOVEL -> 4;
            case SlimeState.HOE -> 5;
            default -> 0; // SlimeState.SLIME(素の見た目)
        };
    }

    public static UUID getUuid(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.hasUUID(KEY_UUID)) return null;
        return tag.getUUID(KEY_UUID);
    }

    public static boolean hasUuid(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.hasUUID(KEY_UUID);
    }

    public static void setUuid(ItemStack stack, UUID uuid) {
        stack.getOrCreateTag().putUUID(KEY_UUID, uuid);
    }

    private SlimeItemData() {}
}
