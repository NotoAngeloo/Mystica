package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Paladin;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityMarkManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.PaladinAbilities;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.PlayerStateManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.PvpManager;
import me.angeloo.mystica.Components.CombatSystem.TargetManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;
import me.angeloo.mystica.Utility.Logic.PveChecker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Set;

public class DecreeHonor extends BaseAbility {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final StatusEffectManager statusEffectManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final CooldownManager cooldownManager;
    private final AbilityMarkManager abilityMarkManager;
    private final PlayerStateManager playerStateManager;

    private final Purity purity;


    public DecreeHonor(Mystica main, AbilityManager manager){
        super("decree_honor");
        this.main = main;
        profileManager = main.getProfileManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        statusEffectManager = main.getStatusEffectManager();
        changeResourceHandler = main.getChangeResourceHandler();
        cooldownManager = manager.getCooldownManager();
        purity = manager.getPurity();
        abilityMarkManager = manager.getAbilityMarkManager();
        playerStateManager = manager.getPlayerStateManager();
    }

    private final int baseCooldown = 5;
    private final double range = 10;
    private final int baseDamage = 20;
    private final int healPower = 5;

    @Override
    public void use(LivingEntity caster){

        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(target != null){

            double distance = caster.getLocation().distance(target.getLocation());

            if(distance > range + statusEffectManager.getAdditionalRange(caster)){
                return;
            }

            if(target instanceof Player){
                if(profileManager.getAnyProfile(target).getIfDead()){
                    target = caster;
                }
            }

        }

        if(target == null){
            target = caster;
        }

        if(!usable(caster)){
            return;
        }


        execute(caster, target);

        cooldownManager.start(caster.getUniqueId(), 1, (long) (baseCooldown * 1000));

    }

    private void execute(LivingEntity caster, LivingEntity target){

        Location start = target.getLocation();

        ArmorStand armorStand = caster.getWorld().spawn(start.clone(), ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);

        EntityEquipment entityEquipment = armorStand.getEquipment();

        ItemStack item = new ItemStack(Material.SUGAR);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(11);
        item.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setHelmet(item);

        double finalSkillDamage = getSkillDamage(caster);
        double finalHealPower = getHealPower(caster);
        new BukkitRunnable(){
            double down = 0;
            @Override
            public void run(){

                if(caster instanceof Player){
                    if(!((Player)caster).isOnline()){
                        cancelTask();
                        return;
                    }
                }

                double increment = (2 * Math.PI) / 16; // angle between particles

                for (int i = 0; i < 16; i++) {
                    double angle = i * increment;
                    double x = start.getX() + (1 * Math.cos(angle));
                    double y = start.clone().add(0,4-down,0).getY();
                    double z = start.getZ() + (1 * Math.sin(angle));
                    Location loc = new Location(start.getWorld(), x, y, z);

                    caster.getWorld().spawnParticle(Particle.WAX_OFF, loc, 1,0, 0, 0, 0);
                }

                if(down>=4){
                    cancelTask();
                    doSomething(target);
                }

                down+=0.7;
            }

            private void cancelTask(){
                this.cancel();
                armorStand.remove();
                //abilityManager.setSkillRunning(player, false);

            }

            private void doSomething(LivingEntity target){

                if(checkValid(target)){
                    return;
                }

                boolean crit = damageCalculator.checkIfCrit(caster, 0);

                if(crit){
                    playerStateManager.get(caster.getUniqueId()).set("move_cast", true);
                }

                if(target instanceof Player){

                    if(!pvpManager.pvpLogic(caster, (Player) target)){

                        double healAmount  = damageCalculator.calculateHealing(caster, finalHealPower, crit);

                        if(abilityMarkManager.getTargets(caster).contains(target)){
                            markHealInstead(caster, healAmount);
                            return;
                        }


                        changeResourceHandler.addHealthToEntity(target, healAmount, caster);
                        return;
                    }

                }

                if(!(target instanceof Player)){
                    if(!pveChecker.pveLogic(target)){
                        double healAmount  = damageCalculator.calculateHealing(caster, finalHealPower, crit);

                        if(abilityMarkManager.getTargets(caster).contains(target)){
                            markHealInstead(caster, healAmount);
                            return;
                        }

                        changeResourceHandler.addHealthToEntity(target, healAmount, caster);
                        return;
                    }
                }

                double damage = damageCalculator.calculateDamage(caster, target, "Physical", finalSkillDamage, crit);

                Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, caster));
                changeResourceHandler.subtractHealthFromEntity(target, damage, caster, crit);

            }

            private boolean checkValid(LivingEntity target){

                if(target instanceof Player){
                    if(((Player)target).isOnline()){
                        return true;
                    }

                    if(profileManager.getAnyProfile(target).getIfDead()){
                        return true;
                    }
                }

                return target.isDead();
            }

        }.runTaskTimer(main, 0, 1);
    }

    private void markHealInstead(LivingEntity caster, double healAmount){

        Set<LivingEntity> affected = abilityMarkManager.getTargets(caster);

        for(LivingEntity thisPlayer : affected){
            changeResourceHandler.addHealthToEntity(thisPlayer, healAmount, caster);

            Location center = thisPlayer.getLocation().clone().add(0,1,0);

            double increment = (2 * Math.PI) / 16; // angle between particles

            for (int i = 0; i < 16; i++) {
                double angle = i * increment;
                double x = center.getX() + (1 * Math.cos(angle));
                double z = center.getZ() + (1 * Math.sin(angle));
                Location loc = new Location(center.getWorld(), x, (center.getY()), z);

                thisPlayer.getWorld().spawnParticle(Particle.WAX_OFF, loc, 1,0, 0, 0, 0);
            }
        }

    }

    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_1_Level_Bonus();
        double damage = baseDamage + ((int)(skillLevel/3));

        if(purity.active(caster)){
            damage = damage * 3;
            purity.reset(caster);
        }

        return damage;
    }

    public double getHealPower(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_1_Level_Bonus();

        double damage = healPower + ((int)(skillLevel/3));

        if(purity.active(caster)){
            damage = damage * 3;
            purity.reset(caster);
        }

        return damage;
    }

    @Override
    public boolean usable(LivingEntity caster){

        return cooldownManager.isReady(caster.getUniqueId(), 1, statusEffectManager.getHastePercent(caster));
    }


}
