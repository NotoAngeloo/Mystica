package me.angeloo.mystica.Components.Commands;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Managers.AggroManager;
import me.angeloo.mystica.Managers.BuffAndDebuffManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SkillTargetValidCheck implements CommandExecutor {

    private final ProfileManager profileManager;
    private final AggroManager aggroManager;

    public SkillTargetValidCheck(Mystica main){
        profileManager = main.getProfileManager();
        aggroManager = main.getAggroManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (!(sender.isOp())) {
            sender.sendMessage("you do not have permissions");
            return true;
        }

        if(args.length==2){

            LivingEntity caster;
            Player target;

            try{
                caster = (LivingEntity) Bukkit.getEntity(UUID.fromString(args[0]));
            }catch (IllegalArgumentException exception){
                return true;
            }

            assert caster != null;

            BoundingBox hitBox = new BoundingBox(
                    caster.getLocation().getX() - 50,
                    caster.getLocation().getY() - 10,
                    caster.getLocation().getZ() - 50,
                    caster.getLocation().getX() + 50,
                    caster.getLocation().getY() + 10,
                    caster.getLocation().getZ() + 50
            );

            boolean someone = false;

            for (Entity entity : caster.getWorld().getNearbyEntities(hitBox)) {

                if(!(entity instanceof Player)){
                    continue;
                }

                Player player = (Player) entity;
                boolean playerDeathStatus = profileManager.getAnyProfile(player).getIfDead();
                boolean playerAggroValidity = aggroManager.getIfOnBlackList(player);

                if(!playerDeathStatus && !playerAggroValidity){
                    someone = true;
                    break;
                }

            }

            if(!someone){
                Bukkit.getLogger().info("none valid");
                return true;
            }

            try{
                target = (Player) Bukkit.getEntity(UUID.fromString(args[1]));
            }catch (IllegalArgumentException exception){
                return true;
            }

            assert target != null;

            boolean targetDeathStatus = profileManager.getAnyProfile(target).getIfDead();
            boolean targetAggroValidity = aggroManager.getIfOnBlackList(target);

            boolean sameWorld = caster.getWorld()==target.getWorld();

            if(targetDeathStatus
                    || targetAggroValidity
                    || !sameWorld){
                //send signal

                //Bukkit.getLogger().info("target " + target.getName() +  " invalid, trying again");

                if(MythicBukkit.inst().getAPIHelper().isMythicMob(caster.getUniqueId())){
                    AbstractEntity abstractEntity = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(caster).getEntity();
                    MythicBukkit.inst().getAPIHelper().getMythicMobInstance(caster).signalMob(abstractEntity, "retarget");
                }
                return true;
            }

            //Bukkit.getLogger().info("target " + target.getName() +  " still valid");

            //send different signal
            if(MythicBukkit.inst().getAPIHelper().isMythicMob(caster.getUniqueId())){
                AbstractEntity abstractEntity = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(caster).getEntity();
                MythicBukkit.inst().getAPIHelper().getMythicMobInstance(caster).signalMob(abstractEntity, "targetvalid");
            }
            return true;
        }


        return false;
    }

}
