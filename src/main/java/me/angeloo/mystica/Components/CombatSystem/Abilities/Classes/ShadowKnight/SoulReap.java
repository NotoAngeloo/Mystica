package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.ShadowKnight;


import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.PlayerStateManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.CrowdControl.Root;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.PvpManager;
import me.angeloo.mystica.Components.CombatSystem.TargetManager;
import me.angeloo.mystica.Components.Hud.BossCastingManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.CustomEvents.HudUpdateEvent;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;
import me.angeloo.mystica.Utility.Enums.BarType;
import me.angeloo.mystica.Utility.Enums.SubClass;
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
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class SoulReap extends BaseAbility {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final AbilityManager abilityManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final StatusEffectManager statusEffectManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final CooldownManager cooldownManager;
    private final BossCastingManager bossCastingManager;
    private final PlayerStateManager playerStateManager;

    private final Energy energy;

    public SoulReap(Mystica main, AbilityManager manager){
        super("soul_reap");
        this.main = main;
        profileManager = main.getProfileManager();
        abilityManager = manager;
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        statusEffectManager = main.getStatusEffectManager();
        changeResourceHandler = main.getChangeResourceHandler();
        cooldownManager = manager.getCooldownManager();
        energy = manager.getEnergy();
        bossCastingManager = main.getBossCastingManager();
        playerStateManager = manager.getPlayerStateManager();
    }

    private final int baseCooldown = 10;
    private final double range = 8;
    private final int baseDamage = 30;
    private final int cost = 30;

    @Override
    public boolean use(LivingEntity caster){


        targetManager.setTargetToNearestValid(caster, range);

        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(!usable(caster, target)){
            return false;
        }

        energy.subTractEnergyFromEntity(caster, cost);

        execute(caster);

        cooldownManager.start(caster.getUniqueId(), 5, (long) (baseCooldown * 1000));
        return true;
    }

    @Override
    public int cooldown() {
        return baseCooldown;
    }

    private void execute(LivingEntity caster){

        boolean doom = profileManager.getAnyProfile(caster).getPlayerSubclass().equals(SubClass.Doom);

        LivingEntity target = targetManager.getPlayerTarget(caster);

        Location start = caster.getLocation().clone();

        Location end = target.getLocation().clone();
        Vector initDir = end.toVector().subtract(start.toVector());

        caster.teleport(start.clone().setDirection(initDir));
        statusEffectManager.applyEffect(caster, new Root(), -1, null, caster);

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

        abilityManager.setSkillCurrentlyCasting(caster, statusBarIcon());

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
            int count = 0;
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

                    double targetHealthPercent = profileManager.getAnyProfile(target).getCurrentHealth() / (profileManager.getAnyProfile(target).getTotalHealth() + statusEffectManager.getHealthBuffAmount(target));

                    if(targetHealthPercent<=.3){
                        skillDamage = skillDamage * .3;
                    }

                    boolean crit = damageCalculator.checkIfCrit(caster, 0);

                    double extra = 0;

                    if(doom && playerStateManager.get(caster.getUniqueId()).has("infection_enhanced")){
                        extra = playerStateManager.get(caster.getUniqueId()).getInt("reap_bonus_damage", 0);
                    }

                    double damage = damageCalculator.calculateDamage(caster, target, "Physical", skillDamage, crit);
                    damage = damage + extra;
                    playerStateManager.get(caster.getUniqueId()).remove("soul_mark");
                    Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, caster));
                    changeResourceHandler.subtractHealthFromEntity(target, damage, caster, crit);
                    bossCastingManager.interrupt(caster, target);
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
                abilityManager.stopCasting(caster);
                statusEffectManager.removeEffect(caster, "root");
            }

        }.runTaskTimer(main, 0, 1);

    }


    @Override
    public void onExternalTrigger(LivingEntity caster){
        addSoulMark(caster);
    }

    private void addSoulMark(LivingEntity caster){


        int stacks = playerStateManager.get(caster.getUniqueId()).getInt("soul_mark", 0);

        if(stacks>5){
            return;
        }

        stacks ++;

        playerStateManager.get(caster.getUniqueId()).set("soul_mark", stacks);
    }



    public double getSkillDamage(LivingEntity caster){
        double skillDamage = baseDamage + (2*playerStateManager.get(caster.getUniqueId()).getInt("soul_mark", 0));
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_5_Level_Bonus();

        return skillDamage + ((int)(skillLevel/3));
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

            if(distance > range){
                return false;
            }
        }

        if(target == null){
            return false;
        }

        if(energy.getCurrentEnergy(caster)<cost){
            return false;
        }


        return cooldownManager.isReady(caster.getUniqueId(), 5, statusEffectManager.getHastePercent(caster));
    }

    /*public int returnWhichItem(Player player){

        if(energy.getCurrentEnergy(player)<getCost()){
            return 8;
        }

        return 0;
    }*/

}
