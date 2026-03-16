package merlin1809.irisextension.mixin.client;

import net.irisshaders.iris.gl.blending.BlendModeOverride;
import net.irisshaders.iris.shaderpack.include.AbsolutePackPath;
import net.irisshaders.iris.shaderpack.programs.ProgramSet;
import net.irisshaders.iris.shaderpack.properties.ShaderProperties;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;
import java.util.List;
import java.util.Set;

import merlin1809.irisextension.Variables;


@Mixin(ProgramSet.class)
public class ProgramMixin {
    @ModifyVariable(
        method = "readProgramSource(Lnet/irisshaders/iris/shaderpack/include/AbsolutePackPath;Ljava/util/function/Function;Ljava/lang/String;Lnet/irisshaders/iris/shaderpack/programs/ProgramSet;Lnet/irisshaders/iris/shaderpack/properties/ShaderProperties;Lnet/irisshaders/iris/gl/blending/BlendModeOverride;Z)Lnet/irisshaders/iris/shaderpack/programs/ProgramSource;",
        at = @At("HEAD"),
        argsOnly = true
    )
    private static boolean changeTessellation(boolean readTesselation,
        AbsolutePackPath directory, Function<AbsolutePackPath, String> sourceProvider, String program,
        ProgramSet programSet, ShaderProperties properties,
        BlendModeOverride defaultBlendModeOverride) {
            if (!readTesselation) return false;
            return !Variables.tessellationPrograms.contains(program);
    }

    @Redirect(
        method = "readProgramSource(Lnet/irisshaders/iris/shaderpack/include/AbsolutePackPath;Ljava/util/function/Function;Ljava/lang/String;Lnet/irisshaders/iris/shaderpack/programs/ProgramSet;Lnet/irisshaders/iris/shaderpack/properties/ShaderProperties;Lnet/irisshaders/iris/gl/blending/BlendModeOverride;Z)Lnet/irisshaders/iris/shaderpack/programs/ProgramSource;",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/function/Function;apply(Ljava/lang/Object;)Ljava/lang/Object;",
            ordinal = 1
        )
    )
    private static Object changeGeometry(Function<AbsolutePackPath, String> sourceProvider, Object path,
        AbsolutePackPath directory, Function<AbsolutePackPath, String> sourceProviderArg, String program,
        ProgramSet programSet, ShaderProperties properties,
        BlendModeOverride defaultBlendModeOverride, boolean readTesselation) {
            if (Variables.geometryPrograms.contains(program)) return null;
            return sourceProvider.apply((AbsolutePackPath) path);
    }
}
