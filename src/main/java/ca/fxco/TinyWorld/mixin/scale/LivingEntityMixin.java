package ca.fxco.TinyWorld.mixin.scale;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Shadow public abstract float getScale();

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(
            method = "jumpFromGround",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;isSprinting()Z"
            )
    )
    private void tiny$getScaleOnce(CallbackInfo ci, @Share("scale") LocalDoubleRef scaleRef) {
        scaleRef.set(this.getScale());
    }

    // TODO: I think this should actually be using speed, not scale.
    // Fixes part of: https://bugs.mojang.com/browse/MC-2112
    @ModifyConstant(
            method = "jumpFromGround",
            constant = @Constant(doubleValue = 0.2)
    )
    private double tiny$modifyJumpVelocityStrength(double constant, @Share("scale") LocalDoubleRef scaleRef) {
        return scaleRef.get() * constant;
    }

    @ModifyArg(
            method = "calculateEntityAnimation",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;updateWalkAnimation(F)V"
            )
    )
    private float tiny$scaleWalkingAnimation(float f) {
        return f / this.getScale();
    }
}
