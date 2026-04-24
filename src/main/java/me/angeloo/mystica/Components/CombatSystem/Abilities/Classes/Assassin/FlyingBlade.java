package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Assassin;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.CrowdControl.Stun;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.PvpManager;
import me.angeloo.mystica.Components.CombatSystem.TargetManager;
import me.angeloo.mystica.Components.Hud.BossCastingManager;
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
import org.bukkit.util.Vector;

public class FlyingBlade extends BaseAbility {

    private final Mystica main;

    private final MysticaEquipment weapon;

    private final ProfileManager profileManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final StatusEffectManager statusEffectManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final CooldownManager cooldownManager;
    private final BossCastingManager bossCastingManager;


    public FlyingBlade(Mystica main, AbilityManager manager){
        super("flying_blade");
        this.main = main;
        profileManager = main.getProfileManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        statusEffectManager = main.getStatusEffectManager();
        changeResourceHandler = main.getChangeResourceHandler();
        cooldownManager = manager.getCooldownManager();
        this.weapon = new MysticaEquipment(EquipmentSlot.WEAPON, PlayerClass.Assassin, 1);
        bossCastingManager = main.getBossCastingManager();
    }

    private final double range = 15;
    private final int baseCooldown = 15;
    private final int baseDamage = 25;

    @Override
    public boolean use(LivingEntity caster){

        targetManager.setTargetToNearestValid(caster, range + statusEffectManager.getAdditionalRange(caster));
        LivingEntity target = targetManager.getPlayerTarget(caster);


        if(!usable(caster, target)){
            return false;
        }


        execute(caster);

        cooldownManager.start(caster.getUniqueId(), 7, (long) (baseCooldown * 1000));

        return true;
    }

    @Override
    public int cooldown() {
        return baseCooldown;
    }

    private void execute(LivingEntity caster){

        LivingEntity target = targetManager.getPlayerTarget(caster);


        Location start = caster.getLocation();

        ArmorStand armorStand = caster.getWorld().spawn(start.clone().subtract(0,5,0), ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);
        EntityEquipment entityEquipment = armorStand.getEquipment();
        ItemStack weapon = this.weapon.build();
        assert entityEquipment != null;
        entityEquipment.setHelmet(weapon);
        armorStand.teleport(start);


        double finalSkillDamage = getSkillDamage(caster);
        new BukkitRunnable(){
            Location targetWasLoc = target.getLocation().clone();
            @Override
            public void run(){

                if(targetStillValid(target)){
                    Location targetLoc = target.getLocation();
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

                if (distance <= 1) {

                    cancelTask();

                    boolean crit = damageCalculator.checkIfCrit(caster, 0);
                    double damage = damageCalculator.calculateDamage(caster, target, "Physical", finalSkillDamage, crit);

                    Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, caster));
                    changeResourceHandler.subtractHealthFromEntity(target, damage, caster, crit);

                    if(profileManager.getAnyProfile(target).getIsMovable()){
                        statusEffectManager.applyEffect(target, new Stun(), 20, null, caster);
                    }

                    if(target instanceof Player){
                        statusEffectManager.removeEffect(target, "shield");
                    }

                    lookup.get(PlayerClass.Assassin, 8).onExternalTrigger(caster, target);
                    bossCastingManager.interrupt(caster, target);
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
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_7_Level_Bonus();

        return baseDamage + ((int)(skillLevel/3));
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

            if(distance<1){
                return false;
            }
        }

        if(target == null){
            return false;
        }

        return cooldownManager.isReady(caster.getUniqueId(), 7, statusEffectManager.getHastePercent(caster));
    }


}
