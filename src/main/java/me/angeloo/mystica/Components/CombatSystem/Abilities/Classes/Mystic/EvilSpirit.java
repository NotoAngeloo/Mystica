package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Mystic;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.PlayerStateManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.CrowdControl.Root;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.CustomEvents.HudUpdateEvent;
import me.angeloo.mystica.CustomEvents.RemoveStealthEffectEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Enums.BarType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class EvilSpirit extends BaseAbility {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final StatusEffectManager statusEffectManager;
    private final AbilityManager abilityManager;
    private final PlayerStateManager playerStateManager;


    //private final Map<UUID, Boolean> isEvilSpirit = new HashMap<>();

    public EvilSpirit(Mystica main, AbilityManager manager){
        super("evil_spirit");
        this.main = main;
        profileManager = main.getProfileManager();
        statusEffectManager = main.getStatusEffectManager();
        abilityManager = manager;
        playerStateManager = manager.getPlayerStateManager();
    }

    @Override
    public boolean use(LivingEntity caster){

        if(!usable(caster)){
            return false;
        }

        execute(caster);
        return true;
    }

    //im gonna make this look nicer eventually

    @Override
    public int cooldown() {
        return 0;
    }

    private void execute(LivingEntity caster){

        //hide player and display animation. when over, put hat on head
        int castTime = 25;

        statusEffectManager.applyEffect(caster, new Root(), castTime, null);


        Location spawnStart = caster.getLocation().clone();
        spawnStart.subtract(0, 1, 0);

        Location current = caster.getLocation().clone();

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
            final Location loc = current.clone();
            Vector initialDirection;
            int ran = 0;
            int angle = 0;
            double height = 0;
            final double radius = 4;
            @Override
            public void run(){

                if (initialDirection == null) {
                    initialDirection = current.getDirection().setY(0).normalize();
                }

                Vector rotation = initialDirection.clone();
                double radians = Math.toRadians(angle);
                rotation.rotateAroundY(radians);
                current.setDirection(rotation);



                double x = loc.getX() + rotation.getX() * radius;
                double z = loc.getZ() + rotation.getZ() * radius;

                double x2 = loc.getX() - rotation.getX() * radius;
                double z2 = loc.getZ() - rotation.getZ() * radius;

                Location particleLoc = new Location(loc.getWorld(), x, loc.getY() + height, z);
                Location particleLoc2 = new Location(loc.getWorld(), x2, loc.getY() + height, z2);

                caster.getWorld().spawnParticle(Particle.GLOW_SQUID_INK, particleLoc, 1, 0, 0, 0, 0);
                caster.getWorld().spawnParticle(Particle.GLOW_SQUID_INK, particleLoc2, 1, 0, 0, 0, 0);


                height += .1;



                double percent = ((double) ran / castTime) * 100;

                abilityManager.setCastBar(caster, percent);

                if(ran >= castTime){
                    this.cancel();
                    abilityManager.stopCasting(caster);

                    evilSpiritTime(caster);
                }

                angle += 5;
                if (angle >=360) {
                    angle = 0;
                }

                ran++;
            }
        }.runTaskTimer(main, 0, 1);

    }

    private void evilSpiritTime(LivingEntity caster){

        if(caster instanceof Player){
            if(!((Player)caster).isOnline()){
                return;
            }
        }



        if(profileManager.getAnyProfile(caster).getIfDead()){
            return;
        }


        playerStateManager.get(caster.getUniqueId()).set("evil_spirit", true);

        playerStateManager.get(caster.getUniqueId()).remove("chaos_shard");


        ItemStack spirit = new ItemStack(Material.SPECTRAL_ARROW);
        ItemMeta meta2 = spirit.getItemMeta();
        assert meta2 != null;
        meta2.setCustomModelData(5);
        spirit.setItemMeta(meta2);

        if(caster instanceof Player){
            ((Player)caster).getInventory().setItemInOffHand(spirit);
        }


        new BukkitRunnable(){
            int count = 0;

            @Override
            public void run(){

                if(count >= 30){
                    this.cancel();
                    playerStateManager.get(caster.getUniqueId()).remove("evil_spirit");
                    Bukkit.getServer().getPluginManager().callEvent(new RemoveStealthEffectEvent(caster));
                }

                count++;
            }
        }.runTaskTimer(main, 0, 10);

    }


    @Override
    public void onExternalTrigger(LivingEntity caster, int amount){
        addChaosShard(caster, amount);
    }

    private void addChaosShard(LivingEntity caster, int added){

        int current = 0;

        if(playerStateManager.get(caster.getUniqueId()).has("chaos_shard")){
            current = playerStateManager.get(caster.getUniqueId()).getInt("chaos_shard", 0);
        }

        current = current + added;

        if(current > 6){
            current = 6;
        }


        playerStateManager.get(caster.getUniqueId()).set("chaos_shard", current);

    }

    /*public void removeShards(LivingEntity caster){
        chaosShards.put(caster.getUniqueId(), 0);
        if(caster instanceof Player player){
            Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, BarType.Status));
        }

    }*/

    /*public int returnWhichItem(Player player){

        if(getChaosShards(player) >= 6){
            return 1;
        }

        return 0;
    }*/

    @Override
    public boolean usable(LivingEntity caster){

        if(!playerStateManager.get(caster.getUniqueId()).has("chaos_shard")){
            return false;
        }

        if(playerStateManager.get(caster.getUniqueId()).getInt("chaos_shard", 0) < 6){
            return false;
        }


        Block block = caster.getLocation().subtract(0,1,0).getBlock();

        return block.getType() != Material.AIR;
    }

}
