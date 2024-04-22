package ca.fxco.TinyWorld.bridge;

import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public interface BlockBehaviourBridge {

    /**
     * Set the custom collision shape for this block
     */
    default void tiny$setCustomCollisionShape(@Nullable VoxelShape collisionShape) {}
}
