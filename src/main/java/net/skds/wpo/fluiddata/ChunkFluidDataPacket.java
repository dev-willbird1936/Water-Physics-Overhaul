package net.skds.wpo.fluiddata;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public final class ChunkFluidDataPacket {

    private final ResourceLocation dimensionId;
    private final int chunkX;
    private final int chunkZ;
    private final CompoundTag data;

    public ChunkFluidDataPacket(ResourceLocation dimensionId, ChunkPos chunkPos, CompoundTag data) {
        this.dimensionId = dimensionId;
        this.chunkX = chunkPos.x;
        this.chunkZ = chunkPos.z;
        this.data = data;
    }

    public static void encode(ChunkFluidDataPacket packet, FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(packet.dimensionId);
        buffer.writeInt(packet.chunkX);
        buffer.writeInt(packet.chunkZ);
        buffer.writeNbt(packet.data);
    }

    public static ChunkFluidDataPacket decode(FriendlyByteBuf buffer) {
        ResourceLocation dimensionId = buffer.readResourceLocation();
        int chunkX = buffer.readInt();
        int chunkZ = buffer.readInt();
        CompoundTag data = buffer.readNbt();
        return new ChunkFluidDataPacket(dimensionId, new ChunkPos(chunkX, chunkZ), data == null ? new CompoundTag() : data);
    }

    public static void handle(ChunkFluidDataPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (!context.getDirection().getReceptionSide().isClient()) {
            context.setPacketHandled(true);
            return;
        }
        context.enqueueWork(() -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.level != null) {
                WPOFluidChunkStorage.applyClientSync(packet.dimensionId, new ChunkPos(packet.chunkX, packet.chunkZ), packet.data);
            }
        });
        context.setPacketHandled(true);
    }
}
