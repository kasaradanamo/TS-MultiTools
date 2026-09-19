package net.kasara.ts_multitools.neoforge.mixin;

import com.mojang.datafixers.util.Pair;
import net.kasara.ts_multitools.component.ModComponentsCommon;
import net.kasara.ts_multitools.item.ModItemsCommon;
import net.kasara.ts_multitools.server.data.SlimeStateServerCache;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * SLIMEのパケット送信の際、SLIMEが入っていた場合、
 * 別で保存してるデータを差し込んだSLIMEに偽造する
 */
@Mixin(ClientboundSetEquipmentPacket.class)
public class ClientboundSetEquipmentPacketMixin {

    /**
     * コンストラクタに渡されるリストを、SLIMEだけ状態偽装した新しいリストに差し替える
     */
    @ModifyVariable(method = "<init>(ILjava/util/List;)V", at = @At("HEAD"), argsOnly = true)
    private static List<Pair<EquipmentSlot, ItemStack>> onConstruct(List<Pair<EquipmentSlot, ItemStack>> list) {
        List<Pair<EquipmentSlot, ItemStack>> result = new ArrayList<>(list.size());

        for (Pair<EquipmentSlot, ItemStack> pair : list) {
            ItemStack stack = pair.getSecond();
            if (stack.getItem() == ModItemsCommon.SLIME) {
                UUID uuid = stack.get(ModComponentsCommon.SLIME_UUID);
                if (uuid != null) {
                    // サーバー側に保存されてるstateを取得
                    String state = SlimeStateServerCache.getSlimeState(uuid);
                    if (state != null) {
                        // コピーを作成してstate上書き
                        ItemStack fake = stack.copy();
                        fake.set(ModComponentsCommon.SLIME_STATE, state);

                        result.add(new Pair<>(pair.getFirst(), fake));
                        continue;
                    }
                }
            }
            result.add(pair);
        }

        return result;
    }
}
