package me.angeloo.mystica.Components.Abilities.ShadowKnight;


import me.angeloo.mystica.Components.Abilities.ShadowKnightAbilities;
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
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SoulReap {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;

    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    private final Map<UUID, Integer> soulMarks = new HashMap<>();

    private final Infection infection;

    public SoulReap(Mystica main, AbilityManager manager, ShadowKnightAbilities shadowKnightAbilities){
        this.main = main;
        profileManager = main.getProfileManager();
        combatManager = manager.getCombatManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
        infection = shadowKnightAbilities.getInfection();
    }

    public void use(Player player){

        if(!abilityReadyInMap.containsKey(player.getUniqueId())){
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }

        double baseRange = 8;

        targetManager.setTargetToNearestValid(player, baseRange);

        LivingEntity target = targetManager.getPlayerTarget(player);

        if(target != null){
            if(target instanceof Player){
                if(!pvpManager.pvpLogic(player, (Player) target)){
                    return;
                }
            }

            if(!(target instanceof Player)){
                if(!pveChecker.pveLogic(target)){
                    return;
                }
            }

            double distance = player.getLocation().distance(target.getLocation());

            if(distance > baseRange){
                return;
            }
        }

        if(target == null){
            return;
        }

        if(abilityReadyInMap.get(player.getUniqueId()) > 0){
            return;
        }

        combatManager.startCombatTimer(player);

        execute(player);

        abilityReadyInMap.put(player.getUniqueId(), 3);
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

    private void execute(Player player){

        boolean doom = profileManager.getAnyProfile(player).getPlayerSubclass().equalsIgnoreCase("doom");

        LivingEntity target = targetManager.getPlayerTarget(player);

        Location start = player.getLocation().clone();

        Location end = target.getLocation().clone();
        Vector initDir = end.toVector().subtract(start.toVector());

        player.teleport(start.clone().setDirection(initDir));
        buffAndDebuffManager.getImmobile().applyImmobile(player,0);

        Vector crossProduct = initDir.clone().crossProduct(new Vector(0,1,0)).normalize();

        Location spawnLoc = start.clone().subtract(crossProduct.multiply(1));
        spawnLoc.subtract(0,5,0);

        ArmorStand armorStand = start.getWorld().spawn(spawnLoc, ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);

        int initAngle = 0;

        armorStand.setLeftArmPose(new EulerAngle(Math.toRadians(initAngle), Math.toRadians(0), Math.toRadians(0)));

        EntityEquipment entityEquipment = armorStand.getEquipment();

        ItemStack item = new ItemStack(Material.REDSTONE);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(7);
        item.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setItemInOffHand(item);

        new BukkitRunnable(){
            final Location hitBoxCenter = target.getLocation().clone();
            final Location center = target.getLocation().clone();
            int angle = 0;
            double height = 0;
            @Override
            public void run(){

                if(!player.isOnline()){
                    cancelTask();
                    return;
                }

                if(buffAndDebuffManager.getIfInterrupt(player)){
                    cancelTask();
                    return;
                }

                Location start = player.getLocation().clone();
                Location end = target.getLocation().clone();
                Vector initDir = end.toVector().subtract(start.toVector());
                Vector crossProduct = initDir.clone().crossProduct(new Vector(0,1,0)).normalize();
                Location loc = start.clone().subtract(crossProduct.multiply(1));

                armorStand.teleport(loc);

                double increment = (2 * Math.PI) / 16; // angle between particles

                for (int i = 0; i < 16; i++) {
                    double angle = i * increment;
                    double x = center.getX() + (2 * Math.cos(angle));
                    double z = center.getZ() + (2 * Math.sin(angle));
                    Location ploc = new Location(target.getWorld(), x, (center.add(0,height,0).getY()), z);

                    player.getWorld().spawnParticle(Particle.SPELL_WITCH, ploc, 1,0, 0, 0, 0);
                }

                if(angle>=-900){
                    armorStand.setLeftArmPose(new EulerAngle(Math.toRadians(angle), Math.toRadians(0), Math.toRadians(0)));
                }

                if(angle==-900){
                    slash();
                }

                if(angle<=-1500){

                    cancelTask();

                    double distance = target.getLocation().distance(hitBoxCenter);

                    if(distance>=3){
                        return;
                    }

                    //damage
                    double skillDamage = 7 + getSoulMarks(player);

                    double targetHealthPercent = profileManager.getAnyProfile(target).getCurrentHealth() / (double) profileManager.getAnyProfile(target).getTotalHealth();

                    if(targetHealthPercent<=.3){
                        skillDamage = skillDamage * .3;
                    }

                    double skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkill_5_Level() +
                            profileManager.getAnyProfile(player).getSkillLevels().getSkill_5_Level_Bonus();

                    boolean crit = damageCalculator.checkIfCrit(player, 0);

                    double extra = 0;

                    if(doom && infection.getIfEnhanced(player)){
                        extra = infection.soulReapToRemove(player, target);
                    }

                    double damage = damageCalculator.calculateDamage(player, target, "Physical", (skillDamage * skillLevel) + extra, crit);
                    Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, player));
                    changeResourceHandler.subtractHealthFromEntity(target, damage, player);
                }

                height+=.001;
                angle-=60;
            }

            private void slash(){

                Location start2 = player.getLocation().clone().subtract(0,1,0);

                Vector direction2 = player.getLocation().getDirection().setY(0).normalize();
                direction2.rotateAroundY(-45);
                start2.setDirection(direction2);

                Vector crossProduct = direction2.clone().crossProduct(new Vector(0,1,0)).normalize();
                start2.subtract(crossProduct.multiply(2));

                ArmorStand armorStand2 = start2.getWorld().spawn(start2, ArmorStand.class);
                armorStand2.setInvisible(true);
                armorStand2.setGravity(false);
                armorStand2.setCollidable(false);
                armorStand2.setInvulnerable(true);
                armorStand2.setMarker(true);

                EntityEquipment entityEquipment2 = armorStand2.getEquipment();

                ItemStack basicItem = new ItemStack(Material.REDSTONE);
                ItemMeta meta = basicItem.getItemMeta();
                assert meta != null;
                meta.setCustomModelData(2);
                basicItem.setItemMeta(meta);
                assert entityEquipment2 != null;
                entityEquipment2.setHelmet(basicItem);

                new BukkitRunnable(){
                    double traveled = 0;
                    @Override
                    public void run(){

                        armorStand2.teleport(start2.clone().add(0,traveled,0));


                        if(traveled>=2){
                            cancelTask();
                        }

                        traveled +=.3;

                    }

                    private void cancelTask(){
                        armorStand2.remove();
                        this.cancel();

                    }

                }.runTaskTimer(main, 0, 1);
            }

            private void cancelTask() {
                this.cancel();
                armorStand.remove();
                buffAndDebuffManager.getImmobile().removeImmobile(player);
            }

        }.runTaskTimer(main, 0, 1);

    }

    public int getSoulMarks(Player player){

        if(!soulMarks.containsKey(player.getUniqueId())){
            soulMarks.put(player.getUniqueId(), 0);
        }

        return soulMarks.get(player.getUniqueId());
    }

    public void addSoulMark(Player player){

        int stacks = getSoulMarks(player);

        if(stacks >=4){
            return;
        }

        stacks ++;

        soulMarks.put(player.getUniqueId(), stacks);
    }

    public void removeSoulMarks(Player player){
        soulMarks.put(player.getUniqueId(), 0);
    }

    public int getCooldown(Player player){
        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

}