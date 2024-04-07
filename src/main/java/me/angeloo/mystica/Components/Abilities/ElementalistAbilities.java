package me.angeloo.mystica.Components.Abilities;

import me.angeloo.mystica.Components.Abilities.Elementalist.*;
import me.angeloo.mystica.Managers.AbilityManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.entity.Player;

public class ElementalistAbilities {

    private final ProfileManager profileManager;
    private final FieryWing fieryWing;
    private final ConjuringForce conjuringForce;
    private final ElementalBreath elementalBreath;
    private final FieryMagma fieryMagma;
    private final DescendingInferno descendingInferno;
    private final ElementalMatrix elemental_matrix;
    private final IceBolt iceBolt;
    private final DragonBreathing dragonBreathing;
    private final WindrushForm windrushForm;
    private final WindWall windWall;
    private final ElementalistBasic elementalistBasic;

    public ElementalistAbilities(Mystica main, AbilityManager manager){
        profileManager = main.getProfileManager();
        fieryWing = new FieryWing(main, manager);
        conjuringForce = new ConjuringForce(main, manager);
        elementalBreath = new ElementalBreath(main, manager);
        fieryMagma = new FieryMagma(main, manager, this);
        descendingInferno = new DescendingInferno(main, manager, this);
        elemental_matrix = new ElementalMatrix(main, manager);
        iceBolt = new IceBolt(main, manager, this);
        dragonBreathing = new DragonBreathing(main, manager, this);
        windrushForm = new WindrushForm(main, manager);
        windWall = new WindWall(main, manager);
        elementalistBasic = new ElementalistBasic(main, manager);
    }

    public void useElementalistAbility(Player player, int abilityNumber){

        switch (abilityNumber){
            case 1:
                iceBolt.use(player);
                return;
            case 2:{
                fieryMagma.use(player);
                return;
            }
            case 3:{
                descendingInferno.use(player);
                return;
            }
            case 4:{
                windrushForm.use(player);
                return;
            }
            case 5:{
                windWall.use(player);
                return;
            }
            case 6:{
                dragonBreathing.use(player);
                return;
            }
            case 7:
                elementalBreath.use(player);
                return;
            case 8:
                elemental_matrix.use(player);
                return;
        }

    }

    public void useElementalistUltimate(Player player){

        String subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        switch (subclass.toLowerCase()){
            case "pyromancer":{
                fieryWing.use(player);
                return;
            }
            case "conjurer":{
                conjuringForce.use(player);
                return;
            }
        }

    }

    public void useElementalistBasic(Player player){



        elementalistBasic.use(player);
    }

    public int getAbilityCooldown(Player player, int abilityNumber){

        switch (abilityNumber){
            case 1:
                return iceBolt.getCooldown(player);
            case 2:
                return fieryMagma.getCooldown(player);
            case 3:
                return descendingInferno.getCooldown(player);
            case 4:
                return windrushForm.getCooldown(player);
            case 5:
                return windWall.getCooldown(player);
            case 6:
                return dragonBreathing.getCooldown(player);
            case 7:
                return elementalBreath.getCooldown(player);
            case 8:
                return elemental_matrix.getCooldown(player);
        }

        return 0;
    }


    public int getUltimateCooldown(Player player){
        String subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        switch (subclass.toLowerCase()){
            case "pyromancer":{
                return fieryWing.getCooldown(player);
            }
            case "conjurer":{
                return conjuringForce.getCooldown(player);
            }
        }

        return 0;
    }

    public FieryWing getFieryWing(){
        return fieryWing;
    }
    public ElementalBreath getElementalBreath() {
        return elementalBreath;
    }
    public IceBolt getIceBolt(){return iceBolt;}
    public DescendingInferno getDescendingInferno(){return descendingInferno;}
    public FieryMagma getFieryMagma(){return fieryMagma;}
    public WindrushForm getWindrushForm() {
        return windrushForm;
    }
    public WindWall getWindWall() {
        return windWall;
    }
    public DragonBreathing getDragonBreathing(){return dragonBreathing;}
    public ElementalMatrix getElemental_matrix(){return elemental_matrix;}
    public ConjuringForce getConjuringForce(){return conjuringForce;}
    public ElementalistBasic getElementalistBasic(){return elementalistBasic;}
}
