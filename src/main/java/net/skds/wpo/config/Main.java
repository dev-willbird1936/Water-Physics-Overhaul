package net.skds.wpo.config;

import java.util.function.Function;

import net.minecraftforge.common.ForgeConfigSpec;
import net.skds.core.config.PerformancePreset;
import net.skds.wpo.WPO;
import net.skds.wpo.WPOConfig;

public class Main {

    //public final ForgeConfigSpec.BooleanValue slide;
    public final ForgeConfigSpec.EnumValue<PerformancePreset> performancePreset;
    public final ForgeConfigSpec.IntValue maxSlideDist, maxEqDist, maxBucketDist;

    // public final ForgeConfigSpec.ConfigValue<ArrayList<String>> ss;
    // private final ForgeConfigSpec.IntValue maxFluidLevel;

    public Main(ForgeConfigSpec.Builder innerBuilder) {
        Function<String, ForgeConfigSpec.Builder> builder = name -> innerBuilder .translation(WPO.MOD_ID + ".config." + name);

        innerBuilder.push("Performance");

        // slide = builder.apply("setSlide").comment("Will fluids slide down from hills").define("setSlide", true);
        performancePreset = builder.apply("performancePreset")
                .comment("DEFAULT uses equalizeDistance=16 and slidingDistance=5. VERY_LOW=8/2, LOW=12/3, HIGH=24/7, VERY_HIGH=32/10. Set to CUSTOM to use the manual values below.")
                .defineEnum("performancePreset", PerformancePreset.DEFAULT);
        maxEqDist = builder.apply("setMaxEqualizeDistance")
                .comment("Manual value used only when performancePreset=CUSTOM. The distance over which water levels will equalize.")
                .defineInRange("setMaxEqualizeDistance", 16, 0, 256);
        maxSlideDist = builder.apply("setMaxSlidingDistance")
                .comment("Manual value used only when performancePreset=CUSTOM. The maximum distance water will slide to reach lower ground.")
                .defineInRange("setMaxSlidingDistance", 5, 0, 256);
        innerBuilder.pop();

        innerBuilder.push("Interaction");
        maxBucketDist = builder.apply("setMaxBucketDistance")
                .comment("Maximum horizontal bucket reach from click location (for water packet pickup)")
                .defineInRange("setMaxBucketDistance", 8, 0, WPOConfig.MAX_FLUID_LEVEL);

        innerBuilder.pop();
    }
}
