package net.kasara.ts_multitools.fabric.mixin;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ItemCombinerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * {@link ItemCombinerMenu}(AnvilMenuの親クラス)が持つ{@code protected final}フィールドに
 * アクセスするためのアクセサミキシン。{@link AnvilMenuMixin}から利用する。
 */
@Mixin(ItemCombinerMenu.class)
public interface ItemCombinerMenuAccessor {

    @Accessor("player")
    Player ts_multitools$getPlayer();

    @Accessor("inputSlots")
    Container ts_multitools$getInputSlots();
}
