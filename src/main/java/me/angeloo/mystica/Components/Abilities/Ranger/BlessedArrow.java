package me.angeloo.mystica.Components.Abilities.Ranger;

import me.angeloo.mystica.Components.Abilities.RangerAbilities;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
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
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BlessedArrow {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;

    private final StarVolley starVolley;
    private final RallyingCry rallyingCry;

    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public BlessedArrow(Mystica main, AbilityManager manager, RangerAbilities rangerAbilities){
        this.main = main;
        profileManager = main.getProfileManager();
        rallyingCry = rangerAbilities.getRallyingCry();
        combatManager = manager.getCombatManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
        starVolley = rangerAbilities.getStarVolley();
    }

    public void use(Player player){

        if(!abilityReadyInMap.containsKey(player.getUniqueId())){
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }

        double baseRange = 20;
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
        }

        if(target == null){
            target = player;
        }

        if(abilityReadyInMap.get(player.getUniqueId()) > 0){
            return;
        }

        combatManager.startCombatTimer(player);

        execute(player, target);

        abilityReadyInMap.put(player.getUniqueId(), 10);
        new BukkitRunnable(){
            @Override
            public void run(){

                if(abilityReadyInMap.get(player.getUniqueId()) <= 0){
                    this.cancel();
                    return;
                }

                int cooldown = abilityReadyInMap.get(player.getUniqueId()) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);

            }
        }.runTaskTimer(main, 0,20);

    }

    private void execute(Player player, LivingEntity target){

        boolean scout = profileManager.getAnyProfile(player).getPlayerSubclass().equalsIgnoreCase("scout");

        double skillDamage = 4;

        double skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkill_5_Level() +
                profileManager.getAnyProfile(player).getSkillLevels().getSkill_5_Level_Bonus();

        skillDamage = skillDamage + ((int)(skillLevel/10));

        if(rallyingCry.getIfBuffTime(player) > 0){
            skillDamage = skillDamage * 1.25;
        }

        double mana = profileManager.getAnyProfile(player).getTotalMana() * .5;

        //check cry active
        if(target == player){
            restoreManaToAlly(player, mana * skillLevel);
            return;
        }

        Location start = player.getLocation();
        start.subtract(0, 1, 0);


        ArmorStand armorStand = player.getWorld().spawn(start, ArmorStand.class);
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
                current.add(direction.normalize().multiply(distanceThisTick));
                current.setDirection(direction);

                armorStand.teleport(current);

                //check for target tyoe to see if restore mana

                if (distance <= 1) {

                    cancelTask();

                    if(target instanceof Player){

                        Player playerTarget = (Player) target;

                        if(!pvpManager.pvpLogic(player, playerTarget)){
                            restoreManaToAlly(playerTarget, mana * skillLevel);
                            return;
                        }
                    }
                    //check pvp logic

                    boolean crit = damageCalculator.checkIfCrit(player, 0);

                    if(scout && crit){
                        starVolley.decreaseCooldown(player);
                        buffAndDebuffManager.getHaste().applyHaste(player, 1, 2);
                    }

                    double damage = damageCalculator.calculateDamage(player, target, "Physical", finalSkillDamage, crit);

                    Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, player));
                    changeResourceHandler.subtractHealthFromEntity(target, damage, player);

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

    private void restoreManaToAlly(Player playerTarget, double amount){

        playerTarget.getWorld().spawnParticle(Particle.DRIP_WATER, playerTarget.getLocation(), 50, .5, 1, .5, 0);

        changeResourceHandler.addManaToPlayer(playerTarget, amount);

        Bukkit.getLogger().info(String.valueOf(amount));
    }

    public int getCooldown(Player player){
        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

}
