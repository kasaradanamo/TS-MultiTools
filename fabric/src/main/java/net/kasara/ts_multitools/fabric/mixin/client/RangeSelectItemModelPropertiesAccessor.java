package net.kasara.ts_multitools.fabric.mixin.client;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * アイテムの見た目のJSONで使える数値の種類の登録先を読む
 */
@Mixin(RangeSelectItemModelProperties.class)
public interface RangeSelectItemModelPropertiesAccessor {

    @Accessor("ID_MAPPER")
    static ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<? extends RangeSelectItemModelProperty>> ts_multitools$getIdMapper() {
        throw new AssertionError();
    }
}
