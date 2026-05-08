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
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class Assault extends BaseAbility {

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


    public Assault(Mystica main, AbilityManager manager){
        super("assault");
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

    private final double range = 4;

    public int baseCooldown = 4;

    private final int baseDamage = 30;

    @Override
    public boolean use(LivingEntity caster){


        targetManager.setTargetToNearestValid(caster, range);

        LivingEntity target = targetManager.getPlayerTarget(caster);


        if(!usable(caster, target)){
            return false;
        }


        execute(caster);

        cooldownManager.start(caster.getUniqueId(), 1, (long) (baseCooldown * 1000));

        return true;
    }

    @Override
    public int cooldown() {
        return baseCooldown;
    }

    private void execute(LivingEntity caster){

        LivingEntity target = targetManager.getPlayerTarget(caster);

        Location start = caster.getLocation().clone();

        ArmorStand stand = caster.getWorld().spawn(start.clone().subtract(0,10,0), ArmorStand.class);
        stand.setInvisible(true);
        stand.setGravity(false);
        stand.setCollidable(false);
        stand.setInvulnerable(true);
        stand.setMarker(true);
        ItemStack item = new ItemStack(Material.SLIME_BALL);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(1);
        item.setItemMeta(meta);
        EntityEquipment entityEquipment = stand.getEquipment();
        assert entityEquipment != null;
        entityEquipment.setItemInMainHand(item);
        stand.setRightArmPose(new EulerAngle(Math.toRadians(180), Math.toRadians(0), Math.toRadians(0)));
        stand.teleport(start.clone().add(0,.5,0));

        double skillDamage = getSkillDamage(caster);
        //abilityManager.setSkillRunning(player, true);
        new BukkitRunnable(){
            int angle = 180;
            @Override
            public void run(){

                if(caster instanceof Player){
                    if(!((Player)caster).isOnline()){
                        cancelTask();
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

                double distance = current.distance(targetLoc);
                double distanceThisTick = Math.min(distance, .5);

                if(distanceThisTick>1){
                    current.add(direction.normalize().multiply(distanceThisTick));
                }

                current.setDirection(direction);

                caster.teleport(current);
                stand.teleport(current.clone().add(0, 0.5, 0));
                angle+=15;
                stand.setRightArmPose(new EulerAngle(Math.toRadians(angle), Math.toRadians(0), Math.toRadians(0)));

                if(angle>=360){

                    boolean crit = damageCalculator.checkIfCrit(caster, 0);
                    double damage = damageCalculator.calculateDamage(caster, target, DamageType.Physical, skillDamage, crit, 0);

                    Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, caster));
                    changeResourceHandler.subtractHealthFromEntity(target, damage, caster, crit);

                    combo.addComboPoint(caster);

                    lookup.get(PlayerClass.Assassin, 8).onExternalTrigger(caster, target);


                    cancelTask();
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
                stand.remove();
            }

        }.runTaskTimer(main, 0, 1);
    }

    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_1_Level_Bonus();
        return baseDamage + ((int)(skillLevel/3));
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


        return cooldownManager.isReady(caster.getUniqueId(), 1, statusEffectManager.getHastePercent(caster));
    }


    @Override
    public String skillBarIcon(LivingEntity entity) {
        return "\ue3b6";
    }
}
