package net.kasara.ts_multitools.entity;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

/**
 * スライム矢エンティティ
 * クリエ以外では拾えない
 * 矢をコピーしてる
 */
public class SlimeArrowEntity extends AbstractArrow {

    private static final int EXPOSED_POTION_DECAY_TIME = 600;
    private static final int NO_EFFECT_COLOR = -1;
    private static final EntityDataAccessor<Integer> ID_EFFECT_COLOR = SynchedEntityData.defineId(SlimeArrowEntity.class, EntityDataSerializers.INT);
    private static final byte EVENT_POTION_PUFF = 0;

    public SlimeArrowEntity(final EntityType<? extends SlimeArrowEntity> type, final Level level) {
        super(type, level);
        this.pickup = Pickup.CREATIVE_ONLY;
    }

    public SlimeArrowEntity(final Level level, final double x, final double y, final double z, final ItemStack pickupItemStack, @Nullable final ItemStack firedFromWeapon) {
        super(ModEntities.SLIME_ARROW.get(), x, y, z, level, pickupItemStack, firedFromWeapon);
        this.updateColor();
        this.pickup = Pickup.CREATIVE_ONLY;
    }

    // 弓から発射されるときに使う
    public SlimeArrowEntity(final Level level, final LivingEntity owner, final ItemStack pickupItemStack, @Nullable final ItemStack firedFromWeapon) {
        super(ModEntities.SLIME_ARROW.get(), owner, level, pickupItemStack, firedFromWeapon);
        this.updateColor();
        this.pickup = Pickup.CREATIVE_ONLY;
    }

    private PotionContents getPotionContents() {
        return this.getPickupItemStackOrigin().getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
    }

    private float getPotionDurationScale() {
        return this.getPickupItemStackOrigin().getOrDefault(DataComponents.POTION_DURATION_SCALE, 1.0F);
    }

    private void setPotionContents(final PotionContents potionContents) {
        this.getPickupItemStackOrigin().set(DataComponents.POTION_CONTENTS, potionContents);
        this.updateColor();
    }

    @Override
    protected void setPickupItemStack(final ItemStack itemStack) {
        super.setPickupItemStack(itemStack);
        this.updateColor();
    }

    private void updateColor() {
        PotionContents potionContents = this.getPotionContents();
        this.entityData.set(ID_EFFECT_COLOR, potionContents.equals(PotionContents.EMPTY) ? -1 : potionContents.getColor());
    }

    public void addEffect(final MobEffectInstance effect) {
        this.setPotionContents(this.getPotionContents().withEffectAdded(effect));
    }

    @Override
    protected void defineSynchedData(final SynchedEntityData.Builder entityData) {
        super.defineSynchedData(entityData);
        entityData.define(ID_EFFECT_COLOR, -1);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            if (this.isInGround()) {
                if (this.inGroundTime % 5 == 0) {
                    this.makeParticle(1);
                }
            } else {
                this.makeParticle(2);
            }
        } else if (this.isInGround() && this.inGroundTime != 0 && !this.getPotionContents().equals(PotionContents.EMPTY) && this.inGroundTime >= EXPOSED_POTION_DECAY_TIME) {
            this.level().broadcastEntityEvent(this, EVENT_POTION_PUFF);
            this.setPickupItemStack(new ItemStack(Items.ARROW));
        }
    }

    private void makeParticle(final int amount) {
        int colorValue = this.getColor();
        if (colorValue != -1 && amount > 0) {
            for (int i = 0; i < amount; i++) {
                this.level()
                        .addParticle(
                                ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, colorValue), this.getRandomX(0.5), this.getRandomY(), this.getRandomZ(0.5), 0.0, 0.0, 0.0
                        );
            }
        }
    }

    public int getColor() {
        return this.entityData.get(ID_EFFECT_COLOR);
    }

    @Override
    protected void doPostHurtEffects(final LivingEntity mob) {
        super.doPostHurtEffects(mob);
        Entity effectSource = this.getEffectSource();
        PotionContents potionContents = this.getPotionContents();
        float durationScale = this.getPotionDurationScale();
        potionContents.forEachEffect(effect -> mob.addEffect(effect, effectSource), durationScale);
    }

    /**
     * 拾ったときのアイテム設定(ここは変更する)
     */
    @Override
    protected ItemStack getDefaultPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public void handleEntityEvent(final byte id) {
        if (id == 0) {
            int colorValue = this.getColor();
            if (colorValue != -1) {
                float red = (colorValue >> 16 & 0xFF) / 255.0F;
                float green = (colorValue >> 8 & 0xFF) / 255.0F;
                float blue = (colorValue >> 0 & 0xFF) / 255.0F;

                for (int i = 0; i < 20; i++) {
                    this.level()
                            .addParticle(
                                    ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, red, green, blue), this.getRandomX(0.5), this.getRandomY(), this.getRandomZ(0.5), 0.0, 0.0, 0.0
                            );
                }
            }
        } else {
            super.handleEntityEvent(id);
        }
    }
}