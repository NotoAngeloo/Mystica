package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.Shields;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusInstance;
import me.angeloo.mystica.CustomEvents.HudUpdateEvent;
import me.angeloo.mystica.Utility.Enums.BarType;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class ShieldInstance extends StatusInstance {

    //private double amount;
    private double excess = 0;

    public ShieldInstance(StatusEffect effect, int duration, double amount, LivingEntity source){
        super(effect, duration, amount, source);
        //this.amount = amount;
    }

    public double getRemaining(){
        return magnitude;
    }

    public double getExcess(){
        return excess;
    }

    @Override
    public void onApply(LivingEntity entity) {
        super.onApply(entity);
        if(entity instanceof Player player){
            Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, BarType.Health));
        }
    }

    @Override
    public void onRemove(LivingEntity entity){
        super.onRemove(entity);
        if(entity instanceof Player player){
            Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, BarType.Health));
        }
    }

    @Override
    public void onDamage(LivingEntity entity, double incomingDamage) {

        double newAmount = magnitude - incomingDamage;

        //Bukkit.getLogger().info("old: " + magnitude + " new: " + newAmount);

        if (newAmount > 0) {
            magnitude = newAmount; // still some shield left

            if(entity instanceof Player player){
                Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, BarType.Health));
            }
            return;
        }

        // Shield fully consumed; pass excess damage through
        excess = -newAmount;

        magnitude = 0;
        //remainingTicks = 0; // mark for removal
        //never *really* removed

        if(entity instanceof Player player){
            Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, BarType.Health));
        }

        return;
    }



}
