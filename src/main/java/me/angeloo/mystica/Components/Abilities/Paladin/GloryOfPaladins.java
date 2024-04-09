package me.angeloo.mystica.Components.Abilities.Paladin;

import me.angeloo.mystica.Managers.AbilityManager;
import me.angeloo.mystica.Managers.BuffAndDebuffManager;
import me.angeloo.mystica.Managers.CombatManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.CooldownDisplayer;
import me.angeloo.mystica.Utility.DamageCalculator;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GloryOfPaladins {

    private final Mystica main;
    private final ProfileManager profileManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final CombatManager combatManager;
    private final DamageCalculator damageCalculator;
    private final ChangeResourceHandler changeResourceHandler;
    private final CooldownDisplayer cooldownDisplayer;

    private final Map<UUID, Integer> buffActiveMap = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public GloryOfPaladins(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        combatManager = manager.getCombatManager();
        damageCalculator = main.getDamageCalculator();
        changeResourceHandler = main.getChangeResourceHandler();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
    }

    public void use(Player player){

        if(!abilityReadyInMap.containsKey(player.getUniqueId())){
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }

        if(abilityReadyInMap.get(player.getUniqueId()) > 0){
            return;
        }

        if(getIfBuffTime(player) > 0){
            return;
        }


        if(profileManager.getAnyProfile(player).getCurrentMana()<getCost()){
            return;
        }

        changeResourceHandler.subTractManaFromPlayer(player, getCost());

        combatManager.startCombatTimer(player);

        execute(player);

        abilityReadyInMap.put(player.getUniqueId(), 12);
        new BukkitRunnable(){
            @Override
            public void run(){

                if(abilityReadyInMap.get(player.getUniqueId()) <= 0){
                    cooldownDisplayer.displayCooldown(player, 6);
                    this.cancel();
                    return;
                }

                int cooldown = abilityReadyInMap.get(player.getUniqueId()) - 1;

                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(player, 6);

            }
        }.runTaskTimer(main, 0,20);

    }

    private void execute(Player player){

        //increase max hp as well

        buffActiveMap.put(player.getUniqueId(), 8);
        new BukkitRunnable(){
            @Override
            public void run(){

                if(buffActiveMap.get(player.getUniqueId()) <= 0){
                    this.cancel();
                    return;
                }

                int cooldown = buffActiveMap.get(player.getUniqueId()) - 1;

                buffActiveMap.put(player.getUniqueId(), cooldown);

            }
        }.runTaskTimer(main, 0,20);

        new BukkitRunnable(){
            double height = 0;
            boolean up = true;
            final double radius = 1;
            double angle = 0;
            Vector initialDirection;
            @Override
            public void run(){

                if(getIfBuffTime(player) <= 0){
                    this.cancel();
                    return;
                }

                if(!player.isOnline()){
                    this.cancel();
                    return;
                }


                Location loc = player.getLocation();

                if(initialDirection == null) {
                    initialDirection = loc.getDirection().setY(0).normalize();
                    initialDirection.rotateAroundY(Math.toRadians(-45));
                }

                Vector direction = initialDirection.clone();
                double radians = Math.toRadians(angle);

                direction.rotateAroundY(radians);

                double x = loc.getX() + direction.getX() * radius;
                double z = loc.getZ() + direction.getZ() * radius;

                double x2 = loc.getX() - direction.getX() * radius;
                double z2 = loc.getZ() - direction.getZ() * radius;

                Location particleLoc = new Location(loc.getWorld(), x, loc.getY() + height, z);
                Location particleLoc2 = new Location(loc.getWorld(), x2, loc.getY() + height, z2);

                player.getWorld().spawnParticle(Particle.WAX_OFF, particleLoc, 1, 0, 0, 0, 0);
                player.getWorld().spawnParticle(Particle.WAX_OFF, particleLoc2, 1, 0, 0, 0, 0);

                if(up){
                    height += .1;
                }
                else{
                    height -= .1;
                }

                angle += 5;

                if(height >= 2){
                    up = false;
                }

                if(height < 0){
                    up = true;
                }

            }
        }.runTaskTimer(main, 0, 2);

    }

    public void procGlory(Player player, LivingEntity livingEntity){

        if(getIfBuffTime(player) <= 0){
            return;
        }


        boolean crit = damageCalculator.checkIfCrit(player, 0);
        double damage = damageCalculator.calculateDamage(player, livingEntity, "Physical", getSkillDamage(player), crit);

        changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, player);

        double healAmount = (profileManager.getAnyProfile(player).getTotalHealth()+ buffAndDebuffManager.getHealthBuffAmount(player)) * .05;
        //chance to restore
        int random = (int) (Math.random() * 100) + 1;
        if(random >= 25){
            changeResourceHandler.addHealthToEntity(player, healAmount, player);
        }
    }

    public double getCost(){
        return 10;
    }

    public double getSkillDamage(Player player){
        double skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) +
                profileManager.getAnyProfile(player).getSkillLevels().getSkill_6_Level_Bonus();
        return 20 + ((int)(skillLevel/3));
    }

    public int getIfBuffTime(Player player){
        return buffActiveMap.getOrDefault(player.getUniqueId(), 0);
    }

    public int getCooldown(Player player){
        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

}
