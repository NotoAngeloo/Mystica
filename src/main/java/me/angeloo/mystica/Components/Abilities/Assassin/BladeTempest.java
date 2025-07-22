package me.angeloo.mystica.Components.Abilities.Assassin;

import me.angeloo.mystica.Components.Abilities.AssassinAbilities;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.Hud.CooldownDisplayer;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;
import me.angeloo.mystica.Utility.Logic.PveChecker;
import me.angeloo.mystica.Utility.Enums.SubClass;
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

import java.util.*;

public class BladeTempest {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final DamageCalculator damageCalculator;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final CooldownDisplayer cooldownDisplayer;

    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();
    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();

    private final Stealth stealth;
    private final Combo combo;

    public BladeTempest(Mystica main, AbilityManager manager, AssassinAbilities assassinAbilities){
        this.main = main;
        profileManager = main.getProfileManager();
        combatManager = manager.getCombatManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
        damageCalculator = main.getDamageCalculator();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
        stealth = assassinAbilities.getStealth();
        combo = assassinAbilities.getCombo();
    }

    public void use(LivingEntity caster){
        if (!abilityReadyInMap.containsKey(caster.getUniqueId())) {
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }

        if(!usable(caster)){
            return;
        }

        combatManager.startCombatTimer(caster);

        execute(caster);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), 17);
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {

                if (getCooldown(caster) <= 0) {
                    cooldownDisplayer.displayCooldown(caster, 6);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(caster) - 1;

                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(caster);

                abilityReadyInMap.put(caster.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(caster, 6);

            }
        }.runTaskTimerAsynchronously(main, 0, 20);
        cooldownTask.put(caster.getUniqueId(), task);
    }

    private void execute(LivingEntity caster){

        boolean duelist = profileManager.getAnyProfile(caster).getPlayerSubclass().equals(SubClass.Duelist);

        if(duelist){
            buffAndDebuffManager.getBladeTempestCrit().applyBonus(caster);
        }

        Location start = caster.getLocation().clone();

        Vector direction = start.getDirection().setY(0).normalize();

        ItemStack item = new ItemStack(Material.SLIME_BALL);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(2);
        item.setItemMeta(meta);


        final boolean[] trigger = {false};
        Set<LivingEntity> hitBySkill = new HashSet<>();
        double finalSkillDamage = getSkillDamage(caster);
        for(int i = 0;i<6;i++){

            direction.rotateAroundY(Math.toRadians(60));

            ArmorStand stand = caster.getWorld().spawn(start.clone().subtract(0,10,0), ArmorStand.class);
            stand.setInvisible(true);
            stand.setGravity(false);
            stand.setCollidable(false);
            stand.setInvulnerable(true);
            stand.setMarker(true);
            EntityEquipment entityEquipment = stand.getEquipment();
            assert entityEquipment != null;
            entityEquipment.setHelmet(item);
            stand.teleport(start.clone().setDirection(direction));

            new BukkitRunnable(){
                boolean hit = false;
                final Vector finalDirection = direction.clone();
                double traveled = 0;
                @Override
                public void run(){

                    Location current = stand.getLocation();
                    double distanceThisTick = .4;
                    current.add(finalDirection.normalize().multiply(distanceThisTick));
                    current.setDirection(finalDirection);
                    traveled+=distanceThisTick;
                    stand.teleport(current);

                    BoundingBox hitBox = new BoundingBox(
                            current.getX() - 2,
                            current.getY() - 2,
                            current.getZ() - 2,
                            current.getX() + 2,
                            current.getY() + 4,
                            current.getZ() + 2
                    );

                    for (Entity entity : caster.getWorld().getNearbyEntities(hitBox)) {

                        if(entity == caster){
                            continue;
                        }

                        if(!(entity instanceof LivingEntity)){
                            continue;
                        }

                        if(entity instanceof ArmorStand){
                            continue;
                        }

                        LivingEntity livingEntity = (LivingEntity) entity;

                        if(hitBySkill.contains(livingEntity)){
                            continue;
                        }

                        hitBySkill.add(livingEntity);

                        boolean crit = damageCalculator.checkIfCrit(caster, 0);

                        double damage = (damageCalculator.calculateDamage(caster, livingEntity, "Physical", finalSkillDamage, crit));

                        //pvp logic
                        if(entity instanceof Player){
                            if(pvpManager.pvpLogic(caster, (Player) entity)){
                                changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, caster, crit);
                                stealth.stealthBonusCheck(caster, livingEntity);
                                hit = true;
                                if(!trigger[0]){
                                    trigger[0] = true;
                                    combo.addComboPoint(caster);
                                }

                                break;
                            }
                            continue;
                        }

                        if(pveChecker.pveLogic(livingEntity)){
                            Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(livingEntity, caster));
                            changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, caster, crit);
                            stealth.stealthBonusCheck(caster, livingEntity);
                            hit = true;

                            if(!trigger[0]){
                                trigger[0] = true;
                                combo.addComboPoint(caster);
                            }

                            break;
                        }

                    }

                    if(traveled>=8 || hit){
                        this.cancel();
                        stand.remove();
                    }
                }
            }.runTaskTimer(main, 0, 1);

        }

    }

    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_6_Level_Bonus();
        return 30 + ((int)(skillLevel/3));
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

    public boolean usable(LivingEntity caster){
        return getCooldown(caster) <= 0;
    }


}
