package me.angeloo.mystica.Components.BuffsAndDebuffs;

import me.angeloo.mystica.Managers.BossCastingManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

public class BossInterrupt {

    private final BossCastingManager bossCastingManager;

    public BossInterrupt(Mystica main){
        bossCastingManager = main.getBossCastingManager();
    }

    public void interrupt(LivingEntity caster, LivingEntity target){

        if(!bossCastingManager.bossIsCasting(target)){
            return;
        }

        BoundingBox hitBox = new BoundingBox(
                caster.getLocation().getX() - 20,
                caster.getLocation().getY() - 20,
                caster.getLocation().getZ() - 20,
                caster.getLocation().getX() + 20,
                caster.getLocation().getY() + 20,
                caster.getLocation().getZ() + 20
        );

        for (Entity thisEntity : caster.getWorld().getNearbyEntities(hitBox)) {

            if(!(thisEntity instanceof Player)){
                continue;
            }

            Player player = (Player) thisEntity;

            player.sendMessage(caster.getName() + " has successfully interrupted " + target.getName() +"'s ability!");
        }

        bossCastingManager.setShouldInterrupt(target);

    }

}
