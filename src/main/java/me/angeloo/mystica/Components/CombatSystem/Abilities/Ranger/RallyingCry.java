package me.angeloo.mystica.Components.CombatSystem.Abilities.Ranger;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.Hud.CooldownDisplayer;
import me.angeloo.mystica.CustomEvents.HudUpdateEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Enums.BarType;
import org.bukkit.Bukkit;
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RallyingCry {

    private final Mystica main;
    private final StatusEffectManager statusEffectManager;
    private final CooldownDisplayer cooldownDisplayer;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> buffActiveMap = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public RallyingCry(Mystica main, AbilityManager manager){
        this.main = main;
        statusEffectManager = main.getStatusEffectManager();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
    }

    public void use(LivingEntity caster){

        if(!abilityReadyInMap.containsKey(caster.getUniqueId())){
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }

        if(!usable(caster)){
            return;
        }

        execute(caster);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), 15);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getCooldown(caster) <= 0){
                    cooldownDisplayer.displayCooldown(caster, 6);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(caster) - 1;
                cooldown = cooldown - statusEffectManager.getHasteLevel(caster);

                abilityReadyInMap.put(caster.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(caster, 6);

            }
        }.runTaskTimerAsynchronously(main, 0,20);
        cooldownTask.put(caster.getUniqueId(), task);

    }

    private void execute(LivingEntity caster){

        buffActiveMap.put(caster.getUniqueId(), getDuration());
        new BukkitRunnable(){
            @Override
            public void run(){

                if(caster instanceof Player player){
                    Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, BarType.Status));
                }


                if(buffActiveMap.get(caster.getUniqueId()) <= 0){
                    this.cancel();
                    return;
                }

                int left = buffActiveMap.get(caster.getUniqueId()) - 1;

                buffActiveMap.put(caster.getUniqueId(), left);

            }
        }.runTaskTimer(main, 0,20);

        Location start = caster.getLocation();
        ArmorStand armorStand = caster.getWorld().spawn(start.clone().subtract(0,5,0), ArmorStand.class);
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

                armorStand.teleport(caster.getLocation());

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

    public int getIfBuffTime(LivingEntity caster){
        return buffActiveMap.getOrDefault(caster.getUniqueId(), 0);
    }

    public int getDuration(){
        return 11;
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
        if(getCooldown(caster) > 0){
            return false;
        }

        return getIfBuffTime(caster) <= 0;
    }

}
