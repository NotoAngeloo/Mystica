package me.angeloo.mystica.Components.Abilities;


import me.angeloo.mystica.Components.Abilities.ShadowKnight.*;
import me.angeloo.mystica.Managers.AbilityManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
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
        energy = new Energy();
        shadowKnightBasic = new ShadowKnightBasic(main, manager);
        infection = new Infection(main, manager);
        soulReap = new SoulReap(main, manager, this);
        spiritualAttack = new SpiritualAttack(main, manager, this);
        annihilation = new Annihilation(main, manager, this);
        bloodShield = new BloodShield(main, manager, this);
        burialGround = new BurialGround(main, manager, this);
        bloodsucker = new Bloodsucker(main, manager, this);
        shadowGrip = new ShadowGrip(main, manager, this);
        spectralSteed = new SpectralSteed(main, manager);
        soulcrack = new Soulcrack(main, manager, this);
    }

    public void useShadowKnightAbility(LivingEntity caster, int abilityNumber){

        switch (abilityNumber){
            case 1:{
                infection.use(caster);
                return;
            }
            case 2:{
                spiritualAttack.use(caster);
                return;
            }
            case 3:{
                burialGround.use(caster);
                return;
            }
            case 4:{
                bloodsucker.use(caster);
                return;
            }
            case 5:{
                soulReap.use(caster);
                return;
            }
            case 6:{
                shadowGrip.use(caster);
                return;
            }
            case 7:{
                spectralSteed.use(caster);
                return;
            }
            case 8:{
                soulcrack.use(caster);
                return;
            }
        }
    }

    public void useShadowKnightUltimate(LivingEntity caster){

        String subclass = profileManager.getAnyProfile(caster).getPlayerSubclass();

        switch (subclass.toLowerCase()){
            case "blood":{
                bloodShield.use(caster);
                return;
            }
            case "doom":{
                annihilation.use(caster);
                return;
            }
        }
    }

    public void useShadowKnightBasic(LivingEntity caster){
        shadowKnightBasic.useBasic(caster);
    }

    public void regenEnergy(LivingEntity caster){energy.regenEnergyNaturally(caster);}

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
                return shadowGrip.getCooldown(player);
            case 7:
                return spectralSteed.getCooldown(player);
            case 8:
                return soulcrack.getCooldown(player);
        }

        return 0;
    }

    public int getUltimateCooldown(Player player){
        String subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        switch (subclass.toLowerCase()){
            case "blood":
                return bloodShield.getCooldown(player);
            case "doom":
                return annihilation.getCooldown(player);
        }

        return 0;
    }

    public void resetCooldowns(LivingEntity caster){
        annihilation.resetCooldown(caster);
        bloodShield.resetCooldown(caster);
        bloodsucker.resetCooldown(caster);
        burialGround.resetCooldown(caster);
        infection.resetCooldown(caster);
        shadowGrip.resetCooldown(caster);
        soulcrack.resetCooldown(caster);
        soulReap.resetCooldown(caster);
        spectralSteed.resetCooldown(caster);
        spiritualAttack.resetCooldown(caster);
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
