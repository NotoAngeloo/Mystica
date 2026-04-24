package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Ranger;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.DamageModifiers.WildRoarBuff;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.PvpManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class WildRoar extends BaseAbility {

    private final Mystica main;
    private final ProfileManager profileManager;
    private final StatusEffectManager statusEffectManager;
    private final PvpManager pvpManager;
    private final CooldownManager cooldownManager;

    public WildRoar(Mystica main, AbilityManager manager){
        super("wild_roar");
        this.main = main;
        profileManager = main.getProfileManager();
        statusEffectManager = main.getStatusEffectManager();
        pvpManager = main.getPvpManager();
        cooldownManager = manager.getCooldownManager();;
    }

    private final int baseCooldown = 30;

    @Override
    public boolean use(LivingEntity caster){

        if(!usable(caster)){
            return false;
        }

        execute(caster);

        cooldownManager.start(caster.getUniqueId(), -1, (long) (baseCooldown * 1000));

        return true;
    }

    @Override
    public int cooldown() {
        return baseCooldown;
    }

    private void execute(LivingEntity caster){

        Location start = caster.getLocation();

        World world = caster.getWorld();
        List<LivingEntity> allLivingEntitiesInWorld = world.getLivingEntities();

        List<LivingEntity> allValidEntities = new ArrayList<>();

        for(LivingEntity thisEntity : allLivingEntitiesInWorld){

            if(profileManager.getAnyProfile(thisEntity).fakePlayer() || thisEntity instanceof Player){
                boolean deathStatus = profileManager.getAnyProfile(thisEntity).getIfDead();

                if(deathStatus){
                    continue;
                }

                if(thisEntity instanceof Player){
                    if(pvpManager.pvpLogic(caster, (Player) thisEntity)){
                        continue;
                    }
                }


                boolean hasBuffAlready = statusEffectManager.hasEffect(thisEntity, "wild_roar");

                if(hasBuffAlready){
                    continue;
                }

                allValidEntities.add(thisEntity);
            }


        }


        allValidEntities.sort(Comparator.comparingDouble(p -> p.getLocation().distance(start)));
        List<LivingEntity> affected = allValidEntities.subList(0, Math.min(5, allValidEntities.size()));



        for(LivingEntity thisEntity : affected){

            statusEffectManager.applyEffect(thisEntity, new WildRoarBuff(), null, getBuffAmount(caster), caster);


            ArmorStand armorStand = caster.getWorld().spawn(start.clone().subtract(0,5,0), ArmorStand.class);
            armorStand.setInvisible(true);
            armorStand.setGravity(false);
            armorStand.setCollidable(false);
            armorStand.setInvulnerable(true);
            armorStand.setMarker(true);

            EntityEquipment entityEquipment = armorStand.getEquipment();

            ItemStack buffIcon = new ItemStack(Material.ARROW);
            ItemMeta meta = buffIcon.getItemMeta();
            assert meta != null;

            meta.setCustomModelData(7);

            buffIcon.setItemMeta(meta);
            assert entityEquipment != null;
            entityEquipment.setHelmet(buffIcon);

            new BukkitRunnable(){
                int count = 0;
                @Override
                public void run(){

                    armorStand.teleport(thisEntity.getLocation());

                    if(count >= 40){
                        cancelTask();
                    }

                    count ++;
                }

                private void cancelTask() {
                    this.cancel();
                    armorStand.remove();
                }

            }.runTaskTimer(main, 0, 1);
        }

    }

    public double getBuffAmount(LivingEntity caster){
        return profileManager.getAnyProfile(caster).getStats().getLevel() * 1.25;
    }

    @Override
    public boolean usable(LivingEntity caster){
        return cooldownManager.isReady(caster.getUniqueId(),-1, statusEffectManager.getHastePercent(caster));
    }
}
