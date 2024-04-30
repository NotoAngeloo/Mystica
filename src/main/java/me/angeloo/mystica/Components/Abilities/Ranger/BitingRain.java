package me.angeloo.mystica.Components.Abilities.Ranger;

import me.angeloo.mystica.Components.Abilities.RangerAbilities;
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
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.*;

public class BitingRain {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final CooldownDisplayer cooldownDisplayer;

    private final StarVolley starVolley;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public BitingRain(Mystica main, AbilityManager manager, RangerAbilities rangerAbilities){
        this.main = main;
        profileManager = main.getProfileManager();
        combatManager = manager.getCombatManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
        starVolley = rangerAbilities.getStarVolley();
    }

    public void use(Player player){

        if(!abilityReadyInMap.containsKey(player.getUniqueId())){
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }

        double baseRange = 20;
        double extraRange = buffAndDebuffManager.getTotalRangeModifier(player);
        double totalRange = baseRange + extraRange;

        targetManager.setTargetToNearestValid(player, totalRange);

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

            if(distance > totalRange){
                return;
            }
        }

        if(target == null){
            return;
        }

        if(getCooldown(player) > 0){
            return;
        }


        if(profileManager.getAnyProfile(player).getCurrentMana()<getCost()){
            return;
        }

        changeResourceHandler.subTractManaFromPlayer(player, getCost());

        combatManager.startCombatTimer(player);

        execute(player);

        if(cooldownTask.containsKey(player.getUniqueId())){
            cooldownTask.get(player.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(player.getUniqueId(), 10);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getCooldown(player) <= 0){
                    cooldownDisplayer.displayCooldown(player, 1);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(player) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(player, 1);

            }
        }.runTaskTimer(main, 0,20);
        cooldownTask.put(player.getUniqueId(), task);

    }

    private void execute(Player player){

        boolean scout = profileManager.getAnyProfile(player).getPlayerSubclass().equalsIgnoreCase("scout");

        LivingEntity target = targetManager.getPlayerTarget(player);

        Location start = target.getLocation().clone();
        Location upstart = start.clone().add(0, 8, 0);

        Set<LivingEntity> hitBySkill = new HashSet<>();



        double finalSkillDamage = getSkillDamage(player);
        new BukkitRunnable(){
            final Set<ArmorStand> allStands = new HashSet<>();
            int count = 0;
            @Override
            public void run(){

                if(!player.isOnline()){
                    cancelTask();
                    return;
                }

                if(count <=5){
                    Location playerLoc = player.getLocation();

                    ArmorStand armorStand = player.getWorld().spawn(playerLoc, ArmorStand.class);
                    armorStand.setInvisible(true);
                    armorStand.setGravity(false);
                    armorStand.setCollidable(false);
                    armorStand.setInvulnerable(true);
                    armorStand.setMarker(true);
                    armorStand.setRightArmPose(new EulerAngle(Math.toRadians(-180), Math.toRadians(0), 0));

                    EntityEquipment entityEquipment = armorStand.getEquipment();

                    ItemStack boltItem = new ItemStack(Material.ARROW);
                    ItemMeta meta = boltItem.getItemMeta();
                    assert meta != null;
                    meta.setCustomModelData(1);
                    boltItem.setItemMeta(meta);
                    assert entityEquipment != null;
                    entityEquipment.setItemInMainHand(boltItem);

                    allStands.add(armorStand);


                    new BukkitRunnable(){
                        int count = 0;
                        @Override
                        public void run(){

                            Location current = armorStand.getLocation();
                            Vector direction = new Vector(0,1,0);
                            current.add(direction.normalize().multiply(3));

                            armorStand.teleport(current);

                            if (count >= 5){
                                armorStand.remove();
                                this.cancel();
                            }

                            count++;
                        }
                    }.runTaskTimer(main, 0, 1);

                }

                if(count == 5){
                    double increment = (2 * Math.PI) / 16; // angle between particles

                    for (int i = 0; i < 16; i++) {
                        double angle = i * increment;
                        double x = start.getX() + (4 * Math.cos(angle));
                        double z = start.getZ() + (4 * Math.sin(angle));
                        Location loc = new Location(start.getWorld(), x, (start.getY()), z);

                        target.getWorld().spawnParticle(Particle.SCRAPE, loc, 1,0, 0, 0, 0);
                    }
                }

                if(count >= 10){
                    BoundingBox hitBox = new BoundingBox(
                            start.getX() - 4,
                            start.getY() - 4,
                            start.getZ() - 4,
                            start.getX() + 4,
                            start.getY() + 4,
                            start.getZ() + 4
                    );

                    for (Entity entity : player.getWorld().getNearbyEntities(hitBox)) {

                        if(entity == player){
                            continue;
                        }

                        if(!(entity instanceof LivingEntity)){
                            continue;
                        }

                        if(entity instanceof ArmorStand){
                            continue;
                        }

                        LivingEntity livingEntity = (LivingEntity) entity;

                        if(hitBySkill.contains(livingEntity)){
                            continue;
                        }

                        hitBySkill.add(livingEntity);

                        boolean crit = damageCalculator.checkIfCrit(player, 0);

                        if(scout && crit){
                            starVolley.decreaseCooldown(player);
                            buffAndDebuffManager.getHaste().applyHaste(player, 1, 2*20);
                        }

                        double damage = (damageCalculator.calculateDamage(player, livingEntity, "Physical", finalSkillDamage, crit));

                        //pvp logic
                        if(entity instanceof Player){
                            if(pvpManager.pvpLogic(player, (Player) entity)){
                                changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, player);
                            }
                            continue;
                        }

                        if(pveChecker.pveLogic(livingEntity)){
                            Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(livingEntity, player));
                            changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, player);
                        }

                    }
                }

                if(count > 5){

                    int randomNumberX = new Random().nextInt(9) - 4;
                    int randomNumberZ = new Random().nextInt(9) - 4;

                    Location spawnLoc = upstart.clone().add(randomNumberX, 8, randomNumberZ);

                    ArmorStand armorStand = player.getWorld().spawn(spawnLoc, ArmorStand.class);
                    armorStand.setInvisible(true);
                    armorStand.setGravity(false);
                    armorStand.setCollidable(false);
                    armorStand.setInvulnerable(true);
                    armorStand.setMarker(true);
                    armorStand.setRightArmPose(new EulerAngle(Math.toRadians(180), Math.toRadians(0), 0));

                    EntityEquipment entityEquipment = armorStand.getEquipment();

                    ItemStack boltItem = new ItemStack(Material.ARROW);
                    ItemMeta meta = boltItem.getItemMeta();
                    assert meta != null;
                    meta.setCustomModelData(1);
                    boltItem.setItemMeta(meta);
                    assert entityEquipment != null;
                    entityEquipment.setItemInMainHand(boltItem);

                    allStands.add(armorStand);

                    new BukkitRunnable(){
                        int count = 0;
                        @Override
                        public void run(){

                            Location current = armorStand.getLocation();
                            Vector direction = new Vector(0,-1,0);
                            current.add(direction.normalize().multiply(3));

                            armorStand.teleport(current);


                            if (count >= 12){
                                armorStand.remove();
                                this.cancel();
                            }

                            count++;

                        }
                    }.runTaskTimer(main, 0, 1);

                }


                if(count >=35){
                    cancelTask();
                }

                count++;
            }

            private void cancelTask(){
                this.cancel();
                removeStands();
            }

            private void removeStands(){
                for(ArmorStand stand : allStands){
                    stand.remove();
                }
            }

        }.runTaskTimer(main, 0, 1);

    }

    public double getCost(){
        return 5;
    }

    public double getSkillDamage(Player player){
        double skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) +
                profileManager.getAnyProfile(player).getSkillLevels().getSkill_1_Level_Bonus();
        return 20 + ((int)(skillLevel/3));
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
