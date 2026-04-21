package net.skds.wpo.network;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.skds.wpo.WPO;

public record DebugPacket(BlockPos pos) implements CustomPacketPayload {

    public static final Type<DebugPacket> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(WPO.MOD_ID, "debug"));

    public static final StreamCodec<RegistryFriendlyByteBuf, DebugPacket> STREAM_CODEC =
        StreamCodec.composite(BlockPos.STREAM_CODEC, DebugPacket::pos, DebugPacket::new);

    @Override
    public Type<DebugPacket> type() {
        return TYPE;
    }

    public static void handle(DebugPacket packet, IPayloadContext context) {
        context.player().level().addParticle(
            ParticleTypes.FLAME,
            packet.pos.getX() + 0.5,
            packet.pos.getY() + 0.5,
            packet.pos.getZ() + 0.5,
            0.0,
            0.06,
            0.0
        );
    }
}
