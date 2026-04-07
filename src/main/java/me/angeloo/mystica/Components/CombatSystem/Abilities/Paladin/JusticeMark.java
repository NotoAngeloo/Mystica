package me.angeloo.mystica.Components.CombatSystem.Abilities.Paladin;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.PvpManager;
import me.angeloo.mystica.Components.CombatSystem.TargetManager;
import me.angeloo.mystica.Components.Hud.CooldownDisplayer;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
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
    private final StatusEffectManager statusEffectManager;
    private final PvpManager pvpManager;
    private final CooldownDisplayer cooldownDisplayer;

    private final Map<UUID, List<LivingEntity>> marked = new HashMap<>();

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public JusticeMark(Mystica main, AbilityManager manager){
        this.main = main;
        targetManager = main.getTargetManager();
        profileManager = main.getProfileManager();
        statusEffectManager = main.getStatusEffectManager();
        pvpManager = main.getPvpManager();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
    }

    private final double range = 10;

    public void use(LivingEntity caster){

        if(!abilityReadyInMap.containsKey(caster.getUniqueId())){
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }


        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(!usable(caster, target)){
            return;
        }

        if(target == null){
            target = caster;
        }

        execute(caster, target);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), 15);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getCooldown(caster) <= 0){
                    cooldownDisplayer.displayCooldown(caster, 8);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(caster) - 1;
                cooldown = cooldown - statusEffectManager.getHasteLevel(caster);

                abilityReadyInMap.put(caster.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(caster, 8);

            }
        }.runTaskTimerAsynchronously(main, 0,20);
        cooldownTask.put(caster.getUniqueId(), task);

    }

    private void execute(LivingEntity caster, LivingEntity target){

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

        for (Entity entity : caster.getWorld().getNearbyEntities(hitBox)) {

            if(!(entity instanceof Player hitPlayer)){
                continue;
            }

            if(pvpManager.pvpLogic(caster, hitPlayer)){
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

        marked.put(caster.getUniqueId(), affected);

        new BukkitRunnable(){
            @Override
            public void run(){
                marked.remove(caster.getUniqueId());
            }
        }.runTaskLater(main, 20*8);

        //TODO: display the marked players with an icon, when i have them

    }

    public boolean markProc(LivingEntity caster, LivingEntity target){

        if(!marked.containsKey(caster.getUniqueId())){
            return false;
        }

        List<LivingEntity> targets = marked.get(caster.getUniqueId());

        return targets.contains(target);
    }

    public List<LivingEntity> getMarkedTargets(LivingEntity caster){
        return marked.getOrDefault(caster.getUniqueId(), new ArrayList<>());
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

    public boolean usable(LivingEntity caster, LivingEntity target){
        if(target != null){

            if(!(target instanceof Player)){
                target = caster;
            }

            double distance = caster.getLocation().distance(target.getLocation());

            if(distance > range + statusEffectManager.getAdditionalRange(caster)){
                return false;
            }
        }


        return getCooldown(caster) <= 0;
    }
}
