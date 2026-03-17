package net.skds.wpo.api;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluids;
import net.skds.wpo.WPOConfig;
import net.skds.wpo.fluiddata.WPOFluidChunkStorage;
import net.skds.wpo.fluidphysics.FFluidStatic;
import net.skds.wpo.fluidphysics.FluidTasksManager;
import net.skds.wpo.registry.BlockStateProps;
import net.skds.wpo.util.interfaces.IBaseWL;

public final class WPOFluidAccess {

    private WPOFluidAccess() {
    }

    public static boolean isChunkLoaded(ServerLevel level, BlockPos pos) {
        return level.getChunkSource().getChunkNow(pos.getX() >> 4, pos.getZ() >> 4) != null;
    }

    public static int getWaterAmount(BlockGetter level, BlockPos pos) {
        return getFluidAmount(level, pos, Fluids.WATER);
    }

    public static int getWaterAmount(FluidState fluidState) {
        return getFluidAmount(fluidState, Fluids.WATER);
    }

    public static int getFluidAmount(BlockGetter level, BlockPos pos, FlowingFluid fluid) {
        return getFluidAmount(level.getFluidState(pos), fluid);
    }

    public static int getFluidAmount(FluidState fluidState, FlowingFluid fluid) {
        return fluidState.getType().isSame(fluid) ? fluidState.getAmount() : 0;
    }

    public static boolean canWaterFlowBetween(BlockGetter level, BlockPos fromPos, BlockPos toPos) {
        return FFluidStatic.canReach(fromPos, toPos, Fluids.WATER, level);
    }

    public static int addWater(ServerLevel level, BlockPos pos, int amount) {
        return addFluid(level, pos, Fluids.WATER, amount);
    }

    public static int removeWater(ServerLevel level, BlockPos pos, int amount) {
        return setWaterAmount(level, pos, Math.max(0, getWaterAmount(level, pos) - Math.max(0, amount)));
    }

    public static int setWaterAmount(ServerLevel level, BlockPos pos, int targetAmount) {
        return setFluidAmount(level, pos, Fluids.WATER, targetAmount);
    }

    public static int addFluid(ServerLevel level, BlockPos pos, FlowingFluid fluid, int amount) {
        return setFluidAmount(level, pos, fluid, Math.min(WPOConfig.MAX_FLUID_LEVEL, getFluidAmount(level, pos, fluid) + Math.max(0, amount)));
    }

    public static int setFluidAmount(ServerLevel level, BlockPos pos, FlowingFluid fluid, int targetAmount) {
        if (targetAmount < 0 || targetAmount > WPOConfig.MAX_FLUID_LEVEL) {
            throw new IllegalArgumentException("WPO fluid amount out of range: " + targetAmount);
        }
        LevelChunk chunk = level.getChunkSource().getChunkNow(pos.getX() >> 4, pos.getZ() >> 4);
        if (chunk == null) {
            return 0;
        }

        BlockState currentState = chunk.getBlockState(pos);
        int currentAmount = getFluidAmount(currentState.getFluidState(), fluid);
        if (currentAmount == targetAmount) {
            return currentAmount;
        }
        if (targetAmount > 0 && !canHostFluid(level, pos, currentState, fluid)) {
            return currentAmount;
        }

        BlockState updatedState = FFluidStatic.getUpdatedState(currentState, targetAmount, fluid);
        if (updatedState.equals(currentState)) {
            return currentAmount;
        }

        if (!level.setBlock(pos, updatedState, 3)) {
            return currentAmount;
        }
        afterFluidChange(level, pos, updatedState);
        return targetAmount;
    }

    public static void wakeWater(ServerLevel level, BlockPos pos) {
        if (!isChunkLoaded(level, pos)) {
            return;
        }
        FFluidStatic.scheduleFluidTicksAround(level, pos);
        FluidTasksManager.addFluidTask(level, pos, level.getBlockState(pos));
    }

    private static boolean canHostFluid(ServerLevel level, BlockPos pos, BlockState state, FlowingFluid fluid) {
        if (state.getFluidState().getType().isSame(fluid) || state.isAir()) {
            return true;
        }
        if (state.getBlock() instanceof IBaseWL
            && state.hasProperty(BlockStateProperties.WATERLOGGED)
            && state.hasProperty(BlockStateProps.FFLUID_LEVEL)) {
            if (!fluid.isSame(Fluids.WATER)) {
                return false;
            }
            return true;
        }
        if (state.getBlock() instanceof LiquidBlockContainer liquidBlockContainer) {
            return liquidBlockContainer.canPlaceLiquid(level, pos, state, fluid);
        }
        return state.canBeReplaced(fluid);
    }

    private static void afterFluidChange(ServerLevel level, BlockPos pos, BlockState updatedState) {
        WPOFluidChunkStorage.mirrorBlockState(level, pos, updatedState);
        FFluidStatic.scheduleFluidTicksAround(level, pos);
        FluidTasksManager.addFluidTask(level, pos, updatedState);
    }
}
