package me.angeloo.mystica.Components.Abilities.Assassin;

import me.angeloo.mystica.Components.Abilities.AssassinAbilities;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
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
    private final ShieldAbilityManaDisplayer shieldAbilityManaDisplayer;
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
        shieldAbilityManaDisplayer = new ShieldAbilityManaDisplayer(main, manager);
        combatManager = manager.getCombatManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
        stealth = assassinAbilities.getStealth();
    }

    public void use(Player player){

        if(!abilityReadyInMap.containsKey(player.getUniqueId())){
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }

        double baseRange = 15;
        double extraRange = buffAndDebuffManager.getTotalRangeModifier(player);
        double totalRange = baseRange + extraRange;

        LivingEntity target = targetManager.getPlayerTarget(player);

        if(target != null){

            if(!(target instanceof Player)){
                if(!pveChecker.pveLogic(target)){
                    return;
                }
            }

            double distance = player.getLocation().distance(target.getLocation());

            if(distance > totalRange){
                return;
            }

            if(target instanceof Player){
                if(profileManager.getAnyProfile(target).getIfDead()){
                    target = player;
                }
            }

        }

        if(target == null){
            target = player;
        }

        if(getCooldown(player) > 0){
            return;
        }


        if(profileManager.getAnyProfile(player).getCurrentMana()<getCost()){
            return;
        }

        changeResourceHandler.subTractManaFromPlayer(player, getCost());

        combatManager.startCombatTimer(player);

        execute(player, target);

        if(cooldownTask.containsKey(player.getUniqueId())){
            cooldownTask.get(player.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(player.getUniqueId(), 20);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getCooldown(player) <= 0){
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(player) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);
                shieldAbilityManaDisplayer.displayPlayerHealthPlusInfo(player);

            }
        }.runTaskTimer(main, 0,20);
        cooldownTask.put(player.getUniqueId(), task);

    }

    private void execute(Player player, LivingEntity target){

        Location start = player.getLocation();

        ItemStack item = new ItemStack(Material.SLIME_BALL);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(3);
        item.setItemMeta(meta);

        ArmorStand stand = player.getWorld().spawn(start.clone().subtract(0,10,0), ArmorStand.class);
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
            if(!pvpManager.pvpLogic(player, (Player) target)){
                heal = true;
            }
        }


        boolean finalHeal = heal;
        double finalSkillDamage = getSkillDamage(player);
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

                    boolean crit = damageCalculator.checkIfCrit(player, 0);

                    if(!finalHeal){

                        double damage = damageCalculator.calculateDamage(player, target, "Physical", finalSkillDamage, crit);

                        Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, player));
                        changeResourceHandler.subtractHealthFromEntity(target, damage, player);
                        stealth.stealthBonusCheck(player, target);
                        buffAndDebuffManager.getConcoctionDebuff().applyDebuff(target);
                        return;
                    }

                    double healAmount = damageCalculator.calculateHealing(player, getHealPower(), crit);

                    changeResourceHandler.addHealthToEntity(target, healAmount, player);
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

    public double getSkillDamage(Player player){
        double skillLevel = profileManager.getAnyProfile(player).getStats().getLevel();
        return 50 + ((int)(skillLevel/3));
    }

    public double getCost(){
        return 20;
    }

    public double getHealPower(){
        return 10;
    }

    public int getCooldown(Player player){

        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

}
