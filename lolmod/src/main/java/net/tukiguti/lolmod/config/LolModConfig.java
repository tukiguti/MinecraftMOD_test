package net.tukiguti.lolmod.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "lolmod", bus = Mod.EventBusSubscriber.Bus.MOD)
public class LolModConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.IntValue BASE_XP_FOR_LEVEL_UP;
    public static final ForgeConfigSpec.DoubleValue XP_INCREASE_RATE;
    public static final ForgeConfigSpec.IntValue XP_FROM_SKELETON;

    static {
        BUILDER.push("Level System");

        BASE_XP_FOR_LEVEL_UP = BUILDER
                .comment("Base XP required for first level up")
                .defineInRange("baseXPForLevelUp", 100, 1, Integer.MAX_VALUE);

        XP_INCREASE_RATE = BUILDER
                .comment("Rate at which XP requirement increases per level")
                .defineInRange("xpIncreaseRate", 1.1, 1.0, 10.0);

        XP_FROM_SKELETON = BUILDER
                .comment("XP gained from killing a skeleton")
                .defineInRange("xpFromSkeleton", 10, 1, Integer.MAX_VALUE);

        BUILDER.pop();
    }

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SPEC, "lolmod-common.toml");
        System.out.println("LolMod config registered.");
    }

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent.Loading configEvent) {
        System.out.println("Loaded lolmod config file.");
        printConfigValues();
    }

    @SubscribeEvent
    public static void onReload(final ModConfigEvent.Reloading configEvent) {
        System.out.println("Reloaded lolmod config file.");
        printConfigValues();
    }

    private static void printConfigValues() {
        System.out.println("Current config values:");
        System.out.println("BASE_XP_FOR_LEVEL_UP: " + BASE_XP_FOR_LEVEL_UP.get());
        System.out.println("XP_INCREASE_RATE: " + XP_INCREASE_RATE.get());
        System.out.println("XP_FROM_SKELETON: " + XP_FROM_SKELETON.get());
    }
}