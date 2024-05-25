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
import org.bukkit.scheduler.BukkitTask;
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

    public void use(Player player){
        if(!abilityReadyInMap.containsKey(player.getUniqueId())){
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }

        double baseRange = 10;
        double extraRange = buffAndDebuffManager.getTotalRangeModifier(player);
        double totalRange = baseRange + extraRange;

        LivingEntity target = targetManager.getPlayerTarget(player);

        if(target == null){
            return;
        }

        double distance = player.getLocation().distance(target.getLocation());

        if(distance>totalRange){
            return;
        }

        if(pveChecker.pveLogic(target)){
            return;
        }

        if(pvpManager.pvpLogic(player, (Player) target)){
            return;
        }

        if(!profileManager.getAnyProfile(target).getIfDead()){
            return;
        }

        if(getCooldown(player) > 0){
            return;
        }


        if(profileManager.getAnyProfile(player).getCurrentMana()<getCost()){
            return;
        }

        changeResourceHandler.subTractManaFromPlayer(player, getCost());

        PartiesAPI api = Parties.getApi();

        PartyPlayer partyPlayer = api.getPartyPlayer(player.getUniqueId());

        assert partyPlayer != null;
        if(partyPlayer.isInParty()){

            Party party = api.getParty(partyPlayer.getPartyId());

            assert party != null;
            Set<UUID> partyMemberList = party.getMembers();

            for(UUID partyMemberId : partyMemberList){
                putOnCooldown(partyMemberId);
            }
        }

        putOnCooldown(player.getUniqueId());
        //also for rest of team

        combatManager.startCombatTimer(player);

        execute(player, (Player) target);


    }

    private void execute(Player player, Player target){

        deathManager.playerNowLive(target, true, player);

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

    public int getCooldown(Player player){
        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

    public void resetCooldown(Player player){
        abilityReadyInMap.remove(player.getUniqueId());
    }

}
