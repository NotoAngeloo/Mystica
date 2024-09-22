package me.angeloo.mystica.Components.Abilities.Ranger;

import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.ShieldAbilityManaDisplayer;
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
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class WildRoar {

    private final Mystica main;
    private final ProfileManager profileManager;
    private final ShieldAbilityManaDisplayer shieldAbilityManaDisplayer;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final PvpManager pvpManager;
    private final CombatManager combatManager;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public WildRoar(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        shieldAbilityManaDisplayer = new ShieldAbilityManaDisplayer(main, manager);
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        pvpManager = main.getPvpManager();
        combatManager = manager.getCombatManager();
    }

    public void use(LivingEntity caster){

        if(!abilityReadyInMap.containsKey(caster.getUniqueId())){
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }

        if(!usable(caster)){
            return;
        }

        combatManager.startCombatTimer(caster);

        execute(caster);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), 30);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getCooldown(caster) <= 0){
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(caster) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(caster);
                abilityReadyInMap.put(caster.getUniqueId(), cooldown);

                if(caster instanceof Player){
                    shieldAbilityManaDisplayer.displayPlayerHealthPlusInfo((Player) caster);
                }


            }
        }.runTaskTimer(main, 0,20);
        cooldownTask.put(caster.getUniqueId(), task);

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


                boolean hasBuffAlready = buffAndDebuffManager.getWildRoarBuff().getBuffTime(thisEntity) > 0;

                if(hasBuffAlready){
                    continue;
                }

                allValidEntities.add(thisEntity);
            }


        }


        allValidEntities.sort(Comparator.comparingDouble(p -> p.getLocation().distance(start)));
        List<LivingEntity> affected = allValidEntities.subList(0, Math.min(5, allValidEntities.size()));



        for(LivingEntity thisEntity : affected){

            buffAndDebuffManager.getWildRoarBuff().applyBuff(thisEntity, getBuffAmount(caster));

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

    public int getCooldown(LivingEntity caster){

        int cooldown = abilityReadyInMap.getOrDefault(caster.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

    public void resetCooldown(LivingEntity caster){
        abilityReadyInMap.remove(caster.getUniqueId());
    }

    public boolean usable(LivingEntity caster){
        if(getCooldown(caster) > 0){
            return false;
        }


        return true;
    }
}
