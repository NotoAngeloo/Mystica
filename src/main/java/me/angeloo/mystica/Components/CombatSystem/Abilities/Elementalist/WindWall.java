package me.angeloo.mystica.Components.CombatSystem.Abilities.Elementalist;

import me.angeloo.mystica.Components.CombatSystem.Abilities.ElementalistAbilities;
import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.Shields.WindWallShield;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.CombatManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Components.Hud.CooldownDisplayer;
import org.bukkit.Location;
import org.bukkit.Material;
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

public class WindWall {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final StatusEffectManager statusEffectManager;
    private final CooldownDisplayer cooldownDisplayer;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    private final Heat heat;

    public WindWall(Mystica main, AbilityManager manager, ElementalistAbilities elementalistAbilities){
        this.main = main;
        profileManager = main.getProfileManager();
        statusEffectManager = main.getStatusEffectManager();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
        heat = elementalistAbilities.getHeat();
    }

    public void use(LivingEntity caster) {

        if (!abilityReadyInMap.containsKey(caster.getUniqueId())) {
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }

        if(!usable(caster)) {
            return;
        }

        execute(caster);

        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_5_Level_Bonus();

        int cooldown = 21;

        cooldown = cooldown - ((int)(skillLevel/3));

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), cooldown);
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {

                if (getCooldown(caster) <= 0) {
                    cooldownDisplayer.displayCooldown(caster, 5);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(caster) - 1;
                cooldown = cooldown - statusEffectManager.getHasteLevel(caster);

                abilityReadyInMap.put(caster.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(caster, 5);
            }
        }.runTaskTimerAsynchronously(main, 0, 20);
        cooldownTask.put(caster.getUniqueId(), task);
    }

    private void execute(LivingEntity caster){

        heat.reduceHeat(caster, 5);

        Location start = caster.getLocation();

        ArmorStand armorStand = caster.getWorld().spawn(start, ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);

        EntityEquipment entityEquipment = armorStand.getEquipment();

        ItemStack matrixItem = new ItemStack(Material.DRAGON_BREATH);
        ItemMeta meta = matrixItem.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(12);
        matrixItem.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setHelmet(matrixItem);

        double fourth = profileManager.getAnyProfile(caster).getTotalHealth() + statusEffectManager.getHealthBuffAmount(caster);
        fourth += 0.25;

        statusEffectManager.applyEffect(caster, new WindWallShield(), null, fourth);

        new BukkitRunnable(){
            int timeRan = 0;
            Vector initialDirection;
            double angle = 0;
            @Override
            public void run(){

                if(caster instanceof Player){
                    if(!((Player)caster).isOnline()){
                        cancelTask();
                    }
                }

                if(!statusEffectManager.hasEffect(caster, "wind_wall")){
                    cancelTask();
                }

                if (initialDirection == null) {
                    initialDirection = caster.getLocation().getDirection().setY(0).normalize();
                }

                Vector direction = initialDirection.clone();
                double radians = Math.toRadians(angle);
                direction.rotateAroundY(radians);

                Location playerLoc = caster.getLocation().clone();
                playerLoc.setDirection(direction);

                armorStand.teleport(playerLoc);

                if(timeRan >= 200){
                    cancelTask();
                }

                timeRan++;

                angle += 20;
                if (angle >= 360) {
                    angle = 0;
                }
            }

            private void cancelTask() {
                this.cancel();
                armorStand.remove();
                statusEffectManager.removeEffect(caster, "wind_wall");
            }

        }.runTaskTimer(main, 0, 1);

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
