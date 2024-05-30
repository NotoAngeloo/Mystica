package me.angeloo.mystica.Components.Abilities.Mystic;

import me.angeloo.mystica.Components.Abilities.MysticAbilities;
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
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpiritualDescent {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final CooldownDisplayer cooldownDisplayer;

    private final EvilSpirit evilSpirit;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public SpiritualDescent(Mystica main, AbilityManager manager, MysticAbilities mysticAbilities){
        this.main = main;
        profileManager = main.getProfileManager();
        combatManager = manager.getCombatManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
        cooldownDisplayer = new CooldownDisplayer(main, manager);

        evilSpirit = mysticAbilities.getEvilSpirit();
    }

    private final double range = 20;

    public void use(LivingEntity caster){

        if(!abilityReadyInMap.containsKey(caster.getUniqueId())){
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }

        targetManager.setTargetToNearestValid(caster, range + buffAndDebuffManager.getTotalRangeModifier(caster));

        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(!usable(caster, target)){
            return;
        }

        changeResourceHandler.subTractManaFromEntity(caster, getCost());

        combatManager.startCombatTimer(caster);

        execute(caster);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), 16);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getCooldown(caster) <= 0){
                    cooldownDisplayer.displayCooldown(caster, 5);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(caster) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(caster);


                abilityReadyInMap.put(caster.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(caster, 5);

            }
        }.runTaskTimer(main, 0,20);
        cooldownTask.put(caster.getUniqueId(), task);

    }

    private void execute(LivingEntity caster){

        evilSpirit.addChaosShard(caster, 1);

        LivingEntity target = targetManager.getPlayerTarget(caster);

        Location origin = target.getLocation().clone().subtract(0,1.3,0);


        double finalSkillDamage = getSkillDamage(caster);
        new BukkitRunnable(){
            int count = 0;
            @Override
            public void run(){

                double randomValueX = Math.random() * 4 - 2;
                double randomValueZ = Math.random() * 4 - 2;

                Location spawnLoc = origin.clone().add(randomValueX, 0, randomValueZ);

                new BukkitRunnable(){
                    Location current = spawnLoc.clone().add(0,10,0);
                    int count = 0;
                    @Override
                    public void run(){

                        caster.getWorld().spawnParticle(Particle.GLOW_SQUID_INK, current, 1, 0, 0, 0, 0);

                        current = current.subtract(0,1,0);

                        count++;

                        if(count >= 10){
                            this.cancel();
                            ArmorStand armorStand = caster.getWorld().spawn(spawnLoc, ArmorStand.class);
                            armorStand.setInvisible(true);
                            armorStand.setGravity(false);
                            armorStand.setCollidable(false);
                            armorStand.setInvulnerable(true);
                            armorStand.setMarker(true);

                            EntityEquipment entityEquipment = armorStand.getEquipment();

                            ItemStack descentItem = new ItemStack(Material.SPECTRAL_ARROW);
                            ItemMeta meta = descentItem.getItemMeta();
                            assert meta != null;
                            meta.setCustomModelData(8);
                            descentItem.setItemMeta(meta);
                            assert entityEquipment != null;
                            entityEquipment.setHelmet(descentItem);

                            BoundingBox hitBox = new BoundingBox(
                                    current.getX() - 4,
                                    current.getY() - 2,
                                    current.getZ() - 4,
                                    current.getX() + 4,
                                    current.getY() + 4,
                                    current.getZ() + 4
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

                                boolean crit = damageCalculator.checkIfCrit(caster, 0);
                                double damage = (damageCalculator.calculateDamage(caster, livingEntity, "Magical", finalSkillDamage, crit));

                                //pvp logic
                                if(entity instanceof Player){
                                    if(pvpManager.pvpLogic(caster, (Player) entity)){
                                        changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, caster);
                                    }
                                    continue;
                                }

                                if(pveChecker.pveLogic(livingEntity)){
                                    Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(livingEntity, caster));
                                    changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, caster);
                                }

                            }

                            new BukkitRunnable(){
                                @Override
                                public void run(){
                                    armorStand.remove();
                                }
                            }.runTaskLater(main, 10);

                        }

                    }
                }.runTaskTimer(main, 0, 1);

                if(count >=8){
                    this.cancel();
                }

                count++;
            }
        }.runTaskTimer(main, 0, 6);

    }

    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_5_Level_Bonus();
        return 5 + ((int)(skillLevel/3));
    }

    public double getCost(){return 10;}


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

            if(distance > range + buffAndDebuffManager.getTotalRangeModifier(caster)){
                return false;
            }
        }

        if(target == null){
            return false;
        }

        if(getCooldown(caster) > 0){
            return false;
        }


        if(profileManager.getAnyProfile(caster).getCurrentMana()<getCost()){
            return false;
        }

        return true;
    }

}
