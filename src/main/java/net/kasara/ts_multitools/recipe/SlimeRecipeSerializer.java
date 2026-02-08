package net.kasara.ts_multitools.recipe;

import com.mojang.serialization.*;
import net.kasara.tokorotenslime.api.TokorotenSlimeAPI;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;

import java.util.stream.Stream;

/**
 * SlimeRecipe 専用のレシピシリアライザ
 * JSON やネットワーク経由で SlimeRecipe を読み書きする役割
 */
public class SlimeRecipeSerializer implements RecipeSerializer<SlimeRecipe> {

    // シングルトンとしてインスタンスを保持
    public static final SlimeRecipeSerializer INSTANCE = new SlimeRecipeSerializer();

    /**
     * データシリアル化用の codec を返す
     * JSON や NBT などの DynamicOps を使ってデータを読み書きする際に使う
     */
    @Override
    public MapCodec<SlimeRecipe> codec() {
        return new MapCodec<>() {

            // エンコード処理（レシピをデータに変換）
            @Override
            public <T> RecordBuilder<T> encode(SlimeRecipe copyEnchantRecipe, DynamicOps<T> dynamicOps, RecordBuilder<T> recordBuilder) {
                // SlimeRecipe は固定なので特に書き込む情報なし
                return recordBuilder;
            }

            // デコード処理（データからレシピを復元）
            @Override
            public <T> DataResult<SlimeRecipe> decode(DynamicOps<T> dynamicOps, MapLike<T> mapLike) {
                // 固定IDの SlimeRecipe を返す
                return DataResult.success(new SlimeRecipe(Identifier.of(TokorotenSlimeAPI.getModId(), "slime_recipe")));
            }

            // JSON などのキー情報を返す
            @Override
            public <T> Stream<T> keys(DynamicOps<T> dynamicOps) {
                // 特に使わないので空
                return Stream.empty();
            }
        };
    }

    /**
     * ネットワーク用の PacketCodec を返す
     * サーバーとクライアント間でレシピ情報を送受信する際に使用
     */
    @Override
    public PacketCodec<RegistryByteBuf, SlimeRecipe> packetCodec() {
        return new PacketCodec<>() {

            // エンコード処理（レシピ情報をバッファに書き込む）
            @Override
            public void encode(RegistryByteBuf buf, SlimeRecipe value) {
                // SlimeRecipe は固定なので何も書き込まない
            }

            // デコード処理（バッファからレシピを読み込む）
            @Override
            public SlimeRecipe decode(RegistryByteBuf buf) {
                // 固定IDの SlimeRecipe を返す
                return new SlimeRecipe(Identifier.of(TokorotenSlimeAPI.getModId(), "slime_recipe"));
            }
        };
    }
}
