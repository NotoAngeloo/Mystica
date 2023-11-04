package me.angeloo.mystica.Components.Abilities.Mystic;

import me.angeloo.mystica.Components.Abilities.MysticAbilities;
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
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.*;

public class PlagueCurse {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;

    private final EvilSpirit evilSpirit;

    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();
    private final Map<UUID, Boolean> curseMap = new HashMap<>();

    public PlagueCurse(Mystica main, AbilityManager manager, MysticAbilities mysticAbilities){
        this.main = main;
        profileManager = main.getProfileManager();
        combatManager = manager.getCombatManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();

        evilSpirit = mysticAbilities.getEvilSpirit();
    }

    public void use(Player player){

        if(!abilityReadyInMap.containsKey(player.getUniqueId())){
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }

        double baseRange = 20;
        double extraRange = buffAndDebuffManager.getTotalRangeModifier(player);
        double totalRange = baseRange + extraRange;

        targetManager.setTargetToNearestValid(player, totalRange);

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

            if(distance > totalRange){
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

        abilityReadyInMap.put(player.getUniqueId(), 6);
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

        evilSpirit.addChaosShard(player, 1);

        LivingEntity target = targetManager.getPlayerTarget(player);

        Location spawnLoc = target.getLocation().subtract(0,1.5,0);

        ArmorStand armorStand = spawnLoc.getWorld().spawn(spawnLoc, ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);

        EntityEquipment entityEquipment = armorStand.getEquipment();

        ItemStack curseItem = new ItemStack(Material.SPECTRAL_ARROW);
        ItemMeta meta = curseItem.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(6);
        curseItem.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setHelmet(curseItem);

        double skillDamage = 3;
        double skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkill_2_Level() +
                profileManager.getAnyProfile(player).getSkillLevels().getSkill_2_Level_Bonus();

        new BukkitRunnable(){
            Location targetWasLoc = target.getLocation().clone().subtract(0,.75,0);
            int ran = 0;
            Vector initialDirection;
            double angle = 0;
            @Override
            public void run(){

                if(targetStillValid(target)){
                    Location targetLoc = target.getLocation().clone().subtract(0,.75,0);
                    targetWasLoc = targetLoc.clone();
                }

                if (initialDirection == null) {
                    initialDirection = targetWasLoc.getDirection().setY(0).normalize();
                }

                Vector direction = initialDirection.clone();
                double radians = Math.toRadians(angle);
                direction.rotateAroundY(radians);

                targetWasLoc.setDirection(direction);

                armorStand.teleport(targetWasLoc);

                if(ran%20 == 0){
                    BoundingBox hitBox = new BoundingBox(
                            targetWasLoc.getX() - 4,
                            targetWasLoc.getY() - 2,
                            targetWasLoc.getZ() - 4,
                            targetWasLoc.getX() + 4,
                            targetWasLoc.getY() + 4,
                            targetWasLoc.getZ() + 4
                    );

                    for (Entity entity : player.getWorld().getNearbyEntities(hitBox)) {

                        if(entity == player){
                            continue;
                        }

                        if(!(entity instanceof LivingEntity)){
                            continue;
                        }

                        if(entity instanceof ArmorStand){
                            continue;
                        }

                        LivingEntity livingEntity = (LivingEntity) entity;

                        if(!getIfCursed(livingEntity)){
                            applyCurse(livingEntity);
                        }

                        boolean crit = damageCalculator.checkIfCrit(player, 0);
                        double damage = (damageCalculator.calculateDamage(player, livingEntity, "Magical", skillDamage * skillLevel, crit));

                        //pvp logic
                        if(entity instanceof Player){
                            if(pvpManager.pvpLogic(player, (Player) entity)){
                                changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, player);
                            }
                            continue;
                        }

                        if(pveChecker.pveLogic(livingEntity)){
                            Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(livingEntity, player));
                            changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, player);
                        }

                    }
                }

                angle += 2; // adjust the rotation speed here
                if (angle >= 360) {
                    angle = 0;
                }

                ran++;

                if(ran >= 10 * 20){
                    cancelTask();
                }
            }

            private void applyCurse(LivingEntity entity){
                curseMap.put(entity.getUniqueId(), true);

                new BukkitRunnable(){
                    int count = 0;
                    @Override
                    public void run(){

                        if(!targetStillValid(entity)){
                            this.cancel();
                            curseMap.remove(entity.getUniqueId());
                            return;
                        }

                        Location center = entity.getLocation().clone().add(0,1,0);

                        double increment = (2 * Math.PI) / 16; // angle between particles

                        for (int i = 0; i < 16; i++) {
                            double angle = i * increment;
                            double j = center.getX() + (1 * Math.cos(angle));
                            double k = center.getZ() + (1 * Math.sin(angle));
                            Location loc = new Location(center.getWorld(), j, (center.getY()), k);

                            entity.getWorld().spawnParticle(Particle.GLOW_SQUID_INK, loc, 1, 0, 0, 0, 0);
                        }

                        if(count >= 10){
                            this.cancel();
                            curseMap.remove(entity.getUniqueId());
                        }

                        count++;

                    }
                }.runTaskTimer(main, 0, 20);
            }

            private boolean targetStillValid(LivingEntity target){

                if(target instanceof Player){

                    if(!((Player) target).isOnline()){
                        return false;
                    }

                }

                return !target.isDead();
            }

            private void cancelTask() {
                this.cancel();
                armorStand.remove();
            }

        }.runTaskTimer(main, 0, 1);

    }

    public boolean getIfCursed(LivingEntity entity){
        return curseMap.getOrDefault(entity.getUniqueId(), false);
    }

    public int getCooldown(Player player){

        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

}
