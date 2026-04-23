package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Ranger;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.PlayerStateManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class RallyingCry extends BaseAbility {

    private final Mystica main;
    private final StatusEffectManager statusEffectManager;
    private final CooldownManager cooldownManager;
    private final PlayerStateManager playerStateManager;

    public RallyingCry(Mystica main, AbilityManager manager){
        super("rallying_cry");
        this.main = main;
        statusEffectManager = main.getStatusEffectManager();
        cooldownManager = manager.getCooldownManager();
        playerStateManager = manager.getPlayerStateManager();
    }

    private final int baseCooldown = 15;
    public int duration = 11;

    @Override
    public boolean use(LivingEntity caster){

        if(!usable(caster)){
            return false;
        }

        execute(caster);

        cooldownManager.start(caster.getUniqueId(), 6, (long) (baseCooldown * 1000));

        return true;
    }

    @Override
    public int cooldown() {
        return baseCooldown;
    }

    private void execute(LivingEntity caster){

        playerStateManager.get(caster.getUniqueId()).set("rallying_cry", true);

        new BukkitRunnable(){
            @Override
            public void run(){
                playerStateManager.get(caster.getUniqueId()).remove("rallying_cry");
            }
        }.runTaskLaterAsynchronously(main, duration * 20L);


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



    @Override
    public boolean usable(LivingEntity caster){
        if(playerStateManager.get(caster.getUniqueId()).has("rallying_cry")){
            return false;
        }

        return cooldownManager.isReady(caster.getUniqueId(), 6, statusEffectManager.getHastePercent(caster));
    }

}
