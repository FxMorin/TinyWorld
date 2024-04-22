package ca.fxco.TinyWorld.mixin.collision.json;

import ca.fxco.TinyWorld.bridge.BlockModelBridge;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.block.model.BlockModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Type;

import static ca.fxco.TinyWorld.TinyWorld.CUSTOM_COLLISION_KEY;

@Mixin(BlockModel.Deserializer.class)
public class DeserializerMixin {

    @Inject(
            method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;" +
                    "Lcom/google/gson/JsonDeserializationContext;)" +
                    "Lnet/minecraft/client/renderer/block/model/BlockModel;",
            at = @At("RETURN")
    )
    private void tiny$onDeserialize(JsonElement jsonElement, Type type,
                                    JsonDeserializationContext jsonContext, CallbackInfoReturnable<BlockModel> cir) {
        JsonObject jsonObj = jsonElement.getAsJsonObject();
        JsonElement customCollision = jsonObj.get(CUSTOM_COLLISION_KEY);
        if (customCollision != null && !customCollision.isJsonNull()) {
            ((BlockModelBridge) cir.getReturnValue()).tiny$setCustomCollisionShape(customCollision.getAsBoolean());
        }
    }
}
