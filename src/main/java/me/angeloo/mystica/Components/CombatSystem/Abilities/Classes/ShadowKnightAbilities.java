package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes;


import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.ShadowKnight.*;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Enums.SubClass;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class ShadowKnightAbilities {

    private final ProfileManager profileManager;

    private final Energy energy;
    private final ShadowKnightBasic shadowKnightBasic;
    private final Infection infection;
    private final SpiritualAttack spiritualAttack;
    private final Annihilation annihilation;
    private final BloodShield bloodShield;
    private final SoulReap soulReap;
    private final BurialGround burialGround;
    private final Bloodsucker bloodsucker;
    private final ShadowGrip shadowGrip;
    private final SpectralSteed spectralSteed;
    private final Soulcrack soulcrack;

    public ShadowKnightAbilities(Mystica main, AbilityManager manager){
        profileManager = main.getProfileManager();
        energy = new Energy(main, manager);
        shadowKnightBasic = new ShadowKnightBasic(main);
        infection = new Infection(main, manager);
        soulReap = new SoulReap(main, manager, this);
        spiritualAttack = new SpiritualAttack(main, manager, this);
        annihilation = new Annihilation(main, this, manager);
        bloodShield = new BloodShield(main, this, manager);
        burialGround = new BurialGround(main, manager, this);
        bloodsucker = new Bloodsucker(main, manager, this);
        shadowGrip = new ShadowGrip(main, manager, this);
        spectralSteed = new SpectralSteed(main, manager);
        soulcrack = new Soulcrack(main, manager, this);
    }

    public void useShadowKnightAbility(LivingEntity caster, int abilityNumber){

        switch (abilityNumber) {
            case 1 -> {
                infection.use(caster);
                return;
            }
            case 2 -> {
                spiritualAttack.use(caster);
                return;
            }
            case 3 -> {
                burialGround.use(caster);
                return;
            }
            case 4 -> {
                bloodsucker.use(caster);
                return;
            }
            case 5 -> {
                soulReap.use(caster);
                return;
            }
            case 6 -> {
                shadowGrip.use(caster);
                return;
            }
            case 7 -> {
                spectralSteed.use(caster);
                return;
            }
            case 8 -> {
                soulcrack.use(caster);
                return;
            }
        }
    }

    public void useShadowKnightUltimate(LivingEntity caster){

        SubClass subclass = profileManager.getAnyProfile(caster).getPlayerSubclass();

        switch (subclass) {
            case Blood -> {
                bloodShield.use(caster);
                return;
            }
            case Doom -> {
                annihilation.use(caster);
                return;
            }
        }
    }

    public void useShadowKnightBasic(LivingEntity caster){
        shadowKnightBasic.useBasic(caster);
    }

    public Infection getInfection(){return infection;}
    public SoulReap getSoulReap(){return soulReap;}
    public BloodShield getBloodShield(){return bloodShield;}
    public SpiritualAttack getSpiritualAttack(){return spiritualAttack;}
    public BurialGround getBurialGround(){return burialGround;}
    public Bloodsucker getBloodsucker(){return bloodsucker;}
    public ShadowGrip getShadowGrip(){return shadowGrip;}
    public Soulcrack getSoulcrack(){return soulcrack;}
    public Annihilation getAnnihilation(){return annihilation;}
    public ShadowKnightBasic getShadowKnightBasic(){return shadowKnightBasic;}
    public Energy getEnergy(){return energy;}

}
