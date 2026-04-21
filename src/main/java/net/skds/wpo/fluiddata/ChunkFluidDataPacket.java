package net.skds.wpo.fluiddata;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.skds.wpo.WPO;

public record ChunkFluidDataPacket(ResourceLocation dimensionId, int chunkX, int chunkZ, CompoundTag data)
    implements CustomPacketPayload {

    public static final Type<ChunkFluidDataPacket> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(WPO.MOD_ID, "chunk_fluid_data"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ChunkFluidDataPacket> STREAM_CODEC =
        StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            ChunkFluidDataPacket::dimensionId,
            ByteBufCodecs.VAR_INT,
            ChunkFluidDataPacket::chunkX,
            ByteBufCodecs.VAR_INT,
            ChunkFluidDataPacket::chunkZ,
            ByteBufCodecs.COMPOUND_TAG,
            ChunkFluidDataPacket::data,
            ChunkFluidDataPacket::new
        );

    public ChunkFluidDataPacket(ResourceLocation dimensionId, ChunkPos chunkPos, CompoundTag data) {
        this(dimensionId, chunkPos.x, chunkPos.z, data);
    }

    public ChunkPos chunkPos() {
        return new ChunkPos(chunkX, chunkZ);
    }

    @Override
    public Type<ChunkFluidDataPacket> type() {
        return TYPE;
    }

    public static void handle(ChunkFluidDataPacket packet, IPayloadContext context) {
        WPOFluidChunkStorage.applyClientSync(packet.dimensionId, packet.chunkPos(), packet.data);
    }
}
