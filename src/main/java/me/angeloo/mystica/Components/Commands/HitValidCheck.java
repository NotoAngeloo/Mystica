package me.angeloo.mystica.Components.Commands;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class HitValidCheck implements CommandExecutor {

    private final ProfileManager profileManager;

    public HitValidCheck(Mystica main){
        profileManager = main.getProfileManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (!(sender.isOp())) {
            sender.sendMessage("you do not have permissions");
            return true;
        }

        if(args.length==2){

            LivingEntity caster;
            LivingEntity target;

            try{
                caster = (LivingEntity) Bukkit.getEntity(UUID.fromString(args[0]));
            }catch (IllegalArgumentException exception){
                return true;
            }

            assert caster != null;

            try{
                target = (LivingEntity) Bukkit.getEntity(UUID.fromString(args[1]));
            }catch (IllegalArgumentException exception){
                return true;
            }

            assert target != null;

            boolean targetDeathStatus = profileManager.getAnyProfile(target).getIfDead();

            if(targetDeathStatus){
                return true;
            }


            //send different signal
            if(MythicBukkit.inst().getAPIHelper().isMythicMob(caster.getUniqueId())){
                AbstractEntity abstractEntity = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(caster).getEntity();
                MythicBukkit.inst().getAPIHelper().getMythicMobInstance(caster).signalMob(abstractEntity, "hitValid");
            }
            return true;
        }

        return true;
    }




}
