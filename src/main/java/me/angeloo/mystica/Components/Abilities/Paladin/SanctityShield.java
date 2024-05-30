package me.angeloo.mystica.Components.Abilities.Paladin;

import me.angeloo.mystica.Managers.AbilityManager;
import me.angeloo.mystica.Managers.BuffAndDebuffManager;
import me.angeloo.mystica.Managers.CombatManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.ShieldAbilityManaDisplayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SanctityShield {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final ShieldAbilityManaDisplayer shieldAbilityManaDisplayer;
    private final CombatManager combatManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public SanctityShield(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        shieldAbilityManaDisplayer = new ShieldAbilityManaDisplayer(main, manager);
        combatManager = manager.getCombatManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
    }

    public void use(LivingEntity caster){


        if(!abilityReadyInMap.containsKey(caster.getUniqueId())){
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }

        if(!usable(caster)){
            return;
        }

        changeResourceHandler.subTractManaFromEntity(caster, getCost());

        combatManager.startCombatTimer(caster);

        execute(caster);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), 12);
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


        double healAmount = getHealAmount(caster);
        double shield = getShieldAmount(caster);

        buffAndDebuffManager.getGenericShield().applyOrAddShield(caster, shield);

        new BukkitRunnable(){
            int count = 0;
            @Override
            public void run(){

                if(buffAndDebuffManager.getGenericShield().getCurrentShieldAmount(caster)==0){
                    this.cancel();
                    return;
                }

                if(caster instanceof Player){
                    if(!((Player)caster).isOnline()){
                        this.cancel();
                        buffAndDebuffManager.getGenericShield().removeSomeShieldAndReturnHowMuchOver(caster, shield);
                        return;
                    }
                }

                if(profileManager.getAnyProfile(caster).getIfDead()){
                    this.cancel();
                    buffAndDebuffManager.getGenericShield().removeSomeShieldAndReturnHowMuchOver(caster, shield);
                    return;
                }

                changeResourceHandler.addHealthToEntity(caster, healAmount, caster);

                if(count>=5){
                    this.cancel();
                    buffAndDebuffManager.getGenericShield().removeSomeShieldAndReturnHowMuchOver(caster, shield);
                }
                count++;
            }
        }.runTaskTimer(main, 0, 20);

    }

    public double getShieldAmount(LivingEntity caster){
        double maxHealth = profileManager.getAnyProfile(caster).getTotalHealth()+ buffAndDebuffManager.getHealthBuffAmount(caster);
        double level = profileManager.getAnyProfile(caster).getStats().getLevel();
        return  (level + 10 / maxHealth) * 100;
    }

    public double getHealAmount(LivingEntity caster){
        double maxHealth = profileManager.getAnyProfile(caster).getTotalHealth()+ buffAndDebuffManager.getHealthBuffAmount(caster);
        double level = profileManager.getAnyProfile(caster).getStats().getLevel();
        return maxHealth * ((level + 5) /100);
    }

    public double getCost(){
        return 20;
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


        if(profileManager.getAnyProfile(caster).getCurrentMana()<getCost()){
            return false;
        }

        return true;
    }


}
