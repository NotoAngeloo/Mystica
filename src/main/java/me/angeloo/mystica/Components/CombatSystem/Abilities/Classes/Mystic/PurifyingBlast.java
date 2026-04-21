package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Mystic;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityMarkManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.MysticAbilities;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.PlayerStateManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.CrowdControl.Root;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.PvpManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.CustomEvents.HudUpdateEvent;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;
import me.angeloo.mystica.Utility.Enums.BarType;
import me.angeloo.mystica.Utility.Enums.SubClass;
import me.angeloo.mystica.Utility.Logic.PveChecker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;

import java.util.*;

public class PurifyingBlast extends BaseAbility {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final AbilityManager abilityManager;
    private final StatusEffectManager statusEffectManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final DamageCalculator damageCalculator;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final CooldownManager cooldownManager;
    private final AbilityMarkManager abilityMarkManager;
    private final PlayerStateManager playerStateManager;

    private final Mana mana;


    public PurifyingBlast(Mystica main, AbilityManager manager){
        super("purifying_blast");
        this.main = main;
        profileManager = main.getProfileManager();
        abilityManager = manager;
        statusEffectManager = main.getStatusEffectManager();
        changeResourceHandler = main.getChangeResourceHandler();
        damageCalculator = main.getDamageCalculator();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        cooldownManager = manager.getCooldownManager();;
        mana = manager.getMana();
        abilityMarkManager = manager.getAbilityMarkManager();
        playerStateManager = manager.getPlayerStateManager();
    }

    private final int baseCooldown = 5;
    private final int cost = 50;
    private final int baseDamage = 15;

    @Override
    public void use(LivingEntity caster){

        if(!usable(caster)){
            return;
        }

        mana.subTractManaFromEntity(caster, cost);

        execute(caster);

        cooldownManager.start(caster.getUniqueId(), 2, (long) (baseCooldown * 1000));
    }

    @Override
    public int cooldown() {
        return baseCooldown;
    }

    private void execute(LivingEntity caster){

        int castTime = 20;

        if(playerStateManager.get(caster.getUniqueId()).has("instant_blast")){
            blastTask(caster);
            return;
        }


        statusEffectManager.applyEffect(caster, new Root(), castTime, null);


        abilityManager.setCasting(caster, true);

        new BukkitRunnable(){
            int count = 0;
            @Override
            public void run(){

                if(caster instanceof Player){
                    if(!((Player)caster).isOnline()){
                        this.cancel();
                        abilityManager.setCasting(caster, false);
                        return;
                    }
                }

                if(!statusEffectManager.canCast(caster)){
                    this.cancel();
                    abilityManager.setCasting(caster, false);
                    return;
                }

                double percent = ((double) count / castTime) * 100;

                if(caster instanceof Player){
                    abilityManager.setCastBar((Player) caster, percent);
                }


                if(count >= castTime){
                    this.cancel();
                    abilityManager.setCasting(caster, false);
                    blastTask(caster);
                }

                count++;
            }


        }.runTaskTimer(main, 0, 1);

    }

    private void blastTask(LivingEntity caster){

        boolean arcane = profileManager.getAnyProfile(caster).getPlayerSubclass().equals(SubClass.Arcane);
        boolean shepard = profileManager.getAnyProfile(caster).getPlayerSubclass().equals(SubClass.Shepard);


        playerStateManager.get(caster.getUniqueId()).remove("instant_blast");


        double healPower = getSkillDamage(caster);

        if(shepard){
            healPower *= 1.5;
        }

        double skillDamage = getSkillDamage(caster);

        if(arcane){
            skillDamage*=3;
        }

        Location center = caster.getLocation().clone();

        Set<LivingEntity> hitBySkill = new HashSet<>();

        double finalSkillDamage = skillDamage;
        double finalHealPower = healPower;
        new BukkitRunnable(){
            double progress = 0;
            final int maxDistance = 10;
            @Override
            public void run(){

                BoundingBox hitBox = new BoundingBox(
                        center.getX() - progress,
                        center.getY() - 2,
                        center.getZ() - progress,
                        center.getX() + progress,
                        center.getY() + 4,
                        center.getZ() + progress
                );

                for (Entity entity : caster.getWorld().getNearbyEntities(hitBox)) {


                    if(!(entity instanceof LivingEntity livingEntity)){
                        continue;
                    }

                    if(entity instanceof ArmorStand){
                        continue;
                    }

                    if(profileManager.getAnyProfile(livingEntity).getIfObject()){
                        continue;
                    }

                    if(hitBySkill.contains(livingEntity)){
                        continue;
                    }

                    hitBySkill.add(livingEntity);

                    boolean crit = damageCalculator.checkIfCrit(caster, 0);
                    double damage = (damageCalculator.calculateDamage(caster, livingEntity, "Magical", finalSkillDamage, crit));

                    //pvp logic
                    if(entity instanceof Player){
                        if(pvpManager.pvpLogic(caster, (Player) entity)){
                            changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, caster, crit);
                        }
                        else{
                            double healAmount  = damageCalculator.calculateHealing(caster, finalHealPower, crit);
                            changeResourceHandler.addHealthToEntity(livingEntity, healAmount, caster);
                            if(shepard){
                                abilityMarkManager.apply(caster, livingEntity);
                            }
                        }

                        continue;
                    }

                    if(pveChecker.pveLogic(livingEntity)){
                        Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(livingEntity, caster));
                        changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, caster, crit);
                    }
                    else{
                        double healAmount  = damageCalculator.calculateHealing(caster, finalHealPower, crit);
                        changeResourceHandler.addHealthToEntity(livingEntity, healAmount, caster);
                        if(shepard){
                            abilityMarkManager.apply(caster, livingEntity);
                        }
                    }

                }

                //particles
                double radius = progress;
                double thisNumber = (Math.pow(2, progress));
                double increment = (2 * Math.PI) / thisNumber;

                for (double i = 0; i < thisNumber; i++) {
                    double angle = i * increment;
                    double x = center.getX() + (radius * Math.cos(angle));
                    double z = center.getZ() + (radius * Math.sin(angle));
                    Location loc = new Location(caster.getWorld(), x, center.getY(), z);
                    caster.getWorld().spawnParticle(Particle.WAX_OFF, loc, 1, 0, 0, 0, 0);
                }


                progress += .6;

                if(progress >= maxDistance){
                    this.cancel();
                }


            }
        }.runTaskTimer(main, 0, 1);
    }


    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_2_Level_Bonus();

        return baseDamage + ((int)(skillLevel/3));
    }


    @Override
    public boolean usable(LivingEntity caster){
        if (mana.getCurrentMana(caster)<cost) {
            return false;
        }

        return cooldownManager.isReady(caster.getUniqueId(), 2, statusEffectManager.getHastePercent(caster));
    }

    /*public int returnWhichItem(Player player){

        if(mana.getCurrentMana(player)<getCost()){

            //Bukkit.getLogger().info("player doesnt have enough mana, adding model daat by 10");

            return 10;
        }

        return 0;
    }*/
}
