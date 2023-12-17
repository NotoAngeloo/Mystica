package me.angeloo.mystica.Components.Abilities;


import me.angeloo.mystica.Components.Abilities.ShadowKnight.*;
import me.angeloo.mystica.Managers.AbilityManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.entity.Player;

public class ShadowKnightAbilities {

    private final ProfileManager profileManager;

    private final ShadowKnightBasic shadowKnightBasic;
    private final Infection infection;
    private final SpiritualAttack spiritualAttack;
    private final Annihilation annihilation;
    private final SoulReap soulReap;
    private final BurialGround burialGround;
    private final Bloodsucker bloodsucker;

    public ShadowKnightAbilities(Mystica main, AbilityManager manager){
        profileManager = main.getProfileManager();
        shadowKnightBasic = new ShadowKnightBasic(main, manager);
        infection = new Infection(main, manager);
        soulReap = new SoulReap(main, manager, this);
        spiritualAttack = new SpiritualAttack(main, manager, this);
        annihilation = new Annihilation(main, manager, this);
        burialGround = new BurialGround(main, manager);
        bloodsucker = new Bloodsucker(main, manager);
    }

    public void useShadowKnightAbility(Player player, int abilityNumber){

        switch (abilityNumber){
            case 1:{
                infection.use(player);
                return;
            }
            case 2:{
                spiritualAttack.use(player);
                return;
            }
            case 3:{
                burialGround.use(player);
                return;
            }
            case 4:{
                bloodsucker.use(player);
                return;
            }
            case 5:{
                soulReap.use(player);
                return;
            }
            case 6:{
                return;
            }
            case 7:{
                return;
            }
            case 8:{
                return;
            }
        }
    }

    public void useShadowKnightUltimate(Player player){

        String subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        switch (subclass.toLowerCase()){
            case "blood":{
                return;
            }
            case "doom":{
                annihilation.use(player);
                return;
            }
        }
    }

    public void useShadowKnightBasic(Player player){
        shadowKnightBasic.useBasic(player);
    }

    public int getAbilityCooldown(Player player, int abilityNumber){

        switch (abilityNumber){
            case 1:
                return infection.getCooldown(player);
            case 2:
                return spiritualAttack.getCooldown(player);
            case 3:
                return burialGround.getCooldown(player);
            case 4:
                return bloodsucker.getCooldown(player);
            case 5:
                return soulReap.getCooldown(player);
            case 6:

            case 7:

            case 8:

        }

        return 0;
    }

    public int getUltimateCooldown(Player player){
        String subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        switch (subclass.toLowerCase()){
            case "blood":

            case "doom":
                return annihilation.getCooldown(player);
        }

        return 0;
    }

    public Infection getInfection(){return infection;}
    public SoulReap getSoulReap(){return soulReap;}

}
