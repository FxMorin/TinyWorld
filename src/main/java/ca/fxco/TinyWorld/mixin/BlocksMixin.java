package ca.fxco.TinyWorld.mixin;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Blocks.class)
public class BlocksMixin {

    // TODO: To be removed
    @Redirect(
            method = "stainedGlass",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;" +
                            "noOcclusion()Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;"
            )
    )
    private static BlockBehaviour.Properties tiny$hackStainedGlassProperties(BlockBehaviour.Properties instance,
                                                                             DyeColor dyeColor) {
        instance.noOcclusion();
        instance.noTerrainParticles();
        instance.isViewBlocking((a,b,c) -> true);
        return instance;
    }
}
