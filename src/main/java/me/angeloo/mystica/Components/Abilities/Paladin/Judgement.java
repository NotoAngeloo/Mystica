package me.angeloo.mystica.Components.Abilities.Paladin;

import me.angeloo.mystica.Components.Abilities.PaladinAbilities;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.CooldownDisplayer;
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Judgement {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final AggroManager aggroManager;
    private final CooldownDisplayer cooldownDisplayer;

    private final Decision decision;

    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public Judgement(Mystica main, AbilityManager manager, PaladinAbilities paladinAbilities){
        this.main = main;
        profileManager = main.getProfileManager();
        combatManager = manager.getCombatManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
        aggroManager = main.getAggroManager();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
        decision = paladinAbilities.getDecision();
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

        if(abilityReadyInMap.get(player.getUniqueId()) > 0){
            return;
        }

        combatManager.startCombatTimer(player);

        execute(player, target);

        abilityReadyInMap.put(player.getUniqueId(), 15);
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
                cooldownDisplayer.displayCooldown(player, 8);

            }
        }.runTaskTimer(main, 0,20);

    }

    private void execute(Player player, LivingEntity target){

        boolean templar = profileManager.getAnyProfile(player).getPlayerSubclass().equalsIgnoreCase("templar");

        Location start = target.getLocation();

        ArmorStand armorStand = player.getWorld().spawn(start.clone(), ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);

        EntityEquipment entityEquipment = armorStand.getEquipment();

        ItemStack item = new ItemStack(Material.SUGAR);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(8);
        item.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setHelmet(item);

        double skillDamage = 6;
        double skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkill_5_Level() +
                profileManager.getAnyProfile(player).getSkillLevels().getSkill_5_Level_Bonus();
        skillDamage = skillDamage + ((int)(skillLevel/10));

        double finalSkillDamage = skillDamage;
        new BukkitRunnable(){
            double down = 0;
            @Override
            public void run(){


                double increment = (2 * Math.PI) / 16; // angle between particles

                for (int i = 0; i < 16; i++) {
                    double angle = i * increment;
                    double x = start.getX() + (1 * Math.cos(angle));
                    double y = start.clone().add(0,7-down,0).getY();
                    double z = start.getZ() + (1 * Math.sin(angle));
                    Location loc = new Location(start.getWorld(), x, y, z);

                    player.getWorld().spawnParticle(Particle.FLAME, loc, 1,0, 0, 0, 0);
                }

                if(down>=7){
                    this.cancel();
                    armorStand.remove();
                    doSomething(target);
                }

                down+=0.7;
            }

            private void doSomething(LivingEntity target){

                if(checkValid(target)){
                    return;
                }

                boolean crit = damageCalculator.checkIfCrit(player, 0);

                if(target instanceof Player){

                    if(!pvpManager.pvpLogic(player, (Player) target)){

                        double healAmount = profileManager.getAnyProfile(target).getTotalHealth() * .05;
                        healAmount = healAmount + profileManager.getAnyProfile(player).getTotalAttack() * .1;

                        if(crit){
                            healAmount = healAmount*1.5;
                        }

                        changeResourceHandler.addHealthToEntity(target, healAmount, player);
                        return;
                    }

                }

                double damage = damageCalculator.calculateDamage(player, target, "Physical", finalSkillDamage, crit);
                damage = damage * decisionMultiplier(player);

                Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, player));
                changeResourceHandler.subtractHealthFromEntity(target, damage, player);

                if(templar){
                    aggroManager.setAsHighPriorityTarget(target, player);
                }

                decision.removeDecision(player);
            }

            private boolean checkValid(LivingEntity target){

                if(target instanceof Player){
                    if(((Player)target).isOnline()){
                        return true;
                    }

                    if(profileManager.getAnyProfile(target).getIfDead()){
                        return true;
                    }
                }

                return target.isDead();
            }

        }.runTaskTimer(main, 0, 1);
    }

    private double decisionMultiplier(Player player){

        if(decision.getDecision(player)){
            return 1.8;
        }

        return 1;
    }

    public void resetCooldown(Player player){

        boolean dawn = profileManager.getAnyProfile(player).getPlayerSubclass().equalsIgnoreCase("dawn");

        if(!dawn){
            return;
        }

        abilityReadyInMap.put(player.getUniqueId(), 0);

    }

    public int getCooldown(Player player){

        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

}
