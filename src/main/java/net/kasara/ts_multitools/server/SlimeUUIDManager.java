package net.kasara.ts_multitools.server;

import net.kasara.ts_multitools.component.ModComponents;
import net.kasara.ts_multitools.item.SlimeItem;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

/**
 * プレイヤーのインベントリに存在するSlimeItemにUUIDを付与する
 */
public class SlimeUUIDManager {

    /**
     * 新しく生成されたSlimeItemにUUIDを割り当てる
     * 既にUUIDが存在する場合はスキップ、重複している場合は新規UUIDを付与
     */
    public static void ensureSlimeUUIDs(ServerPlayerEntity player) {
        PlayerInventory inv = player.getInventory();

        // インベントリ内を探す
        for (int i = 0; i< inv.size(); i++) {
            ItemStack stack = inv.getStack(i);
            if (stack.isEmpty() || !(stack.getItem() instanceof SlimeItem)) continue;

            UUID stackUuid = stack.get(ModComponents.SLIME_UUID);

            // UUIDが未設定の場合再生成
            if (stackUuid == null) {
                UUID newUuid = UUID.randomUUID();   // 新規UUID生成
                stack.set(ModComponents.SLIME_UUID, newUuid);
            }
        }
    }
}
