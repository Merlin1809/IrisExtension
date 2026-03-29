package merlin1809.irisextension.mixin;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.end.EnderDragonFight;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.network.Connection;
import net.minecraft.server.network.CommonListenerCookie;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import merlin1809.irisextension.networking.DragonUniformsPayload;

@Mixin(PlayerList.class)
public class PlayerJoinMixin {
    @Shadow
    private MinecraftServer server;

    @Inject(
      method = {"placeNewPlayer"}, at = {@At("TAIL")}
    )
    private void onPlayerJoin(final Connection connection, final ServerPlayer player, final CommonListenerCookie cookie, CallbackInfo ci) {
      if (player != null) {
         if (ServerPlayNetworking.canSend(player, DragonUniformsPayload.ID)) {
            ServerLevel endLevel = this.server.getLevel(Level.END);
            if (endLevel == null) {
               return;
            }

            EnderDragonFight fight = endLevel.getDragonFight();
            boolean dragonAlive = fight != null && !((GetDragonKilledMixin)fight).isDragonKilled();
            boolean firstDragonKilled = fight != null && ((GetDragonKilledMixin)fight).firstDragonKilled();
            int crystals = 0;
            int playersDragon = 0;
            float progress = 0.0f;
            if (fight != null) {
               crystals = ((GetDragonKilledMixin)fight).getCrystals();

               playersDragon = ((GetDragonKilledMixin)fight).getDragonEvent().getPlayers().size();

               progress = ((GetDragonKilledMixin)fight).getDragonEvent().getProgress();
            } 

            ServerPlayNetworking.send(player, new DragonUniformsPayload(dragonAlive, firstDragonKilled, crystals, playersDragon, progress));
         }

      }
   }
    
}
