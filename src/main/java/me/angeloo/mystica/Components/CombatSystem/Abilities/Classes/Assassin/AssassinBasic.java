package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Assassin;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityLookup;
import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BasicAttacks.BasicAttackDefinition;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.AssassinAbilities;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.Classes.PlayerClass;
import me.angeloo.mystica.Components.CombatSystem.FakePlayerTargetManager;
import me.angeloo.mystica.Components.CombatSystem.PvpManager;
import me.angeloo.mystica.Components.CombatSystem.TargetManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;
import me.angeloo.mystica.Utility.Enums.DamageType;
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

public class AssassinBasic implements BasicAttackDefinition{

    private final Mystica main;

    private final ProfileManager profileManager;
    private final StatusEffectManager statusEffectManager;
    private final TargetManager targetManager;
    private final FakePlayerTargetManager fakePlayerTargetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final ChangeResourceHandler changeResourceHandler;

    private final AbilityLookup lookup;

    private final Combo combo;

    public AssassinBasic(Mystica main, AbilityManager manager, AbilityLookup lookup){
        this.main = main;
        profileManager = main.getProfileManager();
        statusEffectManager = main.getStatusEffectManager();
        targetManager = main.getTargetManager();
        fakePlayerTargetManager = main.getFakePlayerTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        changeResourceHandler = main.getChangeResourceHandler();
        this.lookup = lookup;
        combo = manager.getCombo();

    }


    //all the same for now
    @Override
    public boolean performStage(LivingEntity caster, int stage) {
        basicStage(caster);
        //because it will always succeed
        return true;
    }

    @Override
    public int getMaxStages(LivingEntity caster) {
        return 1;
    }

    //every 8 ticks
    @Override
    public int getStageDelay(LivingEntity caster, int stage) {
        return 8;
    }

    @Override
    public boolean canStart(LivingEntity caster) {
        return statusEffectManager.canBasic(caster);
    }

    @Override
    public boolean canContinue(LivingEntity caster, int nextStage) {
        return statusEffectManager.canBasic(caster);
    }


    private void basicStage(LivingEntity caster){
        Location start = caster.getLocation().clone();
        Vector direction = caster.getLocation().getDirection().setY(0).normalize();
        Location center = start.clone().add(direction.clone().multiply(3));

        BoundingBox hitBox = new BoundingBox(
                center.getX() - 4,
                center.getY() - 2,
                center.getZ() - 4,
                center.getX() + 4,
                center.getY() + 6,
                center.getZ() + 4
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

        if(!targetHit && firstHit!= null){
            targetToHit = firstHit;
        }


        if(targetToHit != null){

            if(caster instanceof Player){
                targetManager.setPlayerTarget((Player)caster, targetToHit);
            }
            else{
                fakePlayerTargetManager.setFakePlayerTarget(caster, targetToHit);
            }



            Location casterLoc = caster.getLocation().clone();
            Location targetLoc = targetToHit.getLocation();
            Vector targetDir = targetLoc.toVector().subtract(casterLoc.toVector());

            if(casterLoc!=targetLoc){
                Location warpLoc = targetLoc.add(targetDir.clone().normalize().multiply(-1.5));
                warpLoc.setDirection(targetDir);

                while (!warpLoc.getBlock().isPassable()){
                    warpLoc.add(0,.1,0);
                }

                if(caster instanceof Player){
                    if(((Player)caster).isSneaking()){
                        caster.teleport(warpLoc);
                    }
                }



            }


            boolean crit = damageCalculator.checkIfCrit(caster, 0);
            double damage = damageCalculator.calculateDamage(caster, targetToHit, DamageType.Physical, getSkillDamage(caster), crit, 0);

            Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(targetToHit, caster));
            changeResourceHandler.subtractHealthFromEntity(targetToHit, damage, caster, crit);

            lookup.get(PlayerClass.ASSASSIN, 8).onExternalTrigger(caster, targetToHit);


            //perhaps only if has an equipment set
            if(statusEffectManager.hasEffect(caster, "duelists_frenzy")){
                combo.addComboPoint(caster);
            }

        }


        Location startStand = caster.getLocation();

        ArmorStand armorStand = caster.getWorld().spawn(startStand.clone().subtract(0,10,0), ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);

        EntityEquipment entityEquipment = armorStand.getEquipment();

        ItemStack item = new ItemStack(Material.SLIME_BALL);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(4);
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

    @Override
    public String skillBarIcon(LivingEntity entity) {
        return "\ue3b5";
    }
}
