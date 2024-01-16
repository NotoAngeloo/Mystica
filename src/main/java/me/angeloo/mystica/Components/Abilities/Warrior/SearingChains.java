package me.angeloo.mystica.Components.Abilities.Warrior;

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
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.*;

public class SearingChains {

    private final Mystica main;
    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final AggroManager aggroManager;
    private final CooldownDisplayer cooldownDisplayer;

    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public SearingChains(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        combatManager = manager.getCombatManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
        aggroManager = main.getAggroManager();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
    }

    public void use(Player player){

        if(!abilityReadyInMap.containsKey(player.getUniqueId())){
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }


        if(abilityReadyInMap.get(player.getUniqueId()) > 0){
            return;
        }

        Block block = player.getLocation().subtract(0,1,0).getBlock();

        if(block.getType() == Material.AIR){
            return;
        }

        combatManager.startCombatTimer(player);

        execute(player);

        abilityReadyInMap.put(player.getUniqueId(), 12);
        new BukkitRunnable(){
            @Override
            public void run(){

                if(abilityReadyInMap.get(player.getUniqueId()) <= 0){
                    cooldownDisplayer.displayCooldown(player, 2);
                    this.cancel();
                    return;
                }

                int cooldown = abilityReadyInMap.get(player.getUniqueId()) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(player, 2);

            }
        }.runTaskTimer(main, 0,20);

    }

    private void execute(Player player){

        double baseRange = 8;

        targetManager.setTargetToNearestValid(player, baseRange);

        LivingEntity target = targetManager.getPlayerTarget(player);

        boolean targeted = false;

        Vector direction = player.getLocation().getDirection().setY(0).normalize();

        if(target != null){

            if(target instanceof Player){
                if(pvpManager.pvpLogic(player, (Player) target)){

                    double distance = player.getLocation().distance(target.getLocation());

                    if(distance < baseRange){
                        targeted = true;
                    }

                }
            }

            if(!(target instanceof Player)){
                if(pveChecker.pveLogic(target)){

                    double distance = player.getLocation().distance(target.getLocation());

                    if(distance < baseRange){
                        targeted = true;
                    }

                }
            }


        }

        if(targeted){
            direction = target.getLocation().toVector().subtract(player.getLocation().toVector()).setY(0).normalize();
        }

        Location start = player.getLocation().clone().add(direction.multiply(1));
        start.setDirection(direction);

        Location end = start.clone().add(direction.multiply(8));

        double skillDamage = 20;
        double skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkill_2_Level() +
                profileManager.getAnyProfile(player).getSkillLevels().getSkill_2_Level_Bonus();
        skillDamage = skillDamage + ((int)(skillLevel/10));

        Vector finalDirection = direction;
        Vector crossSection = finalDirection.clone().crossProduct(new Vector(0,1,0)).normalize();
        double finalSkillDamage = skillDamage;
        Set<LivingEntity> hitBySkill = new HashSet<>();
        Set<LivingEntity> validCCTargets = new HashSet<>();
        new BukkitRunnable(){
            final Map<LivingEntity, Boolean> done = new HashMap<>();
            int count = 0;
            boolean hooked = false;
            boolean going = true;
            final List<ArmorStand> middle = new ArrayList<>();
            final List<ArmorStand> left = new ArrayList<>();
            final List<ArmorStand> right = new ArrayList<>();
            final Location current = start.clone();
            final Location currentLeft = current.clone();
            final Location currentRight = current.clone();
            Vector directionLeft;
            Vector directionRight;
            @Override
            public void run(){

                if(going){
                    double distance = current.distance(end);

                    if(distance>=1){

                        ItemStack chain = new ItemStack(Material.NETHER_WART);
                        ItemMeta meta = chain.getItemMeta();
                        assert meta != null;
                        meta.setCustomModelData(3);
                        chain.setItemMeta(meta);

                        double distanceThisTick = Math.min(distance, .7);
                        current.add(finalDirection.normalize().multiply(distanceThisTick));
                        distanceThisTick = Math.min(distance, .45);
                        currentLeft.add(finalDirection.normalize().multiply(distanceThisTick)).subtract(crossSection.clone().normalize().multiply(distanceThisTick));
                        if(directionLeft==null){
                            directionLeft=currentLeft.clone().toVector().subtract(start.clone().toVector()).setY(0).normalize();
                        }
                        currentLeft.setDirection(directionLeft);
                        currentRight.add(finalDirection.normalize().multiply(distanceThisTick)).add(crossSection.clone().normalize().multiply(distanceThisTick));
                        if(directionRight==null){
                            directionRight=currentRight.clone().toVector().subtract(start.clone().toVector()).setY(0).normalize();
                        }
                        currentRight.setDirection(directionRight);

                        ArmorStand armorStand = player.getWorld().spawn(current.clone().subtract(0,5,0), ArmorStand.class);
                        armorStand.setInvisible(true);
                        armorStand.setGravity(false);
                        armorStand.setCollidable(false);
                        armorStand.setInvulnerable(true);
                        armorStand.setMarker(true);
                        EntityEquipment entityEquipment = armorStand.getEquipment();
                        assert entityEquipment != null;
                        entityEquipment.setHelmet(chain);
                        middle.add(armorStand);
                        armorStand.teleport(current);

                        ArmorStand armorStand2 = player.getWorld().spawn(currentLeft.clone().subtract(0,5,0), ArmorStand.class);
                        armorStand2.setInvisible(true);
                        armorStand2.setGravity(false);
                        armorStand2.setCollidable(false);
                        armorStand2.setInvulnerable(true);
                        armorStand2.setMarker(true);
                        EntityEquipment entityEquipment2 = armorStand2.getEquipment();
                        assert entityEquipment2 != null;
                        entityEquipment2.setHelmet(chain);
                        left.add(armorStand2);
                        armorStand2.teleport(currentLeft);

                        ArmorStand armorStand3 = player.getWorld().spawn(currentRight.clone().subtract(0,5,0), ArmorStand.class);
                        armorStand3.setInvisible(true);
                        armorStand3.setGravity(false);
                        armorStand3.setCollidable(false);
                        armorStand3.setInvulnerable(true);
                        armorStand3.setMarker(true);
                        EntityEquipment entityEquipment3 = armorStand3.getEquipment();
                        assert entityEquipment3 != null;
                        entityEquipment3.setHelmet(chain);
                        right.add(armorStand3);
                        armorStand3.teleport(currentRight);

                    }

                    if(distance<=1){
                        going = false;
                    }
                }

                if(!going){

                    if(!hooked){
                        ItemStack hook = new ItemStack(Material.NETHER_WART);
                        ItemMeta meta = hook.getItemMeta();
                        assert meta != null;
                        meta.setCustomModelData(4);
                        hook.setItemMeta(meta);

                        //sort each list by distance
                        middle.sort(Comparator.comparingDouble(s -> s.getLocation().distance(start)));
                        ArmorStand farMid = middle.get(middle.size()-1);
                        EntityEquipment midEq = farMid.getEquipment();
                        assert midEq != null;
                        midEq.setHelmet(hook);

                        left.sort(Comparator.comparingDouble(s -> s.getLocation().distance(start)));
                        ArmorStand farLeft = left.get(left.size()-1);
                        EntityEquipment leftEq = farLeft.getEquipment();
                        assert leftEq != null;
                        leftEq.setHelmet(hook);

                        right.sort(Comparator.comparingDouble(s -> s.getLocation().distance(start)));
                        ArmorStand farRight = right.get(right.size()-1);
                        EntityEquipment rightEq = farRight.getEquipment();
                        assert rightEq != null;
                        rightEq.setHelmet(hook);

                        hooked = true;

                        double midX = (start.getX() + end.getX()) / 2;
                        double midY = (start.getY() + end.getY()) / 2;
                        double midZ = (start.getZ() + end.getZ()) / 2;

                        Location center = new Location(player.getWorld(), midX,midY,midZ);
                        //player.getWorld().spawnParticle(Particle.GLOW_SQUID_INK, center, 1,0,0,0,0);

                        BoundingBox hitBox = new BoundingBox(
                                center.getX() - 5,
                                center.getY() - 2,
                                center.getZ() - 5,
                                center.getX() + 5,
                                center.getY() + 6,
                                center.getZ() + 5
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

                            aggroManager.setAsHighPriorityTarget(livingEntity, player);

                            boolean crit = damageCalculator.checkIfCrit(player, 0);
                            double damage = (damageCalculator.calculateDamage(player, livingEntity, "Physical", finalSkillDamage, crit));

                            //pvp logic
                            if(entity instanceof Player){
                                if(pvpManager.pvpLogic(player, (Player) entity)){
                                    changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, player);
                                    validCCTargets.add(livingEntity);
                                    buffAndDebuffManager.getPulled().applyPull(livingEntity);
                                    targetManager.setPlayerTarget((Player) entity, player);
                                    return;
                                }
                                continue;
                            }

                            if(pveChecker.pveLogic(livingEntity)){
                                Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(livingEntity, player));
                                changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, player);
                                if(profileManager.getAnyProfile(livingEntity).getIsMovable()){
                                    validCCTargets.add(livingEntity);
                                    buffAndDebuffManager.getPulled().applyPull(livingEntity);
                                }
                            }

                        }

                        finalDirection.multiply(-1);
                        directionLeft.multiply(-1);
                        directionRight.multiply(-1);


                    }


                    //move the chains back then remove them
                    for(ArmorStand stand : middle){

                        Location sLoc = stand.getLocation();
                        double distance = sLoc.distance(start);

                        if(distance<=1){
                            stand.remove();
                            continue;
                        }

                        double distanceThisTick = Math.min(distance, 1);
                        sLoc.add(finalDirection.normalize().multiply(distanceThisTick));
                        stand.teleport(sLoc);

                    }

                    for(ArmorStand stand : left){

                        Location sLoc = stand.getLocation();
                        double distance = sLoc.distance(start);

                        if(distance<=1){
                            stand.remove();
                            continue;
                        }

                        double distanceThisTick = Math.min(distance, 1);
                        sLoc.add(directionLeft.normalize().multiply(distanceThisTick));
                        stand.teleport(sLoc);

                    }

                    for(ArmorStand stand : right){

                        Location sLoc = stand.getLocation();
                        double distance = sLoc.distance(start);

                        if(distance<=1){
                            stand.remove();
                            continue;
                        }

                        double distanceThisTick = Math.min(distance, 1);
                        sLoc.add(directionRight.normalize().multiply(distanceThisTick));
                        stand.teleport(sLoc);
                    }

                    for(LivingEntity entity : validCCTargets){

                        double distance = start.distance(entity.getLocation());

                        if(distance <= 1){
                            done.put(entity, true);
                            continue;
                        }

                        if(done.getOrDefault(entity, false)){
                            buffAndDebuffManager.getPulled().removePull(entity);
                            continue;
                        }

                        Vector direction = start.toVector().subtract(entity.getLocation().toVector());

                        double distanceThisTick = Math.min(distance, 1);

                        //do a wall check here
                        if(wallCheck(entity.getLocation(), direction, distanceThisTick)){
                            done.put(entity, true);
                            continue;
                        }

                        if(targetStillValid(entity)){

                            entity.teleport(entity.getLocation().add(direction.normalize().multiply(distanceThisTick)));
                        }


                    }

                    if(count>=75){
                        this.cancel();
                    }

                    count++;
                }
            }

            private boolean targetStillValid(LivingEntity target){

                if(target instanceof Player){

                    if(!((Player) target).isOnline()){
                        return false;
                    }

                }

                return !target.isDead();
            }

            private boolean wallCheck(Location current, Vector direction, double distance){

                Location newLoc = current.clone().add(direction.normalize().multiply(distance));
                newLoc.add(0,1,0);
                Location newLoc2 = newLoc.clone().add(0,1,0);

                return !newLoc.getBlock().isPassable() || !newLoc2.getBlock().isPassable();
            }

        }.runTaskTimer(main, 0, 1);

    }

    public void tryToDecreaseCooldown(Player player){

        if(!profileManager.getAnyProfile(player).getPlayerSubclass().equalsIgnoreCase("gladiator")){
            return;
        }

        int current = getCooldown(player);
        current-=2;
        if(current<0){
            current=0;
        }

        abilityReadyInMap.put(player.getUniqueId(), current);
    }

    public int getCooldown(Player player){
        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

}
