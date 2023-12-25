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
    private final DpsManager dpsManager;

    private final Map<UUID, Boolean> seeingRawDamage;

    public DamageCalculator(Mystica main){
        profileManager = main.getProfileManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        dpsManager = main.getDpsManager();
        seeingRawDamage = new HashMap<>();
    }

    public boolean checkIfCrit(Player player, int bonus){

        Profile playerProfile = profileManager.getAnyProfile(player);

        int random = (int) (Math.random() * 100) + 1;

        bonus = bonus+ buffAndDebuffManager.getCritBuffAmount(player);

        if(random <= (playerProfile.getTotalCrit()) + bonus){
            buffAndDebuffManager.getWellCrit().removeBonus(player);
        }

        return random <= (playerProfile.getTotalCrit()) + bonus;
    }

    public Double calculateDamage(Player player, LivingEntity entity, String type, Double damage, boolean crit){

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

        if(entity instanceof Player){

            if(type.equalsIgnoreCase("Physical")){

                attack = playerProfile.getTotalAttack();
                defence = enemyProfile.getTotalDefense();

                damage = (damage * multiplierForCrit)
                        * ((attack) / (defence));
            }

            if(type.equalsIgnoreCase("Magical")){

                attack = playerProfile.getTotalMagic();
                defence = enemyProfile.getTotalMagicDefense();

                damage = (damage * multiplierForCrit)
                        * (attack / defence);
            }

        }

        if(!(entity instanceof Player)){

            if(type.equalsIgnoreCase("Physical")){

                attack = playerProfile.getTotalAttack();
                defence = enemyProfile.getStats().getDefense();

                damage = (damage * multiplierForCrit)
                        * (attack / defence);

            }

            if(type.equalsIgnoreCase("Magical")){

                attack = playerProfile.getTotalMagic();
                defence = enemyProfile.getStats().getMagic_Defense();

                damage = (damage * multiplierForCrit)
                        * (attack / defence);
            }
        }

        damage = damage * buffAndDebuffManager.getTotalDamageMultipliers(player, entity);
        damage = damage + buffAndDebuffManager.getTotalDamageAddition(player, entity);

        dpsManager.addDamageToDamageDealt(player, damage);
        dpsManager.setPlayerDps(player);

        //addding buffs here is easier

        if(seeingRawDamage.containsKey(player.getUniqueId())){

            if(seeingRawDamage.get(player.getUniqueId())){
                player.sendMessage("you deal " + damage);
            }

        }


        return damage;
    }


    public double calculateGettingDamaged(Player player, LivingEntity entity, String type, Double damage){

        if(buffAndDebuffManager.getImmune().getImmune(player)){
            return 0.0;
        }

        Profile playerProfile = profileManager.getAnyProfile(player);
        Profile enemyProfile = profileManager.getAnyProfile(entity);

        double attack;
        double defence;

        double multiplierForCrit = 1;
        int random = (int) (Math.random() * 100) + 1;

        if(random <= enemyProfile.getStats().getCrit()){
            multiplierForCrit = 1.5;
        }


        if(type.equalsIgnoreCase("Physical")){

            attack = enemyProfile.getStats().getAttack();
            defence = playerProfile.getTotalDefense();

            damage = (damage * multiplierForCrit)
                    * (attack / defence);

        }

        if(type.equalsIgnoreCase("Magical")){

            attack = enemyProfile.getStats().getMagic();
            defence = playerProfile.getTotalMagicDefense();

            damage = (damage * multiplierForCrit)
                    * (attack / defence);
        }

        damage = damage * buffAndDebuffManager.getTotalDamageMultipliers(entity, player);

        if(seeingRawDamage.containsKey(player.getUniqueId())){

            if(seeingRawDamage.get(player.getUniqueId())){
                player.sendMessage("you take " + damage);
            }

        }


        return damage;
    }

    public void toggleSeeingRawDamage(Player player){

        if(!seeingRawDamage.containsKey(player.getUniqueId())){
            seeingRawDamage.put(player.getUniqueId(), false);
        }

        if(seeingRawDamage.get(player.getUniqueId())){
            seeingRawDamage.put(player.getUniqueId(), false);
            player.sendMessage("see all damage dealt toggled false");
            return;
        }

        if(!seeingRawDamage.get(player.getUniqueId())){
            seeingRawDamage.put(player.getUniqueId(), true);
            player.sendMessage("see all damage dealt toggled true");
        }

    }
}
