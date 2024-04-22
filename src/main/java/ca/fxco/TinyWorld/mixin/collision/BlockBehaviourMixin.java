package ca.fxco.TinyWorld.mixin.collision;

import ca.fxco.TinyWorld.bridge.BlockBehaviourBridge;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.class)
public abstract class BlockBehaviourMixin implements BlockBehaviourBridge {

    @Unique
    private VoxelShape tiny$customCollisionShape; // Yes, I also hate this

    @Override
    public void tiny$setCustomCollisionShape(@Nullable VoxelShape collisionShape) {
        this.tiny$customCollisionShape = collisionShape;
    }

    @Inject(
            method = "getCollisionShape",
            at = @At("HEAD"),
            cancellable = true
    )
    private void tiny$useCustomCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos,
                                              CollisionContext collisionContext,
                                              CallbackInfoReturnable<VoxelShape> cir) {
        if (this.tiny$customCollisionShape != null) {
            cir.setReturnValue(this.tiny$customCollisionShape);
        }
    }
}
