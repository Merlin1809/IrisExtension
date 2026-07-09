package merlin1809.irisextension;

import merlin1809.irisextension.networking.DragonUniformsPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;

public class IrisExtensionClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		if (FabricLoader.getInstance().isModLoaded("sereneseasons")) merlin1809.irisextension.Variables.isSereneSeasonsLoaded = true;
		if (FabricLoader.getInstance().isModLoaded("beltborne_lanterns")) merlin1809.irisextension.Variables.isBeltborneLanternsLoaded = true;
		if (FabricLoader.getInstance().isModLoaded("aeronautics")) merlin1809.irisextension.Variables.isCreateAeronauticsLoaded = true;

		ClientPlayNetworking.registerGlobalReceiver(DragonUniformsPayload.ID, (payload, context) -> context.client().execute(() -> {
            Variables.dragonAlive = payload.dragonAlive();
            Variables.firstdragonKilled = payload.firstDragonKilled();
            Variables.crystalsAmount = payload.crystalsAmount();
            Variables.playersCountDragon = payload.playerCount();
			Variables.dragonProgress = payload.progress();
         }));
	}
}