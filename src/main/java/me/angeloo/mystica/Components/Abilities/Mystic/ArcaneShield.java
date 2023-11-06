package me.angeloo.mystica.Components.Abilities.Mystic;

import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.PveChecker;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ArcaneShield {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final CombatManager combatManager;
    private final AbilityManager abilityManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;

    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public ArcaneShield(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        combatManager = manager.getCombatManager();
        abilityManager = manager;
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
    }

    public void use(Player player){

        if(!abilityReadyInMap.containsKey(player.getUniqueId())){
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }

        double baseRange = 20;
        double extraRange = buffAndDebuffManager.getTotalRangeModifier(player);
        double totalRange = baseRange + extraRange;

        LivingEntity target = targetManager.getPlayerTarget(player);

        if(target != null){

            if(target instanceof Player){
                if(pvpManager.pvpLogic(player, (Player) target)){
                    target = player;
                }
            }

            if(!(target instanceof Player)){
                target = player;
            }
        }

        if(target == null){
            target = player;
        }

        double distance = player.getLocation().distance(target.getLocation());

        if(distance > totalRange){
            return;
        }

        if(abilityReadyInMap.get(player.getUniqueId()) > 0){
            return;
        }

        combatManager.startCombatTimer(player);

        execute(player, target);

        abilityReadyInMap.put(player.getUniqueId(), 10);
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

        int skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkill_1_Level() +
                profileManager.getAnyProfile(player).getSkillLevels().getSkill_1_Level_Bonus();

        double fivePercent = (double) profileManager.getAnyProfile(target).getTotalHealth() / 20;

        double shieldAmount = fivePercent * skillLevel;

        buffAndDebuffManager.getGenericShield().applyOrAddShield(target, shieldAmount);

        int shieldDurationInTicks = 20*60;

        new BukkitRunnable(){
            @Override
            public void run(){
                buffAndDebuffManager.getGenericShield().removeSomeShieldAndReturnHowMuchOver(target, shieldAmount);
            }
        }.runTaskLater(main, shieldDurationInTicks);


        String subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        if(subclass.equalsIgnoreCase("shepard")){
            //task to heal them for as long as they have a shield
            double thirtyPercent = (double) profileManager.getAnyProfile(target).getTotalHealth() * .3;

            new BukkitRunnable(){
                @Override
                public void run(){

                    boolean stillHasAShield = buffAndDebuffManager.getGenericShield().getCurrentShieldAmount(target) > 0;

                    if(!stillHasAShield){
                        this.cancel();
                        return;
                    }

                    changeResourceHandler.addHealthToEntity(target, thirtyPercent, player);

                    Location center = target.getLocation().clone().add(0,1,0);

                    double increment = (2 * Math.PI) / 16; // angle between particles

                    for (int i = 0; i < 16; i++) {
                        double angle = i * increment;
                        double x = center.getX() + (1 * Math.cos(angle));
                        double z = center.getZ() + (1 * Math.sin(angle));
                        Location loc = new Location(center.getWorld(), x, (center.getY()), z);

                        target.getWorld().spawnParticle(Particle.WAX_OFF, loc, 1,0, 0, 0, 0);
                    }

                }
            }.runTaskTimer(main, 0, 20 * 20);

        }

    }

    public int getCooldown(Player player){
        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

}
