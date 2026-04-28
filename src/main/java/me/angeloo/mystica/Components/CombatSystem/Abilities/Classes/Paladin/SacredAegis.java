package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Paladin;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.DamageModifiers.Immune;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.PvpManager;
import me.angeloo.mystica.Components.CombatSystem.TargetManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class SacredAegis extends BaseAbility {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final StatusEffectManager statusEffectManager;
    private final CooldownManager cooldownManager;

    public SacredAegis(Mystica main, AbilityManager manager){
        super("sacred_aegis");
        this.main = main;
        profileManager = main.getProfileManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        statusEffectManager = main.getStatusEffectManager();
        cooldownManager = main.getCooldownManager();
    }

    private final int baseCooldown = 120;

    @Override
    public boolean use(LivingEntity caster){


        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(!usable(caster, target)){
            return false;
        }

        if(target == null){
            target = caster;
        }

        if (profileManager.getAnyProfile(target).getIfDead()) {
            target = caster;
        }

        if(pvpManager.pvpLogic(caster, (Player) target)){
            target = caster;
        }

        execute(caster, target);


        //this is because cooldown is reduced on skill levels
        cooldownManager.start(caster.getUniqueId(), 6, (long) (getSkillCooldown(caster) * 1000L));

        return true;
    }

    @Override
    public int cooldown() {
        return baseCooldown;
    }

    private double getRange(LivingEntity caster){
        double baseRange = 12;
        double extraRange = statusEffectManager.getAdditionalRange(caster);
        return baseRange + extraRange;
    }

    private void execute(LivingEntity caster, LivingEntity target){

        Location start = target.getLocation().clone();
        Vector direction = target.getLocation().getDirection().setY(0).normalize();
        Vector crossProduct = direction.clone().crossProduct(new Vector(0,1,0)).normalize();

        ItemStack item = new ItemStack(Material.SUGAR);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(7);
        item.setItemMeta(meta);

        Location s1Spawn = start.clone().add(direction.clone().multiply(1)).setDirection(direction);
        ArmorStand shield = caster.getWorld().spawn(s1Spawn.clone().subtract(0,5,0), ArmorStand.class);
        shield.setInvisible(true);
        shield.setGravity(false);
        shield.setCollidable(false);
        shield.setInvulnerable(true);
        shield.setMarker(true);
        EntityEquipment entityEquipment = shield.getEquipment();
        assert entityEquipment != null;
        entityEquipment.setItemInMainHand(item);
        shield.setRightArmPose(new EulerAngle(Math.toRadians(0), Math.toRadians(0), Math.toRadians(0)));
        shield.teleport(s1Spawn);

        Location s2spawn = start.clone().subtract(direction.clone().multiply(1)).setDirection(direction.clone().multiply(-1));
        ArmorStand shield2 = caster.getWorld().spawn(s2spawn.clone().subtract(0,5,0), ArmorStand.class);
        shield2.setInvisible(true);
        shield2.setGravity(false);
        shield2.setCollidable(false);
        shield2.setInvulnerable(true);
        shield2.setMarker(true);
        EntityEquipment entityEquipment2 = shield2.getEquipment();
        assert entityEquipment2 != null;
        entityEquipment2.setItemInMainHand(item);
        shield2.setRightArmPose(new EulerAngle(Math.toRadians(0), Math.toRadians(0), Math.toRadians(0)));
        shield2.teleport(s2spawn);

        Location s3spawn = start.clone().add(crossProduct.clone().multiply(1)).setDirection(crossProduct);
        ArmorStand shield3 = caster.getWorld().spawn(s3spawn.clone().subtract(0,5,0), ArmorStand.class);
        shield3.setInvisible(true);
        shield3.setGravity(false);
        shield3.setCollidable(false);
        shield3.setInvulnerable(true);
        shield3.setMarker(true);
        EntityEquipment entityEquipment3 = shield3.getEquipment();
        assert entityEquipment3 != null;
        entityEquipment3.setItemInMainHand(item);
        shield3.setRightArmPose(new EulerAngle(Math.toRadians(0), Math.toRadians(0), Math.toRadians(0)));
        shield3.teleport(s3spawn);

        Location shield4spawn = start.clone().subtract(crossProduct.clone().multiply(1)).setDirection(crossProduct.clone().multiply(-1));
        ArmorStand shield4 = caster.getWorld().spawn(shield4spawn.clone().subtract(0,5,0), ArmorStand.class);
        shield4.setInvisible(true);
        shield4.setGravity(false);
        shield4.setCollidable(false);
        shield4.setInvulnerable(true);
        shield4.setMarker(true);
        EntityEquipment entityEquipment4 = shield4.getEquipment();
        assert entityEquipment4 != null;
        entityEquipment4.setItemInMainHand(item);
        shield4.setRightArmPose(new EulerAngle(Math.toRadians(0), Math.toRadians(0), Math.toRadians(0)));
        shield4.teleport(shield4spawn);

        statusEffectManager.applyEffect(target, new Immune(), 100, null, caster);


        new BukkitRunnable(){
            Vector initialDirection;
            double angle = 0;
            int count = 0;
            @Override
            public void run(){

                if(target instanceof Player){
                    if(!((Player)target).isOnline()){
                        cancelTask();
                        return;
                    }
                }


                if (initialDirection == null) {
                    initialDirection = target.getLocation().getDirection().setY(0).normalize();
                }

                Location center = target.getLocation();
                Vector direction = initialDirection.clone();
                double radians = Math.toRadians(angle);
                direction.rotateAroundY(radians);
                Vector crossProduct = direction.clone().crossProduct(new Vector(0,1,0)).normalize();

                Location s1 = center.clone().add(direction.clone().multiply(1)).setDirection(direction);
                shield.teleport(s1);
                Location s2 = center.clone().subtract(direction.clone().multiply(1)).setDirection(direction.clone().multiply(-1));
                shield2.teleport(s2);
                Location s3 = center.clone().add(crossProduct.clone().multiply(1)).setDirection(crossProduct);
                shield3.teleport(s3);
                Location s4 = center.clone().subtract(crossProduct.clone().multiply(1)).setDirection(crossProduct.clone().multiply(-1));
                shield4.teleport(s4);

                if(count>=20*5){
                    cancelTask();
                }

                angle+=5;
                count++;
            }

            private void cancelTask(){
                this.cancel();
                shield.remove();
                shield2.remove();
                shield3.remove();
                shield4.remove();
            }

        }.runTaskTimer(main, 0, 1);

    }
    private int getSkillCooldown(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_6_Level_Bonus();
        return baseCooldown - ((int) skillLevel/3);
    }


    @Override
    public boolean usable(LivingEntity caster, LivingEntity target){
        if(target != null){

            if(!(target instanceof Player)){
                target = caster;
            }

            double distance = caster.getLocation().distance(target.getLocation());

            if(distance > getRange(caster)){
                return false;
            }

        }


        return cooldownManager.isReady(caster.getUniqueId(), 6, statusEffectManager.getHastePercent(caster));
    }

}
