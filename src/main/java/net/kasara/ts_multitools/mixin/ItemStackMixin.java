package net.kasara.ts_multitools.mixin;

import net.kasara.ts_multitools.item.SlimeItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

/**
 * ItemStackMixin クラス
 * ItemStack の特定メソッドに対して Mixin を適用
 * SlimeItem 専用の耐久無効化や弓判定を追加する
 */
@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    /** 元の ItemStack.getItem() メソッドを呼び出すための Shadow */
    @Shadow
    public abstract Item getItem();

    /**
     * SlimeItem の耐久消費を無効化
     *
     * damage メソッド実行前にチェックし、SlimeItem ならキャンセルする
     *
     * @param amount ダメージ量
     * @param world サーバーワールド
     * @param player ダメージ対象のプレイヤー（nullの可能性あり）
     * @param breakCallback 耐久0時のコールバック
     * @param ci Mixin 用 CallbackInfo
     */
    @Inject(
            method = "damage(ILnet/minecraft/server/world/ServerWorld;Lnet/minecraft/server/network/ServerPlayerEntity;Ljava/util/function/Consumer;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void preventDurabilityForSlime(int amount, ServerWorld world, @Nullable ServerPlayerEntity player, Consumer<Item> breakCallback, CallbackInfo ci) {
        if (getItem() instanceof SlimeItem) {
            ci.cancel(); // SlimeItem の耐久消費を完全に無効化
        }
    }

    /**
     * SlimeItem を弓として扱う判定のフック
     *
     * ItemStack.isOf(Item) の返り値を改変
     * 弓判定が false でも、SlimeItem なら true に変更
     *
     * @param item 判定対象の Item
     * @param cir Mixin 用 CallbackInfoReturnable<Boolean>
     */
    @Inject(
            method = "isOf(Lnet/minecraft/item/Item;)Z",
            at = @At("RETURN"),
            cancellable = true
    )
    private void isOfHook(Item item, CallbackInfoReturnable<Boolean> cir) {
        boolean original = cir.getReturnValue();
        // 元の判定が false で、対象が弓かつ自分が SlimeItem の場合
        if (!original && item instanceof BowItem && getItem() instanceof SlimeItem) {
            cir.setReturnValue(true);  // SlimeItem を弓として扱う
        }
    }
}



