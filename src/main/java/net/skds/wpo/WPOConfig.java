package net.skds.wpo;

import java.nio.file.Paths;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.skds.core.config.PerformancePreset;
import net.skds.wpo.config.Main;

public class WPOConfig {
    private static final int DEFAULT_MAX_EQ_DIST = 16;
    private static final int DEFAULT_MAX_SLIDE_DIST = 5;
    private static final int VERY_LOW_MAX_EQ_DIST = 8;
    private static final int VERY_LOW_MAX_SLIDE_DIST = 2;
    private static final int LOW_MAX_EQ_DIST = 12;
    private static final int LOW_MAX_SLIDE_DIST = 3;
    private static final int HIGH_MAX_EQ_DIST = 24;
    private static final int HIGH_MAX_SLIDE_DIST = 7;
    private static final int VERY_HIGH_MAX_EQ_DIST = 32;
    private static final int VERY_HIGH_MAX_SLIDE_DIST = 10;

    public static final Main COMMON;
    //public static final Waterlogged WATERLOGGED;
    private static final ForgeConfigSpec SPEC;//, SPEC_WL;


    public static final int MAX_FLUID_LEVEL = 8;

    static {
        Pair<Main, ForgeConfigSpec> cm = new ForgeConfigSpec.Builder().configure(Main::new);
        COMMON = cm.getLeft();
        SPEC = cm.getRight();

        //Pair<Waterlogged, ForgeConfigSpec> wl = new ForgeConfigSpec.Builder().configure(Waterlogged::new);
        ///WATERLOGGED = wl.getLeft();
        //SPEC_WL = wl.getRight();

        // FINITE_WATER = COMMON.finiteWater.get();
        // MAX_EQ_DIST = COMMON.maxEqDist.get();
    }

    public static void init() {
        Paths.get(System.getProperty("user.dir"), "config", WPO.MOD_ID).toFile().mkdir();
        ModLoadingContext.get().registerConfig(Type.COMMON, SPEC, Paths.get(WPO.MOD_ID, "common.toml").toString());
        //ModLoadingContext.get().registerConfig(Type.COMMON, SPEC_WL, PhysEX.MOD_ID + "/waterlogged.toml");
    }

    public static PerformancePreset getPerformancePreset() {
        return COMMON.performancePreset.get();
    }

    public static int getManualMaxEqDist() {
        return COMMON.maxEqDist.get();
    }

    public static int getManualMaxSlideDist() {
        return COMMON.maxSlideDist.get();
    }

    public static int getMaxEqDist() {
        return switch (getPerformancePreset()) {
            case VERY_LOW -> VERY_LOW_MAX_EQ_DIST;
            case LOW -> LOW_MAX_EQ_DIST;
            case DEFAULT -> DEFAULT_MAX_EQ_DIST;
            case HIGH -> HIGH_MAX_EQ_DIST;
            case VERY_HIGH -> VERY_HIGH_MAX_EQ_DIST;
            case CUSTOM -> COMMON.maxEqDist.get();
        };
    }

    public static int getMaxSlideDist() {
        return switch (getPerformancePreset()) {
            case VERY_LOW -> VERY_LOW_MAX_SLIDE_DIST;
            case LOW -> LOW_MAX_SLIDE_DIST;
            case DEFAULT -> DEFAULT_MAX_SLIDE_DIST;
            case HIGH -> HIGH_MAX_SLIDE_DIST;
            case VERY_HIGH -> VERY_HIGH_MAX_SLIDE_DIST;
            case CUSTOM -> COMMON.maxSlideDist.get();
        };
    }

    public static int getMaxBucketDist() {
        return COMMON.maxBucketDist.get();
    }

    public static void setPerformancePreset(PerformancePreset preset) {
        COMMON.performancePreset.set(preset);
    }

    public static void setManualMaxEqDist(int value) {
        COMMON.maxEqDist.set(value);
    }

    public static void setManualMaxSlideDist(int value) {
        COMMON.maxSlideDist.set(value);
    }

    public static void setMaxBucketDist(int value) {
        COMMON.maxBucketDist.set(value);
    }

    public static void save() {
        SPEC.save();
    }
}
