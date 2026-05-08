package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Ranger;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.DamageModifiers.Haste;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.PvpManager;
import me.angeloo.mystica.Components.CombatSystem.TargetManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;
import me.angeloo.mystica.Utility.Enums.DamageType;
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
import org.bukkit.util.Vector;

public class StarVolley extends BaseAbility {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final StatusEffectManager statusEffectManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final Focus focus;
    private final CooldownManager cooldownManager;

    public StarVolley(Mystica main, AbilityManager manager){
        super("star_volley");
        this.main = main;
        profileManager = main.getProfileManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        statusEffectManager = main.getStatusEffectManager();
        changeResourceHandler = main.getChangeResourceHandler();
        focus = manager.getFocus();
        cooldownManager = main.getCooldownManager();
    }

    private final int baseCooldown = 45;
    private final double range = 20;
    private final int baseDamage = 60;

    @Override
    public boolean use(LivingEntity caster){

        targetManager.setTargetToNearestValid(caster, range + statusEffectManager.getAdditionalRange(caster));

        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(!usable(caster, target)){
            return false;
        }


        execute(caster);

        cooldownManager.start(caster.getUniqueId(), -1, (long) (baseCooldown * 1000));

        return true;
    }

    @Override
    public int cooldown() {
        return baseCooldown;
    }

    private void execute(LivingEntity caster){

        LivingEntity target = targetManager.getPlayerTarget(caster);

        Location spawnStart = target.getLocation().clone();
        spawnStart.add(0, 10, 0);

        ArmorStand spawnTexture = caster.getWorld().spawn(spawnStart, ArmorStand.class);
        spawnTexture.setInvisible(true);
        spawnTexture.setGravity(false);
        spawnTexture.setCollidable(false);
        spawnTexture.setInvulnerable(true);
        spawnTexture.setMarker(true);

        EntityEquipment entityEquipment2 = spawnTexture.getEquipment();

        ItemStack spawnItem = new ItemStack(Material.ARROW);
        ItemMeta meta2 = spawnItem.getItemMeta();
        assert meta2 != null;
        meta2.setCustomModelData(8);
        spawnItem.setItemMeta(meta2);
        assert entityEquipment2 != null;
        entityEquipment2.setHelmet(spawnItem);

        new BukkitRunnable(){
            @Override
            public void run(){
                spawnTexture.remove();
            }
        }.runTaskLater(main, 20*3);

        ArmorStand armorStand = caster.getWorld().spawn(spawnStart, ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);

        EntityEquipment entityEquipment = armorStand.getEquipment();

        ItemStack arrow = new ItemStack(Material.ARROW);
        ItemMeta meta = arrow.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(9);
        arrow.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setHelmet(arrow);



        Location end = target.getLocation().clone().subtract(0,2,0);

        double finalSkillDamage = getSkillDamage(caster);
        new BukkitRunnable(){
            @Override
            public void run(){

                Location current = armorStand.getLocation();

                Vector direction = end.toVector().subtract(current.toVector());
                double distance = current.distance(end);
                double distanceThisTick = Math.min(distance, 1);

                if(distanceThisTick!=0){
                    current.add(direction.normalize().multiply(distanceThisTick));
                }

                armorStand.teleport(current);

                if (distance <= 1) {

                    cancelTask();

                    boolean crit = damageCalculator.checkIfCrit(caster, 0);

                    if(crit){
                        statusEffectManager.applyEffect(caster, new Haste(), 2*20, 0.1, caster);
                    }

                    double damage = damageCalculator.calculateDamage(caster, target, DamageType.Physical, finalSkillDamage, crit, 0);

                    Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, caster));
                    changeResourceHandler.subtractHealthFromEntity(target, damage, caster, crit);

                }
            }

            private void cancelTask() {
                this.cancel();
                armorStand.remove();
            }

        }.runTaskTimer(main, 0, 1);

    }

    @Override
    public void onExternalTrigger(LivingEntity caster){
        decreaseCooldown(caster);
    }

    private void decreaseCooldown(LivingEntity caster){
        cooldownManager.reduceCooldownFlat(caster.getUniqueId(), -1, 2);
    }

    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getStats().getLevel();
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

        return cooldownManager.isReady(caster.getUniqueId(), -1, statusEffectManager.getHastePercent(caster));
    }

}
