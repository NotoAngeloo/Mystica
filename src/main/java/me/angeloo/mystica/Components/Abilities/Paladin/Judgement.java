package me.angeloo.mystica.Components.Abilities.Paladin;

import me.angeloo.mystica.Components.Abilities.PaladinAbilities;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.CooldownDisplayer;
import me.angeloo.mystica.Utility.DamageCalculator;
import me.angeloo.mystica.Utility.PveChecker;
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Judgement {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final FakePlayerTargetManager fakePlayerTargetManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final AggroManager aggroManager;
    private final CooldownDisplayer cooldownDisplayer;

    private final Purity purity;
    private final Decision decision;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public Judgement(Mystica main, AbilityManager manager, PaladinAbilities paladinAbilities){
        this.main = main;
        profileManager = main.getProfileManager();
        combatManager = manager.getCombatManager();
        fakePlayerTargetManager = main.getFakePlayerTargetManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
        aggroManager = main.getAggroManager();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
        decision = paladinAbilities.getDecision();
        purity = paladinAbilities.getPurity();
    }

    public void use(LivingEntity caster){

        if(!abilityReadyInMap.containsKey(caster.getUniqueId())){
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }

        if(!usable(caster)){
            return;
        }

        LivingEntity target = targetManager.getPlayerTarget(caster);


        combatManager.startCombatTimer(caster);

        if(target == null){
            target = caster;
        }

        if(profileManager.getAnyProfile(target).getIfDead()){
            target = caster;
        }

        execute(caster, target);
        purity.skillListAdd(caster, 8);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), 15);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getCooldown(caster) <= 0){
                    cooldownDisplayer.displayCooldown(caster, 8);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(caster) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(caster);

                abilityReadyInMap.put(caster.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(caster, 8);

            }
        }.runTaskTimer(main, 0,20);
        cooldownTask.put(caster.getUniqueId(), task);

    }

    private void execute(LivingEntity caster, LivingEntity target){

        boolean templar = profileManager.getAnyProfile(caster).getPlayerSubclass().equalsIgnoreCase("templar");

        Location start = target.getLocation();

        ArmorStand armorStand = caster.getWorld().spawn(start.clone(), ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);

        EntityEquipment entityEquipment = armorStand.getEquipment();

        ItemStack item = new ItemStack(Material.SUGAR);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(8);
        item.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setHelmet(item);



        double finalSkillDamage = getSkillDamage(caster);
        new BukkitRunnable(){
            double down = 0;
            @Override
            public void run(){


                double increment = (2 * Math.PI) / 16; // angle between particles

                for (int i = 0; i < 16; i++) {
                    double angle = i * increment;
                    double x = start.getX() + (1 * Math.cos(angle));
                    double y = start.clone().add(0,7-down,0).getY();
                    double z = start.getZ() + (1 * Math.sin(angle));
                    Location loc = new Location(start.getWorld(), x, y, z);

                    caster.getWorld().spawnParticle(Particle.FLAME, loc, 1,0, 0, 0, 0);
                }

                if(down>=7){
                    this.cancel();
                    armorStand.remove();
                    doSomething(target);
                }

                down+=0.7;
            }

            private void doSomething(LivingEntity target){

                if(checkValid(target)){
                    return;
                }

                boolean crit = damageCalculator.checkIfCrit(caster, 0);

                if(target instanceof Player){

                    if(!pvpManager.pvpLogic(caster, (Player) target)){

                        double healPower = 5;
                        double healAmount = damageCalculator.calculateHealing(caster, healPower, crit);
                        changeResourceHandler.addHealthToEntity(target, healAmount, caster);
                        return;
                    }

                }

                if(!(target instanceof Player)){
                    if(!pveChecker.pveLogic(target)){
                        double healPower = 5;
                        double healAmount = damageCalculator.calculateHealing(caster, healPower, crit);
                        changeResourceHandler.addHealthToEntity(target, healAmount, caster);
                        return;
                    }
                }

                double damage = damageCalculator.calculateDamage(caster, target, "Physical", finalSkillDamage, crit);
                damage = damage * decisionMultiplier(caster);


                Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, caster));
                changeResourceHandler.subtractHealthFromEntity(target, damage, caster);

                if(templar){
                    aggroManager.setAsHighPriorityTarget(target, caster);

                    if(target instanceof Player){
                        targetManager.setPlayerTarget(target, caster);
                        return;
                    }
                }

                decision.removeDecision(caster);
            }

            private boolean checkValid(LivingEntity target){

                if(target instanceof Player){
                    if(((Player)target).isOnline()){
                        return true;
                    }

                    if(profileManager.getAnyProfile(target).getIfDead()){
                        return true;
                    }
                }

                return target.isDead();
            }

        }.runTaskTimer(main, 0, 1);
    }

    private double decisionMultiplier(LivingEntity caster){

        if(decision.getDecision(caster)){
            return 1.8;
        }

        return 1;
    }

    public void resetCooldownDawn(LivingEntity caster){

        boolean dawn = profileManager.getAnyProfile(caster).getPlayerSubclass().equalsIgnoreCase("dawn");

        if(!dawn){
            return;
        }

        abilityReadyInMap.put(caster.getUniqueId(), 0);

    }

    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_5_Level_Bonus();
        return (purity.calculatePurityPercentDamage(caster, 8, 30)) + ((int)(skillLevel/3));
    }

    public int getCooldown(LivingEntity entity){

        int cooldown = abilityReadyInMap.getOrDefault(entity.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

    public void resetCooldown(LivingEntity caster){
        abilityReadyInMap.remove(caster.getUniqueId());
    }

    public boolean usable(LivingEntity caster){
        double baseRange = 15;
        double extraRange = buffAndDebuffManager.getTotalRangeModifier(caster);
        double totalRange = baseRange + extraRange;

        LivingEntity target;

        if(caster instanceof Player){
            target = targetManager.getPlayerTarget(caster);
        }
        else{
            target = fakePlayerTargetManager.getTarget(caster);
        }


        if(target != null){

            double distance = caster.getLocation().distance(target.getLocation());

            if(distance > totalRange){
                return false;
            }


        }

        return getCooldown(caster) <= 0;
    }

}
