package net.skds.wpo.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.skds.wpo.WPO;
import net.skds.wpo.fluiddata.ChunkFluidDataPacket;

public final class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";

    private PacketHandler() {
    }

    public static void init(IEventBus modBus) {
        modBus.addListener(PacketHandler::registerPayloads);
    }

    public static void send(ServerPlayer target, CustomPacketPayload message) {
        PacketDistributor.sendToPlayer(target, message);
    }

    public static void sendTrackingChunk(ServerLevel level, ChunkPos chunkPos, CustomPacketPayload message) {
        PacketDistributor.sendToPlayersTrackingChunk(level, chunkPos, message);
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);
        registrar.playToClient(DebugPacket.TYPE, DebugPacket.STREAM_CODEC, DebugPacket::handle);
        registrar.playToClient(ChunkFluidDataPacket.TYPE, ChunkFluidDataPacket.STREAM_CODEC, ChunkFluidDataPacket::handle);
    }
}
