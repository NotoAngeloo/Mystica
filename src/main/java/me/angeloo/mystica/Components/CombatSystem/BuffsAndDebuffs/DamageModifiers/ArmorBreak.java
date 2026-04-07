package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.DamageModifiers;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusInstance;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusStackType;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

public class ArmorBreak implements StatusEffect {

    @Override
    public String getId() {
        return "armor_break";
    }

    @Override
    public StatusStackType stackType(){
        return StatusStackType.ADDITIVE;
    }

    @Override
    public int getDuration() {
        return 10 * 20;
    }

    @Override
    public double getMagnitude(){
        return 1;
    }

    @Override
    public void onApply(LivingEntity entity, StatusInstance instance){

        //check amount stacks

        if(instance.getEffect().getMagnitude() <3){
            return;
        }

        Location center = entity.getLocation().clone();

        BoundingBox hitbox = new BoundingBox(
                center.getX() - 20,
                center.getY() - 20,
                center.getZ() - 20,
                center.getX() + 20,
                center.getY() + 20,
                center.getZ() + 20);

        for(Entity e : entity.getWorld().getNearbyEntities(hitbox)){
            if(e instanceof Player player){
                player.sendMessage(e.getName() + " armor has broken! Off tank take aggro!");
            }
        }

    }
}
