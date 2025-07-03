package me.angeloo.mystica.Components.Abilities.ShadowKnight;


import me.angeloo.mystica.Components.Abilities.ShadowKnightAbilities;
import me.angeloo.mystica.CustomEvents.HudUpdateEvent;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.Hud.CooldownDisplayer;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;
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
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SoulReap {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final AbilityManager abilityManager;
    private final CombatManager combatManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final CooldownDisplayer cooldownDisplayer;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    private final Map<UUID, Integer> soulMarks = new HashMap<>();

    private final Energy energy;
    private final Infection infection;

    public SoulReap(Mystica main, AbilityManager manager, ShadowKnightAbilities shadowKnightAbilities){
        this.main = main;
        profileManager = main.getProfileManager();
        abilityManager = manager;
        combatManager = manager.getCombatManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
        infection = shadowKnightAbilities.getInfection();
        energy = shadowKnightAbilities.getEnergy();
    }

    private final double range = 8;

    public void use(LivingEntity caster){

        if(!abilityReadyInMap.containsKey(caster.getUniqueId())){
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }

        targetManager.setTargetToNearestValid(caster, range);

        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(!usable(caster, target)){
            return;
        }

        energy.subTractEnergyFromEntity(caster, getCost());

        combatManager.startCombatTimer(caster);

        execute(caster);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), 10);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getCooldown(caster) <= 0){
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(caster) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(caster);

                abilityReadyInMap.put(caster.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(caster, 5);

            }
        }.runTaskTimerAsynchronously(main, 0,20);
        cooldownTask.put(caster.getUniqueId(), task);
    }

    private void execute(LivingEntity caster){

        boolean doom = profileManager.getAnyProfile(caster).getPlayerSubclass().equalsIgnoreCase("doom");

        LivingEntity target = targetManager.getPlayerTarget(caster);

        Location start = caster.getLocation().clone();

        Location end = target.getLocation().clone();
        Vector initDir = end.toVector().subtract(start.toVector());

        caster.teleport(start.clone().setDirection(initDir));
        buffAndDebuffManager.getImmobile().applyImmobile(caster,0);

        Vector crossProduct = initDir.clone().crossProduct(new Vector(0,1,0)).normalize();

        Location spawnLoc = start.clone().subtract(crossProduct.multiply(1));
        spawnLoc.subtract(0,5,0);

        ArmorStand armorStand = caster.getWorld().spawn(spawnLoc, ArmorStand.class);
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

        abilityManager.setCasting(caster, true);
        new BukkitRunnable(){
            final Location hitBoxCenter = target.getLocation().clone();
            final Location center = target.getLocation().clone();
            int angle = 0;
            double height = 0;
            int count = 0;
            @Override
            public void run(){

                if(caster instanceof Player){
                    if(!((Player)caster).isOnline()){
                        cancelTask();
                        return;
                    }
                }

                if(buffAndDebuffManager.getIfInterrupt(caster)){
                    cancelTask();
                    return;
                }

                Location start = caster.getLocation().clone();
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

                    caster.getWorld().spawnParticle(Particle.SPELL_WITCH, ploc, 1,0, 0, 0, 0);
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
                    double skillDamage = getSkillDamage(caster);

                    double targetHealthPercent = profileManager.getAnyProfile(target).getCurrentHealth() / (profileManager.getAnyProfile(target).getTotalHealth() + buffAndDebuffManager.getHealthBuffAmount(target));

                    if(targetHealthPercent<=.3){
                        skillDamage = skillDamage * .3;
                    }

                    boolean crit = damageCalculator.checkIfCrit(caster, 0);

                    double extra = 0;

                    if(doom && infection.getIfEnhanced(caster)){
                        extra = infection.soulReapToRemove(caster);
                    }

                    double damage = damageCalculator.calculateDamage(caster, target, "Physical", skillDamage, crit);
                    damage = damage + extra;
                    removeSoulMarks(caster);
                    Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, caster));
                    changeResourceHandler.subtractHealthFromEntity(target, damage, caster, crit);
                    buffAndDebuffManager.getBossInterrupt().interrupt(caster, target);
                }

                double percent = ((double) angle / -1500) * 100;
                abilityManager.setCastBar(caster, percent);

                if(count>100){
                    cancelTask();
                }

                height+=.001;
                angle-=60;
                count++;
            }

            private void slash(){

                Location start2 = caster.getLocation().clone().subtract(0,1,0);

                Vector direction2 = caster.getLocation().getDirection().setY(0).normalize();
                direction2.rotateAroundY(-45);
                start2.setDirection(direction2);

                Vector crossProduct = direction2.clone().crossProduct(new Vector(0,1,0)).normalize();
                start2.subtract(crossProduct.multiply(2));

                ArmorStand armorStand2 = caster.getWorld().spawn(start2, ArmorStand.class);
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
                abilityManager.setCasting(caster, false);
                abilityManager.setCastBar(caster, 0);
                buffAndDebuffManager.getImmobile().removeImmobile(caster);
            }

        }.runTaskTimer(main, 0, 1);

    }

    public int getSoulMarks(LivingEntity caster){

        if(!soulMarks.containsKey(caster.getUniqueId())){
            soulMarks.put(caster.getUniqueId(), 0);
        }

        return soulMarks.get(caster.getUniqueId());
    }

    public void addSoulMark(LivingEntity caster){

        if(caster instanceof Player){
            Player player = (Player) caster;
            Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, "status", false));
        }

        int stacks = getSoulMarks(caster);

        if(stacks>5){
            return;
        }

        stacks ++;

        soulMarks.put(caster.getUniqueId(), stacks);
        if(caster instanceof Player){
            Player player = (Player) caster;
            Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, "status", false));
        }
    }

    public void removeSoulMarks(LivingEntity caster){
        soulMarks.put(caster.getUniqueId(), 0);
        if(caster instanceof Player){
            Player player = (Player) caster;
            Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, "status", false));
        }
    }

    public int getCooldown(LivingEntity caster){
        int cooldown = abilityReadyInMap.getOrDefault(caster.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

    public double getSkillDamage(LivingEntity caster){
        double skillDamage = 30 + (2*getSoulMarks(caster));
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_5_Level_Bonus();

        return skillDamage + ((int)(skillLevel/3));
    }

    public int getCost(){
        return 30;
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


        return energy.getCurrentEnergy(caster) >= getCost();
    }

    public int returnWhichItem(Player player){

        if(energy.getCurrentEnergy(player)<getCost()){
            return 8;
        }

        return 0;
    }

}
