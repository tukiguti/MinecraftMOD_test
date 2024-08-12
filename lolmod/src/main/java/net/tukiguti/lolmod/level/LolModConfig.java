package net.tukiguti.lolmod.level;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
                .defineInRange("baseXPForLevelUp", 300, 1, Integer.MAX_VALUE);

        XP_INCREASE_RATE = BUILDER
                .comment("Rate at which XP requirement increases per level")
                .defineInRange("xpIncreaseRate", 1.2, 1.0, 10.0);

        XP_FROM_SKELETON = BUILDER
                .comment("XP gained from killing a skeleton")
                .defineInRange("xpFromSkeleton", 20, 1, Integer.MAX_VALUE);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    public static void loadConfig() {
        LOGGER.info("Loading LolMod config");
        if (!configLoaded) {
            try {
                // コンフィグの値を取得して初期化
                int baseXP = BASE_XP_FOR_LEVEL_UP.get();
                double rate = XP_INCREASE_RATE.get();
                int xpFromSkeleton = XP_FROM_SKELETON.get();

                LOGGER.info("Config loaded: BaseXP={}, Rate={}, XPFromSkeleton={}", baseXP, rate, xpFromSkeleton);
                configLoaded = true;
            } catch (IllegalStateException e) {
                LOGGER.error("Failed to load LolMod config: {}", e.getMessage());
                configLoaded = false;
            }
        }
    }

    public static void register(IEventBus modEventBus) {
        LOGGER.info("LolModConfig: Registering config");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SPEC);
        modEventBus.addListener(LolModConfig::onLoad);
        modEventBus.addListener(LolModConfig::onReload);
        LOGGER.info("LolModConfig: Config registered");
    }

    public static void onLoad(final ModConfigEvent.Loading configEvent) {
        LOGGER.info("LolModConfig: Config loading event triggered");
        loadConfig();
    }

    public static void onReload(final ModConfigEvent.Reloading configEvent) {
        LOGGER.info("LolModConfig: Config reloading event triggered");
        loadConfig();
    }

    public static boolean isLoaded() {
        return configLoaded;
    }

    public static int getBaseXPForLevelUp() {
        return configLoaded ? BASE_XP_FOR_LEVEL_UP.get() : 200;
    }
    //configLoadedがtrueならBASE_XP_FOR_LEVEL_UP.get()
    //configLoadedがfalseなら200

    public static double getXPIncreaseRate() {
        return configLoaded ? XP_INCREASE_RATE.get() : 1.2; // デフォルト値
    }

    public static int getXPFromSkeleton() {
        return configLoaded ? XP_FROM_SKELETON.get() : 20; // デフォルト値
    }
}