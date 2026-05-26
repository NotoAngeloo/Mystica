package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Paladin;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.PvpManager;
import me.angeloo.mystica.Components.CombatSystem.TargetManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;
import me.angeloo.mystica.Utility.Enums.DamageType;
import me.angeloo.mystica.Components.CombatSystem.Classes.SubClass;
import me.angeloo.mystica.Utility.Logic.PveChecker;
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
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class CovenantSword extends BaseAbility {

    private final Mystica main;
    private final ProfileManager profileManager;
    private final TargetManager targetManager;
    private final StatusEffectManager statusEffectManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final DamageCalculator damageCalculator;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final AbilityManager abilityManager;
    private final CooldownManager cooldownManager;

    private final Purity purity;

    public CovenantSword(Mystica main, AbilityManager manager){
        super("covenant_sword");
        this.main = main;
        abilityManager = manager;
        targetManager = main.getTargetManager();
        profileManager = main.getProfileManager();
        statusEffectManager = main.getStatusEffectManager();
        changeResourceHandler = main.getChangeResourceHandler();
        damageCalculator = main.getDamageCalculator();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        cooldownManager = main.getCooldownManager();
        purity = manager.getPurity();
    }

    private final int baseCooldown = 20;
    private final double range = 8;
    private final int baseDamage = 40;
    private final int tickDamage = 5;

    @Override
    public boolean use(LivingEntity caster){

        if(!usable(caster)){
            return false;
        }

        execute(caster);

        if(profileManager.getAnyProfile(caster).getPlayerSubclass().equals(SubClass.DAWN)){
            purity.add(caster, 4);
        }


        cooldownManager.start(caster.getUniqueId(), 4, (long) (baseCooldown * 1000));

        return true;
    }

    @Override
    public int cooldown() {
        return baseCooldown;
    }

    private void execute(LivingEntity caster){

        targetManager.setTargetToNearestValid(caster, range);

        LivingEntity target = targetManager.getPlayerTarget(caster);

        boolean targeted = false;

        Vector direction = caster.getLocation().getDirection().setY(0).normalize();

        if(target != null){

            if(target instanceof Player){
                if(pvpManager.pvpLogic(caster, (Player) target)){

                    double distance = caster.getLocation().distance(target.getLocation());

                    if(distance < range){
                        targeted = true;
                    }

                }
            }

            if(!(target instanceof Player)){
                if(pveChecker.pveLogic(target)){

                    double distance = caster.getLocation().distance(target.getLocation());

                    if(distance < range){
                        targeted = true;
                    }

                }
            }


        }

        if(targeted){
            direction = target.getLocation().toVector().subtract(caster.getLocation().toVector()).setY(0).normalize();
        }


        Location start = caster.getLocation().clone().add(direction.multiply(1));
        start.setDirection(direction);

        ArmorStand sword = caster.getWorld().spawn(start.clone().subtract(0,5,0), ArmorStand.class);
        sword.setInvisible(true);
        sword.setGravity(false);
        sword.setCollidable(false);
        sword.setInvulnerable(true);
        sword.setMarker(true);

        EntityEquipment entityEquipment = sword.getEquipment();

        ItemStack item = new ItemStack(Material.SUGAR);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(5);
        item.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setItemInMainHand(item);

        sword.setRightArmPose(new EulerAngle(Math.toRadians(0), Math.toRadians(0), Math.toRadians(0)));

        sword.teleport(start);

        abilityManager.setSkillCurrentlyCasting(caster, statusBarIcon());

        boolean finalTargeted = targeted;
        double finalSkillDamage = getSkillDamage(caster);
        new BukkitRunnable(){
            boolean stage1 = false;
            Vector initialDirection;
            double angle = 0;
            int ea1 = 0;
            @Override
            public void run(){

                if(caster instanceof Player){
                    if(!((Player)caster).isOnline()){
                        cancelTask();
                        return;
                    }
                }

                if (initialDirection == null) {
                    initialDirection = caster.getLocation().getDirection().setY(0).normalize();
                }

                Location center = caster.getLocation();

                Vector direction = initialDirection.clone();
                double radians = Math.toRadians(angle);
                direction.rotateAroundY(radians);

                Vector dir = caster.getLocation().getDirection().setY(0).normalize();;

                if(stage1){

                    if(finalTargeted && targetStillValid(target)){
                        dir = target.getLocation().toVector().subtract(caster.getLocation().toVector()).setY(0).normalize();
                    }

                    direction = dir.multiply(-1);

                }

                Location loc = center.clone().add(direction.clone().multiply(1)).setDirection(direction);
                sword.teleport(loc);

                if(stage1){
                    ea1-=15;
                }

                sword.setRightArmPose(new EulerAngle(Math.toRadians(ea1), Math.toRadians(0), Math.toRadians(0)));


                if(!stage1){
                    if(angle<180){
                        angle+=20;
                    }

                    if(angle>=180){
                        stage1 = true;
                    }
                }

                if(ea1 < -180){

                    Location cLoc = sword.getLocation().clone().add(dir.multiply(-5));

                    double increment = (2 * Math.PI) / 16; // angle between particles

                    for(int j = 1; j<5; j++){
                        for (int i = 0; i < 16; i++) {
                            double angle = i * increment;
                            double x = cLoc.getX() + (j * Math.cos(angle));
                            double z = cLoc.getZ() + (j * Math.sin(angle));
                            Location ploc = new Location(cLoc.getWorld(), x, (cLoc.getY()), z);

                            caster.getWorld().spawnParticle(Particle.LAVA, ploc, 1,0, 0, 0, 0);
                        }
                    }

                    ignite(cLoc);

                    BoundingBox hitBox = new BoundingBox(
                            cLoc.getX() - 5,
                            cLoc.getY() - 2,
                            cLoc.getZ() - 5,
                            cLoc.getX() + 5,
                            cLoc.getY() + 5,
                            cLoc.getZ() + 5
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

                        double damage = (damageCalculator.calculateDamage(caster, livingEntity, DamageType.Physical, finalSkillDamage, crit, 1.2));
                        damage = damage * decisionMultiplier(caster);

                        //pvp logic
                        if(entity instanceof Player){
                            if(pvpManager.pvpLogic(caster, (Player) entity)){
                                changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, caster, crit);
                            }
                            continue;
                        }

                        if(pveChecker.pveLogic(livingEntity)){
                            Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(livingEntity, caster));
                            changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, caster, crit);
                        }

                    }

                    cancelTask();
                    return;
                }

                double percent = ((Math.abs(angle) + (Math.abs(ea1))) / 195) * 100;

                abilityManager.setCastBar(caster, percent);

            }

            private boolean targetStillValid(LivingEntity target){

                if(target instanceof Player){
                    if(!((Player)target).isOnline()){
                        return false;
                    }

                    if(profileManager.getAnyProfile(target).getIfDead()){
                        return false;
                    }
                }

                if(target.isDead()){
                    return false;
                }

                return target.getLocation().getWorld() == caster.getWorld();
            }

            private void ignite(Location center){

                double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                        profileManager.getAnyProfile(caster).getSkillLevels().getSkill_4_Level_Bonus();

                new BukkitRunnable(){
                    int count = 0;
                    @Override
                    public void run(){
                        double increment = (2 * Math.PI) / 16; // angle between particles

                        for(int j = 1; j<5; j++){
                            for (int i = 0; i < 16; i++) {
                                double angle = i * increment;
                                double x = center.getX() + (j * Math.cos(angle));
                                double z = center.getZ() + (j * Math.sin(angle));
                                Location ploc = new Location(center.getWorld(), x, (center.getY()), z);

                                caster.getWorld().spawnParticle(Particle.LAVA, ploc, 1,0, 0, 0, 0);
                            }
                        }

                        BoundingBox hitBox = new BoundingBox(
                                center.getX() - 5,
                                center.getY() - 2,
                                center.getZ() - 5,
                                center.getX() + 5,
                                center.getY() + 5,
                                center.getZ() + 5
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

                            double damage = (damageCalculator.calculateDamage(caster, livingEntity, DamageType.Physical, tickDamage * skillLevel, crit, 0));

                            //pvp logic
                            if(entity instanceof Player){
                                if(pvpManager.pvpLogic(caster, (Player) entity)){
                                    changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, caster, crit);
                                }
                                continue;
                            }

                            if(pveChecker.pveLogic(livingEntity)){
                                Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(livingEntity, caster));
                                changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, caster, crit);
                            }

                        }

                        if(count>=3){
                            this.cancel();
                        }

                        count++;
                    }
                }.runTaskTimer(main, 20, 20);

            }

            private void cancelTask(){
                this.cancel();
                sword.remove();
                abilityManager.stopCasting(caster);
                statusEffectManager.removeEffect(caster,"decision");
            }

        }.runTaskTimer(main, 0, 1);

    }

    private double decisionMultiplier(LivingEntity caster){

        if(statusEffectManager.hasEffect(caster, "decision")){
            return 1.8;
        }

        return 1;
    }

    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_4_Level_Bonus();

        double damage = baseDamage + ((int)(skillLevel/3));

        if(purity.active(caster)){
            damage = damage * 3;
            purity.reset(caster);
        }

        return damage;
    }

    @Override
    public boolean usable(LivingEntity caster){
        return cooldownManager.isReady(caster.getUniqueId(), 4, statusEffectManager.getHastePercent(caster));
    }

    @Override
    public String skillBarIcon(LivingEntity entity) {
        return "\ue3e0";
    }

    @Override
    public String statusBarIcon() {
        return "\ue41c";
    }
}
