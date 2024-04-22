package ca.fxco.TinyWorld.mixin.scale;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

    @Shadow public abstract float getSpeed();

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    // Fixes part of: https://bugs.mojang.com/browse/MC-2112
    @Inject(
            method = "getFlyingSpeed",
            at = @At(
                    value = "RETURN",
                    ordinal = 1
            ),
            cancellable = true
    )
    private void tiny$modifyHorizontalAirSpeed(CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue((this.getSpeed() * 10) * cir.getReturnValue());
    }
}
