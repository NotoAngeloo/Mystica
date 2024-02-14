package me.angeloo.mystica.Components.Abilities.Assassin;

import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.CooldownDisplayer;
import me.angeloo.mystica.Utility.DamageCalculator;
import me.angeloo.mystica.Utility.StealthTargetBlacklist;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Stealth {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final StealthTargetBlacklist stealthTargetBlacklist;
    private final PvpManager pvpManager;
    private final TargetManager targetManager;
    private final DamageCalculator damageCalculator;
    private final CombatManager combatManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final CooldownDisplayer cooldownDisplayer;

    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();
    private final Map<UUID, Boolean> stealthed = new HashMap<>();
    private final Map<UUID, Integer> timeInStealth = new HashMap<>();

    public Stealth(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        stealthTargetBlacklist = main.getStealthTargetBlacklist();
        pvpManager = main.getPvpManager();
        targetManager = main.getTargetManager();
        damageCalculator = main.getDamageCalculator();
        combatManager = manager.getCombatManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
    }

    public void toggle(Player player){

        if(!abilityReadyInMap.containsKey(player.getUniqueId())){
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }

        if(abilityReadyInMap.get(player.getUniqueId()) > 0){
            return;
        }


        combatManager.startCombatTimer(player);

        if(!getIfStealthed(player)){

            double cost = 10;

            if(profileManager.getAnyProfile(player).getCurrentMana()<cost){
                return;
            }

            changeResourceHandler.subTractManaFromPlayer(player, cost);

            vanish(player);
            return;
        }

        reveal(player);
    }

    private void vanish(Player player){

        new BukkitRunnable(){
            int count = 0;
            @Override
            public void run(){

                if(!player.isOnline()){
                    this.cancel();
                    return;
                }

                boolean deathStatus = profileManager.getAnyProfile(player).getIfDead();

                if(deathStatus || buffAndDebuffManager.getIfInterrupt(player)){
                    this.cancel();
                    return;
                }

                Location particleLocation = player.getLocation().add(0, 1, 0);
                player.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, particleLocation, 10, 0.3, 0.5, 0.3, 0.05);

                if(count >= 5){
                    buffAndDebuffManager.getHidden().hidePlayer(player, true);
                    stealthTargetBlacklist.add(player);
                    stealthed.put(player.getUniqueId(), true);
                    revealAfterTime(player);

                    for(Map.Entry<UUID, LivingEntity> entry: targetManager.getTargetMap().entrySet()){
                        UUID playerID = entry.getKey();
                        Player thisPlayer = Bukkit.getPlayer(playerID);
                        Entity target = entry.getValue();

                        if(target != null && target.equals(player)){
                            assert thisPlayer != null;

                            if(pvpManager.pvpLogic(player, thisPlayer)){
                                targetManager.removeAllBars(thisPlayer);
                            }

                        }
                    }
                    this.cancel();
                }
                count ++;
            }
        }.runTaskTimer(main, 0, 1);


    }

    private void revealAfterTime(Player player){

        new BukkitRunnable(){
            int count = 0;
            @Override
            public void run(){

                cooldownDisplayer.displayCooldown(player, 8);

                if(!getIfStealthed(player)){
                    this.cancel();
                    timeInStealth.remove(player.getUniqueId());
                    return;
                }

                if(count>=10){
                    this.cancel();
                    forceReveal(player, null);
                    timeInStealth.remove(player.getUniqueId());
                    return;
                }

                count++;

                timeInStealth.put(player.getUniqueId(), count);
            }
        }.runTaskTimer(main, 0, 20);

    }

    public void reveal(Player player){

        buffAndDebuffManager.getHidden().unhidePlayer(player);
        stealthTargetBlacklist.remove(player);
        stealthed.put(player.getUniqueId(), false);
    }

    private void forceReveal(Player player, LivingEntity victim){

        buffAndDebuffManager.getHidden().unhidePlayer(player);
        stealthTargetBlacklist.remove(player);
        stealthed.put(player.getUniqueId(), false);

        abilityReadyInMap.put(player.getUniqueId(), 30);
        new BukkitRunnable(){
            @Override
            public void run(){

                if(abilityReadyInMap.get(player.getUniqueId()) <= 0){
                    cooldownDisplayer.displayCooldown(player, 8);
                    this.cancel();
                    return;
                }

                int cooldown = abilityReadyInMap.get(player.getUniqueId()) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(player, 8);
            }
        }.runTaskTimer(main, 0,20);

        if(victim==null){
            return;
        }

        double skillDamage = 40;

        double skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkill_8_Level() +
                profileManager.getAnyProfile(player).getSkillLevels().getSkill_8_Level_Bonus();
        skillDamage = skillDamage + ((int)(skillLevel/10));

        boolean crit = damageCalculator.checkIfCrit(player, 0);
        double damage = damageCalculator.calculateDamage(player, victim, "Physical", skillDamage, crit);

        Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(victim, player));
        changeResourceHandler.subtractHealthFromEntity(victim, damage, player);
    }

    public void stealthBonusCheck(Player player, LivingEntity entity){

        if(!getIfStealthed(player)){
            return;
        }

        forceReveal(player, entity);

    }

    public boolean getIfStealthed(Player player){
        return stealthed.getOrDefault(player.getUniqueId(), false);
    }

    public int getCooldown(Player player){

        if(getIfStealthed(player)){
            int time = timeInStealth.getOrDefault(player.getUniqueId(), 0);
            return 10 - time;
        }


        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

}
