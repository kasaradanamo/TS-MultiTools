package net.kasara.ts_multitools.client;

import net.kasara.ts_multitools.data.SlimeItemData;
import net.kasara.ts_multitools.network.packet.c2s.SetSlimeUuidC2SPacket;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class SlimeUuidClientManager {

    /**
     * uuidがあったらそのまま返す
     * なかったら、生成して返す
     */
    public static UUID getOrCreate(ItemStack stack, int slot) {
        UUID uuid = SlimeItemData.getUuid(stack);
        if (uuid == null) {
            uuid = UUID.randomUUID();
            SlimeItemData.setUuid(stack, uuid);      // ローカルにも即時反映
            SetSlimeUuidC2SPacket.send(uuid, slot); // uuidをサーバーに通知
        }
        return uuid;
    }
}
