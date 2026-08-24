package net.kasara.ts_multitools.neoforge.item;

import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

import java.util.HashSet;
import java.util.Set;

/**
 * マルチツールとスライムがNeoForgeへ申告するツール能力。
 */
final class ModItemAbilities {

    static final Set<ItemAbility> MULTITOOL = new HashSet<>();

    static {
        MULTITOOL.addAll(ItemAbilities.DEFAULT_AXE_ACTIONS);
        MULTITOOL.addAll(ItemAbilities.DEFAULT_SHOVEL_ACTIONS);
        MULTITOOL.addAll(ItemAbilities.DEFAULT_HOE_ACTIONS);
    }

    private ModItemAbilities() {}
}
