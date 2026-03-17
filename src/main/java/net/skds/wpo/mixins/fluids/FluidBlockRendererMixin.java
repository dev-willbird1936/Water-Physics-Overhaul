package net.skds.wpo.mixins.fluids;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.Fluid;
import net.skds.wpo.fluidphysics.FFluidStatic;

@Mixin(value = { LiquidBlockRenderer.class })
public class FluidBlockRendererMixin {
	/**
	 * Heavy LiquidBlockRenderer tweak
	 *
	 * redirects the four corner height samples inside `tesselate` so WPO can
	 * provide its own partial-fluid corner heights without replacing the whole renderer:
	 * 		tesselate -> calculateAverageHeight
	 */

	@Redirect(method = "tesselate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/LiquidBlockRenderer;calculateAverageHeight(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/material/Fluid;FFFLnet/minecraft/core/BlockPos;)F", ordinal = 0))
	public float gc(LiquidBlockRenderer renderer, BlockAndTintGetter w, Fluid f, float thisHeight,
					float northOrSouthNeighborHeight, float eastOrWestNeighborHeight, BlockPos cornerNeighbor) {
		// ordinal 0 => NORTH EAST corner
		BlockPos side1 = cornerNeighbor.relative(Direction.SOUTH);
		BlockPos side2 = cornerNeighbor.relative(Direction.WEST);
		BlockPos center = cornerNeighbor.relative(Direction.SOUTH).relative(Direction.WEST);
		return FFluidStatic.getCornerHeight(w, f, center, side1, side2, cornerNeighbor);
	}

	@Redirect(method = "tesselate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/LiquidBlockRenderer;calculateAverageHeight(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/material/Fluid;FFFLnet/minecraft/core/BlockPos;)F", ordinal = 1))
	public float gc1(LiquidBlockRenderer renderer, BlockAndTintGetter w, Fluid f, float thisHeight,
					 float northOrSouthNeighborHeight, float eastOrWestNeighborHeight, BlockPos cornerNeighbor) {
		// ordinal 1 => NORTH WEST corner
		BlockPos side1 = cornerNeighbor.relative(Direction.SOUTH);
		BlockPos side2 = cornerNeighbor.relative(Direction.EAST);
		BlockPos center = cornerNeighbor.relative(Direction.SOUTH).relative(Direction.EAST);
		return FFluidStatic.getCornerHeight(w, f, center, side1, side2, cornerNeighbor);
	}

	@Redirect(method = "tesselate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/LiquidBlockRenderer;calculateAverageHeight(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/material/Fluid;FFFLnet/minecraft/core/BlockPos;)F", ordinal = 2))
	public float gc2(LiquidBlockRenderer renderer, BlockAndTintGetter w, Fluid f, float thisHeight,
					 float northOrSouthNeighborHeight, float eastOrWestNeighborHeight, BlockPos cornerNeighbor) {
		// ordinal 2 => SOUTH EAST corner
		BlockPos side1 = cornerNeighbor.relative(Direction.NORTH);
		BlockPos side2 = cornerNeighbor.relative(Direction.WEST);
		BlockPos center = cornerNeighbor.relative(Direction.NORTH).relative(Direction.WEST);
		return FFluidStatic.getCornerHeight(w, f, center, side1, side2, cornerNeighbor);
	}

	@Redirect(method = "tesselate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/LiquidBlockRenderer;calculateAverageHeight(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/material/Fluid;FFFLnet/minecraft/core/BlockPos;)F", ordinal = 3))
	public float gc3(LiquidBlockRenderer renderer, BlockAndTintGetter w, Fluid f, float thisHeight,
					 float northOrSouthNeighborHeight, float eastOrWestNeighborHeight, BlockPos cornerNeighbor) {
		// ordinal 3 => SOUTH WEST corner
		BlockPos side1 = cornerNeighbor.relative(Direction.NORTH);
		BlockPos side2 = cornerNeighbor.relative(Direction.EAST);
		BlockPos center = cornerNeighbor.relative(Direction.NORTH).relative(Direction.EAST);
		return FFluidStatic.getCornerHeight(w, f, center, side1, side2, cornerNeighbor);
	}
}
