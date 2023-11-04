package me.angeloo.mystica.Components.Abilities;

import me.angeloo.mystica.Components.Abilities.Mystic.*;
import me.angeloo.mystica.Managers.AbilityManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.entity.Player;


public class MysticAbilities {

    private final ProfileManager profileManager;

    private final EvilSpirit evilSpirit;
    private final PlagueCurse plagueCurse;
    private final ChaosVoid chaosVoid;
    private final ShadowOfDarkness shadowOfDarkness;
    private final Warp warp;
    private final SpiritualDescent spiritualDescent;
    private final MysticBasic mysticBasic;

    public MysticAbilities(Mystica main, AbilityManager manager){
        profileManager = main.getProfileManager();
        evilSpirit = new EvilSpirit(main, manager);
        plagueCurse = new PlagueCurse(main, manager, this);
        chaosVoid = new ChaosVoid(main, manager);
        shadowOfDarkness = new ShadowOfDarkness(main, manager, this);
        warp = new Warp(main, manager);
        spiritualDescent = new SpiritualDescent(main, manager, this);
        mysticBasic = new MysticBasic(main, manager, this);
    }

    public void useMysticAbility(Player player, int abilityNumber){

        if(evilSpirit.getIfEvilSpirit(player)){
            return;
        }

        String subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        if(subclass.equalsIgnoreCase("chaos")){

            switch (abilityNumber){
                case 1:{
                    chaosVoid.use(player);
                    return;
                }
                case 2:{
                    plagueCurse.use(player);
                    return;
                }
                case 3:{
                    shadowOfDarkness.use(player);
                    return;
                }
                case 4:{
                    warp.use(player);
                    return;
                }
                case 5:{
                    spiritualDescent.use(player);
                    return;
                }
            }

        }

        switch (abilityNumber){
            case 4:{
                warp.use(player);
                return;
            }
        }

    }

    public void useMysticUltimate(Player player){

        if(evilSpirit.getIfEvilSpirit(player)){
            return;
        }

        String subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        switch (subclass.toLowerCase()){
            case "chaos":{
                evilSpirit.use(player);
                return;
            }
            case "arcane master":{

                return;
            }
            case "shepard":{

                return;
            }
        }
    }

    public void useMysticBasic(Player player){

        mysticBasic.useBasic(player);
    }

    public int getAbilityCooldown(Player player, int abilityNumber){

        String subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        if(subclass.equalsIgnoreCase("chaos")){

            switch (abilityNumber){
                case 1:
                    return chaosVoid.getCooldown(player);
                case 2:
                    return plagueCurse.getCooldown(player);
                case 3:
                    return shadowOfDarkness.getCooldown(player);
                case 4:
                    return warp.getCooldown(player);
                case 5:
                    return spiritualDescent.getCooldown(player);
            }

        }

        switch (abilityNumber){
            case 4:
                return warp.getCooldown(player);
        }

        return 0;
    }

    public int getUltimateCooldown(Player player){

        String subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        switch (subclass.toLowerCase()){
            case "chaos":{
                return evilSpirit.getIfReady(player);
            }
            case "arcane master":{


            }
            case "shepard":{


            }
        }

        return 0;
    }


    public EvilSpirit getEvilSpirit(){return evilSpirit;}

}
