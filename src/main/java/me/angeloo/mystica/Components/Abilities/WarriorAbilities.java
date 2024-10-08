package me.angeloo.mystica.Components.Abilities;

import me.angeloo.mystica.Components.Abilities.Warrior.*;
import me.angeloo.mystica.Managers.AbilityManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
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
        warriorBasic = new WarriorBasic(main, manager, this);
        lavaQuake = new LavaQuake(main, manager, this);
        searingChains = new SearingChains(main, manager, this);
        tempestRage = new TempestRage(main, manager, this);
        meteorCrater = new MeteorCrater(main, manager, this);
        anvilDrop = new AnvilDrop(main, manager, this);
        flamingSigil = new FlamingSigil(main, manager);
        burningBlessing = new BurningBlessing(main, manager);
        magmaSpikes = new MagmaSpikes(main, manager, this);
        gladiatorHeart = new GladiatorHeart(main, manager);
        deathGaze = new DeathGaze(main, manager, this);
    }

    public void useWarriorAbility(LivingEntity caster, int abilityNumber){

        switch (abilityNumber){
            case 1:{
                lavaQuake.use(caster);
                return;
            }
            case 2:{
                searingChains.use(caster);
                return;
            }
            case 3:{
                tempestRage.use(caster);
                return;
            }
            case 4:{
                meteorCrater.use(caster);
                return;
            }
            case 5:{
                anvilDrop.use(caster);
                return;
            }
            case 6:{
                flamingSigil.use(caster);
                return;
            }
            case 7:{
                magmaSpikes.use(caster);
                return;
            }
            case 8:{
                burningBlessing.use(caster);
                return;
            }
        }
    }

    public void useWarriorUltimate(LivingEntity caster){

        String subclass = profileManager.getAnyProfile(caster).getPlayerSubclass();

        switch (subclass.toLowerCase()){
            case "gladiator":{
                gladiatorHeart.use(caster);
                return;
            }
            case "executioner":{
                deathGaze.use(caster);
                return;
            }
        }
    }

    public void useWarriorBasic(LivingEntity caster){
        warriorBasic.useBasic(caster);
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
                return magmaSpikes.getCooldown(player);
            case 8:
                return burningBlessing.getCooldown(player);
        }

        return 0;
    }

    public int getUltimateCooldown(Player player){
        String subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        switch (subclass.toLowerCase()){
            case "gladiator":
                return gladiatorHeart.getCooldown(player);
            case "executioner":
                return deathGaze.getCooldown(player);

        }

        return 0;
    }

    public void resetCooldowns(LivingEntity caster){
        anvilDrop.resetCooldown(caster);
        burningBlessing.resetCooldown(caster);
        deathGaze.resetCooldown(caster);
        flamingSigil.resetCooldown(caster);
        gladiatorHeart.resetCooldown(caster);
        lavaQuake.resetCooldown(caster);
        magmaSpikes.resetCooldown(caster);
        meteorCrater.resetCooldown(caster);
        searingChains.resetCooldown(caster);
        tempestRage.resetCooldown(caster);
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
