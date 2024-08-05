package kirby.exampletnt;

import kirby.exampletnt.provider.ExampleTNTItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleTNT.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ExampleTNTHooks {

    @SubscribeEvent
    public static void explosionEvent(ExplosionEvent.Detonate event) {
        event.getAffectedEntities().forEach(entity -> {
            if (entity instanceof ItemEntity item && !item.isRemoved() && item.getItem().getItem() == ExampleTNTItems.SMALL_TNT.get()) {
                entity.discard();
                entity.getLevel().explode(null, entity.getX(), entity.getY(), entity.getZ(), 6f, Level.ExplosionInteraction.TNT);
            }else if(entity instanceof ItemEntity item && !item.isRemoved() && item.getItem().getItem() == ExampleTNTItems.BIG_TNT.get()){
                entity.discard();
                entity.getLevel().explode(null, entity.getX(), entity.getY(), entity.getZ(), 12f, Level.ExplosionInteraction.TNT);
            }
        });
    }

    @SubscribeEvent
    public static void entityMobGriefingEvent(EntityMobGriefingEvent event) {
        if (event.getEntity() instanceof EnderMan) {
            if (!event.getEntity().getLevel().isClientSide()) {
                ((EnderMan) event.getEntity()).addEffect(new MobEffectInstance(MobEffects.GLOWING));
            }
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public static void serverChatEvent(ServerChatEvent event) {
        if (event.getRawText().contains("tnt")) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void mobSpawnEvent(MobSpawnEvent.FinalizeSpawn event) {
        if (event.getEntity() instanceof Phantom) {
            event.setSpawnCancelled(true);
        }
    }
    @SubscribeEvent
    public static void itemTossEvent(ItemTossEvent event) {
        if (event.getEntity().getItem().is(ExampleTNTItems.SMALL_TNT.get()) && event.getPlayer() instanceof ServerPlayer serverPlayer) {
            serverPlayer.getAdvancements().award(serverPlayer.getServer().getAdvancements().getAdvancement(new ResourceLocation(ExampleTNT.MOD_ID, "small_tnt")), "toss_small_tnt");
        }
        if (event.getEntity().getItem().is(ExampleTNTItemTagsProvider.TOSS_EXPLOSIVE) && !event.getEntity().getLevel().isClientSide()) {
            event.getEntity().getLevel().explode(event.getPlayer(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), 1f, true, Level.ExplosionInteraction.TNT);
        }
    }
}
