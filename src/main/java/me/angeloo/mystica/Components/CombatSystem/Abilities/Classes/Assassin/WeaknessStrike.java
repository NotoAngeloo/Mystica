package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Assassin;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.PvpManager;
import me.angeloo.mystica.Components.CombatSystem.TargetManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;
import me.angeloo.mystica.Utility.Enums.DamageType;
import me.angeloo.mystica.Utility.Enums.PlayerClass;
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
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class WeaknessStrike extends BaseAbility {

    private final Mystica main;
    private final ProfileManager profileManager;
    private final TargetManager targetManager;
    private final StatusEffectManager statusEffectManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final DamageCalculator damageCalculator;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final CooldownManager cooldownManager;

    private final Combo combo;


    public WeaknessStrike(Mystica main, AbilityManager manager){
        super("weakness_strike");
        this.main = main;
        targetManager = main.getTargetManager();
        profileManager = main.getProfileManager();
        statusEffectManager = main.getStatusEffectManager();
        changeResourceHandler = main.getChangeResourceHandler();
        damageCalculator = main.getDamageCalculator();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        cooldownManager = main.getCooldownManager();
        combo = manager.getCombo();
    }

    private final int baseCooldown = 4;
    private final double range = 7;
    private final int baseDamage = 30;

    @Override
    public boolean use(LivingEntity caster){


        targetManager.setTargetToNearestValid(caster, range);
        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(!usable(caster, target)){
            return false;
        }

        execute(caster);

        cooldownManager.start(caster.getUniqueId(), 3, (long) (baseCooldown * 1000));
        return true;
    }

    @Override
    public int cooldown() {
        return baseCooldown;
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

                if(!statusEffectManager.canCast(caster)){
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
                        double damage = damageCalculator.calculateDamage(caster, target, DamageType.Physical, finalSkillDamage, crit, 0);

                        Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, caster));
                        changeResourceHandler.subtractHealthFromEntity(target, damage, caster, crit);
                        lookup.get(PlayerClass.Assassin, 8).onExternalTrigger(caster, target);

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
        return baseDamage + ((int)(skillLevel/3));
    }


    //grey out if not ready
    public int returnWhichItem(Player player){

        if(combo.getComboPoints(player) == 0){
            return 1;
        }

        return 0;
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


        if(combo.getComboPoints(caster) == 0){
            return false;
        }

        return cooldownManager.isReady(caster.getUniqueId(), 3, statusEffectManager.getHastePercent(caster));
    }

    @Override
    public String skillBarIcon(LivingEntity entity) {

        if(combo.getComboPoints(entity) == 0){
            return "\ue3c2";
        }

        return "\ue3c1";
    }
}
