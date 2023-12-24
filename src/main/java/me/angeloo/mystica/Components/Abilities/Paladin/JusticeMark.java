package me.angeloo.mystica.Components.Abilities.Paladin;

import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;

import java.util.*;

public class JusticeMark {

    private final Mystica main;
    private final TargetManager targetManager;
    private final ProfileManager profileManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final PvpManager pvpManager;
    private final CombatManager combatManager;
    private final ChangeResourceHandler changeResourceHandler;

    private final Map<UUID, List<LivingEntity>> marked = new HashMap<>();

    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public JusticeMark(Mystica main, AbilityManager manager){
        this.main = main;
        targetManager = main.getTargetManager();
        profileManager = main.getProfileManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        pvpManager = main.getPvpManager();
        combatManager = manager.getCombatManager();
        changeResourceHandler = main.getChangeResourceHandler();
    }

    public void use(Player player){

        if(!abilityReadyInMap.containsKey(player.getUniqueId())){
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }

        double baseRange = 10;
        double extraRange = buffAndDebuffManager.getTotalRangeModifier(player);
        double totalRange = baseRange + extraRange;

        LivingEntity target = targetManager.getPlayerTarget(player);

        if(target != null){

            if(!(target instanceof Player)){
                target = player;
            }

            double distance = player.getLocation().distance(target.getLocation());

            if(distance > totalRange){
                return;
            }
        }

        if(target == null){
            target = player;
        }

        if(abilityReadyInMap.get(player.getUniqueId()) > 0){
            return;
        }

        combatManager.startCombatTimer(player);

        execute(player, target);


        abilityReadyInMap.put(player.getUniqueId(), 15);
        new BukkitRunnable(){
            @Override
            public void run(){

                if(abilityReadyInMap.get(player.getUniqueId()) <= 0){
                    this.cancel();
                    return;
                }

                int cooldown = abilityReadyInMap.get(player.getUniqueId()) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);

            }
        }.runTaskTimer(main, 0,20);

    }

    private void execute(Player player, LivingEntity target){

        Location center = target.getLocation().clone();


        BoundingBox hitBox = new BoundingBox(
                center.getX() - 12,
                center.getY() - 2,
                center.getZ() - 12,
                center.getX() + 12,
                center.getY() + 6,
                center.getZ() + 12
        );

        List<LivingEntity> validPlayers = new ArrayList<>();

        for (Entity entity : player.getWorld().getNearbyEntities(hitBox)) {

            if(!(entity instanceof Player)){
                continue;
            }

            Player hitPlayer = (Player) entity;

            if(pvpManager.pvpLogic(player, hitPlayer)){
                continue;
            }

            boolean deathStatus = profileManager.getAnyProfile(hitPlayer).getIfDead();

            if(deathStatus){
                continue;
            }

            validPlayers.add(hitPlayer);
        }

        validPlayers.sort(Comparator.comparingDouble(p -> p.getLocation().distance(center)));

        List<LivingEntity> affected = validPlayers.subList(0, Math.min(5, validPlayers.size()));

        marked.put(player.getUniqueId(), affected);

        new BukkitRunnable(){
            @Override
            public void run(){
                marked.remove(player.getUniqueId());
            }
        }.runTaskLater(main, 20*8);

        //TODO: display the marked players with an icon, when i have them

    }

    public boolean markProc(Player caster, LivingEntity target){

        if(!marked.containsKey(caster.getUniqueId())){
            return false;
        }

        List<LivingEntity> targets = marked.get(caster.getUniqueId());

        return targets.contains(target);
    }

    public List<LivingEntity> getMarkedTargets(Player player){
        return marked.getOrDefault(player.getUniqueId(), new ArrayList<>());
    }

    public int getCooldown(Player player){

        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;

    }

}
