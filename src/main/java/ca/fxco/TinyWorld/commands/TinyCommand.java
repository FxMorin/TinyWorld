package ca.fxco.TinyWorld.commands;

import ca.fxco.TinyWorld.commands.arguments.SectionPosArgument;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static ca.fxco.TinyWorld.TinyWorld.CUSTOM_COLLISION_KEY;

// Command /tiny <name> <from> <to>
public class TinyCommand {

    // We are currently just replacing stained glass blocks with custom models.
    private static final List<String> BLOCK_FILES = List.of(
            "white_stained_glass.json",
            "light_gray_stained_glass.json",
            "gray_stained_glass.json",
            "black_stained_glass.json",
            "brown_stained_glass.json",
            "red_stained_glass.json",
            "orange_stained_glass.json",
            "yellow_stained_glass.json",
            "lime_stained_glass.json",
            "green_stained_glass.json",
            "cyan_stained_glass.json",
            "light_blue_stained_glass.json",
            "blue_stained_glass.json",
            "purple_stained_glass.json",
            "magenta_stained_glass.json",
            "pink_stained_glass.json"
    );
    private static final SimpleCommandExceptionType ERROR_AREA_TOO_LARGE = new SimpleCommandExceptionType(() ->
            "Too many chunks selected. You can only have: " + BLOCK_FILES.size()
    );

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
            dispatcher.register(ClientCommandManager.literal("tiny")
                    .then(ClientCommandManager.argument("name", StringArgumentType.word())
                            .then(ClientCommandManager.argument("from", SectionPosArgument.sectionPos())
                                    .then(ClientCommandManager.argument("to", SectionPosArgument.sectionPos())
                                            .executes(context -> {
                                                try {
                                                    return executeBatchCommand(context);
                                                } catch (IOException e) {
                                                    throw new RuntimeException(e);
                                                }
                                            })))));
    }

    private static int executeBatchCommand(CommandContext<FabricClientCommandSource> context)
            throws CommandSyntaxException, IOException {
        SectionPos from = SectionPosArgument.getSectionPos(context, "from");
        SectionPos to = SectionPosArgument.getSectionPos(context, "to");
        BoundingBox bounds = BoundingBox.fromCorners(from, to);
        int area = bounds.getXSpan() * bounds.getYSpan() * bounds.getZSpan();
        if (area > BLOCK_FILES.size()) {
            throw ERROR_AREA_TOO_LARGE.create();
        }
        Minecraft mc = context.getSource().getClient();
        LocalPlayer player = mc.player;
        if (player == null) {
            return 0;
        }
        ClientLevel level = player.clientLevel;
        Gson gson = new Gson();
        Path blockDir = createNewResourcePack(gson, mc, StringArgumentType.getString(context, "name"));
        int count = 0;
        for (int x = bounds.minX(); x <= bounds.maxX(); x++) {
            for (int z = bounds.minZ(); z <= bounds.maxZ(); z++) {
                ChunkAccess chunk = level.getChunk(x, z);
                for (int y = bounds.minY(); y <= bounds.maxY(); y++) {
                    LevelChunkSection section = chunk.getSection(chunk.getSectionIndexFromSectionY(y));
                    Files.write(
                            blockDir.resolve(BLOCK_FILES.get(count++)),
                            List.of(generateSectionModel(gson, level, section, SectionPos.of(x, y, z))),
                            StandardCharsets.UTF_8
                    );
                }
            }
        }
        context.getSource().sendFeedback(Component.literal(
                "Created resource pack with " + area + " block" + (area == 1 ? "s" : "") + "!"
        ));
        return 1;
    }

    private static Path createNewResourcePack(Gson gson, Minecraft client, String name) throws IOException {
        Path resourcePackDir = client.getResourcePackDirectory();
        Path newPackDir = resourcePackDir.resolve(name);
        Files.createDirectories(newPackDir);

        JsonObject packMcmeta = new JsonObject();
        JsonObject pack = new JsonObject();
        pack.add("description", new JsonPrimitive(name));
        pack.add("pack_format", new JsonPrimitive(12));
        packMcmeta.add("pack", pack);

        Files.write(
                newPackDir.resolve("pack.mcmeta"),
                List.of(gson.toJson(packMcmeta)),
                StandardCharsets.UTF_8
        );

        Path blockDir = newPackDir.resolve("assets").resolve("minecraft").resolve("models").resolve("block");
        Files.createDirectories(blockDir);
        return blockDir;
    }

    private static String generateSectionModel(Gson gson, ClientLevel level, LevelChunkSection section,
                                               SectionPos sectionPos) {
        JsonArray uv = new JsonArray();
        uv.add(0);
        uv.add(0);
        uv.add(16);
        uv.add(16);

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        pos.move(sectionPos.minBlockX(), sectionPos.minBlockY(), sectionPos.minBlockZ());
        JsonArray elements = new JsonArray();
        Map<ResourceLocation, String> textureMap = new HashMap<>();
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    BlockState state = section.getBlockState(x, y, z);
                    if (state.isAir()) {
                        pos.move(0,0,1);
                        continue;
                    }
                    ResourceLocation textureLocation = ModelLocationUtils.getModelLocation(state.getBlock());
                    String id = textureMap.computeIfAbsent(textureLocation, t -> "" + textureMap.size());

                    JsonObject faces = new JsonObject();
                    for (Direction direction : Direction.values()) {
                        if (shouldIncludeFace(level, pos.mutable(), state, direction, x, y, z)) {
                            JsonObject face = new JsonObject();
                            face.add("uv", uv);
                            face.add("texture", new JsonPrimitive(id));
                            faces.add(direction.name().toLowerCase(Locale.ROOT), face);
                        }
                    }
                    if (faces.isEmpty()) {
                        pos.move(0,0,1);
                        continue;
                    }
                    JsonObject block = new JsonObject();
                    JsonArray from = new JsonArray();
                    from.add(x);
                    from.add(y);
                    from.add(z);
                    block.add("from", from);
                    JsonArray to = new JsonArray();
                    to.add(x + 1);
                    to.add(y + 1);
                    to.add(z + 1);
                    block.add("to", to);
                    block.add("faces", faces);
                    elements.add(block);
                    pos.move(0,0,1);
                }
                pos.move(0,1,0);
            }
            pos.move(1,0,0);
        }
        JsonObject model = new JsonObject();

        // Textures
        JsonObject textures = new JsonObject();
        for (var entry : textureMap.entrySet()) {
            textures.add(entry.getValue(), new JsonPrimitive(entry.getKey().toString()));
        }
        model.add("textures", textures);

        model.add("elements", elements);

        model.add(CUSTOM_COLLISION_KEY, new JsonPrimitive(true));

        return gson.toJson(model);
    }

    // TODO: Actually make this work...
    private static boolean shouldIncludeFace(BlockGetter blockGetter, BlockPos pos, BlockState state, Direction dir,
                                             int x, int y, int z) {
        if (switch (dir) {
            case UP -> y != 15;
            case DOWN -> y != 0;
            case EAST -> x != 15;
            case WEST -> x != 0;
            case SOUTH -> z != 15;
            case NORTH -> z != 0;
        }) {
            return Block.shouldRenderFace(state, blockGetter, pos.immutable(), dir, pos.relative(dir));
        }
        return true;
    }
}
