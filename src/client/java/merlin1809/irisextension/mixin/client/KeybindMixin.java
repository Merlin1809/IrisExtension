package merlin1809.irisextension.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.platform.InputConstants;

import merlin1809.irisextension.mixin.client.GetShaderPropertiesMixin;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.gl.uniform.UniformHolder;
import net.irisshaders.iris.platform.IrisPlatformHelpers;
import net.irisshaders.iris.uniforms.FrameUpdateNotifier;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

@Mixin(Iris.class)
public class KeybindMixin {

    private static KeyMapping[] customKeybinds = new KeyMapping[10];
    private static final KeyMapping.Category irisExtensionKeybindCategory = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("iris_extension", "keybinds"));
    
    @Inject(at = @At("HEAD"), method = "onEarlyInitialize")
	private static void addKeybinds(CallbackInfo ci) {
        for (int i = 0; i < 10; i++) {
		    customKeybinds[i] = IrisPlatformHelpers.getInstance().registerKeyBinding(new KeyMapping("iris_extension.keybind.key" + i, InputConstants.Type.KEYSYM, InputConstants.UNKNOWN.getValue(), irisExtensionKeybindCategory));
        }
	}

    @Inject(at = @At("TAIL"), method = "handleKeybinds")
	private static void useKeybinds(Minecraft minecraft, CallbackInfo ci) {
        for (int i = 0; i < 10; i++) {
            if (merlin1809.irisextension.Variables.getKeyMethod(i) == merlin1809.irisextension.Variables.TOGGLE) {
                if (customKeybinds[i].consumeClick()) {
                    merlin1809.irisextension.Variables.keyPressed[i] = !merlin1809.irisextension.Variables.keyPressed[i];
                }
            } else if (merlin1809.irisextension.Variables.getKeyMethod(i) == merlin1809.irisextension.Variables.HOLD) {
                merlin1809.irisextension.Variables.keyPressed[i] = false;
                if (customKeybinds[i].isDown()) {
                    merlin1809.irisextension.Variables.keyPressed[i] = true;
                }
            }
        }
	}
}
