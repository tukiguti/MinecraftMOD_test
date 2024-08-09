package net.tukiguti.lolmod.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = "lolmod", bus = Mod.EventBusSubscriber.Bus.MOD)
public class LolModConfig {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.IntValue BASE_XP_FOR_LEVEL_UP;
    public static final ForgeConfigSpec.DoubleValue XP_INCREASE_RATE;
    public static final ForgeConfigSpec.IntValue XP_FROM_SKELETON;

    private static boolean configLoaded = false;

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

    public static void register(IEventBus modEventBus) {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SPEC, "lolmod-common.toml");
        modEventBus.addListener(LolModConfig::onLoad);
        modEventBus.addListener(LolModConfig::onReload);
        LOGGER.info("LolMod config registered");
    }

    public static void onLoad(final ModConfigEvent.Loading configEvent) {
        configLoaded = true;
        LOGGER.info("Loaded lolmod config file");
        printConfigValues();
    }

    public static void onReload(final ModConfigEvent.Reloading configEvent) {
        configLoaded = true;
        LOGGER.info("Reloaded lolmod config file");
        printConfigValues();
    }

    private static void printConfigValues() {
        LOGGER.info("Current config values:");
        LOGGER.info("BASE_XP_FOR_LEVEL_UP: {}", BASE_XP_FOR_LEVEL_UP.get());
        LOGGER.info("XP_INCREASE_RATE: {}", XP_INCREASE_RATE.get());
        LOGGER.info("XP_FROM_SKELETON: {}", XP_FROM_SKELETON.get());
    }
    public static boolean isLoaded() {
        return configLoaded;
    }
}