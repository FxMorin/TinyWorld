package ca.fxco.TinyWorld;

import ca.fxco.TinyWorld.commands.TinyCommand;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

public class TinyWorldClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            TinyCommand.register(dispatcher);
        });
    }
}
