package me.angeloo.mystica.Utility;

import me.angeloo.mystica.Managers.BuffAndDebuffManager;
import me.angeloo.mystica.Managers.DpsManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Components.Profile;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DamageCalculator {

    private final ProfileManager profileManager;
    private final BuffAndDebuffManager buffAndDebuffManager;


    public DamageCalculator(Mystica main){
        profileManager = main.getProfileManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
    }

    public boolean checkIfCrit(Player player, int bonus){

        Profile playerProfile = profileManager.getAnyProfile(player);

        int random = (int) (Math.random() * 100) + 1;

        bonus = bonus + buffAndDebuffManager.getCritBuffAmount(player);

        if(random <= (playerProfile.getTotalCrit()) + bonus){
            buffAndDebuffManager.getWellCrit().removeBonus(player);
        }

        return random <= (playerProfile.getTotalCrit()) + bonus;
    }

    public double calculateHealing(LivingEntity target, LivingEntity healer, double amount, boolean crit){

        amount = amount / 100;

        double multiplierForCrit = 1;

        if(crit){
            multiplierForCrit = 1.5;
            //maybe spawn particles on a crit here
        }

        double multiplierForHealerBonus = 1;

        if(profileManager.getAnyProfile(healer).getPlayerSubclass().equalsIgnoreCase("shepard")
        || profileManager.getAnyProfile(healer).getPlayerSubclass().equalsIgnoreCase("divine")){
            multiplierForHealerBonus = 1.2;
        }

        double totalTargetHealth = profileManager.getAnyProfile(target).getTotalHealth() + buffAndDebuffManager.getHealthBuffAmount(target);

        amount = totalTargetHealth * amount;

        double attack = profileManager.getAnyProfile(healer).getTotalAttack();
        amount += attack/20;

        amount *= multiplierForCrit;
        amount *= multiplierForHealerBonus;

        return amount;
    }

    public double calculateDamage(Player player, LivingEntity entity, String type, double damage, boolean crit){

        if(buffAndDebuffManager.getImmune().getImmune(entity)){
            return 0.0;
        }

        Profile playerProfile = profileManager.getAnyProfile(player);
        Profile enemyProfile = profileManager.getAnyProfile(entity);

        double multiplierForCrit = 1;

        if(crit){
            multiplierForCrit = 1.5;
            //maybe spawn particles on a crit here
        }

        double attack;
        double defence;

        double attackBonus = buffAndDebuffManager.getAttackBuffAmount(player);

        if(entity instanceof Player){

            if(type.equalsIgnoreCase("Physical")){

                attack = playerProfile.getTotalAttack() + attackBonus;
                defence = enemyProfile.getTotalDefense();

                if(buffAndDebuffManager.getPierceBuff().getIfPierceBuff(player)){
                    defence = defence * .75;
                }

                damage = (damage * multiplierForCrit)
                        * ((attack) / (defence));
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

                if(buffAndDebuffManager.getPierceBuff().getIfPierceBuff(player)){
                    defence = defence * .75;
                }

                damage = (damage * multiplierForCrit)
                        * (attack / defence);

            }

            if(type.equalsIgnoreCase("Magical")){

                attack = playerProfile.getTotalAttack();
                defence = enemyProfile.getStats().getMagic_Defense();

                damage = (damage * multiplierForCrit)
                        * (attack / defence);
            }
        }

        damage = damage * buffAndDebuffManager.getTotalDamageMultipliers(player, entity);
        damage = damage + buffAndDebuffManager.getTotalDamageAddition(player, entity);

        if(buffAndDebuffManager.getBlocking().getIfBlocking(entity)){
            damage*=.5;
        }

        return damage;
    }


    public double calculateGettingDamaged(Player player, LivingEntity entity, String type, double damage){

        if(buffAndDebuffManager.getImmune().getImmune(player)){
            return 0.0;
        }

        Profile playerProfile = profileManager.getAnyProfile(player);
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

            if(buffAndDebuffManager.getPierceBuff().getIfPierceBuff(entity)){
                defence = defence * .75;
            }

            damage = (damage * multiplierForCrit)
                    * (attack / defence);

        }

        if(type.equalsIgnoreCase("Magical")){

            attack = enemyProfile.getStats().getAttack();
            defence = playerProfile.getTotalMagicDefense();

            damage = (damage * multiplierForCrit)
                    * (attack / defence);
        }

        damage = damage * buffAndDebuffManager.getTotalDamageMultipliers(entity, player);

        if(buffAndDebuffManager.getBlocking().getIfBlocking(player)){
            damage*=.5;
        }

        return damage;
    }


}
