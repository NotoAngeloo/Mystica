package me.angeloo.mystica.Utility.DamageUtils;

import me.angeloo.mystica.Managers.BuffAndDebuffManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Components.Profile;
import me.angeloo.mystica.Utility.Enums.SubClass;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class DamageCalculator {

    private final ProfileManager profileManager;
    private final BuffAndDebuffManager buffAndDebuffManager;


    public DamageCalculator(Mystica main){
        profileManager = main.getProfileManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
    }

    public boolean checkIfCrit(LivingEntity caster, int bonus){

        Profile playerProfile = profileManager.getAnyProfile(caster);

        int random = (int) (Math.random() * 100) + 1;

        bonus = bonus + buffAndDebuffManager.getCritBuffAmount(caster);

        if(random <= (playerProfile.getTotalCrit()) + bonus){
            buffAndDebuffManager.getWellCrit().removeBonus(caster);
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

        if(buffAndDebuffManager.getImmune().getImmune(entity)){
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

        double attackBonus = buffAndDebuffManager.getAttackBuffAmount(damager);

        if(entity instanceof Player){

            if(type.equalsIgnoreCase("Physical")){

                attack = playerProfile.getTotalAttack() + attackBonus;
                defence = enemyProfile.getTotalDefense();

                if(buffAndDebuffManager.getPierceBuff().getIfBuffTime(damager)>0){
                    defence = defence * .75;
                }

                damage = (damage * multiplierForCrit)
                        * ((attack) / (defence));

                if(buffAndDebuffManager.getArmorBreak().getStacks(entity) >= 3){
                    damage += (profileManager.getAnyProfile(entity).getTotalHealth() * ((buffAndDebuffManager.getArmorBreak().getStacks(entity) * 10) * .01));
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

                if(buffAndDebuffManager.getPierceBuff().getIfBuffTime(damager)>0){
                    defence = defence * .75;
                }

                damage = (damage * multiplierForCrit)
                        * (attack / defence);

                if(buffAndDebuffManager.getArmorBreak().getStacks(entity) >= 3){
                    damage += (profileManager.getAnyProfile(entity).getStats().getHealth() * ((buffAndDebuffManager.getArmorBreak().getStacks(entity) * 10) * .01));
                }
            }

            if(type.equalsIgnoreCase("Magical")){

                attack = playerProfile.getTotalAttack();
                defence = enemyProfile.getStats().getMagic_Defense();

                damage = (damage * multiplierForCrit)
                        * (attack / defence);
            }
        }

        damage = damage * buffAndDebuffManager.getTotalDamageMultipliers(damager, entity);
        damage = damage + buffAndDebuffManager.getTotalDamageAddition(damager, entity);


        return damage;
    }


    public double calculateGettingDamaged(LivingEntity hitEntity, LivingEntity entity, String type, double damage){

        if(buffAndDebuffManager.getImmune().getImmune(hitEntity)){
            return 0.0;
        }

        Profile playerProfile = profileManager.getAnyProfile(hitEntity);
        Profile enemyProfile = profileManager.getAnyProfile(entity);

        double attack;
        double defence;

        double attackBonus = buffAndDebuffManager.getAttackBuffAmount(entity);

        double multiplierForCrit = 1;
        int random = (int) (Math.random() * 100) + 1;

        if(random <= enemyProfile.getStats().getCrit()){
            multiplierForCrit = 1.5;
        }


        if(type.equalsIgnoreCase("Physical")){

            attack = enemyProfile.getStats().getAttack() + attackBonus;
            defence = playerProfile.getTotalDefense();

            if(buffAndDebuffManager.getPierceBuff().getIfBuffTime(hitEntity)>0){
                defence = defence * .75;
            }

            damage = (damage * multiplierForCrit)
                    * (attack / defence);

            if(buffAndDebuffManager.getArmorBreak().getStacks(hitEntity) >= 3){
                damage += (profileManager.getAnyProfile(hitEntity).getTotalHealth() * ((buffAndDebuffManager.getArmorBreak().getStacks(hitEntity) * 10) * .01));
            }

        }

        if(type.equalsIgnoreCase("Magical")){

            attack = enemyProfile.getStats().getAttack();
            defence = playerProfile.getTotalMagicDefense();

            damage = (damage * multiplierForCrit)
                    * (attack / defence);
        }

        damage = damage * buffAndDebuffManager.getTotalDamageMultipliers(entity, hitEntity);


        return damage;
    }


}
