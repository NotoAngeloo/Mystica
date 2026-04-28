package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Ranger;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.PlayerStateManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.DamageModifiers.Haste;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.PvpManager;
import me.angeloo.mystica.Components.CombatSystem.TargetManager;
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
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class BlessedArrow extends BaseAbility {

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


    public BlessedArrow(Mystica main, AbilityManager manager){
        super("blessed_arrow");
        this.main = main;
        profileManager = main.getProfileManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        statusEffectManager = main.getStatusEffectManager();
        changeResourceHandler = main.getChangeResourceHandler();
        cooldownManager = main.getCooldownManager();
        focus = manager.getFocus();
    }

    private final int baseCooldown = 10;
    private final double range = 20;
    private final int baseDamage = 20;

    @Override
    public boolean use(LivingEntity caster){
        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(!usable(caster, target)){
            return false;
        }

        if(target == null){
            target = caster;
        }

        execute(caster, target);

        cooldownManager.start(caster.getUniqueId(), 5, (long) (baseCooldown * 1000));

        return true;
    }

    @Override
    public int cooldown() {
        return baseCooldown;
    }

    private void execute(LivingEntity caster, LivingEntity target){

        boolean scout = profileManager.getAnyProfile(caster).getPlayerSubclass().equals(SubClass.Scout);

        double skillDamage = getSkillDamage(caster);

        if(statusEffectManager.hasEffect(caster,"rallying_cry")){
            skillDamage = skillDamage * 1.25;
        }



        double health = (profileManager.getAnyProfile(target).getTotalHealth() + statusEffectManager.getHealthBuffAmount(target)) * 0.25;

        //check cry active
        if(target == caster){
            restoreHealthToAlly(caster, health, caster);
            return;
        }

        Location start = caster.getLocation();
        start.subtract(0, 1, 0);


        ArmorStand armorStand = caster.getWorld().spawn(start, ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);

        EntityEquipment entityEquipment = armorStand.getEquipment();

        ItemStack blessedArrow = new ItemStack(Material.ARROW);
        ItemMeta meta = blessedArrow.getItemMeta();
        assert meta != null;

        meta.setCustomModelData(3);

        blessedArrow.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setHelmet(blessedArrow);



        double finalSkillDamage = skillDamage;
        new BukkitRunnable(){
            int count = 0;
            Location targetWasLoc = target.getLocation().clone();
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

                //check for target tyoe to see if restore mana

                if (distance <= 1) {

                    cancelTask();

                    if(target instanceof Player playerTarget){

                        if(!pvpManager.pvpLogic(caster, playerTarget)){
                            restoreHealthToAlly(playerTarget, health, caster);
                            return;
                        }
                    }

                    if(!(target instanceof Player)){
                        if(!pveChecker.pveLogic(target)){
                            restoreHealthToAlly(target, health, caster);
                            return;
                        }
                    }
                    //check pvp logic

                    boolean crit = damageCalculator.checkIfCrit(caster, 0);

                    if(scout && crit){
                        lookup.get(PlayerClass.Ranger,SubClass.Scout,-1).onExternalTrigger(caster);
                        statusEffectManager.applyEffect(caster, new Haste(), 2*20, 0.1, caster);
                    }

                    double damage = damageCalculator.calculateDamage(caster, target, "Physical", finalSkillDamage, crit);

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

    private void restoreHealthToAlly(LivingEntity target, double amount, LivingEntity caster){

        target.getWorld().spawnParticle(Particle.DRIP_WATER, target.getLocation(), 50, .5, 1, .5, 0);

        changeResourceHandler.addHealthToEntity(target, amount, caster);
    }


    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_5_Level_Bonus();

        return focus.calculateFocusMultipliedDamage(caster, baseDamage) + ((int)(skillLevel/3));
    }

    @Override
    public boolean usable(LivingEntity caster, LivingEntity target){
        if(target != null){

            double distance = caster.getLocation().distance(target.getLocation());

            if(distance > range + statusEffectManager.getAdditionalRange(caster)){
                return false;
            }

            if(distance<1){
                return false;
            }
        }

        return cooldownManager.isReady(caster.getUniqueId(), 5, statusEffectManager.getHastePercent(caster));
    }

}
