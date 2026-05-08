package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Elementalist;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.Shields.WindWallShield;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class WindWall extends BaseAbility {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CooldownManager cooldownManager;
    private final StatusEffectManager statusEffectManager;

    private final Heat heat;

    private final int baseCooldown = 21;

    public WindWall(Mystica main, AbilityManager manager){
        super("wind_wall");
        this.main = main;
        profileManager = main.getProfileManager();
        statusEffectManager = main.getStatusEffectManager();
        this.heat = manager.getHeat();
        cooldownManager = main.getCooldownManager();
    }

    @Override
    public boolean use(LivingEntity caster) {



        if(!usable(caster)) {
            return false;
        }

        execute(caster);

        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_5_Level_Bonus();

        cooldownManager.start(caster.getUniqueId(), 5, (long) (baseCooldown * 1000));
        return true;
    }

    @Override
    public int cooldown() {
        return baseCooldown;
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
        fourth *= 0.25;

        statusEffectManager.applyEffect(caster, new WindWallShield(), null, fourth, caster);

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

    @Override
    public boolean usable(LivingEntity caster){
        return cooldownManager.isReady(caster.getUniqueId(), 5, statusEffectManager.getHastePercent(caster));
    }

    @Override
    public String skillBarIcon(LivingEntity entity) {
        return "\ue3ce";
    }
}
