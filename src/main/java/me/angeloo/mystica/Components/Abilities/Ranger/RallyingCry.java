package me.angeloo.mystica.Components.Abilities.Ranger;

import me.angeloo.mystica.Managers.AbilityManager;
import me.angeloo.mystica.Managers.CombatManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RallyingCry {

    private final Mystica main;
    private final CombatManager combatManager;
    private final ChangeResourceHandler changeResourceHandler;

    private final Map<UUID, Integer> buffActiveMap = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public RallyingCry(Mystica main, AbilityManager manager){
        this.main = main;
        combatManager = manager.getCombatManager();
        changeResourceHandler = main.getChangeResourceHandler();
    }

    public void use(Player player){

        if(!abilityReadyInMap.containsKey(player.getUniqueId())){
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }

        if(abilityReadyInMap.get(player.getUniqueId()) > 0){
            return;
        }

        if(getIfBuffTime(player) > 0){
            return;
        }

        combatManager.startCombatTimer(player);

        execute(player);

        abilityReadyInMap.put(player.getUniqueId(), 15);
        new BukkitRunnable(){
            @Override
            public void run(){

                if(abilityReadyInMap.get(player.getUniqueId()) <= 0){
                    this.cancel();
                    return;
                }

                int cooldown = abilityReadyInMap.get(player.getUniqueId()) - 1;

                abilityReadyInMap.put(player.getUniqueId(), cooldown);

            }
        }.runTaskTimer(main, 0,20);

    }

    private void execute(Player player){

        buffActiveMap.put(player.getUniqueId(), 11);
        new BukkitRunnable(){
            @Override
            public void run(){

                if(buffActiveMap.get(player.getUniqueId()) <= 0){
                    this.cancel();
                    return;
                }

                int cooldown = buffActiveMap.get(player.getUniqueId()) - 1;

                buffActiveMap.put(player.getUniqueId(), cooldown);

            }
        }.runTaskTimer(main, 0,20);

        Location start = player.getLocation();
        ArmorStand armorStand = start.getWorld().spawn(start, ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);

        EntityEquipment entityEquipment = armorStand.getEquipment();

        ItemStack buffIcon = new ItemStack(Material.ARROW);
        ItemMeta meta = buffIcon.getItemMeta();
        assert meta != null;

        meta.setCustomModelData(4);

        buffIcon.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setHelmet(buffIcon);

        new BukkitRunnable(){
            int count = 0;
            @Override
            public void run(){

                armorStand.teleport(player.getLocation());

                if(count >= 25){
                    cancelTask();
                }

                count ++;
            }

            private void cancelTask() {
                this.cancel();
                armorStand.remove();
            }

        }.runTaskTimer(main, 0, 1);

    }

    public int getIfBuffTime(Player player){
        return buffActiveMap.getOrDefault(player.getUniqueId(), 0);
    }

    public int getCooldown(Player player){

        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;

    }

}
