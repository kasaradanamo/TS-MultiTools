package net.kasara.ts_multitools.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kasara.ts_multitools.component.ModComponents;
import net.kasara.ts_multitools.network.packet.c2s.SetSlimeUuidC2SPacket;
import net.minecraft.item.ItemStack;

import java.util.UUID;

@Environment(EnvType.CLIENT)
public class SlimeUuidClientManager {

    /**
     * uuidがあったらそのまま返す
     * なかったら、生成して返す
     */
    public static UUID getOrCreate(ItemStack stack, int slot) {
        UUID uuid = stack.get(ModComponents.SLIME_UUID);
        if (uuid == null) {
            uuid = UUID.randomUUID();
            SetSlimeUuidC2SPacket.send(uuid, slot); // uuidをサーバーに通知
        }
        return uuid;
    }
}
