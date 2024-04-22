package ca.fxco.TinyWorld.mixin.collision.models;

import ca.fxco.TinyWorld.bridge.BakedModelBridge;
import net.minecraft.client.resources.model.BakedModel;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BakedModel.class)
public interface BakedModelMixin extends BakedModelBridge {}