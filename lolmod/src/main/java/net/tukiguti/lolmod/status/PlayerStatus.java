package net.tukiguti.lolmod.status;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.tukiguti.lolmod.level.LevelManager;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = "lolmod", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerStatus {

    private static final Map<UUID, PlayerStatus> INSTANCES = new ConcurrentHashMap<>();

    private static final UUID MOVEMENT_SPEED_MODIFIER_ID = UUID.fromString("d994c700-39a8-4e21-9dae-5deb56dc7ea4");
    private static final UUID MAX_HEALTH_MODIFIER_ID = UUID.fromString("5d6f0ba2-1286-46fc-b896-461c5cfd99f1");

    private static final int MAX_LEVEL_FOR_STATS = 18;

    private static final double BASE_MOVEMENT_SPEED = 0.1;
    private static final double MAX_BASE_MOVEMENT_SPEED_INCREASE = 0.2;

    private static final double BASE_MAX_HEALTH = 20.0;
    private static final double MAX_BASE_HEALTH_INCREASE = 10.0;

    private static final double MAX_BASE_DEFENSE = 0.2; // 20%軽減

    private Player player;
    private double baseMovementSpeed;
    private double itemMovementSpeed;
    private double baseMaxHealth;
    private double itemMaxHealth;
    private double baseHealthRegen;
    private double itemHealthRegen;
    private double baseDefense;
    private double itemDefense;
    private double baseManaRegen;
    private double itemManaRegen;
    private double baseAd;
    private double itemAd;
    private double baseAp;
    private double itemAp;
    private double baseArmorPenetration;
    private double itemArmorPenetration;
    private double baseCritical;
    private double itemCritical;
    private double baseLifeSteal;
    private double itemLifeSteal;
    private double baseOmniVamp;
    private double itemOmniVamp;
    private double baseCd;
    private double itemCd;

    private PlayerStatus(Player player) {
        this.player = player;
        updateStats();
    }

    public static PlayerStatus get(Player player) {
        return INSTANCES.compute(player.getUUID(), (uuid, existing) -> {
            if (existing == null) {
                return new PlayerStatus(player);
            }
            existing.player = player;
            return existing;
        });
    }

    public static void remove(Player player) {
        INSTANCES.remove(player.getUUID());
    }

    public void updateStats() {
        if (player == null) {
            return;
        }

        LevelManager levelManager = LevelManager.get(player);
        int level = levelManager.getLevel();

        updateBaseStats(level);

        this.baseManaRegen = 1 + (level * 0.1);
        this.baseAd = 1 + (level * 0.2);
        this.baseAp = 1 + (level * 0.2);
        this.baseArmorPenetration = 2 + (level * 0.2);
        this.baseCritical = 2 + (level * 0.2);
        this.baseLifeSteal = level * 0.01;
        this.baseOmniVamp = level * 0.005;
        this.baseCd = 1 + (level * 0.2);

        applyAttributeModifiers();
    }

    private void updateBaseStats(int level) {
        double levelFactor = Math.min(level - 1, MAX_LEVEL_FOR_STATS - 1) / (double) (MAX_LEVEL_FOR_STATS - 1);

        this.baseMovementSpeed = level <= 1 ? BASE_MOVEMENT_SPEED
                : BASE_MOVEMENT_SPEED * (1 + (MAX_BASE_MOVEMENT_SPEED_INCREASE * levelFactor));

        this.baseMaxHealth = level <= 1 ? BASE_MAX_HEALTH
                : BASE_MAX_HEALTH + (MAX_BASE_HEALTH_INCREASE * levelFactor);

        this.baseHealthRegen = level <= 1 ? 0 : levelFactor;
        this.baseDefense = level <= 1 ? 0 : MAX_BASE_DEFENSE * levelFactor;
    }

    private void applyAttributeModifiers() {
        AttributeInstance movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed != null) {
            movementSpeed.removeModifier(MOVEMENT_SPEED_MODIFIER_ID);
            movementSpeed.addPermanentModifier(
                    new AttributeModifier(MOVEMENT_SPEED_MODIFIER_ID, "Movement Speed Bonus",
                            getTotalMovementSpeed() - BASE_MOVEMENT_SPEED, AttributeModifier.Operation.ADDITION)
            );
        }

        AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null) {
            maxHealth.removeModifier(MAX_HEALTH_MODIFIER_ID);
            maxHealth.addPermanentModifier(
                    new AttributeModifier(MAX_HEALTH_MODIFIER_ID, "Max Health Bonus",
                            getTotalMaxHealth() - BASE_MAX_HEALTH, AttributeModifier.Operation.ADDITION)
            );
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        get(player).updateStats();
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        get(player).updateStats();
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        remove(player);
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        PlayerStatus status = get(player);
        float damage = event.getAmount();

        float armorDamageReduction = 1 - (float) (player.getArmorValue() / 20.0);
        float totalDamageReduction = (float) (1 - ((1 - status.getTotalDamageReduction()) * armorDamageReduction));
        totalDamageReduction = Math.min(totalDamageReduction, 0.9f);

        float reducedDamage = damage * (1 - totalDamageReduction);
        event.setAmount(reducedDamage);
    }

    public void setItemMovementSpeed(double bonus) {
        this.itemMovementSpeed = bonus;
        applyAttributeModifiers();
    }

    public void setItemMaxHealth(double bonus) {
        this.itemMaxHealth = bonus;
        applyAttributeModifiers();
    }

    public void setItemHealthRegen(double bonus) {
        this.itemHealthRegen = bonus;
    }

    public void setItemDefense(double bonus) {
        this.itemDefense = bonus;
    }

    public void setItemManaRegen(double bonus) {
        this.itemManaRegen = bonus;
    }

    public void setItemAd(double bonus) {
        this.itemAd = bonus;
    }

    public void setItemAp(double bonus) {
        this.itemAp = bonus;
    }

    public void setItemArmorPenetration(double bonus) {
        this.itemArmorPenetration = bonus;
    }

    public void setItemCritical(double bonus) {
        this.itemCritical = bonus;
    }

    public void setItemLifeSteal(double bonus) {
        this.itemLifeSteal = bonus;
    }

    public void setItemOmniVamp(double bonus) {
        this.itemOmniVamp = bonus;
    }

    public void setItemCd(double bonus) {
        this.itemCd = bonus;
    }

    public double getTotalMovementSpeed() {
        return baseMovementSpeed + itemMovementSpeed;
    }

    public double getTotalMaxHealth() {
        return baseMaxHealth + itemMaxHealth;
    }

    public double getTotalHealthRegen() {
        return baseHealthRegen + itemHealthRegen;
    }

    public double getTotalDamageReduction() {
        return baseDefense + itemDefense;
    }

    public double getTotalManaRegen() {
        return baseManaRegen + itemManaRegen;
    }

    public double getTotalAd() {
        return baseAd + itemAd;
    }

    public double getTotalAp() {
        return baseAp + itemAp;
    }

    public double getTotalArmorPenetration() {
        return baseArmorPenetration + itemArmorPenetration;
    }

    public double getTotalCritical() {
        return baseCritical + itemCritical;
    }

    public double getTotalLifeSteal() {
        return baseLifeSteal + itemLifeSteal;
    }

    public double getTotalOmniVamp() {
        return baseOmniVamp + itemOmniVamp;
    }

    public double getTotalCd() {
        return baseCd + itemCd;
    }
}
