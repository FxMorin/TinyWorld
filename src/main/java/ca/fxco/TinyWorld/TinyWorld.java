package ca.fxco.TinyWorld;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;

public class TinyWorld implements ModInitializer {

    public static final String CUSTOM_COLLISION_KEY = "useCustomCollisionShape";
    public static BlockRenderDispatcher BLOCK_RENDERER = null;

    @Override
    public void onInitialize() {}
}
