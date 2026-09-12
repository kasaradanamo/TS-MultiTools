package net.kasara.ts_multitools.fabric.mixin;

import com.mojang.datafixers.util.Pair;
import net.kasara.ts_multitools.component.ModComponentsCommon;
import net.kasara.ts_multitools.constant.SlimeState;
import net.kasara.ts_multitools.item.ModItemsCommon;
import net.kasara.ts_multitools.server.data.SlimeStateServerCache;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

/**
 * SLIMEのパケット送信の際、SLIMEが入っていた場合、
 * 別で保存してるデータを差し込んだSLIMEに偽造する
 */
@Mixin(ClientboundSetEquipmentPacket.class)
public class ClientboundSetEquipmentPacketMixin {

    /**
     * EntityEquipmentUpdateS2CPacketのコンストラクタに対して後処理を注入
     */
    @Inject(method = "<init>(ILjava/util/List;)V", at = @At("TAIL"))
    private void onConstruct(int entityId, List<Pair<EquipmentSlot, ItemStack>> list, CallbackInfo ci) {
        // リストを安全に走査しつつ要素を置き換えられる ListIterator を取得
        ListIterator<Pair<EquipmentSlot, ItemStack>> it = list.listIterator();

        // リスト内の全ての (スロット, アイテム) を順番に処理
        while (it.hasNext()) {
            Pair<EquipmentSlot, ItemStack> pair = it.next(); // 現在の要素を取得
            ItemStack stack = pair.getSecond();              // アイテム部分を取得
            if (stack.getItem() == ModItemsCommon.SLIME) {
                UUID uuid = stack.get(ModComponentsCommon.SLIME_UUID);
                if (uuid != null) {
                    // サーバー側に保存されてるstateを取得
                    String state = SlimeStateServerCache.getSlimeState(uuid);
                    if (state != null) {
                        // コピーを作成してstate上書き
                        ItemStack fake = stack.copy();
                        SlimeState.apply(fake, state);

                        // ペアを差し替える（元のリストを上書き）
                        it.set(new Pair<>(pair.getFirst(), fake));
                    }
                }
            }
        }
    }
}
