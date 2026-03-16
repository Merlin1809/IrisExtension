package merlin1809.irisextension.mixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.level.dimension.end.EndDragonFight;

@Mixin({EndDragonFight.class})
public interface GetDragonKilledMixin {
   @Accessor("dragonKilled")
   boolean isDragonKilled();

   @Accessor("previouslyKilled")
   boolean firstDragonKilled();

   @Accessor("crystalsAlive")
   int getCrystals();

   @Accessor("dragonEvent")
   ServerBossEvent getDragonEvent();
}
