package merlin1809.irisextension;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import merlin1809.irisextension.networking.DragonUniformsPayload;

public class IrisExtension implements ModInitializer {
	public static final String MOD_ID = "iris-extension";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		PayloadTypeRegistry.clientboundPlay().register(DragonUniformsPayload.ID, DragonUniformsPayload.CODEC);
	}
}