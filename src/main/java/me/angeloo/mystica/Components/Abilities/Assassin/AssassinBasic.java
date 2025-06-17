package me.angeloo.mystica.Components.Abilities.Assassin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import me.angeloo.mystica.Components.Abilities.AssassinAbilities;
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
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AssassinBasic {

    private final Mystica main;


    private final ProfileManager profileManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final CombatManager combatManager;
    private final TargetManager targetManager;
    private final FakePlayerTargetManager fakePlayerTargetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final ChangeResourceHandler changeResourceHandler;

    private final Map<UUID, Boolean> evenOdd = new HashMap<>();
    private final Map<UUID, BukkitTask> basicRunning = new HashMap<>();

    private final Stealth stealth;
    private final Combo combo;
    private final DuelistsFrenzy duelistsFrenzy;

    public AssassinBasic(Mystica main, AbilityManager manager, AssassinAbilities assassinAbilities){
        this.main = main;
        profileManager = main.getProfileManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        combatManager = manager.getCombatManager();
        targetManager = main.getTargetManager();
        fakePlayerTargetManager = main.getFakePlayerTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        changeResourceHandler = main.getChangeResourceHandler();
        stealth = assassinAbilities.getStealth();
        combo = assassinAbilities.getCombo();
        duelistsFrenzy = assassinAbilities.getDuelistsFrenzy();
    }

    public void useBasic(LivingEntity caster){

        if(getIfBasicRunning(caster)){
            return;
        }

        combatManager.startCombatTimer(caster);

        executeBasic(caster);

    }

    private void executeBasic(LivingEntity caster){

        basicRunning.put(caster.getUniqueId(), null);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(buffAndDebuffManager.getIfBasicInterrupt(caster)){
                    this.cancel();
                    stopBasicRunning(caster);
                    return;
                }

                if(targetManager.getPlayerTarget(caster) != null){
                    if(profileManager.getAnyProfile(targetManager.getPlayerTarget(caster)).getIfDead()){
                        this.cancel();
                        stopBasicRunning(caster);
                        return;
                    }
                }


                basicStage(caster);

                if(caster instanceof Player){
                    combatManager.startCombatTimer(caster);
                }


            }
        }.runTaskTimer(main, 0, 8);
        basicRunning.put(caster.getUniqueId(), task);


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

            if(!(entity instanceof LivingEntity)){
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

            LivingEntity livingEntity = (LivingEntity) entity;

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
            double damage = damageCalculator.calculateDamage(caster, targetToHit, "Physical", getSkillDamage(caster), crit);

            Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(targetToHit, caster));
            changeResourceHandler.subtractHealthFromEntity(targetToHit, damage, caster, crit);

            stealth.stealthBonusCheck(caster, targetToHit);
            if(duelistsFrenzy.getFrenzy(caster)){
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

    private boolean getIfEvenOdd(Player player){
        return evenOdd.getOrDefault(player.getUniqueId(), false);
    }

    private boolean getIfBasicRunning(LivingEntity caster){
        return basicRunning.containsKey(caster.getUniqueId());
    }

    public void stopBasicRunning(LivingEntity caster){
        if(basicRunning.containsKey(caster.getUniqueId())){
            basicRunning.get(caster.getUniqueId()).cancel();
            basicRunning.remove(caster.getUniqueId());
        }
    }

    public double getSkillDamage(LivingEntity caster){
        double level = profileManager.getAnyProfile(caster).getStats().getLevel();
        return 14 + ((int)(level/3));
    }

}
