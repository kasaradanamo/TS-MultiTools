package net.kasara.ts_multitools.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * 特殊な「スライム矢」エンティティ。
 * - 通常の矢と異なり、アイテムとして回収できない（クリエイティブ限定）。
 * - 主に見た目や挙動をカスタムするための基盤クラス。
 */
public class SlimeArrowEntity extends PersistentProjectileEntity {

    /**
     * ワールド内に直接スポーンさせるときに使われるコンストラクタ。
     *
     * @param entityType エンティティの種類
     * @param world      ワールド
     */
    public SlimeArrowEntity(EntityType<? extends SlimeArrowEntity> entityType, World world) {
        super(entityType, world);
        // 矢を回収できるのはクリエイティブモードのみ
        this.pickupType = PickupPermission.CREATIVE_ONLY;
    }

    /**
     * 弓などから発射されたときに使われるコンストラクタ。
     *
     * @param type    エンティティの種類
     * @param owner   矢を発射したエンティティ（プレイヤーやMobなど）
     * @param world   ワールド
     * @param stack   矢として使用した ItemStack
     * @param weapon  発射に使われた武器（弓など）、null の可能性あり
     */
    public SlimeArrowEntity(EntityType<? extends SlimeArrowEntity> type, LivingEntity owner, World world, ItemStack stack, @Nullable ItemStack weapon) {
        super(type, owner, world, stack, weapon);
        // 矢を回収できるのはクリエイティブモードのみ
        this.pickupType = PickupPermission.CREATIVE_ONLY;
    }

    /**
     * 矢が地面に落ちたときなど、アイテムとして回収される際に返すスタック。
     * このクラスでは「拾えない矢」として機能させるため EMPTY を返す。
     *
     * @return 空の ItemStack（何も返さない）
     */
    @Override
    protected ItemStack getDefaultItemStack() {
        return ItemStack.EMPTY;
    }
}
