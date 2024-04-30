package me.angeloo.mystica.Components.Abilities.Paladin;

import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.CooldownDisplayer;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
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
    private final CooldownDisplayer cooldownDisplayer;

    private final Map<UUID, List<LivingEntity>> marked = new HashMap<>();

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public JusticeMark(Mystica main, AbilityManager manager){
        this.main = main;
        targetManager = main.getTargetManager();
        profileManager = main.getProfileManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        pvpManager = main.getPvpManager();
        combatManager = manager.getCombatManager();
        changeResourceHandler = main.getChangeResourceHandler();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
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

        if(getCooldown(player) > 0){
            return;
        }


        if(profileManager.getAnyProfile(player).getCurrentMana()<getCost()){
            return;
        }

        changeResourceHandler.subTractManaFromPlayer(player, getCost());

        combatManager.startCombatTimer(player);

        execute(player, target);

        if(cooldownTask.containsKey(player.getUniqueId())){
            cooldownTask.get(player.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(player.getUniqueId(), 15);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getCooldown(player) <= 0){
                    cooldownDisplayer.displayCooldown(player, 8);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(player) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(player, 8);

            }
        }.runTaskTimer(main, 0,20);
        cooldownTask.put(player.getUniqueId(), task);

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

    public double getCost(){
        return 5;
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
