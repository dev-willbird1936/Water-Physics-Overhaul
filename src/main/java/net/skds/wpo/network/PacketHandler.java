package net.skds.wpo.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.skds.wpo.WPO;
import net.skds.wpo.fluiddata.ChunkFluidDataPacket;

import java.util.Optional;

public class PacketHandler {
	private static final String PROTOCOL_VERSION = "1";
	private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(WPO.MOD_ID, "network"), () -> PROTOCOL_VERSION, v -> true, v -> true);

	public static void send(Player target, Object message) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)target), message);
	}

	public static SimpleChannel get() {
		return CHANNEL;
	}

	public static void init() {
		int id = 0;
		CHANNEL.registerMessage(id++, DebugPacket.class, DebugPacket::encoder, DebugPacket::decoder, DebugPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		CHANNEL.registerMessage(id++, ChunkFluidDataPacket.class, ChunkFluidDataPacket::encode, ChunkFluidDataPacket::decode, ChunkFluidDataPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
	}

	public static void sendTrackingChunk(LevelChunk chunk, Object message) {
		CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), message);
	}
}
