package ca.fxco.TinyWorld.mixin.collision.json;

import ca.fxco.TinyWorld.bridge.BakedModelBridge;
import ca.fxco.TinyWorld.bridge.BlockModelBridge;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Function;

@Mixin(BlockModel.class)
public abstract class BlockModelMixin implements BlockModelBridge {

    @Unique
    private boolean tiny$useCustomCollisionShape = false;

    @Shadow
    public abstract List<BlockElement> getElements();

    @Override
    public void tiny$setCustomCollisionShape(boolean useCustomCollisionShape) {
        this.tiny$useCustomCollisionShape = useCustomCollisionShape;
    }

    @Override
    public boolean tiny$useCustomCollisionShape() {
        return this.tiny$useCustomCollisionShape;
    }

    @Inject(
            method = "bake(Lnet/minecraft/client/resources/model/ModelBaker;" +
                    "Lnet/minecraft/client/renderer/block/model/BlockModel;Ljava/util/function/Function;" +
                    "Lnet/minecraft/client/resources/model/ModelState;Lnet/minecraft/resources/ResourceLocation;Z)" +
                    "Lnet/minecraft/client/resources/model/BakedModel;",
            at = @At("RETURN")
    )
    private void tiny$onBake(ModelBaker modelBaker, BlockModel blockModel,
                             Function<Material, TextureAtlasSprite> function, ModelState modelState,
                             ResourceLocation resourceLocation, boolean bl, CallbackInfoReturnable<BakedModel> cir) {
        if (tiny$useCustomCollisionShape()) {
            List<BlockElement> modelElementList = this.getElements();
            if (modelElementList != null && !modelElementList.isEmpty()) {
                VoxelShape voxelShape = Shapes.empty();
                for (BlockElement e : modelElementList) {
                    if (e.rotation == null || e.rotation.angle() == 0) {
                        VoxelShape shape = Block.box(e.from.x, e.from.y, e.from.z, e.to.x, e.to.y, e.to.z);
                        voxelShape = Shapes.or(voxelShape, shape);
                    }
                }
                ((BakedModelBridge)cir.getReturnValue()).tiny$setCustomCollisionShape(voxelShape);
            }
        }
    }
}
