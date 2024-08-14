package net.tukiguti.lolmod.status;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.tukiguti.lolmod.level.LevelManager;

import java.util.UUID;

public class PlayerStatus {

    private static final UUID MOVEMENT_SPEED_MODIFIER_ID = UUID.fromString("d994c700-39a8-4e21-9dae-5deb56dc7ea4");
    private static final UUID MAX_HEALTH_MODIFIER_ID = UUID.fromString("5d6f0ba2-1286-46fc-b896-461c5cfd99f1");
    private static final UUID ARMOR_MODIFIER_ID = UUID.fromString("67afe8d3-7c55-4d6c-8baa-7340a32d2832");

    private static final int MAX_LEVEL_FOR_STATS = 18;

    private static final double BASE_MOVEMENT_SPEED = 0.1;
    private static final double MAX_BASE_MOVEMENT_SPEED_INCREASE = 0.2;

    private static final int REGEN_EFFECT_DURATION = 50;
    private static final int REGEN_EFFECT_AMPLIFIER = 0;

    private static final double BASE_MAX_HEALTH = 20.0;
    private static final double MAX_BASE_HEALTH_INCREASE = 10.0;

    private static final double MAX_BASE_DEFENSE = 0.2;//20%軽減


    private final Player player;
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

    public PlayerStatus(Player player) {
        this.player = player;
        MinecraftForge.EVENT_BUS.register(this);
        updateStats();
    }

    public void updateStats() {
        LevelManager levelManager = LevelManager.get(player);
        int level = levelManager.getLevel();

        // 基本ステータスの更新
        updateBaseStats(level);

        this.baseManaRegen = 1 + (level * 0.1);
        this.baseAd = 1 + (level * 0.2);
        this.baseAp = 1 + (level * 0.2);
        this.baseArmorPenetration = level * 0.1;
        this.baseCritical = 0.05 + (level * 0.005);
        this.baseLifeSteal = level * 0.01;
        this.baseOmniVamp = level * 0.005;
        this.baseCd = level * 0.01;

        applyAttributeModifiers();
    }
    private void updateBaseStats(int level) {
        double levelFactor = Math.min(level - 1, MAX_LEVEL_FOR_STATS - 1) / (double)(MAX_LEVEL_FOR_STATS - 1);

        // 基礎移動速度の更新（レベル1では増加なし、徐々に増加し、レベル18で俊敏Ⅰ）
        this.baseMovementSpeed = level <= 1 ? BASE_MOVEMENT_SPEED :
                BASE_MOVEMENT_SPEED * (1 + (MAX_BASE_MOVEMENT_SPEED_INCREASE * levelFactor));

        // 基礎最大体力の更新（レベル1では増加なし20、徐々に増加し、レベル18で体力30）
        this.baseMaxHealth = level <= 1 ? BASE_MAX_HEALTH :
                BASE_MAX_HEALTH + (MAX_BASE_HEALTH_INCREASE * levelFactor);

        // 基礎体力回復速度の更新（レベル1では増加なし、徐々に増加し、レベル18で再生Ⅰ）
        this.baseHealthRegen = level <= 1 ? 0 : levelFactor;

        // 基礎ダメージ軽減の更新（レベル1では増加なし、徐々に増加し、レベル18で20%軽減）
        this.baseDefense = level <= 1 ? 0 : MAX_BASE_DEFENSE * levelFactor;
    }

    private void applyAttributeModifiers() {
        // 移動速度の適用
        player.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(MOVEMENT_SPEED_MODIFIER_ID);
        player.getAttribute(Attributes.MOVEMENT_SPEED).addPermanentModifier(
                new AttributeModifier(MOVEMENT_SPEED_MODIFIER_ID, "Movement Speed Bonus",
                        getTotalMovementSpeed() - BASE_MOVEMENT_SPEED, AttributeModifier.Operation.ADDITION)
        );

        // 最大体力の適用
        player.getAttribute(Attributes.MAX_HEALTH).removeModifier(MAX_HEALTH_MODIFIER_ID);
        player.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(
                new AttributeModifier(MAX_HEALTH_MODIFIER_ID, "Max Health Bonus",
                        getTotalMaxHealth() - BASE_MAX_HEALTH, AttributeModifier.Operation.ADDITION)
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
    /*@SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingTickEvent event) {
        if (event.getEntity() instanceof Player && event.getEntity() == player) {
            // 毎秒（20 ticks）ごとに回復チャンスをチェック
            if (event.getEntity().tickCount % 20 == 0) {
                if (Math.random() < getTotalHealthRegen()) {
                    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, REGEN_EFFECT_DURATION, REGEN_EFFECT_AMPLIFIER, false, false));
                }
            }
        }
    }*/
    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity() instanceof Player && event.getEntity() == player) {
            float damage = event.getAmount();

            // 防具による軽減を計算
            float armorDamageReduction = 1 - (float)(player.getArmorValue() / 20.0);

            // MODによる軽減と防具による軽減を組み合わせる
            float totalDamageReduction = (float) (1 - ((1 - getTotalDamageReduction()) * armorDamageReduction));

            // 合計の軽減率の上限が90%
            totalDamageReduction = Math.min(totalDamageReduction, 0.9f);

            float reducedDamage = damage * (1 - totalDamageReduction);
            event.setAmount(reducedDamage);
        }
    }
    // アイテムによるステータスを設定するメソッド
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

    // 合計値を取得するメソッド
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