package me.angeloo.mystica.Components.CombatSystem.Abilities.ShadowKnight;

import me.angeloo.mystica.Components.CombatSystem.Abilities.ShadowKnightAbilities;
import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.BuffAndDebuffManager;
import me.angeloo.mystica.Components.CombatSystem.CombatManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Components.Hud.CooldownDisplayer;
import me.angeloo.mystica.Utility.Enums.SubClass;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BurialGround {

    private final Mystica main;
    private final ProfileManager profileManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final CombatManager combatManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final CooldownDisplayer cooldownDisplayer;

    private final Energy energy;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public BurialGround(Mystica main, AbilityManager manager, ShadowKnightAbilities shadowKnightAbilities){
        this.main = main;
        profileManager = main.getProfileManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        combatManager = manager.getCombatManager();
        changeResourceHandler = main.getChangeResourceHandler();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
        energy = shadowKnightAbilities.getEnergy();
    }

    public void use(LivingEntity caster){

        if(!abilityReadyInMap.containsKey(caster.getUniqueId())){
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }

        if(!usable(caster)){
            return;
        }

        execute(caster);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), 12);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getCooldown(caster) <= 0){
                    cooldownDisplayer.displayCooldown(caster, 3);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(caster) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(caster);

                abilityReadyInMap.put(caster.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(caster, 3);

            }
        }.runTaskTimerAsynchronously(main, 0,20);
        cooldownTask.put(caster.getUniqueId(), task);

    }

    private void execute(LivingEntity caster){

        boolean blood = profileManager.getAnyProfile(caster).getPlayerSubclass().equals(SubClass.Blood);

        Location start = caster.getLocation();


        double finalHealAmount = getHealPercent(caster);
        new BukkitRunnable(){
            int ran = 0;
            @Override
            public void run(){

                if(caster instanceof Player){
                    if(!((Player)caster).isOnline()){
                        this.cancel();

                        if(blood){
                            buffAndDebuffManager.getDamageReduction().removeReduction(caster);
                        }

                        return;
                    }
                }

                if(playerValid()){

                    changeResourceHandler.addHealthToEntity(caster, finalHealAmount, caster);
                    energy.addEnergyToEntity(caster, getEnergyRefund());

                    if(blood){
                        buffAndDebuffManager.getDamageReduction().applyDamageReduction(caster, .8, 0);
                    }
                }

                double increment = (2 * Math.PI) / 16; // angle between particles

                for (int i = 0; i < 16; i++) {
                    double angle = i * increment;
                    double x = start.getX() + (3 * Math.cos(angle));
                    double z = start.getZ() + (3 * Math.sin(angle));
                    double y = start.getY();
                    Location loc = new Location(caster.getWorld(), x, y, z);

                    caster.getWorld().spawnParticle(Particle.GLOW_SQUID_INK, loc, 1,0, 0, 0, 0);
                }

                if(ran>=7){
                    this.cancel();

                    if(blood){
                        buffAndDebuffManager.getDamageReduction().removeReduction(caster);
                    }

                }

                ran++;
            }

            private boolean playerValid(){

                double distance = caster.getLocation().distance(start);

                if(distance>5){
                    return false;
                }

                return !profileManager.getAnyProfile(caster).getIfDead();
            }

        }.runTaskTimer(main, 0, 20);

    }

    public double getHealPercent(LivingEntity caster){
        double healAmount = (profileManager.getAnyProfile(caster).getTotalHealth() + buffAndDebuffManager.getHealthBuffAmount(caster)) * .03;
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_3_Level_Bonus();
        return healAmount + ((int)(skillLevel/3));
    }

    public int getEnergyRefund(){
        return 10;
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

        Block block = caster.getLocation().subtract(0,1,0).getBlock();

        return block.getType() != Material.AIR;
    }


}
