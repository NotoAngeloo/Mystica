package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Paladin;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityMarkManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
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

public class JusticeMark extends BaseAbility {

    private final Mystica main;
    private final TargetManager targetManager;
    private final ProfileManager profileManager;
    private final StatusEffectManager statusEffectManager;
    private final PvpManager pvpManager;
    private final CooldownManager cooldownManager;
    private final AbilityMarkManager abilityMarkManager;


    public JusticeMark(Mystica main, AbilityManager manager){
        super("justice_mark");
        this.main = main;
        targetManager = main.getTargetManager();
        profileManager = main.getProfileManager();
        statusEffectManager = main.getStatusEffectManager();
        pvpManager = main.getPvpManager();
        cooldownManager = manager.getCooldownManager();
        abilityMarkManager = manager.getAbilityMarkManager();
    }

    private final int baseCooldown = 15;
    private final double range = 10;

    @Override
    public void use(LivingEntity caster){

        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(!usable(caster, target)){
            return;
        }

        if(target == null){
            target = caster;
        }

        execute(caster, target);

        cooldownManager.start(caster.getUniqueId(), 8, (long) (baseCooldown * 1000));

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

        Set<LivingEntity> marked = new HashSet<>(affected);

        abilityMarkManager.applyAll(caster, marked);

        new BukkitRunnable(){
            @Override
            public void run(){
                abilityMarkManager.removeTargets(caster);
            }
        }.runTaskLater(main, 20*8);

        //TODO: display the marked players with an icon, when i have them

    }


    @Override
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


        return cooldownManager.isReady(caster.getUniqueId(), 8, statusEffectManager.getHastePercent(caster));
    }
}
