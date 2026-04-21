package net.skds.wpo.fluiddata;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.event.level.ChunkDataEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.ChunkWatchEvent;
import net.skds.core.api.IWWSG;
import net.skds.core.api.IWorldExtended;
import net.skds.wpo.fluidphysics.WorldWorkSet;
import net.skds.wpo.network.PacketHandler;
import net.skds.wpo.registry.BlockStateProps;
import net.skds.wpo.util.interfaces.IBaseWL;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class WPOFluidChunkStorage {

    private static final String CHUNK_TAG = "wpo_fluid_overlay";

    private static final Map<ResourceLocation, Map<Long, WPOFluidChunkData>> LOADED = new ConcurrentHashMap<>();
    private static final Map<ResourceLocation, Map<Long, WPOFluidChunkData>> PENDING_LOADS = new ConcurrentHashMap<>();

    private WPOFluidChunkStorage() {
    }

    public static void onChunkDataLoad(ChunkDataEvent.Load event) {
        ResourceLocation dimensionId = getDimensionId(event.getLevel());
        if (dimensionId == null) {
            return;
        }
        CompoundTag chunkTag = event.getData();
        if (!chunkTag.contains(CHUNK_TAG, CompoundTag.TAG_COMPOUND)) {
            return;
        }
        put(PENDING_LOADS, dimensionId, event.getChunk().getPos().toLong(), WPOFluidChunkData.fromTag(chunkTag.getCompound(CHUNK_TAG)));
    }

    public static void onChunkDataSave(ChunkDataEvent.Save event) {
        ResourceLocation dimensionId = getDimensionId(event.getLevel());
        if (dimensionId == null) {
            return;
        }
        long chunkKey = event.getChunk().getPos().toLong();
        WPOFluidChunkData data = getLoadedData(dimensionId, chunkKey);
        if (data == null) {
            event.getData().remove(CHUNK_TAG);
            return;
        }
        if (data.isEmpty()) {
            event.getData().remove(CHUNK_TAG);
            return;
        }
        event.getData().put(CHUNK_TAG, data.toTag());
    }

    public static void onChunkLoad(ChunkEvent.Load event) {
        ResourceLocation dimensionId = getDimensionId(event.getLevel());
        if (dimensionId == null) {
            return;
        }

        long chunkKey = event.getChunk().getPos().toLong();
        WPOFluidChunkData data = remove(PENDING_LOADS, dimensionId, chunkKey);
        if (data == null) {
            data = getLoadedData(dimensionId, chunkKey);
        }
        if (data == null) {
            data = new WPOFluidChunkData();
        }
        put(LOADED, dimensionId, chunkKey, data);
    }

    public static void onChunkUnload(ChunkEvent.Unload event) {
        ResourceLocation dimensionId = getDimensionId(event.getLevel());
        if (dimensionId == null) {
            return;
        }
        remove(LOADED, dimensionId, event.getChunk().getPos().toLong());
    }

    public static void onChunkWatch(ChunkWatchEvent.Watch event) {
        sendChunkToPlayer(event.getPlayer(), event.getChunk());
    }

    public static void applyClientSync(ResourceLocation dimensionId, ChunkPos chunkPos, CompoundTag dataTag) {
        put(LOADED, dimensionId, chunkPos.toLong(), WPOFluidChunkData.fromTag(dataTag));
    }

    public static void mirrorBlockState(Level level, BlockPos pos, BlockState state) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        LevelChunk chunk = serverLevel.getChunkSource().getChunkNow(pos.getX() >> 4, pos.getZ() >> 4);
        if (chunk == null) {
            return;
        }

        ResourceLocation dimensionId = serverLevel.dimension().location();
        WPOFluidChunkData data = loadedChunks(dimensionId).computeIfAbsent(chunk.getPos().toLong(), ignored -> new WPOFluidChunkData());
        data.setFluidState(pos, shouldPersistState(state) ? state.getFluidState() : Fluids.EMPTY.defaultFluidState());
        if (data.isEmpty()) {
            loadedChunks(dimensionId).remove(chunk.getPos().toLong());
        }
        chunk.setUnsaved(true);
        queueChunkSync(serverLevel, chunk.getPos().toLong());
    }

    public static void sendChunkToPlayer(ServerPlayer player, LevelChunk chunk) {
        PacketHandler.send(player, createPacket(chunk));
    }

    public static void sendChunkToTracking(LevelChunk chunk) {
        PacketHandler.sendTrackingChunk((ServerLevel) chunk.getLevel(), chunk.getPos(), createPacket(chunk));
    }

    private static ChunkFluidDataPacket createPacket(LevelChunk chunk) {
        ResourceLocation dimensionId = chunk.getLevel().dimension().location();
        WPOFluidChunkData data = loadedChunks(dimensionId).get(chunk.getPos().toLong());
        if (data == null) {
            data = new WPOFluidChunkData();
        }
        return new ChunkFluidDataPacket(dimensionId, chunk.getPos(), data.toTag());
    }

    private static void queueChunkSync(ServerLevel level, long chunkKey) {
        IWWSG wwsg = IWorldExtended.getWWS(level);
        if (wwsg == null) {
            return;
        }
        if (wwsg.getTyped(WorldWorkSet.class) instanceof WorldWorkSet workSet) {
            workSet.updatedChunks.add(chunkKey);
        }
    }

    private static ResourceLocation getDimensionId(LevelAccessor level) {
        if (level instanceof Level actualLevel) {
            return actualLevel.dimension().location();
        }
        return null;
    }

    private static boolean shouldPersistState(BlockState state) {
        return state.getBlock() instanceof IBaseWL
            && state.hasProperty(BlockStateProps.FFLUID_LEVEL)
            && state.getValue(BlockStateProps.FFLUID_LEVEL) > 0;
    }

    private static WPOFluidChunkData getLoadedData(ResourceLocation dimensionId, long chunkKey) {
        return loadedChunks(dimensionId).get(chunkKey);
    }

    private static Map<Long, WPOFluidChunkData> loadedChunks(ResourceLocation dimensionId) {
        return LOADED.computeIfAbsent(dimensionId, ignored -> new ConcurrentHashMap<>());
    }

    private static void put(Map<ResourceLocation, Map<Long, WPOFluidChunkData>> store, ResourceLocation dimensionId, long chunkKey, WPOFluidChunkData data) {
        store.computeIfAbsent(dimensionId, ignored -> new ConcurrentHashMap<>()).put(chunkKey, data);
    }

    private static WPOFluidChunkData remove(Map<ResourceLocation, Map<Long, WPOFluidChunkData>> store, ResourceLocation dimensionId, long chunkKey) {
        Map<Long, WPOFluidChunkData> chunks = store.get(dimensionId);
        if (chunks == null) {
            return null;
        }
        return chunks.remove(chunkKey);
    }
}
