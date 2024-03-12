package me.angeloo.mystica.Components.Abilities.ShadowKnight;

import me.angeloo.mystica.Components.Abilities.ShadowKnightAbilities;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.CooldownDisplayer;
import me.angeloo.mystica.Utility.DamageCalculator;
import me.angeloo.mystica.Utility.PveChecker;
import org.bukkit.Bukkit;
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
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Annihilation {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final AbilityManager abilityManager;
    private final CombatManager combatManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;

    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    private final Infection infection;

    public Annihilation(Mystica main, AbilityManager manager, ShadowKnightAbilities shadowKnightAbilities){
        this.main = main;
        profileManager = main.getProfileManager();
        abilityManager = manager;
        combatManager = manager.getCombatManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
        infection = shadowKnightAbilities.getInfection();
    }

    public void use(Player player){

        if(!abilityReadyInMap.containsKey(player.getUniqueId())){
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }

        double baseRange = 8;

        targetManager.setTargetToNearestValid(player, baseRange);

        LivingEntity target = targetManager.getPlayerTarget(player);

        if(target != null){
            if(target instanceof Player){
                if(!pvpManager.pvpLogic(player, (Player) target)){
                    return;
                }
            }

            if(!(target instanceof Player)){
                if(!pveChecker.pveLogic(target)){
                    return;
                }
            }

            double distance = player.getLocation().distance(target.getLocation());

            if(distance > baseRange){
                return;
            }
        }

        if(target == null){
            return;
        }

        if(abilityReadyInMap.get(player.getUniqueId()) > 0){
            return;
        }

        double cost = 30;

        if(profileManager.getAnyProfile(player).getCurrentMana() < cost){
            return;
        }

        changeResourceHandler.subTractManaFromPlayer(player, cost);

        combatManager.startCombatTimer(player);

        execute(player);

        abilityReadyInMap.put(player.getUniqueId(), 3);
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

    private void execute(Player player){

        LivingEntity target = targetManager.getPlayerTarget(player);

        Location start = player.getLocation().clone();
        Location end = target.getLocation().clone();
        Vector initDir = end.toVector().subtract(start.toVector());
        Vector direction = initDir.clone();
        direction.rotateAroundY(-45);

        Location spawnLoc = start.clone().subtract(0,3,0);
        spawnLoc.setDirection(direction);

        ArmorStand armorStand = player.getWorld().spawn(spawnLoc, ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);


        EntityEquipment entityEquipment = armorStand.getEquipment();

        ItemStack item = new ItemStack(Material.REDSTONE);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(6);
        item.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setHelmet(item);

        double skillDamage = 35;
        double skillLevel = profileManager.getAnyProfile(player).getStats().getLevel();
        skillDamage = skillDamage + ((int)(skillLevel/10));

        abilityManager.setSkillRunning(player, true);
        double finalSkillDamage = skillDamage;
        new BukkitRunnable(){
            int ran = 0;
            int height = 0;
            int angle = 0;
            Location pLoc = null;
            Vector pDir = null;
            @Override
            public void run(){

                if(!player.isOnline()){
                    cancelTask();
                    return;
                }

                Location start = player.getLocation().clone();
                Location loc = start.clone().subtract(0,3,0);

                if(ran < 5){
                    height ++;
                }

                if(ran > 5 && angle<=40){
                    angle+=13;
                    double radians = Math.toRadians(angle);
                    direction.rotateAroundY(radians);
                }

                loc.setDirection(direction);

                loc.add(0,((double) height / 2), 0);

                armorStand.teleport(loc);

                if(ran==5){
                    pLoc = loc.clone().add(direction.multiply(1.5));
                    pLoc.add(0,1.5,0);
                    pDir = initDir.clone().crossProduct(new Vector(0,1,0)).normalize();
                }

                if(pLoc != null){
                    player.getWorld().spawnParticle(Particle.SPELL_WITCH, pLoc, 1, 0, 0, 0, 0);
                    if(angle>40){
                        pLoc.subtract(pDir.multiply(1));
                    }
                }

                if(ran >= 20){
                    cancelTask();

                    double distance = player.getLocation().distance(target.getLocation());

                    if(distance<8){
                        hitTarget();
                    }

                }


                ran++;
            }

            private void cancelTask() {
                this.cancel();
                armorStand.remove();
                abilityManager.setSkillRunning(player, false);
            }

            private void hitTarget(){



                boolean crit = damageCalculator.checkIfCrit(player, 0);
                double damage = damageCalculator.calculateDamage(player, target, "Physical", finalSkillDamage, crit);
                Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, player));
                changeResourceHandler.subtractHealthFromEntity(target, damage, player);

                //Bukkit.getLogger().info(String.valueOf(infection.getIfThisPlayerInfectThisEntity(player, target)));
                if(infection.getIfThisPlayerInfectThisEntity(player, target)){
                    infection.infectionEnhancement(player, target);
                }
            }

        }.runTaskTimer(main, 0, 1);

    }

    public int getCooldown(Player player){
        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

}
