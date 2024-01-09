package me.angeloo.mystica.Components.Abilities;

import me.angeloo.mystica.Components.Abilities.Warrior.*;
import me.angeloo.mystica.Managers.AbilityManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.entity.Player;

public class WarriorAbilities {

    private final ProfileManager profileManager;

    private final WarriorBasic warriorBasic;
    private final LavaQuake lavaQuake;
    private final SearingChains searingChains;
    private final TempestRage tempestRage;
    private final MeteorCrater meteorCrater;
    private final AnvilDrop anvilDrop;
    private final FlamingSigil flamingSigil;
    private final BurningBlessing burningBlessing;

    public WarriorAbilities(Mystica main, AbilityManager manager){
        profileManager = main.getProfileManager();

        warriorBasic = new WarriorBasic(main, manager);
        lavaQuake = new LavaQuake(main, manager);
        searingChains = new SearingChains(main, manager);
        tempestRage = new TempestRage(main, manager);
        meteorCrater = new MeteorCrater(main, manager);
        anvilDrop = new AnvilDrop(main, manager);
        flamingSigil = new FlamingSigil(main, manager);
        burningBlessing = new BurningBlessing(main, manager);
    }

    public void useWarriorAbility(Player player, int abilityNumber){

        switch (abilityNumber){
            case 1:{
                lavaQuake.use(player);
                return;
            }
            case 2:{
                searingChains.use(player);
                return;
            }
            case 3:{
                tempestRage.use(player);
                return;
            }
            case 4:{
                meteorCrater.use(player);
                return;
            }
            case 5:{
                anvilDrop.use(player);
                return;
            }
            case 6:{
                flamingSigil.use(player);
                return;
            }
            case 7:{
                return;
            }
            case 8:{
                burningBlessing.use(player);
                return;
            }
        }
    }

    public void useWarriorUltimate(Player player){

        String subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        switch (subclass.toLowerCase()){
            case "gladiator":{
                return;
            }
            case "executioner":{
                return;
            }
        }
    }

    public void useWarriorBasic(Player player){
        warriorBasic.useBasic(player);
    }

    public int getAbilityCooldown(Player player, int abilityNumber){

        switch (abilityNumber){
            case 1:
                return lavaQuake.getCooldown(player);
            case 2:
                return searingChains.getCooldown(player);
            case 3:
                return tempestRage.getCooldown(player);
            case 4:
                return meteorCrater.getCooldown(player);
            case 5:
                return anvilDrop.getCooldown(player);
            case 6:
                return flamingSigil.getCooldown(player);
            case 7:

            case 8:
                return burningBlessing.getCooldown(player);
        }

        return 0;
    }

    public int getUltimateCooldown(Player player){
        String subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        switch (subclass.toLowerCase()){
            case "gladiator":

            case "executioner":


        }

        return 0;
    }

}
