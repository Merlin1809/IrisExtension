package merlin1809.irisextension.mixin;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import merlin1809.irisextension.networking.DragonUniformsPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.dimension.end.EnderDragonFight;

import org.jspecify.annotations.Nullable;

@Mixin({EnderDragonFight.class})
public class DragonFightMixin {
    @Shadow
   private ServerLevel level;
   @Shadow
   private boolean dragonKilled;
   @Shadow
   private boolean hasPreviouslyKilledDragon;
   @Shadow
   private int aliveCrystals;
   @Shadow
   private ServerBossEvent dragonEvent;
   @Shadow
   private @Nullable UUID dragonUUID;

   private int previousCrystals;
   private boolean dragonKilledPrevious;
   private int previousPlayerCount;
   private float previousProgress;

    private void sendDragonStuff(boolean alive, boolean firstDragonKilled, int crystalCount, int playerCount, float progress) {
        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
            if(player != null && ServerPlayNetworking.canSend(player, DragonUniformsPayload.ID)) ServerPlayNetworking.send(player, new DragonUniformsPayload(alive, firstDragonKilled, crystalCount, playerCount, progress));
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void onDragonKilled(CallbackInfo ci) {
        int players = this.dragonEvent.getPlayers().size();

        float progress = this.dragonEvent.getProgress();

        if(this.dragonKilledPrevious != this.dragonKilled || this.previousPlayerCount != players || this.previousCrystals != this.aliveCrystals || Math.abs(progress - this.previousProgress) > 0.025f) {
            sendDragonStuff(!dragonKilled, hasPreviouslyKilledDragon, aliveCrystals, players, progress);
        }

        this.dragonKilledPrevious = this.dragonKilled;
        this.previousPlayerCount = players;
        this.previousCrystals = this.aliveCrystals;
        this.previousProgress = progress;
    }

}
