package me.angeloo.mystica.Utility.DamageUtils;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Components.Profile;
import me.angeloo.mystica.Utility.Enums.SubClass;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class DamageCalculator {

    private final ProfileManager profileManager;
    private final StatusEffectManager statusEffectManager;


    public DamageCalculator(Mystica main){
        profileManager = main.getProfileManager();
        statusEffectManager = main.getStatusEffectManager();
    }

    public boolean checkIfCrit(LivingEntity caster, int bonus){

        Profile playerProfile = profileManager.getAnyProfile(caster);

        int random = (int) (Math.random() * 100) + 1;

        bonus = bonus + statusEffectManager.getCritBuffAmount(caster);

        if(random <= (playerProfile.getTotalCrit()) + bonus){
            statusEffectManager.removeEffect(caster, "light_well");
        }

        return random <= (playerProfile.getTotalCrit()) + bonus;
    }

    public double calculateHealing(LivingEntity healer, double amount, boolean crit){

        double attack = profileManager.getAnyProfile(healer).getTotalAttack();

        //Bukkit.getLogger().info("attack " + attack);

        amount *= (attack * .05);

        //Bukkit.getLogger().info("amount " + amount);

        double multiplierForCrit = 1;

        if(crit){
            multiplierForCrit = 1.5;
            //maybe spawn particles on a crit here
        }

        double multiplierForHealerBonus = 1;



        if(profileManager.getAnyProfile(healer).getPlayerSubclass().equals(SubClass.Shepard)
        || profileManager.getAnyProfile(healer).getPlayerSubclass().equals(SubClass.Divine)){
            multiplierForHealerBonus = 1.2;
        }



        amount *= multiplierForCrit;
        amount *= multiplierForHealerBonus;

        //Bukkit.getLogger().info("final amount " + amount);
        return amount;
    }

    public double calculateDamage(LivingEntity damager, LivingEntity entity, String type, double damage, boolean crit){

        if(statusEffectManager.hasEffect(entity, "immune")){
            return 0.0;
        }

        Profile playerProfile = profileManager.getAnyProfile(damager);
        Profile enemyProfile = profileManager.getAnyProfile(entity);

        double multiplierForCrit = 1;

        if(crit){
            multiplierForCrit = 1.5;
            //maybe spawn particles on a crit here
        }

        double attack;
        double defence;

        double attackBonus = statusEffectManager.getAttackBuffAmount(damager);

        if(entity instanceof Player){

            if(type.equalsIgnoreCase("Physical")){

                attack = playerProfile.getTotalAttack() + attackBonus;
                defence = enemyProfile.getTotalDefense();

                defence *= statusEffectManager.getDefenceIgnoreModifier(damager);


                damage = (damage * multiplierForCrit)
                        * ((attack) / (defence));

                //manually here since interacts in a unique way
                if(statusEffectManager.getArmorBreakStacks(entity) >= 3){
                    damage += (profileManager.getAnyProfile(entity).getTotalHealth() * ((statusEffectManager.getArmorBreakStacks(entity) * 10) * .01));
                }
            }

            if(type.equalsIgnoreCase("Magical")){

                attack = playerProfile.getTotalAttack();
                defence = enemyProfile.getTotalMagicDefense();

                damage = (damage * multiplierForCrit)
                        * (attack / defence);
            }

        }

        if(!(entity instanceof Player)){

            if(type.equalsIgnoreCase("Physical")){

                attack = playerProfile.getTotalAttack() + attackBonus;
                defence = enemyProfile.getStats().getDefense();

                defence *= statusEffectManager.getDefenceIgnoreModifier(damager);


                damage = (damage * multiplierForCrit)
                        * (attack / defence);

                if(statusEffectManager.getArmorBreakStacks(entity) >= 3){
                    damage += (profileManager.getAnyProfile(entity).getStats().getHealth() * ((statusEffectManager.getArmorBreakStacks(entity) * 10) * .01));
                }
            }

            if(type.equalsIgnoreCase("Magical")){

                attack = playerProfile.getTotalAttack();
                defence = enemyProfile.getStats().getMagic_Defense();

                damage = (damage * multiplierForCrit)
                        * (attack / defence);
            }
        }

        damage = damage * statusEffectManager.getTotalDamageMultipliers(damager, entity);
        damage = damage + statusEffectManager.getTotalDamageAdditives(damager, entity);
        return damage;
    }


    public double calculateGettingDamaged(LivingEntity hitEntity, LivingEntity damager, String type, double damage){

        if(statusEffectManager.hasEffect(hitEntity, "immune")){
            return 0.0;
        }

        Profile playerProfile = profileManager.getAnyProfile(hitEntity);
        Profile enemyProfile = profileManager.getAnyProfile(damager);

        double attack;
        double defence;

        double attackBonus = statusEffectManager.getAttackBuffAmount(damager);

        double multiplierForCrit = 1;
        int random = (int) (Math.random() * 100) + 1;

        if(random <= enemyProfile.getStats().getCrit()){
            multiplierForCrit = 1.5;
        }


        if(type.equalsIgnoreCase("Physical")){

            attack = enemyProfile.getStats().getAttack() + attackBonus;
            defence = playerProfile.getTotalDefense();

            defence *= statusEffectManager.getDefenceIgnoreModifier(damager);


            damage = (damage * multiplierForCrit)
                    * (attack / defence);

            if(statusEffectManager.getArmorBreakStacks(hitEntity) >= 3){
                damage += (profileManager.getAnyProfile(hitEntity).getTotalHealth() * ((statusEffectManager.getArmorBreakStacks(hitEntity) * 10) * .01));
            }

        }

        if(type.equalsIgnoreCase("Magical")){

            attack = enemyProfile.getStats().getAttack();
            defence = playerProfile.getTotalMagicDefense();

            damage = (damage * multiplierForCrit)
                    * (attack / defence);
        }

        damage = damage * statusEffectManager.getTotalDamageMultipliers(damager, hitEntity);
        damage = damage + statusEffectManager.getTotalDamageAdditives(damager, hitEntity);

        return damage;
    }


}
