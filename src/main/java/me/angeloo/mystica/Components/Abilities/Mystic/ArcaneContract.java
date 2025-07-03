package me.angeloo.mystica.Components.Abilities.Mystic;

import me.angeloo.mystica.Components.Abilities.MysticAbilities;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Hud.CooldownDisplayer;
import me.angeloo.mystica.Utility.Logic.PveChecker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class ArcaneContract {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final MysticaPartyManager mysticaPartyManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final GravestoneManager gravestoneManager;
    private final CombatManager combatManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final CooldownDisplayer cooldownDisplayer;
    private final DeathManager deathManager;
    private final Mana mana;

    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public ArcaneContract(Mystica main, AbilityManager manager, MysticAbilities mysticAbilities){
        this.main = main;
        profileManager = main.getProfileManager();
        mysticaPartyManager = main.getMysticaPartyManager();
        mana = mysticAbilities.getMana();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        combatManager = manager.getCombatManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
        deathManager = new DeathManager(main);
        gravestoneManager = main.getGravestoneManager();
    }

    private final double range = 10;

    public void use(LivingEntity caster){
        if(!abilityReadyInMap.containsKey(caster.getUniqueId())){
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }


        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(target == null){
            return;
        }

        double distance = caster.getLocation().distance(target.getLocation());

        if(distance>range + buffAndDebuffManager.getTotalRangeModifier(caster)){
            return;
        }

        if(!gravestoneManager.isGravestone(target)){
            return;
        }

        LivingEntity actualTarget = gravestoneManager.getPlayer(target);

        if(pveChecker.pveLogic(actualTarget)){
            return;
        }

        if(actualTarget instanceof Player){
            if(pvpManager.pvpLogic(caster, (Player) actualTarget)){
                return;
            }
        }


        if(!profileManager.getAnyProfile(actualTarget).getIfDead()){
            return;
        }

        if(getCooldown(caster) > 0){
            return;
        }


        if(mana.getCurrentMana(caster)<getCost()){
            return;
        }

        mana.subTractManaFromEntity(caster, getCost());


        List<LivingEntity> mParty = new ArrayList<>(mysticaPartyManager.getMysticaParty(caster));
        for(LivingEntity member : mParty){
            putOnCooldown(member.getUniqueId());
        }

        combatManager.startCombatTimer(caster);

        execute(caster, actualTarget);

    }

    private void execute(LivingEntity caster, LivingEntity target){

        deathManager.playerNowLive(target, true, caster);

        new BukkitRunnable(){
            double height = 0;
            final double radius = 1;
            double angle = 0;
            Vector initialDirection;

            @Override
            public void run(){
                Location playerLoc = target.getLocation();

                if (initialDirection == null) {
                    initialDirection = playerLoc.getDirection().setY(0).normalize();
                    initialDirection.rotateAroundY(Math.toRadians(-45));
                }

                Vector direction = initialDirection.clone();
                double radians = Math.toRadians(angle);

                direction.rotateAroundY(radians);

                double x = playerLoc.getX() + direction.getX() * radius;
                double z = playerLoc.getZ() + direction.getZ() * radius;

                Location particleLoc = new Location(target.getWorld(), x, target.getLocation().getY() + height, z);

                target.getWorld().spawnParticle(Particle.SPELL_WITCH, particleLoc, 1, 0, 0, 0, 0);

                height += .05;
                angle += 11;
                if(height >= 2){
                    this.cancel();
                }
            }
        }.runTaskTimer(main, 0L, 1);

    }

    private void putOnCooldown(UUID id){

        Entity entity = Bukkit.getEntity(id);



        abilityReadyInMap.put(id, 120);
        new BukkitRunnable(){
            @Override
            public void run(){

                if(abilityReadyInMap.containsKey(id)){
                    if(abilityReadyInMap.get(id) <= 0){

                        if(entity instanceof Player){
                            cooldownDisplayer.displayCooldown((LivingEntity) entity, 7);
                        }

                        this.cancel();
                        return;
                    }
                }


                int cooldown;
                if(abilityReadyInMap.containsKey(id)){
                    cooldown = abilityReadyInMap.get(id) - 1;
                }
                else{
                    cooldown = 0;
                }

                abilityReadyInMap.put(id, cooldown);

                if(entity instanceof Player){
                    cooldownDisplayer.displayCooldown((LivingEntity) entity, 7);
                }
            }
        }.runTaskTimerAsynchronously(main, 0,20);
    }

    public int getCost(){
        return 100;
    }

    public int getCooldown(LivingEntity caster){
        int cooldown = abilityReadyInMap.getOrDefault(caster.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

    public void resetCooldown(LivingEntity caster){
        abilityReadyInMap.remove(caster.getUniqueId());
    }

    public int returnWhichItem(Player player){

        if(mana.getCurrentMana(player)<getCost()){
            return 7;
        }

        return 0;
    }

}
