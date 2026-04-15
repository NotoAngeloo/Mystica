package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Warrior.*;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Enums.SubClass;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class WarriorAbilities {

    private final ProfileManager profileManager;

    private final Rage rage;
    private final WarriorBasic warriorBasic;
    private final LavaQuake lavaQuake;
    private final SearingChains searingChains;
    private final TempestRage tempestRage;
    private final MeteorCrater meteorCrater;
    private final AnvilDrop anvilDrop;
    private final FlamingSigil flamingSigil;
    private final BurningBlessing burningBlessing;
    private final MagmaSpikes magmaSpikes;
    private final GladiatorHeart gladiatorHeart;
    private final DeathGaze deathGaze;

    public WarriorAbilities(Mystica main, AbilityManager manager){
        profileManager = main.getProfileManager();

        rage = new Rage(main, manager);
        warriorBasic = new WarriorBasic(main, this);
        lavaQuake = new LavaQuake(main, manager, this);
        searingChains = new SearingChains(main, manager, this);
        tempestRage = new TempestRage(main, manager, this);
        meteorCrater = new MeteorCrater(main, manager, this);
        anvilDrop = new AnvilDrop(main, manager, this);
        flamingSigil = new FlamingSigil(main, manager);
        burningBlessing = new BurningBlessing(main, manager);
        magmaSpikes = new MagmaSpikes(main, manager, this);
        gladiatorHeart = new GladiatorHeart(main, manager);
        deathGaze = new DeathGaze(main, this,manager);
    }

    public void useWarriorAbility(LivingEntity caster, int abilityNumber){

        switch (abilityNumber) {
            case 1 -> {
                lavaQuake.use(caster);
                return;
            }
            case 2 -> {
                searingChains.use(caster);
                return;
            }
            case 3 -> {
                tempestRage.use(caster);
                return;
            }
            case 4 -> {
                meteorCrater.use(caster);
                return;
            }
            case 5 -> {
                anvilDrop.use(caster);
                return;
            }
            case 6 -> {
                flamingSigil.use(caster);
                return;
            }
            case 7 -> {
                magmaSpikes.use(caster);
                return;
            }
            case 8 -> {
                burningBlessing.use(caster);
                return;
            }
        }
    }

    public void useWarriorUltimate(LivingEntity caster){

        SubClass subclass = profileManager.getAnyProfile(caster).getPlayerSubclass();

        switch (subclass) {
            case Gladiator -> {
                gladiatorHeart.use(caster);
                return;
            }
            case Executioner -> {
                deathGaze.use(caster);
                return;
            }
        }
    }

    public void useWarriorBasic(LivingEntity caster){
        warriorBasic.useBasic(caster);
    }


    public SearingChains getSearingChains(){return searingChains;}
    public LavaQuake getLavaQuake(){return lavaQuake;}
    public TempestRage getTempestRage(){return tempestRage;}
    public MeteorCrater getMeteorCrater(){return meteorCrater;}
    public AnvilDrop getAnvilDrop(){return anvilDrop;}
    public FlamingSigil getFlamingSigil(){return flamingSigil;}
    public MagmaSpikes getMagmaSpikes(){return magmaSpikes;}
    public BurningBlessing getBurningBlessing(){return burningBlessing;}
    public DeathGaze getDeathGaze(){return deathGaze;}
    public GladiatorHeart getGladiatorHeart(){return gladiatorHeart;}
    public WarriorBasic getWarriorBasic(){return warriorBasic;}
    public Rage getRage(){return rage;}
}
