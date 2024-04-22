package ca.fxco.TinyWorld.mixin.scale;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Camera.class)
public class CameraMixin {

    @ModifyConstant(
            method = "getMaxZoom",
            constant = @Constant(
                    floatValue = 0.1F
            )
    )
    private float tiny$scaleCameraRays(float constant, @Local(ordinal = 0, argsOnly = true) double d) {
        return (float) (0.025 * d); // d / 4 = scale, 0.1 * scale, simplifies to 0.025 * d
    }
}
