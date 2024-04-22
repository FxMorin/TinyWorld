package ca.fxco.TinyWorld.mixin;

import ca.fxco.TinyWorld.TinyWorld;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Shadow @Final private BlockRenderDispatcher blockRenderer;

    @Shadow @Final private ReloadableResourceManager resourceManager;

    @Inject(
            method = "<init>",
            at = @At(
                    value = "NEW",
                    target = "net/minecraft/client/renderer/LevelRenderer",
                    shift = At.Shift.BEFORE
            )
    )
    private void tiny$getBlockRenderDispatcherInstance(GameConfig gameConfig, CallbackInfo ci) {
        TinyWorld.BLOCK_RENDERER = this.blockRenderer;

        // MoreCulling already registers `Blocks.rebuildCache()` in the reload listener, so don't do it twice.
        if (FabricLoader.getInstance().isModLoaded("moreculling")) {
            return;
        }
        // Make sure to reload block states on resource reload
        this.resourceManager.registerReloadListener((ResourceManagerReloadListener) manager -> Blocks.rebuildCache());
    }
}
