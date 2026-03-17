package net.skds.wpo.fluiddata;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.material.FluidState;

import java.util.HashMap;
import java.util.Map;

public final class WPOFluidSectionData {

    private static final String ENTRIES_TAG = "Entries";
    private static final String INDEX_TAG = "Index";
    private static final String STATE_TAG = "State";

    private final Map<Short, FluidState> entries = new HashMap<>();

    public static WPOFluidSectionData fromTag(CompoundTag tag) {
        WPOFluidSectionData data = new WPOFluidSectionData();
        ListTag entriesTag = tag.getList(ENTRIES_TAG, CompoundTag.TAG_COMPOUND);
        for (int i = 0; i < entriesTag.size(); i++) {
            CompoundTag entryTag = entriesTag.getCompound(i);
            short index = entryTag.getShort(INDEX_TAG);
            FluidState state = FluidStateSerializers.readFluidState(entryTag.getCompound(STATE_TAG));
            data.setFluidState(index, state);
        }
        return data;
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        ListTag entriesTag = new ListTag();
        for (Map.Entry<Short, FluidState> entry : entries.entrySet()) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putShort(INDEX_TAG, entry.getKey());
            entryTag.put(STATE_TAG, FluidStateSerializers.writeFluidState(entry.getValue()));
            entriesTag.add(entryTag);
        }
        tag.put(ENTRIES_TAG, entriesTag);
        return tag;
    }

    public void setFluidState(int localX, int localY, int localZ, FluidState state) {
        setFluidState(packIndex(localX, localY, localZ), state);
    }

    public void setFluidState(short index, FluidState state) {
        if (state.isEmpty()) {
            entries.remove(index);
        } else {
            entries.put(index, state);
        }
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    private static short packIndex(int localX, int localY, int localZ) {
        return (short) (((localY & 15) << 8) | ((localZ & 15) << 4) | (localX & 15));
    }
}
