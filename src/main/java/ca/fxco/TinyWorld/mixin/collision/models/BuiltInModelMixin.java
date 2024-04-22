package ca.fxco.TinyWorld.mixin.collision.models;

import ca.fxco.TinyWorld.bridge.BakedModelBridge;
import net.minecraft.client.resources.model.BuiltInModel;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BuiltInModel.class)
public abstract class BuiltInModelMixin implements BakedModelBridge {

    @Unique
    private @Nullable VoxelShape tiny$customCollisionShape;

    @Override
    public VoxelShape tiny$getCustomCollisionShape() {
        return this.tiny$customCollisionShape;
    }

    @Override
    public void tiny$setCustomCollisionShape(VoxelShape customCollisionShape) {
        this.tiny$customCollisionShape = customCollisionShape;
    }
}
