package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Elementalist;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.PlayerState;
import me.angeloo.mystica.Components.CombatSystem.Abilities.PlayerStateManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.CustomEvents.HudUpdateEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Enums.BarType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ElementalBreath extends BaseAbility {

    private final Mystica main;
    private final ProfileManager profileManager;
    private final StatusEffectManager statusEffectManager;
    private final CooldownManager cooldownManager;
    private final PlayerStateManager playerStateManager;

    private final Map<UUID, Integer> buffActiveMap = new HashMap<>();

    public ElementalBreath(Mystica main, AbilityManager manager){
        super("elemental_breath");
        this.main = main;
        profileManager = main.getProfileManager();
        statusEffectManager =  main.getStatusEffectManager();
        cooldownManager = manager.getCooldownManager();
        playerStateManager = manager.getPlayerStateManager();;
    }

    private final int baseCooldown = 120;
    private final int baseDuration = 15;

    @Override
    public boolean use(LivingEntity caster){


        if(!usable(caster)){
            return false;
        }

        execute(caster);

        cooldownManager.start(caster.getUniqueId(), 7, (long) (baseCooldown * 1000));
        return true;
    }

    public int getDuration(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_7_Level_Bonus();

        int bonus = ((int)(skillLevel/3));

        return baseDuration + bonus;
    }

    @Override
    public int cooldown() {
        return baseCooldown;
    }

    private void execute(LivingEntity caster){


        PlayerState state = playerStateManager.get(caster.getUniqueId());
        state.set("elemental_breath", true);

        buffActiveMap.put(caster.getUniqueId(), getDuration(caster));


        //this is probably effecting when players can use this skill
        new BukkitRunnable(){
            @Override
            public void run(){


                if(buffActiveMap.get(caster.getUniqueId()) <= 0){
                    this.cancel();
                    state.remove("elemental_breath");

                    return;
                }

                int cooldown = buffActiveMap.get(caster.getUniqueId()) - 1;

                buffActiveMap.put(caster.getUniqueId(), cooldown);
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

                if(getIfBuffTime(caster) <= 0){
                    this.cancel();
                    return;
                }

                if(caster instanceof Player){
                    if(!((Player)caster).isOnline()){
                        this.cancel();
                        return;
                    }
                }


                Location loc = caster.getLocation();

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

                caster.getWorld().spawnParticle(Particle.FLAME, particleLoc, 1, 0, 0, 0, 0);
                caster.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, particleLoc2, 1, 0, 0, 0, 0);

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

    private int getIfBuffTime(LivingEntity caster){
        return buffActiveMap.getOrDefault(caster.getUniqueId(), 0);
    }


    @Override
    public boolean usable(LivingEntity caster){

        if(getIfBuffTime(caster) >= 0){
            return false;
        }

        return cooldownManager.isReady(caster.getUniqueId(), 7, statusEffectManager.getHastePercent(caster));
    }

}
