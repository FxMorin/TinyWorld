package ca.fxco.TinyWorld.mixin.collision;

import ca.fxco.TinyWorld.bridge.BakedModelBridge;
import ca.fxco.TinyWorld.bridge.BlockBehaviourBridge;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static ca.fxco.TinyWorld.TinyWorld.BLOCK_RENDERER;

@Mixin(targets = "net/minecraft/world/level/block/state/BlockBehaviour$BlockStateBase$Cache")
public class CacheMixin {

    @WrapOperation(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/Block;getCollisionShape(" +
                            "Lnet/minecraft/world/level/block/state/BlockState;" +
                            "Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;" +
                            "Lnet/minecraft/world/phys/shapes/CollisionContext;)" +
                            "Lnet/minecraft/world/phys/shapes/VoxelShape;"
            )
    )
    private VoxelShape tiny$useCustomCollisionShape(Block block, BlockState blockState, BlockGetter blockGetter,
                                                    BlockPos blockPos, CollisionContext collisionContext,
                                                    Operation<VoxelShape> original) {
        if (BLOCK_RENDERER != null) {
            BakedModel model = BLOCK_RENDERER.getBlockModel(blockState);
            if (model != null) {
                VoxelShape customCollisionShape = ((BakedModelBridge) model).tiny$getCustomCollisionShape();
                if (customCollisionShape != null) {
                    VoxelShape optimizedCollisionShape = customCollisionShape.optimize();
                    // Also set the custom collision within the actual block (Best way to do this currently)
                    ((BlockBehaviourBridge)block).tiny$setCustomCollisionShape(optimizedCollisionShape);
                    return optimizedCollisionShape;
                }
            }
        }
        return original.call(block, blockState, blockGetter, blockPos, collisionContext);
    }
}
