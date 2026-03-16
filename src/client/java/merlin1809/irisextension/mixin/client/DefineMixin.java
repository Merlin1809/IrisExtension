package merlin1809.irisextension.mixin.client;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.sugar.Local;

import net.fabricmc.loader.api.FabricLoader;
import net.irisshaders.iris.gl.shader.StandardMacros;
import net.irisshaders.iris.helpers.StringPair;

import merlin1809.irisextension.Variables;

@Mixin(StandardMacros.class)
public class DefineMixin {
    private static void define(List<StringPair> defines, String key) {
      defines.add(new StringPair(key, ""));
   }

   private static void define(List<StringPair> defines, String key, String value) {
      defines.add(new StringPair(key, value));
   }

   @Inject(at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableList;copyOf(Ljava/util/Collection;)Lcom/google/common/collect/ImmutableList;"), method = "createStandardEnvironmentDefines")
    private static void addDefines(CallbackInfoReturnable<ImmutableList<StringPair>> ci, @Local ArrayList<StringPair> standardDefines) {
        define(standardDefines, "IEXT_ENABLED");
        if (Variables.isSereneSeasonsLoaded) {
            define(standardDefines, "SERENE_SEASONS");
        }

        if (Variables.isBeltborneLanternsLoaded) {
            define(standardDefines, "BELTBORNE_LANTERNS");
        }

    }
}
