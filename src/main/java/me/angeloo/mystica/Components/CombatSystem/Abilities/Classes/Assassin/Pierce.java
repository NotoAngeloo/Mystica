package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Assassin;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.ClassSpecific.PierceBuff;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.PvpManager;
import me.angeloo.mystica.Components.CombatSystem.TargetManager;
import me.angeloo.mystica.Components.Items.MysticaEquipment;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;
import me.angeloo.mystica.Utility.Enums.DamageType;
import me.angeloo.mystica.Utility.Enums.PlayerClass;
import me.angeloo.mystica.Utility.EquipmentSlot;
import me.angeloo.mystica.Utility.Logic.PveChecker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class Pierce extends BaseAbility {

    private final MysticaEquipment weapon;
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

    public Pierce(Mystica main, AbilityManager manager){
        super("pierce");
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
        weapon = new MysticaEquipment(EquipmentSlot.WEAPON, PlayerClass.Assassin, 1);
    }

    private final int baseCooldown = 4;
    private final int baseDamage = 40;
    private final double range = 4;

    @Override
    public boolean use(LivingEntity caster){

        targetManager.setTargetToNearestValid(caster, range);
        LivingEntity target = targetManager.getPlayerTarget(caster);


        if(!usable(caster, target)){
            return false;
        }


        combo.removeAnAmountOfPoints(caster, 1);

        execute(caster);

        cooldownManager.start(caster.getUniqueId(), 4, (long) (baseCooldown * 1000));

        return true;
    }

    @Override
    public int cooldown() {
        return baseCooldown;
    }

    private void execute(LivingEntity caster){

        LivingEntity target = targetManager.getPlayerTarget(caster);

        Location start = caster.getLocation().clone();

        ItemStack weapon = this.weapon.build();

        ArmorStand stand = caster.getWorld().spawn(start.clone().subtract(0,10,0), ArmorStand.class);
        stand.setInvisible(true);
        stand.setGravity(false);
        stand.setCollidable(false);
        stand.setInvulnerable(true);
        stand.setMarker(true);
        EntityEquipment entityEquipment = stand.getEquipment();
        assert entityEquipment != null;
        entityEquipment.setItemInMainHand(weapon); //-90, 90
        stand.setRightArmPose(new EulerAngle(Math.toRadians(0), Math.toRadians(0), Math.toRadians(0)));
        entityEquipment.setItemInOffHand(weapon);
        stand.setLeftArmPose(new EulerAngle(Math.toRadians(0), Math.toRadians(0), Math.toRadians(-0)));
        stand.teleport(start.clone().add(0,.5,0));

        //abilityManager.setSkillRunning(player, true);

        double finalSkillDamage = getSkillDamage(caster);
        new BukkitRunnable(){
            int rAngle = 0;
            int lAngle = 0;
            @Override
            public void run(){

                if(caster instanceof Player){
                    if(!((Player)caster).isOnline()){
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
                current.setDirection(direction);

                stand.teleport(current.clone().add(direction.multiply(0.5)));

                stand.setRightArmPose(new EulerAngle(Math.toRadians(0), Math.toRadians(rAngle), Math.toRadians(0)));
                stand.setLeftArmPose(new EulerAngle(Math.toRadians(0), Math.toRadians(lAngle), Math.toRadians(-0)));

                if(lAngle>=180){
                    cancelTask();

                    boolean crit = damageCalculator.checkIfCrit(caster, 0);
                    double damage = damageCalculator.calculateDamage(caster, target, DamageType.Physical, finalSkillDamage, crit, 0);

                    Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, caster));
                    changeResourceHandler.subtractHealthFromEntity(target, damage, caster, crit);
                    lookup.get(PlayerClass.Assassin, 8).onExternalTrigger(caster, target);

                    statusEffectManager.applyEffect(caster, new PierceBuff(), null, null, caster);
                }

                rAngle-=15;
                lAngle+=15;
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
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_4_Level_Bonus();
        return baseDamage + ((int)(skillLevel/3));
    }



    /*public int returnWhichItem(Player player){

        if(combo.getComboPoints(player) == 0){
            return 1;
        }

        return 0;
    }*/


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

        return cooldownManager.isReady(caster.getUniqueId(), 4, statusEffectManager.getHastePercent(caster));
    }

    @Override
    public String skillBarIcon(LivingEntity entity) {

        if(combo.getComboPoints(entity) == 0){
            return "\ue3be";
        }

        return "\ue3bd";
    }
}
