package me.angeloo.mystica.Components.Commands;

import me.angeloo.mystica.Managers.BuffAndDebuffManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Managers.TargetManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.SubClass;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class MysticaEffect implements CommandExecutor {

    private final ProfileManager profileManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final TargetManager targetManager;

    public MysticaEffect(Mystica main) {
        profileManager = main.getProfileManager();
        changeResourceHandler = main.getChangeResourceHandler();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        targetManager = main.getTargetManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (!(sender.isOp())) {
            sender.sendMessage("you do not have permissions");
            return true;
        }

        if (args.length == 4) {

            if(args[0].equalsIgnoreCase("<target.uuid>")){
                return true;
            }

            LivingEntity target = (LivingEntity) Bukkit.getEntity(UUID.fromString(args[0]));
            LivingEntity caster = (LivingEntity) Bukkit.getEntity(UUID.fromString(args[1]));


            if (target == null) {
                sender.sendMessage("target null");
                return true;
            }

            if (target instanceof Player) {
                if (!((Player) target).isOnline()) {
                    sender.sendMessage("player not online");
                    return true;
                }

            }

            boolean deathStatus = profileManager.getAnyProfile(target).getIfDead();

            if(deathStatus){
                return true;
            }

            String type = args[2];
            int amount = Integer.parseInt(args[3]);

            switch (type.toLowerCase()) {
                case "crush":{

                    if(!(target instanceof Player)){
                        return true;
                    }

                    int bossLevel = profileManager.getAnyProfile(caster).getStats().getLevel();

                    int crushThreshold = 100 + (10 * bossLevel-1);

                    SubClass subclass = profileManager.getAnyProfile(target).getPlayerSubclass();

                    if(subclass.equals(SubClass.Gladiator)
                            || subclass.equals(SubClass.Templar)
                            || subclass.equals(SubClass.Blood)){
                        crushThreshold -= 100;
                    }

                    if(subclass.equals(SubClass.Executioner)){
                        crushThreshold -= 50;
                    }

                    crushThreshold -= profileManager.getAnyProfile(target).getTotalDefense();

                    if(crushThreshold<=0){
                        return true;
                    }

                    double percent = profileManager.getAnyProfile(target).getTotalHealth() * ((double)amount/100);

                    changeResourceHandler.subtractHealthFromEntity(target, percent, caster, true);
                    //Bukkit.getLogger().info("crush");

                    return true;
                }
                case "stun":{
                    buffAndDebuffManager.getStun().applyStun(target, amount);
                    return true;
                }
                case "unstun":{
                    buffAndDebuffManager.getStun().removeStun(target);
                    return true;
                }
                case "immune":{

                    if(buffAndDebuffManager.getImmune().getImmune(target)){
                        buffAndDebuffManager.getImmune().removeImmune(target);
                        return true;
                    }

                    buffAndDebuffManager.getImmune().applyImmune(target, amount);
                    return true;
                }
                case "melt":{
                    buffAndDebuffManager.getArmorBreak().applyArmorBreak(target);
                    return true;
                }
                case "heal_percent":{
                    double totalHealth = profileManager.getAnyProfile(target).getTotalHealth();
                    double healed = totalHealth * (amount * .01);
                    changeResourceHandler.addHealthToEntity(target, healed, caster);
                    return true;
                }
                case "fear":{
                    buffAndDebuffManager.getFear().applyFear(target, amount);
                    return true;
                }
                case "fear_if_targeting":{

                    if(targetManager.isTargeting(target, caster)){
                        buffAndDebuffManager.getFear().applyFear(target, amount);

                        if(target instanceof Player){
                            if(!profileManager.getCompanions((Player)target).isEmpty())  {
                                for(UUID companion : profileManager.getCompanions((Player) target)){
                                    LivingEntity entity = (LivingEntity) Bukkit.getEntity(companion);

                                    if(entity == null){
                                        continue;
                                    }

                                    buffAndDebuffManager.getFear().applyFear(entity, amount);
                                }
                            }
                        }


                    }

                    return true;
                }
            }


        }

        return false;
    }

}
