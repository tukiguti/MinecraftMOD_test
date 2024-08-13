package net.tukiguti.lolmod.status;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.tukiguti.lolmod.level.LevelManager;

import java.util.UUID;

public class PlayerStatus {
    private static final UUID MOVEMENT_SPEED_MODIFIER_ID = UUID.fromString("d994c700-39a8-4e21-9dae-5deb56dc7ea4");
    private static final UUID MAX_HEALTH_MODIFIER_ID = UUID.fromString("5d6f0ba2-1286-46fc-b896-461c5cfd99f1");
    private static final UUID ARMOR_MODIFIER_ID = UUID.fromString("67afe8d3-7c55-4d6c-8baa-7340a32d2832");

    private final Player player;
    private double movementSpeed;
    private double maxHealth;
    private double healthRegen;
    private double manaRegen;
    private double damage;
    private double defense;
    private double armorPenetration;
    private double criticalChance;
    private double lifeSteal;
    private double omniVamp;
    private double tenacity;

    public PlayerStatus(Player player) {
        this.player = player;
        MinecraftForge.EVENT_BUS.register(this);
        updateStats();
    }

    public void updateStats() {
        LevelManager levelManager = LevelManager.get(player);
        int level = levelManager.getLevel();

        // 基本ステータスの更新
        this.movementSpeed = 0.1 + (level * 0.001);
        this.maxHealth = 20 + (level * 0.5);
        this.healthRegen = 1 + (level * 0.05);
        this.defense = level * 0.5;

        // その他のステータスの更新
        this.manaRegen = 1 + (level * 0.1);
        this.damage = 1 + (level * 0.2);
        this.armorPenetration = level * 0.1;
        this.criticalChance = 0.05 + (level * 0.005);
        this.lifeSteal = level * 0.01;
        this.omniVamp = level * 0.005;
        this.tenacity = level * 0.01;

        applyAttributeModifiers();
    }

    private void applyAttributeModifiers() {
        // 移動速度の適用
        player.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(MOVEMENT_SPEED_MODIFIER_ID);
        player.getAttribute(Attributes.MOVEMENT_SPEED).addPermanentModifier(
                new AttributeModifier(MOVEMENT_SPEED_MODIFIER_ID, "Movement Speed Bonus", movementSpeed, AttributeModifier.Operation.ADDITION)
        );

        // 最大体力の適用
        player.getAttribute(Attributes.MAX_HEALTH).removeModifier(MAX_HEALTH_MODIFIER_ID);
        player.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(
                new AttributeModifier(MAX_HEALTH_MODIFIER_ID, "Max Health Bonus", maxHealth - 20, AttributeModifier.Operation.ADDITION)
        );

        // 防御力の適用
        player.getAttribute(Attributes.ARMOR).removeModifier(ARMOR_MODIFIER_ID);
        player.getAttribute(Attributes.ARMOR).addPermanentModifier(
                new AttributeModifier(ARMOR_MODIFIER_ID, "Armor Bonus", defense, AttributeModifier.Operation.ADDITION)
        );
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof Player && event.getEntity() == player) {
            updateStats();
        }
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof Player && event.getEntity() == player) {
            updateStats();
        }
    }

    // Getter methods for all stats
    public double getMovementSpeed() { return movementSpeed; }
    public double getMaxHealth() { return maxHealth; }
    public double getHealthRegen() { return healthRegen; }
    public double getManaRegen() { return manaRegen; }
    public double getDamage() { return damage; }
    public double getDefense() { return defense; }
    public double getArmorPenetration() { return armorPenetration; }
    public double getCriticalChance() { return criticalChance; }
    public double getLifeSteal() { return lifeSteal; }
    public double getOmniVamp() { return omniVamp; }
    public double getTenacity() { return tenacity; }
}