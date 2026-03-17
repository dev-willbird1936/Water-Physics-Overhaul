package net.skds.wpo.fluiddata;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.material.FluidState;

import java.util.HashMap;
import java.util.Map;

public final class WPOFluidChunkData {

    private static final int VERSION = 2;
    private static final String VERSION_TAG = "Version";
    private static final String SECTIONS_TAG = "Sections";
    private static final String SECTION_Y_TAG = "SectionY";
    private static final String SECTION_DATA_TAG = "Data";

    private final Map<Integer, WPOFluidSectionData> sections = new HashMap<>();

    public static WPOFluidChunkData fromTag(CompoundTag tag) {
        WPOFluidChunkData data = new WPOFluidChunkData();
        if (tag.getInt(VERSION_TAG) != VERSION) {
            return data;
        }

        ListTag sectionTags = tag.getList(SECTIONS_TAG, CompoundTag.TAG_COMPOUND);
        for (int i = 0; i < sectionTags.size(); i++) {
            CompoundTag sectionTag = sectionTags.getCompound(i);
            int sectionY = sectionTag.getInt(SECTION_Y_TAG);
            data.sections.put(sectionY, WPOFluidSectionData.fromTag(sectionTag.getCompound(SECTION_DATA_TAG)));
        }
        return data;
    }

    public static WPOFluidChunkData scanFromChunk(ChunkAccess chunk) {
        WPOFluidChunkData data = new WPOFluidChunkData();
        LevelChunkSection[] chunkSections = chunk.getSections();
        for (int sectionIndex = 0; sectionIndex < chunkSections.length; sectionIndex++) {
            LevelChunkSection section = chunkSections[sectionIndex];
            if (section == null) {
                continue;
            }

            WPOFluidSectionData sectionData = null;
            for (int localY = 0; localY < 16; localY++) {
                for (int localZ = 0; localZ < 16; localZ++) {
                    for (int localX = 0; localX < 16; localX++) {
                        FluidState state = section.getBlockState(localX, localY, localZ).getFluidState();
                        if (state.isEmpty()) {
                            continue;
                        }
                        if (sectionData == null) {
                            sectionData = new WPOFluidSectionData();
                        }
                        sectionData.setFluidState(localX, localY, localZ, state);
                    }
                }
            }

            if (sectionData != null && !sectionData.isEmpty()) {
                data.sections.put(chunk.getSectionYFromSectionIndex(sectionIndex), sectionData);
            }
        }
        return data;
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt(VERSION_TAG, VERSION);
        ListTag sectionTags = new ListTag();
        for (Map.Entry<Integer, WPOFluidSectionData> entry : sections.entrySet()) {
            if (entry.getValue().isEmpty()) {
                continue;
            }
            CompoundTag sectionTag = new CompoundTag();
            sectionTag.putInt(SECTION_Y_TAG, entry.getKey());
            sectionTag.put(SECTION_DATA_TAG, entry.getValue().toTag());
            sectionTags.add(sectionTag);
        }
        tag.put(SECTIONS_TAG, sectionTags);
        return tag;
    }

    public void setFluidState(BlockPos pos, FluidState state) {
        int sectionY = pos.getY() >> 4;
        WPOFluidSectionData sectionData = sections.computeIfAbsent(sectionY, ignored -> new WPOFluidSectionData());
        sectionData.setFluidState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, state);
        if (sectionData.isEmpty()) {
            sections.remove(sectionY);
        }
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }
}
