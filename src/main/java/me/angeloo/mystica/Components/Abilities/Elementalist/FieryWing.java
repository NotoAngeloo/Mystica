package me.angeloo.mystica.Components.Abilities.Elementalist;

import me.angeloo.mystica.Components.Abilities.ElementalistAbilities;
import me.angeloo.mystica.CustomEvents.HudUpdateEvent;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.CustomEvents.UltimateStatusChageEvent;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;
import me.angeloo.mystica.Utility.Logic.PveChecker;
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
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FieryWing {

    private final Mystica main;
    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;

    private final Heat heat;

    private final Map<UUID, Integer> inflameMap = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();
    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();

    public FieryWing(Mystica main, AbilityManager manager, ElementalistAbilities elementalistAbilities){
        this.main = main;
        profileManager = main.getProfileManager();
        combatManager = manager.getCombatManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
        heat = elementalistAbilities.getHeat();
    }

    private final double range = 20;

    public void use(LivingEntity caster){

        if(!abilityReadyInMap.containsKey(caster.getUniqueId())){
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }

        targetManager.setTargetToNearestValid(caster, range + buffAndDebuffManager.getTotalRangeModifier(caster));

        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(!usable(caster, target)){
            return;
        }


        combatManager.startCombatTimer(caster);

        execute(caster);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), getSkillCooldown());
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getPlayerCooldown(caster) <= 0){
                    this.cancel();
                    return;
                }

                int cooldown = getPlayerCooldown(caster) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(caster);

                abilityReadyInMap.put(caster.getUniqueId(), cooldown);
                if(caster instanceof Player){
                    Bukkit.getServer().getPluginManager().callEvent(new UltimateStatusChageEvent((Player) caster));
                }

            }
        }.runTaskTimerAsynchronously(main, 0,20);
        cooldownTask.put(caster.getUniqueId(), task);

    }

    private void execute(LivingEntity caster){

        heat.addHeat(caster, 10);

        LivingEntity target = targetManager.getPlayerTarget(caster);

        Location start = caster.getLocation();
        start.subtract(0, 1, 0);


        ArmorStand spawnTexture = caster.getWorld().spawn(start, ArmorStand.class);
        spawnTexture.setInvisible(true);
        spawnTexture.setGravity(false);
        spawnTexture.setCollidable(false);
        spawnTexture.setInvulnerable(true);
        spawnTexture.setMarker(true);

        EntityEquipment entityEquipment2 = spawnTexture.getEquipment();

        ItemStack spawnItem = new ItemStack(Material.DRAGON_BREATH);
        ItemMeta meta2 = spawnItem.getItemMeta();
        assert meta2 != null;
        meta2.setCustomModelData(7);
        spawnItem.setItemMeta(meta2);
        assert entityEquipment2 != null;
        entityEquipment2.setHelmet(spawnItem);

        //abilityManager.setSkillRunning(player, true);
        double finalSkillDamage = getSkillDamage(caster);
        new BukkitRunnable(){
            boolean spawned = false;
            int ran = 0;
            public void run(){

                spawnTexture.teleport(caster.getLocation().clone().subtract(0,1,0));

                if(ran >= 10 && !spawned){

                    //abilityManager.setSkillRunning(player, false);
                    spawned = true;

                    ArmorStand armorStand = caster.getWorld().spawn(caster.getLocation().clone().subtract(0,1,0), ArmorStand.class);
                    armorStand.setInvisible(true);
                    armorStand.setGravity(false);
                    armorStand.setCollidable(false);
                    armorStand.setInvulnerable(true);
                    armorStand.setMarker(true);

                    EntityEquipment entityEquipment = armorStand.getEquipment();

                    ItemStack horseItem = new ItemStack(Material.DRAGON_BREATH);
                    ItemMeta meta = horseItem.getItemMeta();
                    assert meta != null;
                    meta.setCustomModelData(4);
                    horseItem.setItemMeta(meta);
                    assert entityEquipment != null;
                    entityEquipment.setHelmet(horseItem);

                    Location targetLoc = target.getLocation().clone().subtract(0,1,0);

                    new BukkitRunnable(){
                        int count = 0;
                        Location targetWasLoc = targetLoc.clone();
                        @Override
                        public void run(){

                            if(targetStillValid(target)){
                                Location targetLoc = target.getLocation();
                                targetLoc = targetLoc.subtract(0,1,0);
                                targetWasLoc = targetLoc.clone();
                            }

                            Location current = armorStand.getLocation();

                            if (!sameWorld(current, targetWasLoc)) {
                                cancelTask();
                                return;
                            }

                            Vector direction = targetWasLoc.toVector().subtract(current.toVector());
                            double distance = current.distance(targetWasLoc);
                            double distanceThisTick = Math.min(distance, .75);

                            if(distanceThisTick!=0){
                                current.add(direction.normalize().multiply(distanceThisTick));
                            }

                            current.setDirection(direction);

                            armorStand.teleport(current);


                            caster.getWorld().spawnParticle(Particle.FLAME, current.clone().add(0,1,0), 1, 0, 0, 0, 0);

                            if (distance <= 1) {

                                addInflame(caster);

                                cancelTask();

                                boolean crit = damageCalculator.checkIfCrit(caster, 0);
                                double damage = damageCalculator.calculateDamage(caster, target, "Magical", finalSkillDamage, crit);

                                Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, caster));
                                changeResourceHandler.subtractHealthFromEntity(target, damage, caster, crit);


                            }

                            if(count>100){
                                cancelTask();
                            }

                            count++;
                        }

                        private boolean targetStillValid(LivingEntity target){

                            if(target instanceof Player){

                                if(!((Player) target).isOnline()){
                                    return false;
                                }

                            }

                            return !target.isDead();
                        }

                        private boolean sameWorld(Location loc1, Location loc2) {
                            return loc1.getWorld().equals(loc2.getWorld());
                        }

                        private void cancelTask() {
                            this.cancel();
                            armorStand.remove();
                        }
                    }.runTaskTimer(main, 0, 1);

                }

                if(ran >= 30){
                    cancelTask();
                }

                ran++;
            }

            private void cancelTask() {
                this.cancel();
                spawnTexture.remove();
            }

        }.runTaskTimer(main, 0, 1);



    }

    public int getInflame(LivingEntity caster){

        if(!inflameMap.containsKey(caster.getUniqueId())){
            inflameMap.put(caster.getUniqueId(), 0);
        }

        return inflameMap.get(caster.getUniqueId());
    }

    public void addInflame(LivingEntity caster){

        boolean pyromancer = profileManager.getAnyProfile(caster).getPlayerSubclass().equalsIgnoreCase("pyromancer");

        if(!pyromancer){
            return;
        }

        int stacks = getInflame(caster);

        stacks ++;

        if(stacks >=4){
            abilityReadyInMap.put(caster.getUniqueId(), 0);
            removeInflame(caster);
            return;
        }

        inflameMap.put(caster.getUniqueId(), stacks);

        if(caster instanceof Player){
            Player player = (Player) caster;
            Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, "status", false));
        }

    }


    public void removeInflame(LivingEntity caster){
        inflameMap.put(caster.getUniqueId(), 0);

        if(caster instanceof Player){
            Player player = (Player) caster;
            Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, "status", false));
        }
    }

    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getStats().getLevel();
        return 60 + ((int)(skillLevel/3));
    }

    public int getSkillCooldown(){
        return 30;
    }

    public int getPlayerCooldown(LivingEntity caster){

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
            if(target instanceof Player){
                if(!pvpManager.pvpLogic(caster, (Player) target)){
                    return false;
                }
            }

            if(!(target instanceof Player)){
                if(!pveChecker.pveLogic(target)){
                    return false;
                }
            }

            double distance = caster.getLocation().distance(target.getLocation());

            if(distance > range + buffAndDebuffManager.getTotalRangeModifier(caster)){
                return false;
            }

            if(distance<1){
                return false;
            }

        }

        if(target == null){
            return false;
        }

        return getPlayerCooldown(caster) <= 0;
    }

}
