package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Elementalist;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.ClassSpecific.Elemental_Breath;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class ElementalBreath extends BaseAbility {

    private final Mystica main;
    private final ProfileManager profileManager;
    private final StatusEffectManager statusEffectManager;
    private final CooldownManager cooldownManager;

    //private final Map<UUID, Integer> buffActiveMap = new HashMap<>();

    public ElementalBreath(Mystica main, AbilityManager manager){
        super("elemental_breath");
        this.main = main;
        profileManager = main.getProfileManager();
        statusEffectManager =  main.getStatusEffectManager();
        cooldownManager = manager.getCooldownManager();
        ;
    }

    private final int baseCooldown = 120;
    //this is in seconds
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

        return (baseDuration + bonus) * 20;
    }

    @Override
    public int cooldown() {
        return baseCooldown;
    }

    private void execute(LivingEntity caster){


        statusEffectManager.applyEffect(caster,new Elemental_Breath(),getDuration(caster), null, caster);

        new BukkitRunnable(){
            double height = 0;
            boolean up = true;
            final double radius = 1;
            double angle = 0;
            Vector initialDirection;
            @Override
            public void run(){

                if(!statusEffectManager.hasEffect(caster, "elemental_breath")){
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


    @Override
    public boolean usable(LivingEntity caster){

        if(statusEffectManager.hasEffect(caster, "elemental_breath")){
            return false;
        }

        return cooldownManager.isReady(caster.getUniqueId(), 7, statusEffectManager.getHastePercent(caster));
    }

}
