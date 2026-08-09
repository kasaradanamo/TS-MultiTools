package net.kasara.ts_multitools.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * スライム矢エンティティ
 */
public class SlimeArrowEntity extends Arrow {

    public SlimeArrowEntity(final EntityType<? extends SlimeArrowEntity> type, final Level level) {
        super(type, level);
        this.pickup = Pickup.CREATIVE_ONLY;
    }

    // 弓から発射されるときに使う
    public SlimeArrowEntity(final Level level, final LivingEntity owner) {
        super(ModEntitiesCommon.SLIME_ARROW, level);
        this.setPos(owner.getX(), owner.getEyeY() - 0.1D, owner.getZ());
        this.setOwner(owner);
        this.pickup = Pickup.CREATIVE_ONLY;
    }

    /**
     * 拾ったときのアイテム設定(拾えないので何を返しても実際には使われないが、EMPTYにしておく)
     */
    @Override
    protected ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }
}
