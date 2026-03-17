package net.skds.wpo.fluidphysics;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.skds.core.api.IWWSG;
import net.skds.core.api.IWorldExtended;

public class FluidTasksManager {

	public static void addFluidTask(ServerLevel w, BlockPos pos, BlockState state) {		
		IWWSG wwsg = IWorldExtended.getWWS(w);
		if (wwsg == null) {
			wwsg = IWorldExtended.addWWS(w);
		}
		WorldWorkSet wws = (WorldWorkSet) wwsg.getTyped(WorldWorkSet.class);
		if (wws == null) {
			wws = new WorldWorkSet(w, wwsg);
			wwsg.addWWS(wws);
		}

		FluidTask task = new FluidTask.DefaultTask(wws, pos.asLong());
		WorldWorkSet.pushTask(task);
		//System.out.println(pos);
	}
}
