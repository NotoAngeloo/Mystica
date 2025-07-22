package me.angeloo.mystica.Components.Abilities.Mystic;

import me.angeloo.mystica.Components.Abilities.MysticAbilities;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.Hud.CooldownDisplayer;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;
import me.angeloo.mystica.Utility.Logic.PveChecker;
import me.angeloo.mystica.Utility.Enums.SubClass;
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
import org.bukkit.util.Vector;

import java.util.*;

public class LightSigil {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final DamageCalculator damageCalculator;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final CooldownDisplayer cooldownDisplayer;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    private final Mana mana;
    private final PurifyingBlast purifyingBlast;

    public LightSigil(Mystica main, AbilityManager manager, MysticAbilities mysticAbilities){
        this.main = main;
        profileManager = main.getProfileManager();
        combatManager = manager.getCombatManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
        damageCalculator = main.getDamageCalculator();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
        mana = mysticAbilities.getMana();
        purifyingBlast = mysticAbilities.getPurifyingBlast();
    }

    public void use(LivingEntity caster){
        if (!abilityReadyInMap.containsKey(caster.getUniqueId())) {
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }

        if(!usable(caster)){
            return;
        }

        mana.subTractManaFromEntity(caster, getCost());

        combatManager.startCombatTimer(caster);

        execute(caster);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), 20);
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {

                if (getCooldown(caster) <= 0) {
                    cooldownDisplayer.displayCooldown(caster, 8);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(caster) - 1;

                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(caster);

                abilityReadyInMap.put(caster.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(caster, 8);

            }
        }.runTaskTimerAsynchronously(main, 0, 20);
        cooldownTask.put(caster.getUniqueId(), task);
    }

    //perhaps made it shoot at enemies too

    private void execute(LivingEntity caster){

        boolean shepard = profileManager.getAnyProfile(caster).getPlayerSubclass().equals(SubClass.Shepard);

        purifyingBlast.queueInstantCast(caster);

        Location spawnStart = caster.getLocation().clone();

        ArmorStand sigil = caster.getWorld().spawn(spawnStart, ArmorStand.class);
        sigil.setInvisible(true);
        sigil.setGravity(false);
        sigil.setCollidable(false);
        sigil.setInvulnerable(true);
        sigil.setMarker(true);

        EntityEquipment entityEquipment = sigil.getEquipment();

        ItemStack item = new ItemStack(Material.SPECTRAL_ARROW);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(12);
        item.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setHelmet(item);

        Location current = sigil.getLocation();

        new BukkitRunnable(){
            Vector initialDirection;
            int angle = 0;
            int ran = 0;
            @Override
            public void run(){

                if (initialDirection == null) {
                    initialDirection = current.getDirection().setY(0).normalize();
                }

                Vector rotation = initialDirection.clone();
                double radians = Math.toRadians(angle);
                rotation.rotateAroundY(radians);
                current.setDirection(rotation);

                sigil.teleport(current);

                double increment = (2 * Math.PI) / 30; // angle between particles

                for (int i = 0; i < 30; i++) {
                    double angle = i * increment;
                    double x = current.getX() + (10 * Math.cos(angle));
                    double z = current.getZ() + (10 * Math.sin(angle));
                    Location loc = new Location(sigil.getWorld(), x, current.clone().add(0,.5,0).getY(), z);

                    caster.getWorld().spawnParticle(Particle.WAX_OFF, loc, 1, 0, 0, 0, 0);
                }

                if(ran%20==0){

                    BoundingBox hitBox = new BoundingBox(
                            current.getX() - 10,
                            current.getY() - 10,
                            current.getZ() - 10,
                            current.getX() + 10,
                            current.getY() + 10,
                            current.getZ() + 10
                    );

                    Set<LivingEntity> hitByHeal = new HashSet<>();
                    Set<LivingEntity> hitByDamage = new HashSet<>();

                    if(shepard){
                        for (Entity entity : caster.getWorld().getNearbyEntities(hitBox)) {

                            if(!(entity instanceof LivingEntity)){
                                continue;
                            }

                            if(entity instanceof ArmorStand){
                                continue;
                            }

                            LivingEntity hitEntity = (LivingEntity) entity;

                            if(profileManager.getAnyProfile(hitEntity).getIfObject()){
                                continue;
                            }

                            if(hitEntity instanceof Player){
                                if (pvpManager.pvpLogic(caster, (Player) hitEntity)) {
                                    continue;
                                }
                            }

                            if(!(entity instanceof Player)){
                                if(pveChecker.pveLogic(hitEntity)){
                                    continue;
                                }
                            }

                            hitByHeal.add(hitEntity);

                        }


                        for(LivingEntity hitEntity : hitByHeal){

                            if(hitEntity.getLocation().distance(current) <= 10){
                                shootHealAtEntity(caster, sigil, hitEntity);
                            }

                        }



                    }
                    else{
                        for (Entity entity : caster.getWorld().getNearbyEntities(hitBox)) {

                            if(!(entity instanceof LivingEntity)){
                                continue;
                            }

                            if(entity instanceof ArmorStand){
                                continue;
                            }

                            LivingEntity thisEntity = (LivingEntity) entity;

                            if(entity instanceof  Player){
                                if(!pvpManager.pvpLogic(caster, (Player) thisEntity)) {
                                    continue;
                                }
                            }

                            if(!pveChecker.pveLogic(thisEntity)){
                                continue;
                            }

                            hitByDamage.add(thisEntity);

                            for(LivingEntity livingEntity : hitByDamage){
                                shootDamageAtEntity(caster, sigil, livingEntity);
                            }

                        }
                    }


                }

                if(ran>=20*15){
                    this.cancel();
                    sigil.remove();
                }

                angle += 5;
                ran++;
            }



        }.runTaskTimer(main, 0, 1);

    }

    private void shootDamageAtEntity(LivingEntity caster, ArmorStand sigil, LivingEntity damagedEntity){

        Location start = sigil.getLocation();
        start.subtract(0, 1, 0);
        ArmorStand armorStand = caster.getWorld().spawn(start, ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);

        EntityEquipment entityEquipment = armorStand.getEquipment();

        ItemStack bolt = new ItemStack(Material.SPECTRAL_ARROW);
        ItemMeta meta = bolt.getItemMeta();
        assert meta != null;

        meta.setCustomModelData(1);

        bolt.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setHelmet(bolt);


        double finalSkillDamage = getSkillDamage(caster);
        new BukkitRunnable(){
            Location targetWasLoc = damagedEntity.getLocation().clone().subtract(0,1,0);
            @Override
            public void run(){

                if(targetStillValid(damagedEntity)){
                    Location targetLoc = damagedEntity.getLocation().clone().subtract(0,1,0);
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

                if(distanceThisTick !=0){
                    current.add(direction.normalize().multiply(distanceThisTick));
                }

                current.setDirection(direction);

                armorStand.teleport(current);

                if (distance <= 1) {

                    cancelTask();

                    boolean crit = damageCalculator.checkIfCrit(caster, 0);
                    double damage = damageCalculator.calculateDamage(caster, damagedEntity, "Magical", finalSkillDamage, crit);
                    Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(damagedEntity, caster));
                    changeResourceHandler.subtractHealthFromEntity(damagedEntity, damage, caster, crit);

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

            private boolean sameWorld(Location loc1, Location loc2) {
                return loc1.getWorld().equals(loc2.getWorld());
            }

            private void cancelTask() {
                this.cancel();
                armorStand.remove();
            }
        }.runTaskTimer(main, 0, 1);

    }

    private void shootHealAtEntity(LivingEntity caster, ArmorStand sigil, LivingEntity healedEntity){

        Location start = sigil.getLocation();
        start.subtract(0, 1, 0);
        ArmorStand armorStand = caster.getWorld().spawn(start, ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);

        EntityEquipment entityEquipment = armorStand.getEquipment();

        ItemStack bolt = new ItemStack(Material.SPECTRAL_ARROW);
        ItemMeta meta = bolt.getItemMeta();
        assert meta != null;

        meta.setCustomModelData(1);

        bolt.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setHelmet(bolt);

        double healPower = 5;
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_8_Level_Bonus();
        healPower = healPower +  ((int)(skillLevel/10));


        double finalHealPower = healPower;
        new BukkitRunnable(){
            Location targetWasLoc = healedEntity.getLocation().clone().subtract(0,1,0);
            @Override
            public void run(){

                if(targetStillValid(healedEntity)){
                    Location targetLoc = healedEntity.getLocation().clone().subtract(0,1,0);
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

                if(distanceThisTick !=0){
                    current.add(direction.normalize().multiply(distanceThisTick));
                }

                current.setDirection(direction);

                armorStand.teleport(current);

                if (distance <= 1) {

                    cancelTask();

                    boolean crit = damageCalculator.checkIfCrit(caster, 0);

                    double healAmount  = damageCalculator.calculateHealing(caster, finalHealPower, crit);

                    changeResourceHandler.addHealthToEntity(healedEntity, healAmount, caster);

                    Location center = healedEntity.getLocation().clone().add(0,1,0);

                    double increment = (2 * Math.PI) / 16; // angle between particles

                    for (int i = 0; i < 16; i++) {
                        double angle = i * increment;
                        double x = center.getX() + (1 * Math.cos(angle));
                        double z = center.getZ() + (1 * Math.sin(angle));
                        Location loc = new Location(center.getWorld(), x, (center.getY()), z);

                        healedEntity.getWorld().spawnParticle(Particle.WAX_OFF, loc, 1,0, 0, 0, 0);
                    }

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

            private boolean sameWorld(Location loc1, Location loc2) {
                return loc1.getWorld().equals(loc2.getWorld());
            }

            private void cancelTask() {
                this.cancel();
                armorStand.remove();
            }
        }.runTaskTimer(main, 0, 1);

    }

    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_8_Level_Bonus();
        return 14 +  ((int)(skillLevel/3));
    }

    public int getCost(){
        return 20;
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

    public boolean usable(LivingEntity caster){
        if (getCooldown(caster) > 0) {
            return false;
        }

        return mana.getCurrentMana(caster) >= getCost();
    }

    public int returnWhichItem(Player player){

        if(mana.getCurrentMana(player)<getCost()){
            return 7;
        }

        return 0;
    }

}
