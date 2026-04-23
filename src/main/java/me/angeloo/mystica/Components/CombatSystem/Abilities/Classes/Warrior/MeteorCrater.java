package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Warrior;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.CrowdControl.Stun;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.PvpManager;
import me.angeloo.mystica.Components.CombatSystem.TargetManager;
import me.angeloo.mystica.Components.Hud.BossCastingManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;
import me.angeloo.mystica.Utility.Logic.PveChecker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
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

public class MeteorCrater extends BaseAbility {

    private final Mystica main;
    private final ProfileManager profileManager;
    private final TargetManager targetManager;
    private final StatusEffectManager statusEffectManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final DamageCalculator damageCalculator;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final CooldownManager cooldownManager;
    private final BossCastingManager bossCastingManager;

    private final Rage rage;

    public MeteorCrater(Mystica main, AbilityManager manager){
        super("meteor_crater");
        this.main = main;
        targetManager = main.getTargetManager();
        profileManager = main.getProfileManager();
        statusEffectManager = main.getStatusEffectManager();
        changeResourceHandler = main.getChangeResourceHandler();
        damageCalculator = main.getDamageCalculator();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        cooldownManager = manager.getCooldownManager();
        rage = manager.getRage();
        bossCastingManager = main.getBossCastingManager();
    }

    private final int baseCooldown = 2;
    private final int cost = 100;
    private final int baseDamage = 80;

    @Override
    public boolean use(LivingEntity caster){

        if(!usable(caster)){
            return false;
        }

        rage.subTractRageFromEntity(caster, cost);


        execute(caster);

        cooldownManager.start(caster.getUniqueId(), 4, (long) (baseCooldown * 1000));

        return true;
    }

    @Override
    public int cooldown() {
        return baseCooldown;
    }

    private void execute(LivingEntity caster){

        double baseRange = 8;

        targetManager.setTargetToNearestValid(caster, baseRange);

        LivingEntity target = targetManager.getPlayerTarget(caster);

        boolean targeted = false;

        Vector direction = caster.getLocation().getDirection().setY(0).normalize();

        if(target != null){

            if(target instanceof Player){
                if(pvpManager.pvpLogic(caster, (Player) target)){

                    double distance = caster.getLocation().distance(target.getLocation());

                    if(distance < baseRange){
                        targeted = true;
                    }

                }
            }

            if(!(target instanceof Player)){
                if(pveChecker.pveLogic(target)){

                    double distance = caster.getLocation().distance(target.getLocation());

                    if(distance < baseRange){
                        targeted = true;
                    }

                }
            }


        }

        if(targeted){
            direction = target.getLocation().toVector().subtract(caster.getLocation().toVector()).setY(0).normalize();
        }

        Location start = caster.getLocation().clone();
        Location up = start.clone().add(0,3,0);
        Location end = start.clone().add(direction.multiply(2));
        end.setDirection(direction);



        //abilityManager.setSkillRunning(player, true);
        double finalSkillDamage = getSkillDamage(caster);
        Vector finalDirection = direction;
        new BukkitRunnable(){
            ArmorStand stand;
            int count = 0;
            boolean spawn = false;
            boolean land = false;
            boolean going = true;
            @Override
            public void run(){

                if(caster instanceof Player){
                    if(!((Player)caster).isOnline()){
                        cancelTask();
                        return;
                    }
                }

                if(!statusEffectManager.canCast(caster)){
                    cancelTask();
                    //abilityManager.setSkillRunning(player, false);
                    return;
                }

                Location current = caster.getLocation();
                current.setDirection(finalDirection);

                if(going){
                    double distance = current.distance(up);
                    double distanceThisTick = Math.min(distance, .5);
                    Vector upDir = up.toVector().subtract(current.toVector());

                    if(distanceThisTick!=0){
                        current.add(upDir.normalize().multiply(distanceThisTick));
                    }

                    if(distance<=1){
                        going=false;
                    }
                }
                else{
                    double distance = current.distance(start);
                    double distanceThisTick = Math.min(distance, .5);
                    Vector downDir = start.toVector().subtract(current.toVector());

                    if(distanceThisTick!=0){
                        current.add(downDir.normalize().multiply(distanceThisTick));
                    }

                    if(distance<=1){
                        land=true;
                        //abilityManager.setSkillRunning(player, false);
                    }
                }

                if(!land){
                    caster.teleport(current);
                }

                if(land && !spawn){
                    stand = caster.getWorld().spawn(end.clone().subtract(0,5,0), ArmorStand.class);
                    stand.setInvisible(true);
                    stand.setGravity(false);
                    stand.setCollidable(false);
                    stand.setInvulnerable(true);
                    stand.setMarker(true);
                    ItemStack item = new ItemStack(Material.NETHER_WART);
                    ItemMeta meta = item.getItemMeta();
                    assert meta != null;
                    meta.setCustomModelData(6);
                    item.setItemMeta(meta);
                    EntityEquipment entityEquipment = stand.getEquipment();
                    assert entityEquipment != null;
                    entityEquipment.setHelmet(item);
                    stand.teleport(end);
                    spawn=true;

                    double increment = (2 * Math.PI) / 16; // angle between particles

                    for (int i = 0; i < 16; i++) {
                        double angle = i * increment;
                        double x = end.getX() + (4 * Math.cos(angle));
                        double y = end.getY() + 1;
                        double z = end.getZ() + (4 * Math.sin(angle));
                        Location loc = new Location(end.getWorld(), x, y, z);
                        caster.getWorld().spawnParticle(Particle.CRIT, loc, 1,0, 0, 0, 0);
                    }

                    BoundingBox hitBox = new BoundingBox(
                            end.getX() - 4,
                            end.getY() - 2,
                            end.getZ() - 4,
                            end.getX() + 4,
                            end.getY() + 4,
                            end.getZ() + 4
                    );

                    for (Entity entity : caster.getWorld().getNearbyEntities(hitBox)) {

                        if(entity == caster){
                            continue;
                        }

                        if(!(entity instanceof LivingEntity livingEntity)){
                            continue;
                        }

                        if(entity instanceof ArmorStand){
                            continue;
                        }

                        boolean crit = damageCalculator.checkIfCrit(caster, 0);

                        double healthPercent = profileManager.getAnyProfile(livingEntity).getCurrentHealth() / (profileManager.getAnyProfile(livingEntity).getTotalHealth() + statusEffectManager.getHealthBuffAmount(livingEntity));

                        double bonus = 1;

                        if(healthPercent>=.7){
                            bonus = 1.3;
                        }

                        double damage = (damageCalculator.calculateDamage(caster, livingEntity, "Physical", finalSkillDamage * bonus, crit));

                        //pvp logic
                        if(entity instanceof Player){
                            if(pvpManager.pvpLogic(caster, (Player) entity)){
                                changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, caster, crit);
                                bossCastingManager.interrupt(caster, target);
                                stunEntity(target);
                            }
                            continue;
                        }

                        if(pveChecker.pveLogic(livingEntity)){
                            Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(livingEntity, caster));
                            changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, caster, crit);
                            bossCastingManager.interrupt(caster, target);
                            stunEntity(target);
                        }

                    }
                }

                if(spawn){
                    count++;
                }

                if(count>=20*5){
                    cancelTask();
                }
            }

            private void stunEntity(LivingEntity target){

                if(!profileManager.getAnyProfile(target).getIsMovable()){
                    return;
                }

                //should be stun instead
                statusEffectManager.applyEffect(target, new Stun(), 20, null);
            }

            private void cancelTask(){
                this.cancel();
                stand.remove();
            }

        }.runTaskTimer(main, 0, 1);
    }


    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_4_Level_Bonus();
        return baseDamage + ((int)(skillLevel/3));
    }

    @Override
    public boolean usable(LivingEntity caster){
        if(rage.getCurrentRage(caster)<cost){
            return false;
        }

        Block block = caster.getLocation().subtract(0,1,0).getBlock();

        if(block.getType() == Material.AIR){
            return false;
        }

        return cooldownManager.isReady(caster.getUniqueId(), 4, statusEffectManager.getHastePercent(caster));
    }

    /*public int returnWhichItem(Player player){

        if(rage.getCurrentRage(player)<getCost()){
            return 8;
        }

        return 0;
    }*/

}


