package me.angeloo.mystica.Components.CombatSystem.Abilities.Elementalist;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.DamageModifiers.ConjuringForceBuff;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.CombatManager;
import me.angeloo.mystica.Components.CombatSystem.PvpManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.CustomEvents.UltimateStatusChageEvent;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.*;

public class ConjuringForce {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final PvpManager pvpManager;
    private final StatusEffectManager statusEffectManager;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public ConjuringForce(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        combatManager = manager.getCombatManager();
        pvpManager = main.getPvpManager();
        statusEffectManager = main.getStatusEffectManager();
    }

    //TODO: conj force adds health if ur in it

    public void use(LivingEntity caster){
        if (!abilityReadyInMap.containsKey(caster.getUniqueId())) {
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }

        if(!usable(caster)) {
            return;
        }

        execute(caster);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), getSkillCooldown());
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {

                if (getPlayerCooldown(caster) <= 0) {
                    this.cancel();
                    return;
                }

                int cooldown = getPlayerCooldown(caster) - 1;

                cooldown = cooldown - statusEffectManager.getHasteLevel(caster);

                abilityReadyInMap.put(caster.getUniqueId(), cooldown);

                if(caster instanceof Player){

                    Bukkit.getScheduler().runTask(main, () ->{
                        Bukkit.getServer().getPluginManager().callEvent(new UltimateStatusChageEvent((Player) caster));
                    });


                }


            }
        }.runTaskTimerAsynchronously(main, 0, 20);
        cooldownTask.put(caster.getUniqueId(), task);
    }

    private void execute(LivingEntity caster){

        Location start = caster.getLocation().clone();


        new BukkitRunnable(){
            int ran = 0;
            final Set<LivingEntity> affected = new HashSet<>();

            final Location loc = start.clone();
            double height = 0;
            boolean up = true;
            final double radius = 4;
            double angle = 0;
            Vector initialDirection;
            @Override
            public void run(){

                Set<LivingEntity> hitBySkill = new HashSet<>();

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

                if(height >= 4){
                    up = false;
                }

                if(height < 0){
                    up = true;
                }

                double increment = (2 * Math.PI) / 16; // angle between particles

                for (int i = 0; i < 16; i++) {
                    double angle = i * increment;
                    double x3 = start.getX() + (4 * Math.cos(angle));
                    double y3 = start.getY() + 1;
                    double z3 = start.getZ() + (4 * Math.sin(angle));
                    Location loc = new Location(start.getWorld(), x3, y3, z3);
                    caster.getWorld().spawnParticle(Particle.SPELL_WITCH, loc, 1,0, 0, 0, 0);
                }


                BoundingBox hitBox = new BoundingBox(
                        start.getX() - 4,
                        start.getY() - 2,
                        start.getZ() - 4,
                        start.getX() + 4,
                        start.getY() + 4,
                        start.getZ() + 4
                );


                for (Entity entity : caster.getWorld().getNearbyEntities(hitBox)) {


                    if(!(entity instanceof LivingEntity)){
                        continue;
                    }

                    LivingEntity thisEntity = (LivingEntity) entity;

                    if(thisEntity instanceof Player){
                        if(pvpManager.pvpLogic(caster, (Player) thisEntity)){
                            continue;
                        }
                    }


                    hitBySkill.add(caster);
                    affected.add(caster);
                }

                for(LivingEntity thisEntity : affected){
                    if(!hitBySkill.contains(thisEntity)){
                        affected.remove(thisEntity);

                        statusEffectManager.removeEffect(thisEntity, "conjuring_force");
                        continue;
                    }

                    statusEffectManager.applyEffect(thisEntity, new ConjuringForceBuff(), null, getBuffAmount(caster));

                }


                if(ran >=140){
                    cancelTask();
                }

                ran ++;
            }

            private void cancelTask(){
                this.cancel();

                for(LivingEntity thisEntity : affected){
                    statusEffectManager.removeEffect(thisEntity, "conjuring_force");
                }

            }

        }.runTaskTimer(main, 0, 1);
    }

    public double getBuffAmount(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getStats().getLevel();
        return 5 + skillLevel;
    }

    public int getPlayerCooldown(LivingEntity caster){
        int cooldown = abilityReadyInMap.getOrDefault(caster.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

    public int getSkillCooldown(){
        return 26;
    }

    public boolean usable(LivingEntity caster){
        return getPlayerCooldown(caster) <= 0;
    }

    public void resetCooldown(LivingEntity caster){
        abilityReadyInMap.remove(caster.getUniqueId());
    }

}
