package me.angeloo.mystica.Components.Abilities.Elementalist;

import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
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
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.*;

public class CrystalStorm {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;

    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();
    private final Map<UUID, Boolean> effectedByAnyStorm = new HashMap<>();


    public CrystalStorm(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        combatManager = manager.getCombatManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
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

        if(abilityReadyInMap.get(player.getUniqueId()) > 0){
            return;
        }

        combatManager.startCombatTimer(player);

        execute(player);

        abilityReadyInMap.put(player.getUniqueId(), 40);
        new BukkitRunnable(){
            @Override
            public void run(){

                if(abilityReadyInMap.get(player.getUniqueId()) <= 0){
                    this.cancel();
                    return;
                }

                int cooldown = abilityReadyInMap.get(player.getUniqueId()) - 1;

                abilityReadyInMap.put(player.getUniqueId(), cooldown);

            }
        }.runTaskTimer(main, 0,20);

    }

    private void execute(Player player){

        LivingEntity target = targetManager.getPlayerTarget(player);

        Location start = target.getLocation().clone();

        double skillLevel = profileManager.getAnyProfile(player).getStats().getLevel();
        double skillDamage = 5;

        new BukkitRunnable(){
            final Set<LivingEntity> effectedByThisStorm = new HashSet<>();
            final Set<ArmorStand> allStands = new HashSet<>();
            int ran = 0;
            @Override
            public void run(){

                Set<LivingEntity> hitBySkill = new HashSet<>();

                BoundingBox hitBox = new BoundingBox(
                        start.getX() - 6,
                        start.getY() - 2,
                        start.getZ() - 6,
                        start.getX() + 6,
                        start.getY() + 4,
                        start.getZ() + 6
                );

                if(ran%2==0){
                    double increment = (2 * Math.PI) / 16;
                    for (int i = 0; i < 16; i++) {
                        double angle = i * increment;
                        double x = start.getX() + (6 * Math.cos(angle));
                        double z = start.getZ() + (6 * Math.sin(angle));
                        Location loc = new Location(start.getWorld(), x, (start.getY()), z);

                        target.getWorld().spawnParticle(Particle.SNOWFLAKE, loc, 1, 0, 0, 0, 0);
                    }
                }

                if(ran%10 == 0){
                    //summon armorstand with random offset from start, max 6 blocks x or Z

                    int randomNumberX = new Random().nextInt(9) - 4;
                    int randomNumberZ = new Random().nextInt(9) - 4;

                    Location spawnLoc = start.clone().add(randomNumberX, 8, randomNumberZ);

                    ArmorStand armorStand = spawnLoc.getWorld().spawn(spawnLoc, ArmorStand.class);
                    armorStand.setInvisible(true);
                    armorStand.setGravity(false);
                    armorStand.setCollidable(false);
                    armorStand.setInvulnerable(true);
                    armorStand.setMarker(true);

                    EntityEquipment entityEquipment = armorStand.getEquipment();

                    ItemStack boltItem = new ItemStack(Material.DRAGON_BREATH);
                    ItemMeta meta = boltItem.getItemMeta();
                    assert meta != null;
                    meta.setCustomModelData(2);
                    boltItem.setItemMeta(meta);
                    assert entityEquipment != null;
                    entityEquipment.setHelmet(boltItem);

                    allStands.add(armorStand);

                    Location randomLoc = spawnLoc.clone().subtract(0, 8, 0);

                    Iterator<LivingEntity> iterator = effectedByThisStorm.iterator();

                    int size = effectedByThisStorm.size();

                    if(size !=0){
                        Random random = new Random();
                        int randomIndex = random.nextInt(size);

                        for (int i = 0; i < randomIndex; i++) {
                            iterator.next();
                        }

                        LivingEntity randomEntity = iterator.next();
                        randomLoc = randomEntity.getLocation();
                    }


                    Location finalRandomLoc = randomLoc;
                    new BukkitRunnable(){
                        int ran = 0;
                        final Location end = finalRandomLoc.clone().subtract(0,2,0);
                        @Override
                        public void run(){

                            Location current = armorStand.getLocation();

                            Vector direction = end.toVector().subtract(current.toVector());
                            double distance = current.distance(end);
                            double distanceThisTick = Math.min(distance, .75);
                            current.add(direction.normalize().multiply(distanceThisTick));

                            armorStand.teleport(current);

                            if (distance <= 1 || ran >=60) {
                                this.cancel();
                                armorStand.remove();

                                BoundingBox hitBox = new BoundingBox(
                                        end.getX() - 2,
                                        end.getY() - 4,
                                        end.getZ() - 2,
                                        end.getX() + 2,
                                        end.getY() + 4,
                                        end.getZ() + 2
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

                                    boolean crit = damageCalculator.checkIfCrit(player, 0);
                                    double damage = (damageCalculator.calculateDamage(player, livingEntity, "Magical", skillDamage * skillLevel, crit));

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

                            ran ++;

                        }
                    }.runTaskTimer(main, 0, 1);

                }


                for (Entity entity : player.getWorld().getNearbyEntities(hitBox)) {


                    if(!(entity instanceof LivingEntity)){
                        continue;
                    }

                    if(entity == player){
                        continue;
                    }

                    if(entity instanceof ArmorStand){
                        continue;
                    }

                    LivingEntity livingEntity = (LivingEntity) entity;

                    if (entity instanceof Player) {
                        if(!pvpManager.pvpLogic(player, (Player) livingEntity)){
                            continue;
                        }
                    }

                    if(!(entity instanceof Player)){

                        if(!pveChecker.pveLogic(livingEntity)){
                            continue;
                        }

                        Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(livingEntity, player));
                    }



                    hitBySkill.add(livingEntity);
                    effectedByThisStorm.add(livingEntity);
                    effectedByAnyStorm.put(livingEntity.getUniqueId(), true);
                }

                for(LivingEntity thisEntity : effectedByThisStorm){
                    if(!hitBySkill.contains(thisEntity)){
                        //not in the hitbox
                        effectedByThisStorm.remove(thisEntity);
                        effectedByAnyStorm.remove(thisEntity.getUniqueId());
                    }

                    //when status implemented, slow
                }


                if(ran >= 200){
                    cancelTask();
                }

                ran++;
            }

            private void cancelTask() {
                this.cancel();
                //remove all armorstands
                for(ArmorStand stand : allStands){
                    stand.remove();
                }
                //remove all effected by this
                for(LivingEntity entity : effectedByThisStorm){
                    effectedByAnyStorm.remove(entity.getUniqueId());
                }
            }

        }.runTaskTimer(main, 0, 1);

    }

    public boolean getIfEntityEffected(LivingEntity entity){
        return effectedByAnyStorm.getOrDefault(entity.getUniqueId(), false);
    }

    public int getCooldown(Player player){

        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

}
