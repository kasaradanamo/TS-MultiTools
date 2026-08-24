package net.kasara.ts_multitools.neoforge;

import net.kasara.ts_multitools.TSMultiToolsCommon;
import net.kasara.ts_multitools.component.ModComponentsCommon;
import net.kasara.ts_multitools.entity.ModEntitiesCommon;
import net.kasara.ts_multitools.item.ModItemsCommon;
import net.kasara.ts_multitools.network.ModPacketsCommon;
import net.kasara.ts_multitools.neoforge.command.ModCommands;
import net.kasara.ts_multitools.neoforge.component.ModComponents;
import net.kasara.ts_multitools.neoforge.entity.ModEntities;
import net.kasara.ts_multitools.neoforge.item.ModItems;
import net.kasara.ts_multitools.neoforge.network.ModPackets;
import net.kasara.ts_multitools.neoforge.recipe.ModRecipeSerializers;
import net.kasara.ts_multitools.neoforge.server.ModServerEvents;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;

@Mod(TSMultiTools.MOD_ID)
public class TSMultiTools {

    public static final String MOD_ID = TSMultiToolsCommon.MOD_ID;
    public static final Logger LOGGER = TSMultiToolsCommon.LOGGER;

    public TSMultiTools(IEventBus modEventBus) {
        // カスタムコンポーネント登録
        ModComponents.register(modEventBus);

        modEventBus.addListener(this::onRegisterDataComponents);

        // アイテム登録
        ModItems.register(modEventBus);

        // エンティティ登録
        ModEntities.register(modEventBus);

        // レシピシリアライザー登録
        ModRecipeSerializers.register(modEventBus);

        // ペイロード登録
        ModPackets.register(modEventBus);

        // コマンドイベント登録
        ModCommands.register();

        // サーバーイベント登録
        ModServerEvents.register();

        // レジストリ登録の完了後にcommonのブリッジへ反映する
        modEventBus.addListener(this::commonSetup);
    }

    private void onRegisterDataComponents(RegisterEvent event) {
        if (!event.getRegistryKey().equals(Registries.DATA_COMPONENT_TYPE)) return;

        ModComponentsCommon.SLIME_MODE = ModComponents.SLIME_MODE.get();
        ModComponentsCommon.MINING_ENCHANT_LEVEL = ModComponents.MINING_ENCHANT_LEVEL.get();
        ModComponentsCommon.SLIME_STATE = ModComponents.SLIME_STATE.get();
        ModComponentsCommon.SLIME_UUID = ModComponents.SLIME_UUID.get();
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        ModItemsCommon.SLIME = ModItems.SLIME.get();
        ModItemsCommon.NETHERITE_MULTITOOL = ModItems.NETHERITE_MULTITOOL.get();

        ModEntitiesCommon.SLIME_ARROW = ModEntities.SLIME_ARROW.get();

        // サーバーからクライアントへの送信ブリッジ
        ModPacketsCommon.SEND_TO_PLAYER = ModPackets::sendToPlayer;
    }
}
