package me.angeloo.mystica.Components.Abilities.Paladin;

import me.angeloo.mystica.Components.Abilities.PaladinAbilities;
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CovenantSword {

    private final Mystica main;
    private final ProfileManager profileManager;
    private final TargetManager targetManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final CombatManager combatManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final DamageCalculator damageCalculator;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final AbilityManager abilityManager;
    private final CooldownDisplayer cooldownDisplayer;

    private final Decision decision;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public CovenantSword(Mystica main, AbilityManager manager, PaladinAbilities paladinAbilities){
        this.main = main;
        abilityManager = manager;
        targetManager = main.getTargetManager();
        profileManager = main.getProfileManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        combatManager = manager.getCombatManager();
        changeResourceHandler = main.getChangeResourceHandler();
        damageCalculator = main.getDamageCalculator();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
        decision = paladinAbilities.getDecision();
    }

    public void use(Player player){

        if(!abilityReadyInMap.containsKey(player.getUniqueId())){
            abilityReadyInMap.put(player.getUniqueId(), 0);
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

        abilityReadyInMap.put(player.getUniqueId(), 20);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getCooldown(player) <= 0){
                    cooldownDisplayer.displayCooldown(player, 4);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(player) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(player, 4);

            }
        }.runTaskTimer(main, 0,20);
        cooldownTask.put(player.getUniqueId(), task);

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

        ArmorStand sword = player.getWorld().spawn(start.clone().subtract(0,5,0), ArmorStand.class);
        sword.setInvisible(true);
        sword.setGravity(false);
        sword.setCollidable(false);
        sword.setInvulnerable(true);
        sword.setMarker(true);

        EntityEquipment entityEquipment = sword.getEquipment();

        ItemStack item = new ItemStack(Material.SUGAR);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(5);
        item.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setItemInMainHand(item);

        sword.setRightArmPose(new EulerAngle(Math.toRadians(0), Math.toRadians(0), Math.toRadians(0)));

        sword.teleport(start);



        abilityManager.setCasting(player, true);

        boolean finalTargeted = targeted;
        double finalSkillDamage = getSkillDamage(player);
        new BukkitRunnable(){
            boolean stage1 = false;
            Vector initialDirection;
            double angle = 0;
            int ea1 = 0;
            @Override
            public void run(){

                if(!player.isOnline()){
                    cancelTask();
                    return;
                }

                if (initialDirection == null) {
                    initialDirection = player.getLocation().getDirection().setY(0).normalize();
                }

                Location center = player.getLocation();

                Vector direction = initialDirection.clone();
                double radians = Math.toRadians(angle);
                direction.rotateAroundY(radians);

                Vector dir = player.getLocation().getDirection().setY(0).normalize();;

                if(stage1){

                    if(finalTargeted && targetStillValid(target)){
                        dir = target.getLocation().toVector().subtract(player.getLocation().toVector()).setY(0).normalize();
                    }

                    direction = dir.multiply(-1);

                }

                Location loc = center.clone().add(direction.clone().multiply(1)).setDirection(direction);
                sword.teleport(loc);

                if(stage1){
                    ea1-=15;
                }

                sword.setRightArmPose(new EulerAngle(Math.toRadians(ea1), Math.toRadians(0), Math.toRadians(0)));


                if(!stage1){
                    if(angle<180){
                        angle+=20;
                    }

                    if(angle>=180){
                        stage1 = true;
                    }
                }

                if(ea1 < -180){

                    Location cLoc = sword.getLocation().clone().add(dir.multiply(-5));

                    double increment = (2 * Math.PI) / 16; // angle between particles

                    for(int j = 1; j<5; j++){
                        for (int i = 0; i < 16; i++) {
                            double angle = i * increment;
                            double x = cLoc.getX() + (j * Math.cos(angle));
                            double z = cLoc.getZ() + (j * Math.sin(angle));
                            Location ploc = new Location(cLoc.getWorld(), x, (cLoc.getY()), z);

                            player.getWorld().spawnParticle(Particle.LAVA, ploc, 1,0, 0, 0, 0);
                        }
                    }

                    ignite(cLoc);

                    BoundingBox hitBox = new BoundingBox(
                            cLoc.getX() - 5,
                            cLoc.getY() - 2,
                            cLoc.getZ() - 5,
                            cLoc.getX() + 5,
                            cLoc.getY() + 5,
                            cLoc.getZ() + 5
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

                        double damage = (damageCalculator.calculateDamage(player, livingEntity, "Physical", finalSkillDamage, crit));
                        damage = damage * decisionMultiplier(player);

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

                    cancelTask();
                }

                double percent = ((Math.abs(angle) + (Math.abs(ea1))) / 195) * 100;

                abilityManager.setCastBar(player, percent);

            }

            private boolean targetStillValid(LivingEntity target){

                if(target instanceof Player){
                    if(!((Player)target).isOnline()){
                        return false;
                    }

                    if(profileManager.getAnyProfile(target).getIfDead()){
                        return false;
                    }
                }

                if(target.isDead()){
                    return false;
                }

                return target.getLocation().getWorld() == player.getWorld();
            }

            private void ignite(Location center){

                double skillDamage = 5;
                double skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) +
                        profileManager.getAnyProfile(player).getSkillLevels().getSkill_4_Level_Bonus();

                new BukkitRunnable(){
                    int count = 0;
                    @Override
                    public void run(){
                        double increment = (2 * Math.PI) / 16; // angle between particles

                        for(int j = 1; j<5; j++){
                            for (int i = 0; i < 16; i++) {
                                double angle = i * increment;
                                double x = center.getX() + (j * Math.cos(angle));
                                double z = center.getZ() + (j * Math.sin(angle));
                                Location ploc = new Location(center.getWorld(), x, (center.getY()), z);

                                player.getWorld().spawnParticle(Particle.LAVA, ploc, 1,0, 0, 0, 0);
                            }
                        }

                        BoundingBox hitBox = new BoundingBox(
                                center.getX() - 5,
                                center.getY() - 2,
                                center.getZ() - 5,
                                center.getX() + 5,
                                center.getY() + 5,
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

                            boolean crit = damageCalculator.checkIfCrit(player, 0);

                            double damage = (damageCalculator.calculateDamage(player, livingEntity, "Physical", skillDamage * skillLevel, crit));

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

                        if(count>=3){
                            this.cancel();
                        }

                        count++;
                    }
                }.runTaskTimer(main, 20, 20);

            }

            private void cancelTask(){
                this.cancel();
                sword.remove();
                abilityManager.setCasting(player, false);
                decision.removeDecision(player);
            }

        }.runTaskTimer(main, 0, 1);

    }

    private double decisionMultiplier(Player player){

        if(decision.getDecision(player)){
            return 1.8;
        }

        return 1;
    }

    public double getSkillDamage(Player player){
        double skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) +
                profileManager.getAnyProfile(player).getSkillLevels().getSkill_4_Level_Bonus();
        return 40 + ((int)(skillLevel/3));
    }

    public double getCost(){
        return 20;
    }

    public int getCooldown(Player player){
        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

}
