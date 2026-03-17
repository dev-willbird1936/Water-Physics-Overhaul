package net.skds.wpo.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

public interface IWPOFluidPassage {

    default WPOPassageDecision getWPOPassageDecision(BlockState state, BlockGetter level, BlockPos selfPos, BlockPos fromPos, BlockPos toPos, Fluid fluid) {
        return WPOPassageDecision.DEFAULT;
    }
}
