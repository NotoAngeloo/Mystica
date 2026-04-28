package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.ShadowKnight;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.PvpManager;
import me.angeloo.mystica.Components.CombatSystem.TargetManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.BossManager;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;
import me.angeloo.mystica.Utility.Enums.PlayerClass;
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
import org.bukkit.util.Vector;

public class Annihilation extends BaseAbility {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final BossManager bossManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final StatusEffectManager statusEffectManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final CooldownManager cooldownManager;

    private final Energy energy;

    public Annihilation(Mystica main, AbilityManager manager){
        super("annihilation");
        this.main = main;
        profileManager = main.getProfileManager();
        bossManager = main.getBossManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        statusEffectManager = main.getStatusEffectManager();
        changeResourceHandler = main.getChangeResourceHandler();
        energy = manager.getEnergy();
        cooldownManager = main.getCooldownManager();
    }

    private final int baseCooldown = 3;
    private final double range = 8;
    private final int baseDamage = 45;
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

        cooldownManager.start(caster.getUniqueId(), -1, (long) (baseCooldown * 1000));

        return true;
    }

    @Override
    public int cooldown() {
        return baseCooldown;
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

                if(bossManager.getIfResetProcessing(target)){
                    return;
                }

                boolean crit = damageCalculator.checkIfCrit(caster, 0);
                double damage = damageCalculator.calculateDamage(caster, target, "Physical", finalSkillDamage, crit);
                Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, caster));
                changeResourceHandler.subtractHealthFromEntity(target, damage, caster, crit);
                lookup.get(PlayerClass.Shadow_Knight, SubClass.Doom, 1).onExternalTrigger(caster, target);
            }

        }.runTaskTimer(main, 0, 1);

    }

    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getStats().getLevel();
        return baseDamage + ((int)(skillLevel/3));
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


        return cooldownManager.isReady(caster.getUniqueId(), -1, statusEffectManager.getHastePercent(caster));
    }

    /*public int returnWhichItem(Player player){

        if(energy.getCurrentEnergy(player)<getCost()){
            return 6;
        }

        return 0;
    }*/

}
