package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.ShadowKnight;


import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityLookup;
import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BasicAttacks.BasicAttackDefinition;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.FakePlayerTargetManager;
import me.angeloo.mystica.Components.CombatSystem.PvpManager;
import me.angeloo.mystica.Components.CombatSystem.TargetManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;
import me.angeloo.mystica.Utility.Enums.SubClass;
import me.angeloo.mystica.Utility.Logic.PveChecker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShadowKnightBasic implements BasicAttackDefinition {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final TargetManager targetManager;
    private final FakePlayerTargetManager fakePlayerTargetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final ChangeResourceHandler changeResourceHandler;
    private final StatusEffectManager statusEffectManager;

    //perhaps have energy restoration on hit

    public ShadowKnightBasic(Mystica main, AbilityManager manager, AbilityLookup lookup){
        this.main = main;
        profileManager = main.getProfileManager();
        targetManager = main.getTargetManager();
        fakePlayerTargetManager = main.getFakePlayerTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        changeResourceHandler = main.getChangeResourceHandler();
        statusEffectManager = main.getStatusEffectManager();
    }

    @Override
    public boolean performStage(LivingEntity caster, int stage) {


        //triggers animations on companions
        if(MythicBukkit.inst().getAPIHelper().isMythicMob(caster.getUniqueId())){
            AbstractEntity abstractEntity = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(caster).getEntity();
            MythicBukkit.inst().getAPIHelper().getMythicMobInstance(caster).signalMob(abstractEntity, "basic");
        }

        basicStage1(caster, stage+1);

        return true;
    }

    @Override
    public int getMaxStages(LivingEntity caster) {
        return 4;
    }

    @Override
    public int getStageDelay(LivingEntity caster, int stage) {

        if(stage==4){
            return 30;
        }

        return 15;
    }

    @Override
    public boolean canStart(LivingEntity caster) {
        return statusEffectManager.canBasic(caster);
    }

    @Override
    public boolean canContinue(LivingEntity caster, int nextStage) {
        return statusEffectManager.canBasic(caster);
    }


    private void basicStage1(LivingEntity caster, int newStage){

        Vector direction = caster.getLocation().getDirection().setY(0).normalize();

        Location loc = caster.getLocation().clone().add(direction.multiply(1.25));

        BoundingBox hitBox = new BoundingBox(
                loc.getX() - 3,
                loc.getY() - 2,
                loc.getZ() - 3,
                loc.getX() + 3,
                loc.getY() + 4,
                loc.getZ() + 3
        );

        LivingEntity targetToHit = null;
        LivingEntity target = targetManager.getPlayerTarget(caster);
        LivingEntity firstHit = null;

        boolean targetHit = false;


        for (Entity entity : caster.getWorld().getNearbyEntities(hitBox)) {

            if(entity == caster){
                continue;
            }

            if(entity.isDead()){
                continue;
            }

            if(!(entity instanceof LivingEntity livingEntity)){
                continue;
            }

            if(entity instanceof Player){
                if(!pvpManager.pvpLogic(caster, (Player) entity)){
                    continue;
                }
            }

            if(entity instanceof ArmorStand){
                continue;
            }

            if(!(entity instanceof Player)){
                if(!pveChecker.pveLogic(livingEntity)){
                    continue;
                }
            }


            if(firstHit == null){
                firstHit = livingEntity;
            }

            if(target != null){
                if(livingEntity == target){
                    targetHit = true;
                    targetToHit = livingEntity;
                    break;
                }
            }
        }

        if(!targetHit && firstHit != null){
            targetToHit = firstHit;
        }

        if(targetToHit != null){
            if(caster instanceof Player){
                targetManager.setPlayerTarget((Player)caster, targetToHit);
            }
            else{
                fakePlayerTargetManager.setFakePlayerTarget(caster, targetToHit);
            }

            boolean crit = damageCalculator.checkIfCrit(caster, 0);
            double damage = damageCalculator.calculateDamage(caster, targetToHit, "Physical", getSkillDamage(caster), crit);

            Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(targetToHit, caster));
            changeResourceHandler.subtractHealthFromEntity(targetToHit, damage, caster, crit);

        }

        Location startStand = caster.getLocation();

        ArmorStand armorStand = caster.getWorld().spawn(startStand.clone().subtract(0,10,0), ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);

        EntityEquipment entityEquipment = armorStand.getEquipment();


        ItemStack item = new ItemStack(Material.REDSTONE);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        if(newStage%2==0){
            meta.setCustomModelData(1);
        }
        else{
            meta.setCustomModelData(2);
        }


        item.setItemMeta(meta);

        assert entityEquipment != null;
        entityEquipment.setHelmet(item);
        armorStand.teleport(startStand);

        new BukkitRunnable(){
            int count = 0;
            @Override
            public void run(){


                Location current = armorStand.getLocation();

                current.add(direction.normalize().multiply(.25));

                current.setDirection(direction);

                armorStand.teleport(current);


                if (count > 10) {
                    cancelTask();
                }


                count++;
            }

            private void cancelTask() {
                this.cancel();
                armorStand.remove();
            }
        }.runTaskTimer(main, 0, 1);
    }


    public double getSkillDamage(LivingEntity caster){
        double level = profileManager.getAnyProfile(caster).getStats().getLevel();
        return 14 + ((int)(level/3));
    }


}
