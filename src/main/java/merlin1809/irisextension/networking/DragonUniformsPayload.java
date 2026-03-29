package merlin1809.irisextension.networking;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record DragonUniformsPayload(boolean dragonAlive, boolean firstDragonKilled, int crystalsAmount, int playerCount, float progress) implements CustomPacketPayload {
   public static final Identifier DRAGON_PAYLOAD_ID = Identifier.fromNamespaceAndPath("iris-extension", "dragon_alive");
   public static final CustomPacketPayload.Type<DragonUniformsPayload> ID = new CustomPacketPayload.Type<>(DRAGON_PAYLOAD_ID);
   public static final StreamCodec<RegistryFriendlyByteBuf, DragonUniformsPayload> CODEC = StreamCodec.composite(
       ByteBufCodecs.BOOL, DragonUniformsPayload::dragonAlive,
       ByteBufCodecs.BOOL, DragonUniformsPayload::firstDragonKilled,
       ByteBufCodecs.INT, DragonUniformsPayload::crystalsAmount,
       ByteBufCodecs.INT, DragonUniformsPayload::playerCount,
       ByteBufCodecs.FLOAT, DragonUniformsPayload::progress,
       DragonUniformsPayload::new
    );

   @Override
   public Type<? extends CustomPacketPayload> type() {
       return ID;
   }
}
