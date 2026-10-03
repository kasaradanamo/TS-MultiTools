package net.kasara.ts_multitools.client;

import com.mojang.serialization.MapCodec;
import net.kasara.ts_multitools.TSMultiToolsCommon;
import net.kasara.ts_multitools.item.SlimeItem;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.client.renderer.item.properties.numeric.UseDuration;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * スライムの弓の引き絞りの進み具合（使ったtick数÷引き絞りtick）。アイテムの見た目のJSONから ts_multitools:slime_pull で使う
 */
public record SlimePullProperty() implements RangeSelectItemModelProperty {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(TSMultiToolsCommon.MOD_ID, "slime_pull");
    public static final MapCodec<SlimePullProperty> MAP_CODEC = MapCodec.unit(new SlimePullProperty());

    @Override
    public float get(ItemStack stack, ClientLevel level, ItemOwner owner, int seed) {
        LivingEntity entity = owner == null ? null : owner.asLivingEntity();
        if (entity == null || entity.getUseItem() != stack) return 0.0F;
        return UseDuration.useDuration(stack, entity) / SlimeItem.getDrawTicks(stack);
    }

    @Override
    public MapCodec<SlimePullProperty> type() {
        return MAP_CODEC;
    }
}
