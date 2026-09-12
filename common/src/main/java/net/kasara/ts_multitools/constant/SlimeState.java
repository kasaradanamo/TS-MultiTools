package net.kasara.ts_multitools.constant;

import net.kasara.ts_multitools.component.ModComponentsCommon;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;

/**
 * SlimeStateの種類
 */
public final class SlimeState {

    public static final String SLIME = "slime";
    public static final String SWORD = "sword";
    public static final String PICKAXE = "pickaxe";
    public static final String AXE = "axe";
    public static final String SHOVEL = "shovel";
    public static final String HOE = "hoe";

    /**
     * 見た目の状態を書き込む
     */
    public static void apply(ItemStack stack, String state) {
        stack.set(ModComponentsCommon.SLIME_STATE, state);
        stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(customModelDataFor(state)));
    }

    private static int customModelDataFor(String state) {
        return switch (state) {
            case SWORD -> 1;
            case PICKAXE -> 2;
            case AXE -> 3;
            case SHOVEL -> 4;
            case HOE -> 5;
            default -> 0; // SLIME(素の見た目)
        };
    }

    private SlimeState() {}
}
