package merlin1809.irisextension.mixin.client;

import net.irisshaders.iris.Iris;
import net.irisshaders.iris.gl.uniform.UniformHolder;
import net.irisshaders.iris.helpers.StringPair;
import net.irisshaders.iris.shaderpack.option.ShaderPackOptions;
import net.irisshaders.iris.shaderpack.properties.ShaderProperties;
import net.irisshaders.iris.uniforms.FrameUpdateNotifier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import merlin1809.irisextension.Variables;

import java.io.IOException;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;


@Mixin(ShaderProperties.class)
public class GetShaderPropertiesMixin {

    @Shadow
    private static void handleWhitespacedListDirective(String key, String value, String expectedKey, Consumer<List<String>> handler) {
    }

    @Shadow
    private List<String> requiredFeatureFlags;

    @Shadow
    private List<String> optionalFeatureFlags;

    @Inject(at = @At("TAIL"), method = "<init>(Ljava/lang/String;Lnet/irisshaders/iris/shaderpack/option/ShaderPackOptions;Ljava/lang/Iterable;)V", locals = LocalCapture.CAPTURE_FAILHARD)
    private void getShaderProperties(String contents, ShaderPackOptions shaderPackOptions, Iterable<StringPair> environmentDefines, CallbackInfo ci, String preprocessedContents, Properties preprocessed, Properties original) {
		Variables.customKeyMethods = new int[10];

        for (int i = 0; i < 10; i++) {
            String propValue = preprocessed.getProperty("iext_key." + i);
            if (propValue == null) continue;

            int method = Variables.fromString(propValue);
            if (method == Variables.NONE) {
                Iris.logger.warn("Unknown method for iext_key." + i + ": " + propValue);
            } else {
                Variables.customKeyMethods[i] = method;
            }
        }

        Variables.tessellationPrograms = new HashSet<>();
        Variables.geometryPrograms = new HashSet<>();

        preprocessed.forEach((keyObject, valueObject) -> {
            String key = (String) keyObject;
            String value = (String) valueObject;

            if (key.startsWith("iext.tessellation.")) {
                String program = key.substring("iext.tessellation.".length());
                if ("false".equals(value) || "off".equals(value)) {
                    Variables.tessellationPrograms.add(program);
                }
            } else if (key.startsWith("iext.geometry.")) {
                String program = key.substring("iext.geometry.".length());
                if ("false".equals(value) || "off".equals(value)) {
                    Variables.geometryPrograms.add(program);
                }
            }
        });
	}
}
