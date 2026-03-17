package net.skds.wpo;

import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ChunkDataEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.event.level.ChunkWatchEvent;
import net.minecraftforge.event.level.PistonEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.skds.core.api.IWWSG;
import net.skds.core.events.OnWWSAttachEvent;
import net.skds.core.events.SyncTasksHookEvent;
import net.skds.wpo.fluiddata.WPOFluidChunkStorage;
import net.skds.wpo.fluidphysics.FFluidStatic;
import net.skds.wpo.fluidphysics.WorldWorkSet;
import net.skds.wpo.util.pars.ParsApplier;

public class Events {

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void test(PistonEvent.Pre e) {
		FFluidStatic.onPistonPre(e);
	}

	// @SubscribeEvent
	// public void attachCapability(AttachCapabilitiesEvent<Chunk> e) {
	// new ChunkDataProvider().init(e);
	// }

	@SubscribeEvent
	public void onBucketEvent(FillBucketEvent e) {
		FFluidStatic.onBucketEvent(e);
	}

	@SubscribeEvent
	public void onChunkDataLoad(ChunkDataEvent.Load e) {
		WPOFluidChunkStorage.onChunkDataLoad(e);
	}

	@SubscribeEvent
	public void onChunkDataSave(ChunkDataEvent.Save e) {
		WPOFluidChunkStorage.onChunkDataSave(e);
	}

	@SubscribeEvent
	public void onChunkLoad(ChunkEvent.Load e) {
		WPOFluidChunkStorage.onChunkLoad(e);
	}

	@SubscribeEvent
	public void onChunkUnload(ChunkEvent.Unload e) {
		WPOFluidChunkStorage.onChunkUnload(e);
	}

	@SubscribeEvent
	public void onChunkWatch(ChunkWatchEvent.Watch e) {
		WPOFluidChunkStorage.onChunkWatch(e);
	}

	@SubscribeEvent
	public void onBlockPlaceEvent(BlockEvent.EntityPlaceEvent e) {
		FFluidStatic.onBlockPlace(e);
	}

	@SubscribeEvent
	public void onWWSAttach(OnWWSAttachEvent e) {
		IWWSG wwsg = e.getWWS();
		Level w = e.getWorld();
		if (!w.isClientSide) {
			WorldWorkSet w1 = new WorldWorkSet((ServerLevel) w, wwsg);
			wwsg.addWWS(w1);
		}
	}

	@SubscribeEvent
	public void onTagsUpdated(TagsUpdatedEvent e) {
		ParsApplier.refresh();
		// System.out.println("hhhhhhhhhhhhhhhhhhhh");
	}

	//public static int c = 0;
	//public static long t = 0;

	@SubscribeEvent
	public void onSyncMTHook(SyncTasksHookEvent e) {
		WorldWorkSet.runPendingTasks();
	}
}
