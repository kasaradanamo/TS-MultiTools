package net.kasara.ts_multitools.client;

import net.kasara.ts_multitools.component.ModComponentsCommon;
import net.kasara.ts_multitools.network.packet.c2s.SetSlimeUuidC2SPacket;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class SlimeUuidClientManager {

    public static UUID getOrCreate(ItemStack stack, int slot) {
        UUID uuid = stack.get(ModComponentsCommon.SLIME_UUID);
        if (uuid == null) {
            uuid = UUID.randomUUID();
            stack.set(ModComponentsCommon.SLIME_UUID, uuid);  // ローカルにも即時反映
            SetSlimeUuidC2SPacket.send(uuid, slot);      // uuidをサーバーに通知
        }
        return uuid;
    }
}