package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Ranger;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.RangerAbilities;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.DamageModifiers.Haste;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.PvpManager;
import me.angeloo.mystica.Components.CombatSystem.TargetManager;
import me.angeloo.mystica.Components.Hud.CooldownDisplayer;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;
import me.angeloo.mystica.Utility.Enums.PlayerClass;
import me.angeloo.mystica.Utility.Enums.SubClass;
import me.angeloo.mystica.Utility.Logic.PveChecker;
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

public class BitingRain extends BaseAbility {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final StatusEffectManager statusEffectManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final CooldownManager cooldownManager;

    private final Focus focus;

    public BitingRain(Mystica main, AbilityManager manager){
        super("biting_rain");
        this.main = main;
        profileManager = main.getProfileManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        statusEffectManager = main.getStatusEffectManager();
        changeResourceHandler = main.getChangeResourceHandler();
        cooldownManager = manager.getCooldownManager();
        focus = manager.getFocus();
    }

    private final int baseCooldown = 10;
    private final double range = 20;
    private final int baseDamage = 20;

    @Override
    public void use(LivingEntity caster){

        targetManager.setTargetToNearestValid(caster, range + statusEffectManager.getAdditionalRange(caster));

        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(!usable(caster, target)){
            return;
        }

        execute(caster);

        cooldownManager.start(caster.getUniqueId(), 1, (long) (baseCooldown * 1000));

    }

    private void execute(LivingEntity caster){

        boolean scout = profileManager.getAnyProfile(caster).getPlayerSubclass().equals(SubClass.Scout);

        LivingEntity target = targetManager.getPlayerTarget(caster);

        Location start = target.getLocation().clone();
        Location upstart = start.clone().add(0, 8, 0);

        Set<LivingEntity> hitBySkill = new HashSet<>();



        double finalSkillDamage = getSkillDamage(caster);
        new BukkitRunnable(){
            final Set<ArmorStand> allStands = new HashSet<>();
            int count = 0;
            @Override
            public void run(){

                if(caster instanceof Player){
                    if(!((Player)caster).isOnline()){
                        cancelTask();
                        return;
                    }
                }

                if(count <=5){
                    Location playerLoc = caster.getLocation();

                    ArmorStand armorStand = caster.getWorld().spawn(playerLoc, ArmorStand.class);

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

                    for (Entity entity : caster.getWorld().getNearbyEntities(hitBox)) {

                        if(entity == caster){
                            continue;
                        }

                        if(!(entity instanceof LivingEntity livingEntity)){
                            continue;
                        }

                        if(entity instanceof ArmorStand){
                            continue;
                        }

                        if(hitBySkill.contains(livingEntity)){
                            continue;
                        }

                        hitBySkill.add(livingEntity);

                        boolean crit = damageCalculator.checkIfCrit(caster, 0);

                        if(scout && crit){
                            lookup.get(PlayerClass.Ranger,SubClass.Scout,-1).onExternalTrigger(caster);
                            statusEffectManager.applyEffect(caster, new Haste(), 2*20, 0.1);
                        }

                        double damage = (damageCalculator.calculateDamage(caster, livingEntity, "Physical", finalSkillDamage, crit));

                        //pvp logic
                        if(entity instanceof Player){
                            if(pvpManager.pvpLogic(caster, (Player) entity)){
                                changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, caster, crit);
                            }
                            continue;
                        }

                        if(pveChecker.pveLogic(livingEntity)){
                            Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(livingEntity, caster));
                            changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, caster, crit);
                        }

                    }
                }

                if(count > 5){

                    int randomNumberX = new Random().nextInt(9) - 4;
                    int randomNumberZ = new Random().nextInt(9) - 4;

                    Location spawnLoc = upstart.clone().add(randomNumberX, 8, randomNumberZ);

                    ArmorStand armorStand = caster.getWorld().spawn(spawnLoc, ArmorStand.class);
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

    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_1_Level_Bonus();
        return focus.calculateFocusMultipliedDamage(caster, baseDamage) + ((int)(skillLevel/3));
    }

    @Override
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

            if(distance > range + statusEffectManager.getAdditionalRange(caster)){
                return false;
            }
        }

        if(target == null){
            return false;
        }

        return cooldownManager.isReady(caster.getUniqueId(), 1, statusEffectManager.getHastePercent(caster));
    }

}
