package me.angeloo.mystica.Components.Abilities.Assassin;

import me.angeloo.mystica.Components.Abilities.AssassinAbilities;
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
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WeaknessStrike {

    private final Mystica main;
    private final ProfileManager profileManager;
    private final TargetManager targetManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final CombatManager combatManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final DamageCalculator damageCalculator;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final CooldownDisplayer cooldownDisplayer;

    private final Stealth stealth;
    private final Combo combo;

    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();
    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();

    public WeaknessStrike(Mystica main, AbilityManager manager, AssassinAbilities assassinAbilities){
        this.main = main;
        targetManager = main.getTargetManager();
        profileManager = main.getProfileManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        combatManager = manager.getCombatManager();
        changeResourceHandler = main.getChangeResourceHandler();
        damageCalculator = main.getDamageCalculator();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
        combo = assassinAbilities.getCombo();
        stealth = assassinAbilities.getStealth();
    }

    private final double range = 7;

    public void use(LivingEntity caster){

        if(!abilityReadyInMap.containsKey(caster.getUniqueId())){
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }

        targetManager.setTargetToNearestValid(caster, range);
        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(!usable(caster, target)){
            return;
        }

        changeResourceHandler.subTractManaFromEntity(caster, getCost());

        combatManager.startCombatTimer(caster);


        execute(caster);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), 4);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getCooldown(caster) <= 0){
                    cooldownDisplayer.displayCooldown(caster, 3);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(caster) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(caster);

                abilityReadyInMap.put(caster.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(caster, 3);

            }
        }.runTaskTimer(main, 0,20);
        cooldownTask.put(caster.getUniqueId(), task);

    }

    private void execute(LivingEntity caster){

        LivingEntity target = targetManager.getPlayerTarget(caster);

        Location start = caster.getLocation().clone();
        Location up = start.clone().add(0,3,0);

        double skillDamage = getSkillDamage(caster);

        skillDamage = skillDamage + (15 * combo.removeAnAmountOfPoints(caster, combo.getComboPoints(caster)));


        //abilityManager.setSkillRunning(player, true);
        double finalSkillDamage = skillDamage;

        new BukkitRunnable(){
            int angle = 180;
            int pause = 0;
            boolean goUp = true;
            Location current2;
            ArmorStand stand;
            ArmorStand stand2;
            @Override
            public void run(){

                if(caster instanceof Player){
                    if(!((Player) caster).isOnline()){
                        cancelTask();
                        return;
                    }
                }

                if(buffAndDebuffManager.getIfInterrupt(caster)){
                    cancelTask();
                    return;
                }

                if(!targetStillValid(target)){
                    cancelTask();
                    return;
                }

                Location current = caster.getLocation();
                Location targetLoc = target.getLocation().clone();
                Vector direction = targetLoc.toVector().subtract(current.toVector());
                direction.setY(0);
                Vector crossProduct = direction.clone().crossProduct(new Vector(0,1,0)).normalize();

                if(!goUp){
                    double distance = current.distance(targetLoc);
                    double distanceThisTick = Math.min(distance, .5);
                    Vector downDir = targetLoc.toVector().subtract(current.toVector());

                    if(distanceThisTick!=0){
                        current.add(downDir.normalize().multiply(distanceThisTick));
                    }


                    if(distance<=1){

                        double increment = (2 * Math.PI) / 16; // angle between particles

                        for (int i = 0; i < 16; i++) {
                            double angle = i * increment;
                            double x = current.getX() + (2 * Math.cos(angle));
                            double y = current.getY() + 1;
                            double z = current.getZ() + (2 * Math.sin(angle));
                            Location loc = new Location(current.getWorld(), x, y, z);
                            caster.getWorld().spawnParticle(Particle.CRIT_MAGIC, loc, 1,0, 0, 0, 0);
                        }

                        //also damage
                        boolean crit = damageCalculator.checkIfCrit(caster, 0);
                        double damage = damageCalculator.calculateDamage(caster, target, "Physical", finalSkillDamage, crit);

                        Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, caster));
                        changeResourceHandler.subtractHealthFromEntity(target, damage, caster);
                        stealth.stealthBonusCheck(caster, target);

                        cancelTask();
                    }

                }

                if(goUp){
                    double distance = current.distance(up);
                    double distanceThisTick = Math.min(distance, .5);
                    Vector upDir = up.toVector().subtract(current.toVector());

                    if(distance>1){
                        current.add(upDir.normalize().multiply(distanceThisTick));
                    }

                    if(distance<=1){

                        if(pause>=2){
                            goUp = false;

                            ItemStack item = new ItemStack(Material.SLIME_BALL);
                            ItemMeta meta = item.getItemMeta();
                            assert meta != null;
                            meta.setCustomModelData(1);
                            item.setItemMeta(meta);

                            current2 = current.clone();

                            Location s1Loc = current2.clone().add(crossProduct.clone().multiply(1.25));

                            stand = caster.getWorld().spawn(s1Loc.clone().subtract(0,10,0), ArmorStand.class);
                            stand.setInvisible(true);
                            stand.setGravity(false);
                            stand.setCollidable(false);
                            stand.setInvulnerable(true);
                            stand.setMarker(true);
                            EntityEquipment entityEquipment = stand.getEquipment();
                            assert entityEquipment != null;
                            entityEquipment.setItemInMainHand(item);
                            stand.setRightArmPose(new EulerAngle(Math.toRadians(angle), Math.toRadians(0), Math.toRadians(0)));
                            stand.teleport(s1Loc);

                            Location s2Loc = current2.clone().subtract(crossProduct.clone().multiply(1.25));

                            stand2 = caster.getWorld().spawn(s2Loc.clone().subtract(0,10,0), ArmorStand.class);
                            stand2.setInvisible(true);
                            stand2.setGravity(false);
                            stand2.setCollidable(false);
                            stand2.setInvulnerable(true);
                            stand2.setMarker(true);
                            EntityEquipment entityEquipment2 = stand2.getEquipment();
                            assert entityEquipment2 != null;
                            entityEquipment2.setItemInOffHand(item);
                            stand2.setLeftArmPose(new EulerAngle(Math.toRadians(angle), Math.toRadians(0), Math.toRadians(0)));
                            stand2.teleport(s2Loc);

                        }

                        pause++;
                    }

                }

                current.setDirection(direction);

                caster.teleport(current);

                current2 = current.clone();

                if(stand != null){
                    angle+=20;
                    Location s1Loc = current2.clone().add(crossProduct.clone().multiply(1.25));
                    stand.setRightArmPose(new EulerAngle(Math.toRadians(angle), Math.toRadians(0), Math.toRadians(0)));
                    stand.teleport(s1Loc);
                }

                if(stand2 != null){
                    Location s2Loc = current2.clone().subtract(crossProduct.clone().multiply(1.25));
                    stand2.setLeftArmPose(new EulerAngle(Math.toRadians(angle), Math.toRadians(0), Math.toRadians(0)));
                    stand2.teleport(s2Loc);
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

            private void cancelTask(){
                this.cancel();
                //abilityManager.setSkillRunning(player, false);

                if(stand!=null){
                    stand.remove();
                }

                if(stand2!=null){
                    stand2.remove();
                }
            }

        }.runTaskTimer(main, 0, 1);

    }

    public double getSkillDamage(LivingEntity caster){

        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_3_Level_Bonus();
        return 30 + ((int)(skillLevel/3));
    }

    public double getCost(){
        return 20;
    }

    public int getCooldown(LivingEntity caster){
        int cooldown = abilityReadyInMap.getOrDefault(caster.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

    public int returnWhichItem(Player player){

        if(combo.getComboPoints(player) == 0){
            return 1;
        }

        return 0;
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

            if(distance > range){
                return false;
            }
        }

        if(target == null){
            return false;
        }


        if(getCooldown(caster) > 0){
            return false;
        }

        if(combo.getComboPoints(caster) == 0){
            return false;
        }


        if(profileManager.getAnyProfile(caster).getCurrentMana()<getCost()){
            return false;
        }

        return true;
    }

}
