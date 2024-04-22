package ca.fxco.TinyWorld.bridge;

import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public interface BakedModelBridge {

    /**
     * If blocks using this model should use the model shape as the collision shape
     */
    default void tiny$setCustomCollisionShape(@Nullable VoxelShape collisionShape) {}

    /**
     * Get the custom collision shape for this model. Null if not using a custom collision shape.
     */
    default @Nullable VoxelShape tiny$getCustomCollisionShape() {
        return null;
    }
}
