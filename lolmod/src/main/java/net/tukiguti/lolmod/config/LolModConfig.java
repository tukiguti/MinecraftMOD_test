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
    public static final ForgeConfigSpec SPEC;

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
        SPEC = BUILDER.build();
    }

    public static void loadConfig() {
        LOGGER.info("Loading LolMod config");
        try {
            BASE_XP_FOR_LEVEL_UP.get();
            XP_INCREASE_RATE.get();
            XP_FROM_SKELETON.get();
            configLoaded = true;
            LOGGER.info("LolMod config loaded successfully");
            printConfigValues();
        } catch (IllegalStateException e) {
            LOGGER.error("Failed to load LolMod config", e);
            configLoaded = false;
        }
    }

    public static void register(IEventBus modEventBus) {
        LOGGER.info("LolModConfig: Registering config");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SPEC, "forge-common.toml");
        modEventBus.addListener(LolModConfig::onLoad);
        modEventBus.addListener(LolModConfig::onReload);
        LOGGER.info("LolModConfig: Config registered");
    }

    public static void onLoad(final ModConfigEvent.Loading configEvent) {
        LOGGER.info("LolModConfig: Loading config file {}", configEvent.getConfig().getFileName());
        configLoaded = true;
        printConfigValues();
        LOGGER.info("LolModConfig: Config loaded successfully");
    }

    public static void onReload(final ModConfigEvent.Reloading configEvent) {
        LOGGER.info("LolModConfig: Reloading config file {}", configEvent.getConfig().getFileName());
        configLoaded = true;
        printConfigValues();
        LOGGER.info("LolModConfig: Config reloaded successfully");
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

    @SubscribeEvent
    public static void onConfigChanged(final ModConfigEvent.Reloading event) {
        LOGGER.info("Config changed, reloading...");
        loadConfig();
    }
}