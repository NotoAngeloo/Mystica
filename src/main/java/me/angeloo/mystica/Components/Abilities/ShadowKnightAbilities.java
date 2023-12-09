package me.angeloo.mystica.Components.Abilities;


import me.angeloo.mystica.Components.Abilities.ShadowKnight.Annihilation;
import me.angeloo.mystica.Components.Abilities.ShadowKnight.Infection;
import me.angeloo.mystica.Components.Abilities.ShadowKnight.ShadowKnightBasic;
import me.angeloo.mystica.Components.Abilities.ShadowKnight.SpiritualAttack;
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

    public ShadowKnightAbilities(Mystica main, AbilityManager manager){
        profileManager = main.getProfileManager();
        shadowKnightBasic = new ShadowKnightBasic(main, manager);
        infection = new Infection(main, manager);
        spiritualAttack = new SpiritualAttack(main, manager);
        annihilation = new Annihilation(main, manager, this);
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
                return;
            }
            case 4:{
                return;
            }
            case 5:{
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

            case 4:

            case 5:

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

}
