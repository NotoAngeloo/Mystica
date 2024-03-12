package me.angeloo.mystica.Components.Abilities.ShadowKnight;

import me.angeloo.mystica.Managers.AbilityManager;
import me.angeloo.mystica.Managers.BuffAndDebuffManager;
import me.angeloo.mystica.Managers.CombatManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.CooldownDisplayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BurialGround {

    private final Mystica main;
    private final ProfileManager profileManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final CombatManager combatManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final CooldownDisplayer cooldownDisplayer;

    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public BurialGround(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        combatManager = manager.getCombatManager();
        changeResourceHandler = main.getChangeResourceHandler();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
    }

    public void use(Player player){

        if(!abilityReadyInMap.containsKey(player.getUniqueId())){
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }

        if(abilityReadyInMap.get(player.getUniqueId()) > 0){
            return;
        }

        Block block = player.getLocation().subtract(0,1,0).getBlock();

        if(block.getType() == Material.AIR){
            return;
        }

        combatManager.startCombatTimer(player);

        execute(player);

        abilityReadyInMap.put(player.getUniqueId(), 12);
        new BukkitRunnable(){
            @Override
            public void run(){

                if(abilityReadyInMap.get(player.getUniqueId()) <= 0){
                    cooldownDisplayer.displayCooldown(player, 3);
                    this.cancel();
                    return;
                }

                int cooldown = abilityReadyInMap.get(player.getUniqueId()) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(player, 3);

            }
        }.runTaskTimer(main, 0,20);

    }

    private void execute(Player player){

        boolean blood = profileManager.getAnyProfile(player).getPlayerSubclass().equalsIgnoreCase("blood");

        Location start = player.getLocation();
        ArmorStand armorStand = player.getWorld().spawn(start.clone().subtract(0,5,0), ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);

        EntityEquipment entityEquipment = armorStand.getEquipment();

        ItemStack ground = new ItemStack(Material.REDSTONE);
        ItemMeta meta = ground.getItemMeta();
        assert meta != null;

        meta.setCustomModelData(10);

        ground.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setHelmet(ground);

        armorStand.teleport(start);

        double healAmount = (profileManager.getAnyProfile(player).getTotalHealth()+ buffAndDebuffManager.getHealthBuffAmount(player)) * .03;
        double skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkill_3_Level() +
                profileManager.getAnyProfile(player).getSkillLevels().getSkill_3_Level_Bonus();
        healAmount = healAmount + ((int)(skillLevel/10));

        double finalHealAmount = healAmount;
        new BukkitRunnable(){
            int ran = 0;
            @Override
            public void run(){

                if(!player.isOnline()){
                    this.cancel();
                    armorStand.remove();

                    if(blood){
                        buffAndDebuffManager.getDamageReduction().removeReduction(player);
                    }

                    return;
                }

                if(playerValid()){

                    changeResourceHandler.addHealthToEntity(player, finalHealAmount, player);
                    changeResourceHandler.addManaToPlayer(player, 10.0);

                    if(blood){
                        buffAndDebuffManager.getDamageReduction().applyDamageReduction(player, .8, 0);
                    }
                }

                double increment = (2 * Math.PI) / 16; // angle between particles

                for (int i = 0; i < 16; i++) {
                    double angle = i * increment;
                    double x = armorStand.getLocation().getX() + (3 * Math.cos(angle));
                    double z = armorStand.getLocation().getZ() + (3 * Math.sin(angle));
                    double y = armorStand.getLocation().getY();
                    Location loc = new Location(player.getWorld(), x, y, z);

                    player.getWorld().spawnParticle(Particle.GLOW_SQUID_INK, loc, 1,0, 0, 0, 0);
                }

                if(ran>=7){
                    this.cancel();
                    armorStand.remove();

                    if(blood){
                        buffAndDebuffManager.getDamageReduction().removeReduction(player);
                    }

                }

                ran++;
            }

            private boolean playerValid(){

                double distance = player.getLocation().distance(armorStand.getLocation());

                if(distance>5){
                    return false;
                }

                return !profileManager.getAnyProfile(player).getIfDead();
            }

        }.runTaskTimer(main, 0, 20);

    }


    public int getCooldown(Player player){
        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

}
