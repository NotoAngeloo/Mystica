package me.angeloo.mystica.Components.Abilities.Mystic;

import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.interfaces.PartiesAPI;
import com.alessiodp.parties.api.interfaces.Party;
import com.alessiodp.parties.api.interfaces.PartyPlayer;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.CooldownDisplayer;
import me.angeloo.mystica.Utility.PveChecker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ArcaneContract {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final CombatManager combatManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final CooldownDisplayer cooldownDisplayer;
    private final DeathManager deathManager;

    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public ArcaneContract(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        combatManager = manager.getCombatManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
        deathManager = new DeathManager(main);
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

        if(pveChecker.pveLogic(target)){
            return;
        }

        if(target instanceof Player){
            if(pvpManager.pvpLogic(caster, (Player) target)){
                return;
            }
        }


        if(!profileManager.getAnyProfile(target).getIfDead()){
            return;
        }

        if(getCooldown(caster) > 0){
            return;
        }


        if(profileManager.getAnyProfile(caster).getCurrentMana()<getCost()){
            return;
        }

        changeResourceHandler.subTractManaFromEntity(caster, getCost());

        PartiesAPI api = Parties.getApi();
        PartyPlayer partyPlayer;

        if(caster instanceof Player){
            partyPlayer = api.getPartyPlayer(caster.getUniqueId());
        }
        else{
            partyPlayer = api.getPartyPlayer(profileManager.getCompanionsPlayer(caster).getUniqueId());

            if(!profileManager.getCompanions(profileManager.getCompanionsPlayer(caster)).isEmpty()){
                for(LivingEntity companion : profileManager.getCompanions(profileManager.getCompanionsPlayer(caster))){
                    putOnCooldown(companion.getUniqueId());
                }
            }

        }

        assert partyPlayer != null;
        if(partyPlayer.isInParty()){

            Party party = api.getParty(partyPlayer.getPartyId());

            assert party != null;
            Set<UUID> partyMemberList = party.getMembers();

            for(UUID partyMemberId : partyMemberList){
                putOnCooldown(partyMemberId);
            }
        }




        putOnCooldown(caster.getUniqueId());
        //also for rest of team

        combatManager.startCombatTimer(caster);

        execute(caster, target);

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
        Player player = Bukkit.getPlayer(id);

        if(player == null){
            return;
        }

        abilityReadyInMap.put(id, 120);
        new BukkitRunnable(){
            @Override
            public void run(){

                if(abilityReadyInMap.get(id) <= 0){
                    cooldownDisplayer.displayCooldown(player, 7);
                    this.cancel();
                    return;
                }

                int cooldown = abilityReadyInMap.get(id) - 1;

                abilityReadyInMap.put(id, cooldown);
                cooldownDisplayer.displayCooldown(player, 7);
            }
        }.runTaskTimer(main, 0,20);
    }

    public double getCost(){
        return 20;
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

}
