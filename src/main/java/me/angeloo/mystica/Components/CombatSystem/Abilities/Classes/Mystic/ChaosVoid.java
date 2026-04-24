package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Mystic;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.CrowdControl.Root;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.DamageModifiers.Immune;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.CustomEvents.HudUpdateEvent;
import me.angeloo.mystica.CustomEvents.RemoveStealthEffectEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.Enums.BarType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class ChaosVoid extends BaseAbility {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final AbilityManager abilityManager;
    private final StatusEffectManager statusEffectManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final CooldownManager cooldownManager;

    public ChaosVoid(Mystica main, AbilityManager manager){
        super("chaos_void");
        this.main = main;
        profileManager = main.getProfileManager();
        abilityManager = manager;
        statusEffectManager = main.getStatusEffectManager();
        changeResourceHandler = main.getChangeResourceHandler();
        cooldownManager = manager.getCooldownManager();;
    }

    private final int baseCooldown = 120;

    @Override
    public boolean use(LivingEntity caster){

        if(!usable(caster)){
            return false;
        }

        execute(caster);

        cooldownManager.start(caster.getUniqueId(), 8, (long) (baseCooldown * 1000));
        return true;
    }

    @Override
    public int cooldown() {
        return baseCooldown;
    }

    private void execute(LivingEntity caster){

        int castTime = 7 * 20;

        //if stun doesnt work, maybe ill make a clone of stun
        statusEffectManager.applyEffect(caster, new Root(), castTime, null, caster);

        Location start = caster.getLocation().clone();
        statusEffectManager.applyEffect(caster, new Immune(), castTime, null, caster);

        double healAmount = (profileManager.getAnyProfile(caster).getTotalHealth() + statusEffectManager.getHealthBuffAmount(caster)) / 10;

        //instead of using stealth effect, manually do it here
        caster.setInvisible(true);

        if(caster instanceof Player player){
            player.getInventory().setItemInMainHand(null);
            player.getInventory().setItemInOffHand(null);
            player.getInventory().setHelmet(null);
            player.getInventory().setChestplate(null);
            player.getInventory().setLeggings(null);
            player.getInventory().setBoots(null);
        }

        abilityManager.setSkillCurrentlyCasting(caster, statusBarIcon());

        new BukkitRunnable(){
            final Location loc = start.clone();
            Vector initialDirection;
            boolean up = true;
            int ran = 0;
            int angle = 0;
            double height = 0;
            final double radius = 4;
            @Override
            public void run(){

                if (initialDirection == null) {
                    initialDirection = start.getDirection().setY(0).normalize();
                }

                Vector rotation = initialDirection.clone();
                double radians = Math.toRadians(angle);
                rotation.rotateAroundY(radians);


                double x = loc.getX() + rotation.getX() * radius;
                double z = loc.getZ() + rotation.getZ() * radius;

                double x2 = loc.getX() - rotation.getX() * radius;
                double z2 = loc.getZ() - rotation.getZ() * radius;

                Location particleLoc = new Location(loc.getWorld(), x, loc.getY() + height, z);
                Location particleLoc2 = new Location(loc.getWorld(), x2, loc.getY() + height, z2);

                caster.getWorld().spawnParticle(Particle.GLOW_SQUID_INK, particleLoc, 1, 0, 0, 0, 0);
                caster.getWorld().spawnParticle(Particle.GLOW_SQUID_INK, particleLoc2, 1, 0, 0, 0, 0);


                if(up){
                    height += .1;
                }
                else{
                    height -= .1;
                }

                angle += 5;

                if(height >= 4){
                    up = false;
                }

                if(height < 0){
                    up = true;
                }

                if(ran%20 == 0){
                    changeResourceHandler.addHealthToEntity(caster, healAmount, caster);
                    Location center = caster.getLocation().clone().add(0,1,0);

                    double increment = (2 * Math.PI) / 16; // angle between particles

                    for (int i = 0; i < 16; i++) {
                        double angle = i * increment;
                        double j = center.getX() + (1 * Math.cos(angle));
                        double k = center.getZ() + (1 * Math.sin(angle));
                        Location loc = new Location(center.getWorld(), j, (center.getY()), k);

                        caster.getWorld().spawnParticle(Particle.GLOW_SQUID_INK, loc, 1, 0, 0, 0, 0);
                    }
                }

                double percent = ((double) ran / castTime) * 100;

                abilityManager.setCastBar(caster, percent);


                if(ran >= castTime){
                    this.cancel();
                    abilityManager.stopCasting(caster);

                    //doesn't actually need to have the effect to call the event and remove stealth
                    Bukkit.getServer().getPluginManager().callEvent(new RemoveStealthEffectEvent(caster));
                }

                angle += 5;
                if (angle >=360) {
                    angle = 0;
                }

                ran++;
            }
        }.runTaskTimer(main, 0, 1);

    }


    @Override
    public boolean usable(LivingEntity caster){
        if (!cooldownManager.isReady(caster.getUniqueId(), 8, statusEffectManager.getHastePercent(caster))){
            return false;
        }


        Block block = caster.getLocation().subtract(0,1,0).getBlock();

        return block.getType() != Material.AIR;
    }

}
