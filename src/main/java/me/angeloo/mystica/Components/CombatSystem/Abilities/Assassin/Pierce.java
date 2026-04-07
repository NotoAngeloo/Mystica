package me.angeloo.mystica.Components.CombatSystem.Abilities.Assassin;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.AssassinAbilities;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.DamageModifiers.PierceBuff;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.PvpManager;
import me.angeloo.mystica.Components.CombatSystem.TargetManager;
import me.angeloo.mystica.Components.Hud.CooldownDisplayer;
import me.angeloo.mystica.Components.Items.MysticaEquipment;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;
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
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Pierce {

    private final MysticaEquipment weapon;
    private final Mystica main;
    private final ProfileManager profileManager;
    private final TargetManager targetManager;
    private final StatusEffectManager statusEffectManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final DamageCalculator damageCalculator;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final CooldownDisplayer cooldownDisplayer;

    private final Stealth stealth;
    private final Combo combo;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public Pierce(Mystica main, AbilityManager manager, AssassinAbilities assassinAbilities){
        this.main = main;
        targetManager = main.getTargetManager();
        profileManager = main.getProfileManager();
        statusEffectManager = main.getStatusEffectManager();
        changeResourceHandler = main.getChangeResourceHandler();
        damageCalculator = main.getDamageCalculator();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
        combo = assassinAbilities.getCombo();
        stealth = assassinAbilities.getStealth();
        weapon = new MysticaEquipment(EquipmentSlot.WEAPON, PlayerClass.Assassin, 1);
    }

    private final double range = 4;

    public void use(LivingEntity caster){

        if(!abilityReadyInMap.containsKey(caster.getUniqueId())){
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }


        targetManager.setTargetToNearestValid(caster, range);
        LivingEntity target = targetManager.getPlayerTarget(caster);


        if(!usable(caster, target)){
            return;
        }


        combo.removeAnAmountOfPoints(caster, 1);


        execute(caster);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), 4);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getCooldown(caster) <= 0){
                    cooldownDisplayer.displayCooldown(caster, 4);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(caster) - 1;
                cooldown = cooldown - statusEffectManager.getHasteLevel(caster);

                abilityReadyInMap.put(caster.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(caster, 4);

            }
        }.runTaskTimerAsynchronously(main, 0,20);
        cooldownTask.put(caster.getUniqueId(), task);

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
                    double damage = damageCalculator.calculateDamage(caster, target, "Physical", finalSkillDamage, crit);

                    Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, caster));
                    changeResourceHandler.subtractHealthFromEntity(target, damage, caster, crit);
                    stealth.stealthBonusCheck(caster, target);

                    statusEffectManager.applyEffect(caster, new PierceBuff(), null, null);
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
        return 40 + ((int)(skillLevel/3));
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

        return combo.getComboPoints(caster) != 0;
    }

}
