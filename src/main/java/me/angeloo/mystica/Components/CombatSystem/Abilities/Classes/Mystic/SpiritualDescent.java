package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Mystic;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.Classes.PlayerClass;
import me.angeloo.mystica.Components.CombatSystem.PvpManager;
import me.angeloo.mystica.Components.CombatSystem.TargetManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;
import me.angeloo.mystica.Utility.Enums.DamageType;
import me.angeloo.mystica.Components.CombatSystem.Classes.SubClass;
import me.angeloo.mystica.Utility.Logic.PveChecker;
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
import org.bukkit.util.BoundingBox;

public class SpiritualDescent extends BaseAbility {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final StatusEffectManager statusEffectManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final CooldownManager cooldownManager;

    public SpiritualDescent(Mystica main, AbilityManager manager){
        super("spiritual_descent");
        this.main = main;
        profileManager = main.getProfileManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        statusEffectManager = main.getStatusEffectManager();
        changeResourceHandler = main.getChangeResourceHandler();
        cooldownManager = main.getCooldownManager();
    }

    private final int baseCooldown = 16;
    private final double range = 20;
    private final double baseDamage = 5;

    @Override
    public boolean use(LivingEntity caster){


        targetManager.setTargetToNearestValid(caster, range + statusEffectManager.getAdditionalRange(caster));

        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(!usable(caster, target)){
            return false;
        }


        execute(caster);

        cooldownManager.start(caster.getUniqueId(), 4, (long) (baseCooldown * 1000));

        return true;
    }

    @Override
    public int cooldown() {
        return baseCooldown;
    }

    private void execute(LivingEntity caster){

        lookup.get(PlayerClass.MYSTIC, SubClass.CHAOS, -1).onExternalTrigger(caster, 1);

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

                                if(!(entity instanceof LivingEntity livingEntity)){
                                    continue;
                                }

                                if(entity instanceof ArmorStand){
                                    continue;
                                }

                                boolean crit = damageCalculator.checkIfCrit(caster, 0);
                                double damage = (damageCalculator.calculateDamage(caster, livingEntity, DamageType.Magical, finalSkillDamage, crit, 0));

                                //pvp logic
                                if(entity instanceof Player){
                                    if(pvpManager.pvpLogic(caster, (Player) entity)){
                                        changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, caster, crit);
                                    }
                                    continue;
                                }

                                if(pveChecker.pveLogic(livingEntity)){
                                    Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(livingEntity, caster));
                                    changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, caster, crit);
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
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_4_Level_Bonus();
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

            if(distance > range + statusEffectManager.getAdditionalRange(caster)){
                return false;
            }
        }

        if(target == null){
            return false;
        }

        return cooldownManager.isReady(caster.getUniqueId(), 4, statusEffectManager.getHastePercent(caster));
    }

}
