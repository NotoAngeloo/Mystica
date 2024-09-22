package me.angeloo.mystica.Components.Abilities.ShadowKnight;

import me.angeloo.mystica.Components.Abilities.ShadowKnightAbilities;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.*;
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
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Annihilation {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final ShieldAbilityManaDisplayer shieldAbilityManaDisplayer;
    private final CombatManager combatManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    private final Energy energy;
    private final Infection infection;

    public Annihilation(Mystica main, AbilityManager manager, ShadowKnightAbilities shadowKnightAbilities){
        this.main = main;
        profileManager = main.getProfileManager();
        shieldAbilityManaDisplayer = new ShieldAbilityManaDisplayer(main, manager);
        combatManager = manager.getCombatManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
        energy = shadowKnightAbilities.getEnergy();
        infection = shadowKnightAbilities.getInfection();
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

        abilityReadyInMap.put(caster.getUniqueId(), 3);
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

                if(caster instanceof  Player){
                    shieldAbilityManaDisplayer.displayPlayerHealthPlusInfo((Player) caster);
                }


            }
        }.runTaskTimer(main, 0,20);
        cooldownTask.put(caster.getUniqueId(), task);

    }

    private void execute(LivingEntity caster){

        LivingEntity target = targetManager.getPlayerTarget(caster);

        Location start = caster.getLocation().clone();
        Location end = target.getLocation().clone();
        Vector initDir = end.toVector().subtract(start.toVector());
        Vector direction = initDir.clone();
        direction.rotateAroundY(-45);

        Location spawnLoc = start.clone().subtract(0,3,0);
        spawnLoc.setDirection(direction);

        ArmorStand armorStand = caster.getWorld().spawn(spawnLoc, ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);


        EntityEquipment entityEquipment = armorStand.getEquipment();

        ItemStack item = new ItemStack(Material.REDSTONE);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(6);
        item.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setHelmet(item);



        //abilityManager.setSkillRunning(player, true);
        double finalSkillDamage = getSkillDamage(caster);
        new BukkitRunnable(){
            int ran = 0;
            int height = 0;
            int angle = 0;
            Location pLoc = null;
            Vector pDir = null;
            @Override
            public void run(){

                if(caster instanceof Player){
                    if(!((Player)caster).isOnline()){
                        cancelTask();
                        return;
                    }
                }

                if(profileManager.getAnyProfile(caster).getIfDead()){
                    cancelTask();
                    return;
                }

                Location start = caster.getLocation().clone();
                Location loc = start.clone().subtract(0,3,0);

                if(ran < 5){
                    height ++;
                }

                if(ran > 5 && angle<=40){
                    angle+=13;
                    double radians = Math.toRadians(angle);
                    direction.rotateAroundY(radians);
                }

                loc.setDirection(direction);

                loc.add(0,((double) height / 2), 0);

                armorStand.teleport(loc);

                if(ran==5){
                    pLoc = loc.clone().add(direction.multiply(1.5));
                    pLoc.add(0,1.5,0);
                    pDir = initDir.clone().crossProduct(new Vector(0,1,0)).normalize();
                }

                if(pLoc != null){
                    caster.getWorld().spawnParticle(Particle.SPELL_WITCH, pLoc, 1, 0, 0, 0, 0);
                    if(angle>40){
                        pLoc.subtract(pDir.multiply(1));
                    }
                }

                if(ran >= 20){
                    cancelTask();

                    double distance = caster.getLocation().distance(target.getLocation());

                    if(distance<8){
                        hitTarget();
                    }

                }


                ran++;
            }

            private void cancelTask() {
                this.cancel();
                armorStand.remove();
                //abilityManager.setSkillRunning(player, false);
            }

            private void hitTarget(){

                if(profileManager.getIfResetProcessing(target)){
                    return;
                }

                boolean crit = damageCalculator.checkIfCrit(caster, 0);
                double damage = damageCalculator.calculateDamage(caster, target, "Physical", finalSkillDamage, crit);
                Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, caster));
                changeResourceHandler.subtractHealthFromEntity(target, damage, caster);

                //Bukkit.getLogger().info(String.valueOf(infection.getIfThisPlayerInfectThisEntity(player, target)));
                if(infection.getIfThisPlayerInfectThisEntity(caster, target)){
                    infection.infectionEnhancement(caster, target);
                }
            }

        }.runTaskTimer(main, 0, 1);

    }

    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getStats().getLevel();
        return 45 + ((int)(skillLevel/3));
    }

    public int getCost(){
        return 30;
    }

    public int getCooldown(LivingEntity caster){
        int cooldown = abilityReadyInMap.getOrDefault(caster.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
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

}
