package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.DamageOverTime;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.ApplicationBehavior;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.CombatContext;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusInstance;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Utility.Enums.DamageType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import java.util.HashSet;
import java.util.Set;

public class Combust implements StatusEffect {

    @Override
    public String getId() {
        return "combust";
    }

    @Override
    public int getDuration() {
        return 3 * 20;
    }

    @Override
    public boolean requireMagnitudeDeclaration() {
        return true;
    }

    @Override
    public ApplicationBehavior applicationBehavior() {
        return ApplicationBehavior.REPLACE_SMALLER;
    }

    @Override
    public void onTick(LivingEntity entity, StatusInstance instance, CombatContext combatContext) {

        double burn = getMagnitude() * .1;

        if(instance.getLivedTicks()%20==0){
            LivingEntity caster = instance.getSource();
            boolean crit = combatContext.damageCalculator().checkIfCrit(caster, 0);
            double damage = combatContext.damageCalculator().calculateDamage(caster, entity, DamageType.Magical, burn , crit, 0);
            combatContext.changeResourceHandler().subtractHealthFromEntity(entity, damage, caster, crit);
            Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(entity, caster));

        }

    }

    @Override
    public void onRemoveEffects(LivingEntity entity, StatusInstance instance, CombatContext combatContext) {
        //explode
        Set<LivingEntity> hitBySkill = new HashSet<>();

        BoundingBox hitBox = new BoundingBox(
                entity.getLocation().getX() - 4,
                entity.getLocation().getY() - 2,
                entity.getLocation().getZ() - 4,
                entity.getLocation().getX() + 4,
                entity.getLocation().getY() + 4,
                entity.getLocation().getZ() + 4
        );

        double increment = (2 * Math.PI) / 16; // angle between particles

        for (int i = 0; i < 16; i++) {
            double angle = i * increment;
            double x = entity.getLocation().getX() + (4 * Math.cos(angle));
            double z = entity.getLocation().getZ() + (4 * Math.sin(angle));
            Location loc = new Location(entity.getWorld(), x, (entity.getLocation().getY()), z);

            entity.getWorld().spawnParticle(Particle.FLAME, loc, 1,0, 0, 0, 0);
        }

        for (Entity hitEntity : entity.getWorld().getNearbyEntities(hitBox)) {

            if(hitEntity == instance.getSource()){
                continue;
            }

            if(!(hitEntity instanceof LivingEntity livingEntity)){
                continue;
            }

            if(hitEntity instanceof ArmorStand){
                continue;
            }

            if(hitBySkill.contains(livingEntity)){
                continue;
            }

            hitBySkill.add(livingEntity);

            boolean crit2 = combatContext.damageCalculator().checkIfCrit(livingEntity, 0);
            double damage = (combatContext.damageCalculator().calculateDamage(instance.getSource(), livingEntity, DamageType.Magical, getMagnitude(), crit2, 0));

            //pvp logic
            if(entity instanceof Player){
                if(combatContext.pvpManager().pvpLogic(instance.getSource(), (Player) entity)){
                    combatContext.changeResourceHandler().subtractHealthFromEntity(livingEntity, damage, instance.getSource(), crit2);
                }
                continue;
            }

            if(combatContext.pveChecker().pveLogic(livingEntity)){
                Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(livingEntity, instance.getSource()));
                combatContext.changeResourceHandler().subtractHealthFromEntity(livingEntity, damage, instance.getSource(), crit2);
            }

        }
    }

    @Override
    public String getIcon(LivingEntity entity, StatusInstance instance) {
        return "\ue43f";
    }

}
