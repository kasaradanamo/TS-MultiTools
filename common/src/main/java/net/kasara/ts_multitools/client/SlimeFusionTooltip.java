package net.kasara.ts_multitools.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.SmithingScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 鍛冶台の完成品に、スライム合体で外れる・レベルが下がるエンチャントを取り消し線付きで表示する
 */
public final class SlimeFusionTooltip {

    public static void appendTooltip(ItemStack stack, List<Component> lines) {
        if (!(Minecraft.getInstance().screen instanceof SmithingScreen screen)) return;

        SmithingMenu menu = screen.getMenu();
        if (stack != menu.getSlot(SmithingMenu.RESULT_SLOT).getItem()) return;

        Map<Enchantment, Integer> before = EnchantmentHelper.getEnchantments(menu.getSlot(SmithingMenu.BASE_SLOT).getItem());
        Map<Enchantment, Integer> after = EnchantmentHelper.getEnchantments(stack);

        List<Component> removed = new ArrayList<>();
        for (Map.Entry<Enchantment, Integer> entry : before.entrySet()) {
            Enchantment enchantment = entry.getKey();
            // 外れるものと、レベルが下がるものを元のレベルで出す
            if (after.getOrDefault(enchantment, 0) >= entry.getValue()) continue;
            removed.add(enchantment.getFullname(entry.getValue()).copy()
                    .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.STRIKETHROUGH));
        }
        if (removed.isEmpty()) return;

        lines.addAll(insertIndex(lines, after), removed);
    }

    /**
     * 残るエンチャントの最後の行の後ろ。無ければ使用モードの行の後ろ、それも無ければ末尾
     */
    private static int insertIndex(List<Component> lines, Map<Enchantment, Integer> after) {
        Set<Component> enchantLines = new HashSet<>();
        for (Map.Entry<Enchantment, Integer> entry : after.entrySet()) {
            enchantLines.add(entry.getKey().getFullname(entry.getValue()));
        }

        int lastEnchant = -1;
        int useMode = -1;
        for (int i = 0; i < lines.size(); i++) {
            Component line = lines.get(i);
            if (enchantLines.contains(line)) lastEnchant = i;
            if (line.getContents() instanceof TranslatableContents contents
                    && contents.getKey().equals("tooltip.tokorotenslime.use_mode")) useMode = i;
        }

        if (lastEnchant >= 0) return lastEnchant + 1;
        if (useMode >= 0) return useMode + 1;
        return lines.size();
    }

    private SlimeFusionTooltip() {}
}
