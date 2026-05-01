package me.angeloo.mystica.Components.EntityBehavior;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityResolver;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.DeathManager;
import me.angeloo.mystica.Components.CombatSystem.FakePlayerTargetManager;
import me.angeloo.mystica.Components.Parties.MysticaPartyManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Enums.PlayerClass;
import me.angeloo.mystica.Utility.Enums.Role;
import me.angeloo.mystica.Utility.Enums.SubClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;


public class FakePlayerAiManager {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final MysticaPartyManager mysticaPartyManager;
    private final FakePlayerTargetManager fakePlayerTargetManager;
    private final AbilityManager abilityManager;
    private final AbilityResolver abilityResolver;
    private final StatusEffectManager statusEffectManager;

    private final Map<UUID, BukkitTask> aiTaskMap = new HashMap<>();
    private final Map<UUID, Boolean> hardStopMap = new HashMap<>();

    private final Map<UUID, Boolean> cautionMap = new HashMap<>();
    private final Map<UUID, Boolean> needToInterrupt = new HashMap<>();

    public FakePlayerAiManager(Mystica main){
        this.main = main;
        mysticaPartyManager = main.getMysticaPartyManager();
        profileManager = main.getProfileManager();
        abilityManager = main.getAbilityManager();
        fakePlayerTargetManager = main.getFakePlayerTargetManager();
        abilityResolver = abilityManager.getAbilityResolver();
        statusEffectManager = main.getStatusEffectManager();
    }


    public void signal(LivingEntity companion, String signal){

        if(MythicBukkit.inst().getAPIHelper().isMythicMob(companion.getUniqueId())){
            AbstractEntity abstractEntity = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(companion).getEntity();
            MythicBukkit.inst().getAPIHelper().getMythicMobInstance(companion).signalMob(abstractEntity, signal);
        }

        if(signal.equalsIgnoreCase("attack")){

            if(hardStopMap.containsKey(companion.getUniqueId())){
                return;
            }

            if(aiTaskMap.containsKey(companion.getUniqueId())){
                return;
            }

            switch (profileManager.getAnyProfile(companion).getPlayerClass()) {
                case Paladin -> startTemplarRotation(companion);
                case Ranger -> startTamerRotation(companion);
                case Mystic -> startShepardRotation(companion);
                case Warrior -> startExecutionerRotation(companion);
                case Elementalist -> startConjurerRotation(companion);
            }
        }

    }

    private void startTamerRotation(LivingEntity companion){

        profileManager.getAnyProfile(companion).setIfInCombat(true);

        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(this.isCancelled()){
                    stopAiTask(companion.getUniqueId());
                    return;
                }

                if(profileManager.getAnyProfile(companion).getIfDead()){
                    stopAiTask(companion.getUniqueId());
                    return;
                }

                if(hardStopMap.containsKey(companion.getUniqueId())){
                    this.cancel();
                    return;
                }

                LivingEntity target = fakePlayerTargetManager.getTarget(companion);

                if(profileManager.getAnyProfile(target).getIfDead() || profileManager.getAnyProfile(target).getIfDead()){
                    stopAiTask(companion.getUniqueId());
                    return;
                }

                List<AiNode> tree = buildTamerTree(companion);

                for(AiNode node : tree){

                    if(node.tryExecute()){
                        return;
                    }

                }


            }
        }.runTaskTimerAsynchronously(main, 0, 10);

        aiTaskMap.put(companion.getUniqueId(), task);
        profileManager.setCompanionCombat(companion.getUniqueId());
    }

    private void startTemplarRotation(LivingEntity companion){


        profileManager.getAnyProfile(companion).setIfInCombat(true);

        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if (this.isCancelled()) {
                    stopAiTask(companion.getUniqueId());
                    return;
                }

                var profile = profileManager.getAnyProfile(companion);

                if (profile.getIfDead()) {
                    stopAiTask(companion.getUniqueId());
                    return;
                }

                if (hardStopMap.containsKey(companion.getUniqueId())) {
                    this.cancel();
                    return;
                }

                LivingEntity target = fakePlayerTargetManager.getTarget(companion);

                if (target == null || profileManager.getAnyProfile(target).getIfDead()) {
                    stopAiTask(companion.getUniqueId());
                    return;
                }

                List<AiNode> tree = buildTemplarTree(companion);

                for(AiNode node : tree){

                    if(node.tryExecute()){
                        return;
                    }

                }

            }
        }.runTaskTimerAsynchronously(main, 0, 10);

        aiTaskMap.put(companion.getUniqueId(), task);
        profileManager.setCompanionCombat(companion.getUniqueId());

    }

    private void startShepardRotation(LivingEntity companion){

        profileManager.getAnyProfile(companion).setIfInCombat(true);


        BukkitTask task = new BukkitRunnable(){

            @Override
            public void run(){

                if(this.isCancelled()){
                    stopAiTask(companion.getUniqueId());
                    return;
                }

                if(profileManager.getAnyProfile(companion).getIfDead()){
                    stopAiTask(companion.getUniqueId());
                    return;
                }

                if(hardStopMap.containsKey(companion.getUniqueId())){
                    this.cancel();
                    return;
                }

                List<AiNode> tree = buildShepardTree(companion);

                for(AiNode node : tree){

                    if(node.tryExecute()){
                        return;
                    }

                }

            }
        }.runTaskTimerAsynchronously(main, 0, 10);

        aiTaskMap.put(companion.getUniqueId(), task);
        profileManager.setCompanionCombat(companion.getUniqueId());

    }

    private void startExecutionerRotation(LivingEntity companion){

        profileManager.getAnyProfile(companion).setIfInCombat(true);

        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(this.isCancelled()){
                    stopAiTask(companion.getUniqueId());
                    return;
                }


                if(profileManager.getAnyProfile(companion).getIfDead()){
                    stopAiTask(companion.getUniqueId());
                    return;
                }

                if(hardStopMap.containsKey(companion.getUniqueId())){
                    this.cancel();
                    return;
                }

                //check goal before casting skills, they may want to run

                LivingEntity target = fakePlayerTargetManager.getTarget(companion);

                if(profileManager.getAnyProfile(target).getIfDead() || profileManager.getAnyProfile(target).getIfDead()){
                    stopAiTask(companion.getUniqueId());
                    return;
                }

                List<AiNode> tree = buildExecutionerTree(companion);

                for(AiNode node : tree){

                    if(node.tryExecute()){
                        return;
                    }

                }


            }
        }.runTaskTimerAsynchronously(main, 0, 10);

        aiTaskMap.put(companion.getUniqueId(), task);
        profileManager.setCompanionCombat(companion.getUniqueId());


    }

    private void startConjurerRotation(LivingEntity companion){

        profileManager.getAnyProfile(companion).setIfInCombat(true);

        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(this.isCancelled()){
                    stopAiTask(companion.getUniqueId());
                    return;
                }

                if(profileManager.getAnyProfile(companion).getIfDead()){
                    stopAiTask(companion.getUniqueId());
                    return;
                }

                if(hardStopMap.containsKey(companion.getUniqueId())){
                    this.cancel();
                    return;
                }

                //check goal before casting skills, they may want to run

                LivingEntity target = fakePlayerTargetManager.getTarget(companion);

                if(profileManager.getAnyProfile(target).getIfDead() || profileManager.getAnyProfile(target).getIfDead()){
                    stopAiTask(companion.getUniqueId());
                    return;
                }

                List<AiNode> tree = buildConjurerTree(companion);

                for(AiNode node : tree){

                    if(node.tryExecute()){
                        return;
                    }

                }

            }
        }.runTaskTimerAsynchronously(main, 0, 10);

        aiTaskMap.put(companion.getUniqueId(), task);
        profileManager.setCompanionCombat(companion.getUniqueId());

    }

    public void hardStopAiTask(UUID uuid){

        hardStopMap.put(uuid, true);

        Bukkit.getScheduler().runTask(main, ()->{

            if(aiTaskMap.containsKey(uuid)){
                aiTaskMap.get(uuid).cancel();
                profileManager.removeCompanionCombat(uuid);
            }

            Entity entity = Bukkit.getEntity(uuid);
            if(entity instanceof LivingEntity companion){
                abilityManager.interruptBasic(companion);
                profileManager.getAnyProfile(companion).setIfInCombat(false);


            }

        });

    }

    public void stopAiTask(UUID uuid){

        //Bukkit.getLogger().info("stopping companion task for " + uuid + " due to despawn");

        Bukkit.getScheduler().runTask(main, () ->{
            if(aiTaskMap.containsKey(uuid)){
                aiTaskMap.get(uuid).cancel();
                profileManager.removeCompanionCombat(uuid);
            }

            Entity entity = Bukkit.getEntity(uuid);
            if(entity instanceof LivingEntity companion){
                abilityManager.interruptBasic(companion);
                profileManager.getAnyProfile(companion).setIfInCombat(false);


            }

            Bukkit.getScheduler().runTaskLaterAsynchronously(main, ()-> aiTaskMap.remove(uuid),20);


        });



    }

    private List<AiNode> buildTemplarTree(LivingEntity companion) {

        var profile = profileManager.getAnyProfile(companion);
        var resolver = abilityResolver;

        LivingEntity target = fakePlayerTargetManager.getTarget(companion);

        double distance = target.getLocation().distance(companion.getLocation());
        int healthPercent = (int) ((profile.getCurrentHealth() / (double) profile.getTotalHealth()) * 100);

        boolean lowHp = healthPercent <= 50;
        boolean close = distance <= 5;
        boolean mid = distance <= 8;
        boolean far = distance >= 10;
        //cautious means will not jump towards enemy
        boolean aggressive = !getIfCautious(companion);

        var ult = resolver.resolveUltimate(SubClass.Templar);
        var judgement = resolver.resolve(PlayerClass.Paladin, SubClass.Templar, 8);
        var shield = resolver.resolve(PlayerClass.Paladin, SubClass.Templar, 5);
        var glory = resolver.resolve(PlayerClass.Paladin, SubClass.Templar, 6);
        var torah = resolver.resolve(PlayerClass.Paladin, SubClass.Templar, 1);
        var reigning = resolver.resolve(PlayerClass.Paladin, SubClass.Templar, 3);
        var guidance = resolver.resolve(PlayerClass.Paladin, SubClass.Templar, 2);
        var durance = resolver.resolve(PlayerClass.Paladin, SubClass.Templar, 7);

        return List.of(

                new AiNode(
                        () -> lowHp && ult.usable(companion),
                        () -> runSync(() -> ult.use(companion))
                ),

                new AiNode(
                        () -> judgement.usable(companion),
                        () -> runSync(() -> judgement.use(companion))
                ),

                new AiNode(
                        () -> far && shield.usable(companion),
                        () -> runSync(() -> shield.use(companion))
                ),

                new AiNode(
                        () -> aggressive && glory.usable(companion),
                        () -> runSync(() -> glory.use(companion))
                ),

                new AiNode(
                        () -> torah.usable(companion, target),
                        () -> runSync(() -> torah.use(companion))
                ),

                new AiNode(
                        () -> aggressive && reigning.usable(companion),
                        () -> runSync(() -> reigning.use(companion))
                ),

                new AiNode(
                        () -> !aggressive && lowHp && reigning.usable(companion),
                        () -> runSync(() -> reigning.use(companion))
                ),

                new AiNode(
                        () -> lowHp && guidance.usable(companion),
                        () -> runSync(() -> guidance.use(companion))
                ),

                new AiNode(
                        () -> !lowHp && close && guidance.usable(companion),
                        () -> runSync(() -> guidance.use(companion))
                ),

                new AiNode(
                        () -> aggressive && mid && durance.usable(companion),
                        () -> runSync(() -> durance.use(companion))
                ),

                new AiNode(
                        () -> aggressive && close,
                        () -> runSync(() -> abilityManager.useBasic(companion))
                )
        );
    }

    private List<AiNode> buildTamerTree(LivingEntity companion){

        var resolver = abilityResolver;

        LivingEntity target = fakePlayerTargetManager.getTarget(companion);

        boolean cryBuff = statusEffectManager.hasEffect(companion, "rallying_cry");

        var ult = resolver.resolveUltimate(SubClass.Tamer);
        var shadowCrows = resolver.resolve(PlayerClass.Ranger, SubClass.Tamer, 2);
        var wildSpirit = resolver.resolve(PlayerClass.Ranger, SubClass.Tamer, 7);
        var rallyingCry = resolver.resolve(PlayerClass.Ranger, SubClass.Tamer,6);
        var relentless = resolver.resolve(PlayerClass.Ranger, SubClass.Tamer, 3);
        var razorWind = resolver.resolve(PlayerClass.Ranger, SubClass.Tamer, 4);
        var blessedArrow = resolver.resolve(PlayerClass.Ranger, SubClass.Tamer, 5);
        var bitingRain = resolver.resolve(PlayerClass.Ranger, SubClass.Tamer, 1);

        return List.of(

                new AiNode(
                        ()->ult.usable(companion),
                        ()->runSync(()->ult.use(companion))
                ),

                new AiNode(
                        ()->shadowCrows.usable(companion, target),
                        ()->runSync(()->shadowCrows.use(companion))
                ),

                new AiNode(
                        ()->wildSpirit.usable(companion),
                        ()->runSync(()->wildSpirit.use(companion))
                ),

                new AiNode(
                        ()->cryBuff && blessedArrow.usable(companion, target),
                        ()->runSync(()->blessedArrow.use(companion))
                ),

                new AiNode(
                        ()->cryBuff,
                        ()->runSync(()->abilityManager.useBasic(companion))
                ),

                new AiNode(
                        ()->relentless.usable(companion, target),
                        ()->runSync(()->relentless.use(companion))
                ),

                new AiNode(
                        ()->razorWind.usable(companion, target),
                        ()->runSync(()->razorWind.use(companion))
                ),

                new AiNode(
                        ()->rallyingCry.usable(companion),
                        ()->runSync(()->rallyingCry.use(companion))
                ),

                new AiNode(
                        ()->blessedArrow.usable(companion, target),
                        ()->runSync(()->blessedArrow.use(companion))
                ),

                new AiNode(
                        ()->bitingRain.usable(companion, target),
                        ()->runSync(()->bitingRain.use(companion))
                ),

                //fallback
                new AiNode(
                        ()->true,
                        ()->runSync(()->abilityManager.useBasic(companion))
                )
        );

    }

    private List<AiNode> buildConjurerTree(LivingEntity companion) {

        var profile = profileManager.getAnyProfile(companion);
        var resolver = abilityResolver;

        LivingEntity target = fakePlayerTargetManager.getTarget(companion);

        int healthPercent = (int) ((profile.getCurrentHealth() / (double) profile.getTotalHealth()) * 100);

        int heat = abilityManager.getHeat().getHeat(companion);
        boolean breathBuff = statusEffectManager.hasEffect(companion, "elemental_breath");

        var ult = resolver.resolveUltimate(SubClass.Conjurer);
        var elementalBreath = resolver.resolve(PlayerClass.Elementalist, SubClass.Conjurer, 7);
        var elementalMatrix = resolver.resolve(PlayerClass.Elementalist,SubClass.Conjurer, 8);
        var iceBolt = resolver.resolve(PlayerClass.Elementalist, SubClass.Conjurer, 1);
        var descendingInferno = resolver.resolve(PlayerClass.Elementalist, SubClass.Conjurer, 3);
        var fieryMagma = resolver.resolve(PlayerClass.Elementalist, SubClass.Conjurer, 2);
        var dragonBreathing = resolver.resolve(PlayerClass.Elementalist, SubClass.Conjurer, 6);
        var windWall = resolver.resolve(PlayerClass.Elementalist, SubClass.Conjurer, 5);

        return List.of(

                new AiNode(
                        () ->ult.usable(companion),
                        () -> runSync(() -> ult.use(companion))
                ),

                new AiNode(
                        () -> elementalBreath.usable(companion),
                        () -> runSync(() -> elementalBreath.use(companion))
                ),

                new AiNode(
                        () -> elementalMatrix.usable(companion, target),
                        () -> runSync(() -> elementalMatrix.use(companion))
                ),

                new AiNode(
                        () -> iceBolt.usable(companion, target),
                        () -> runSync(() -> iceBolt.use(companion))
                ),

                //always use if buffed
                new AiNode(
                        () -> breathBuff && descendingInferno.usable(companion, target),
                        () -> runSync(() -> descendingInferno.use(companion))
                ),

                new AiNode(
                        () -> heat < 95 && descendingInferno.usable(companion, target),
                        () -> runSync(() -> descendingInferno.use(companion))
                ),

                new AiNode(
                        () -> heat < 95 && fieryMagma.usable(companion, target),
                        () -> runSync(() -> fieryMagma.use(companion))
                ),

                new AiNode(
                        () -> heat < 85 && dragonBreathing.usable(companion, target),
                        () -> runSync(() -> dragonBreathing.use(companion))
                ),

                new AiNode(
                        () -> healthPercent<=50 && windWall.usable(companion),
                        () -> runSync(() -> windWall.use(companion))
                ),

                new AiNode(
                        () -> true,
                        () -> runSync(() -> abilityManager.useBasic(companion))
                )
        );

    }

    private List<AiNode> buildExecutionerTree(LivingEntity companion) {

        var resolver = abilityResolver;

        LivingEntity target = fakePlayerTargetManager.getTarget(companion);

        double distance = target.getLocation().distance(companion.getLocation());

        //cautious means will not jump towards enemy
        boolean aggressive = !getIfCautious(companion);
        boolean needsInterrupt = getIfNeedToInterrupt(companion);

        var ult = resolver.resolveUltimate(SubClass.Executioner);
        var meteorCrater = resolver.resolve(PlayerClass.Warrior, SubClass.Executioner, 4);
        var flamingSigil = resolver.resolve(PlayerClass.Warrior, SubClass.Executioner, 6);
        var tempestRage = resolver.resolve(PlayerClass.Warrior, SubClass.Executioner, 3);
        var lavaQuake = resolver.resolve(PlayerClass.Warrior, SubClass.Executioner, 1);
        var magmaSpikes = resolver.resolve(PlayerClass.Warrior, SubClass.Executioner, 7);
        var anvilDrop = resolver.resolve(PlayerClass.Warrior, SubClass.Executioner, 5);

        return List.of(

                new AiNode(
                        () -> needsInterrupt && distance < 5 && meteorCrater.usable(companion),
                        () -> runSync(new BukkitRunnable() {
                            @Override
                            public void run() {
                                meteorCrater.use(companion);
                                removeInterrupt(companion);
                            }
                        })
                ),

                new AiNode(
                        () -> flamingSigil.usable(companion),
                        () -> runSync(() -> flamingSigil.use(companion))
                ),

                new AiNode(
                        () -> distance<8 && tempestRage.usable(companion),
                        () -> runSync(() -> tempestRage.use(companion))
                ),

                new AiNode(
                        () -> distance<8 && lavaQuake.usable(companion),
                        () -> runSync(() -> lavaQuake.use(companion))
                ),

                new AiNode(
                        () -> distance<5 && aggressive && magmaSpikes.usable(companion, target),
                        () -> runSync(() -> magmaSpikes.use(companion))
                ),

                new AiNode(
                        () -> aggressive && ult.usable(companion),
                        () -> runSync(() -> ult.use(companion))
                ),

                new AiNode(
                        () -> aggressive && distance>=8 && distance<15 && anvilDrop.usable(companion),
                        () -> runSync(() -> anvilDrop.use(companion))
                ),

                new AiNode(
                        () -> aggressive && distance<5,
                        () -> runSync(() -> abilityManager.useBasic(companion))
                )
        );


    }

    private List<AiNode> buildShepardTree(LivingEntity companion) {

        List<LivingEntity> party = mysticaPartyManager.getMysticaParty(companion);

        LivingEntity lowestCalc = null;
        LivingEntity reviveCalc = null;

        double totalPercent = 0;
        int count = 0;

        for (LivingEntity member : party) {
            var profile = profileManager.getAnyProfile(member);

            if (profile.getIfDead()) {
                if (reviveCalc == null && (getRole(member) == Role.Tank || member instanceof Player)) {
                    reviveCalc = member;
                }
                continue;
            }

            double hpPercent = profile.getCurrentHealth() / (double) profile.getTotalHealth();

            totalPercent += hpPercent;
            count++;

            if (lowestCalc == null || hpPercent < (
                    profileManager.getAnyProfile(lowestCalc).getCurrentHealth() /
                            (double) profileManager.getAnyProfile(lowestCalc).getTotalHealth()
            )) {
                lowestCalc = member;
            }
        }

        double averagePhp = count == 0 ? 100 : (totalPercent / count) * 100;
        double lowestHealthPercent = lowestCalc == null ? 1 :
                profileManager.getAnyProfile(lowestCalc).getCurrentHealth() /
                        (double) profileManager.getAnyProfile(lowestCalc).getTotalHealth();

        var resolver = abilityResolver;


        boolean instantBlast = statusEffectManager.hasEffect(companion, "instant_blast");

        var ult = resolver.resolveUltimate(SubClass.Shepard);
        var arcaneContract = resolver.resolve(PlayerClass.Mystic, SubClass.Shepard, 7);
        var purifyingBlast = resolver.resolve(PlayerClass.Mystic, SubClass.Shepard, 2);
        var aurora = resolver.resolve(PlayerClass.Mystic, SubClass.Shepard, 6);
        var arcaneShield = resolver.resolve(PlayerClass.Mystic, SubClass.Shepard, 1);
        var lightSigil = resolver.resolve(PlayerClass.Mystic, SubClass.Shepard, 8);


        LivingEntity toRevive = reviveCalc;
        LivingEntity lowest = lowestCalc;

        return List.of(

                new AiNode(

                        ()-> toRevive != null && arcaneContract.usable(companion, toRevive),
                        () -> runSync(()-> arcaneContract.useAsCompanion(companion, toRevive))
                ),

                new AiNode(

                        ()-> averagePhp<=50 && ult.usable(companion),
                        ()-> runSync(()->ult.use(companion))

                ),

                new AiNode(

                        ()-> averagePhp<=50 && instantBlast && purifyingBlast.usable(companion),
                        ()-> runSync(()->purifyingBlast.use(companion))

                ),

                new AiNode(

                        ()-> averagePhp<=50 && aurora.usable(companion, lowest),
                        ()-> runSync(()-> useOnTarget(companion,lowest, ()->aurora.use(companion)))

                ),

                new AiNode(

                        ()-> lowestHealthPercent<=30 && arcaneShield.usable(companion, lowest),
                        ()-> runSync(()-> useOnTarget(companion,lowest, ()->arcaneShield.use(companion)))

                ),

                new AiNode(

                        ()-> averagePhp<=60 && lightSigil.usable(companion),
                        ()-> runSync(()->lightSigil.use(companion))

                ),

                new AiNode(

                        ()-> true,
                        ()-> runSync(()-> useOnTarget(companion,lowest, ()->abilityManager.useBasic(companion)))

                )




        );

    }

    private void useOnTarget(LivingEntity companion, LivingEntity target, Runnable action) {
        fakePlayerTargetManager.setFakePlayerTarget(companion, target);

        if (fakePlayerTargetManager.getTarget(companion) == companion) {
            fakePlayerTargetManager.setFakePlayerTarget(companion, null);
        }

        action.run();
    }

    private void runSync(Runnable action) {
        Bukkit.getScheduler().runTask(main, action);
    }

    public void setCaution(LivingEntity entity, boolean caution){
        cautionMap.put(entity.getUniqueId(), caution);
    }
    public void setNeedToInterrupt(LivingEntity entity, boolean needs){needToInterrupt.put(entity.getUniqueId(),needs);}
    private void removeInterrupt(LivingEntity entity){needToInterrupt.remove(entity.getUniqueId());}
    private boolean getIfNeedToInterrupt(LivingEntity entity){return needToInterrupt.getOrDefault(entity.getUniqueId(), false);}

    private boolean getIfCautious(LivingEntity entity){
        return cautionMap.getOrDefault(entity.getUniqueId(), false);
    }

    private Role getRole(LivingEntity partyMember){

        SubClass subClass = profileManager.getAnyProfile(partyMember).getPlayerSubclass();

        switch (subClass){
            case Shepard, Divine -> {
                return Role.Healer;
            }
            case Gladiator, Blood, Templar ->{
                return Role.Tank;
            }
            default -> {
                return Role.Damage;
            }
        }

    }

}
