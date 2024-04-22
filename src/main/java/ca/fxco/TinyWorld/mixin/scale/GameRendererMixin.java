package ca.fxco.TinyWorld.mixin.scale;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Shadow @Final private Camera mainCamera;

    // Fixes the clipping issues https://bugs.mojang.com/browse/MC-267376
    @ModifyConstant(
            method = "getProjectionMatrix",
            constant = @Constant(floatValue = 0.05F)
    )
    private float tiny$scaleZNearField(float constant) {
        if (this.mainCamera.getEntity() instanceof LivingEntity livingEntity) {
            double scale = livingEntity.getScale();
            if (scale < 1F) {
                return (float) (constant * scale);
            }
        }
        return constant;
    }
}
