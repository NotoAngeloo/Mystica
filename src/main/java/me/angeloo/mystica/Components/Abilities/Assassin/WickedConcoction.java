package me.angeloo.mystica.Components.Abilities.Assassin;

import me.angeloo.mystica.Components.Abilities.AssassinAbilities;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.CustomEvents.UltimateStatusChageEvent;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.*;
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
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WickedConcoction {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;

    private final Stealth stealth;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public WickedConcoction(Mystica main, AbilityManager manager, AssassinAbilities assassinAbilities){
        this.main = main;
        profileManager = main.getProfileManager();
        combatManager = manager.getCombatManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
        stealth = assassinAbilities.getStealth();
    }

    private final double range = 15;

    public void use(LivingEntity caster){

        if(!abilityReadyInMap.containsKey(caster.getUniqueId())){
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }

        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(!usable(caster, target)){
            return;
        }

        if(target instanceof Player){
            if(profileManager.getAnyProfile(target).getIfDead()){
                target = caster;
            }
        }

        if(target == null){
            target = caster;
        }


        combatManager.startCombatTimer(caster);

        execute(caster, target);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), getSkillCooldown());
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getPlayerCooldown(caster) <= 0){
                    this.cancel();
                    return;
                }

                int cooldown = getPlayerCooldown(caster) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(caster);

                abilityReadyInMap.put(caster.getUniqueId(), cooldown);

                if(caster instanceof Player){
                    Bukkit.getServer().getPluginManager().callEvent(new UltimateStatusChageEvent((Player) caster));
                }


            }
        }.runTaskTimer(main, 0,20);
        cooldownTask.put(caster.getUniqueId(), task);

    }

    private void execute(LivingEntity caster, LivingEntity target){

        Location start = caster.getLocation();

        ItemStack item = new ItemStack(Material.SLIME_BALL);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(3);
        item.setItemMeta(meta);

        ArmorStand stand = caster.getWorld().spawn(start.clone().subtract(0,10,0), ArmorStand.class);
        stand.setInvisible(true);
        stand.setGravity(false);
        stand.setCollidable(false);
        stand.setInvulnerable(true);
        stand.setMarker(true);
        EntityEquipment entityEquipment = stand.getEquipment();
        assert entityEquipment != null;
        entityEquipment.setHelmet(item);
        stand.teleport(start);

        boolean heal = false;

        if(target instanceof Player){
            if(!pvpManager.pvpLogic(caster, (Player) target)){
                heal = true;
            }
        }

        if(!pveChecker.pveLogic(target)){
            heal = true;
        }

        boolean finalHeal = heal;
        double finalSkillDamage = getSkillDamage(caster);
        new BukkitRunnable(){
            Location targetWasLoc = target.getLocation().clone();
            @Override
            public void run(){

                if(targetStillValid(target)){
                    Location targetLoc = target.getLocation();
                    targetWasLoc = targetLoc.clone();
                }

                Location current = stand.getLocation();

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

                stand.teleport(current);

                if (distance <= 1) {

                    cancelTask();

                    boolean crit = damageCalculator.checkIfCrit(caster, 0);

                    if(!finalHeal){

                        double damage = damageCalculator.calculateDamage(caster, target, "Physical", finalSkillDamage, crit);

                        Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, caster));
                        changeResourceHandler.subtractHealthFromEntity(target, damage, caster, crit);
                        stealth.stealthBonusCheck(caster, target);
                        buffAndDebuffManager.getConcoctionDebuff().applyDebuff(target);
                        return;
                    }

                    double healAmount = damageCalculator.calculateHealing(caster, getHealPower(), crit);

                    changeResourceHandler.addHealthToEntity(target, healAmount, caster);
                    buffAndDebuffManager.getDamageReduction().applyDamageReduction(target, .95, 20*15);
                    //and damage reduction

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
                stand.remove();
            }

        }.runTaskTimer(main, 0, 1);

    }

    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getStats().getLevel();
        return 50 + ((int)(skillLevel/3));
    }

    public double getHealPower(){
        return 10;
    }

    public int getPlayerCooldown(LivingEntity caster){

        int cooldown = abilityReadyInMap.getOrDefault(caster.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

    public int getSkillCooldown(){
        return 20;
    }

    public void resetCooldown(LivingEntity caster){
        abilityReadyInMap.remove(caster.getUniqueId());
    }

    public boolean usable(LivingEntity caster, LivingEntity target){
        if(target != null){

            double distance = caster.getLocation().distance(target.getLocation());

            if(distance > range + buffAndDebuffManager.getTotalRangeModifier(caster)){
                return false;
            }

            if(distance<1){
                return false;
            }

        }


        return getPlayerCooldown(caster) <= 0;
    }

}
