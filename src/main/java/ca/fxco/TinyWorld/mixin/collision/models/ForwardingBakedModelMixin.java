package ca.fxco.TinyWorld.mixin.collision.models;

import ca.fxco.TinyWorld.bridge.BakedModelBridge;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

//@Restriction(require = @Condition("fabric-renderer-api-v1"))
@Mixin(ForwardingBakedModel.class)
public class ForwardingBakedModelMixin implements BakedModelBridge {

    @Shadow
    protected BakedModel wrapped;

    @Override
    public VoxelShape tiny$getCustomCollisionShape() {
        return ((BakedModelBridge)wrapped).tiny$getCustomCollisionShape();
    }

    @Override
    public void tiny$setCustomCollisionShape(VoxelShape customCollisionShape) {
        ((BakedModelBridge)wrapped).tiny$setCustomCollisionShape(customCollisionShape);
    }
}
