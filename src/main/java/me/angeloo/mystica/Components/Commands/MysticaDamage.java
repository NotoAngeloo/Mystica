package me.angeloo.mystica.Components.Commands;

import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageCalculator;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class MysticaDamage implements CommandExecutor {

    private final ProfileManager profileManager;
    private final DamageCalculator damageCalculator;
    private final ChangeResourceHandler changeResourceHandler;

    public MysticaDamage(Mystica main) {
        profileManager = main.getProfileManager();
        damageCalculator = main.getDamageCalculator();
        changeResourceHandler = main.getChangeResourceHandler();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (!(sender.isOp())) {
            sender.sendMessage("you do not have permissions");
            return true;
        }

        if(args.length == 2){

            Player player = Bukkit.getPlayer(args[0]);
            if(player == null){
                sender.sendMessage("player doesn't exist");
                return true;
            }

            double damage = Double.parseDouble(args[1]);

            changeResourceHandler.subtractHealthFromEntity(player, damage, player);

            return true;
        }


        if (args.length == 4) {

            if(args[0].equalsIgnoreCase("<target.uuid>")){
                return true;
            }

            LivingEntity target = (LivingEntity) Bukkit.getEntity(UUID.fromString(args[0]));
            LivingEntity caster = (LivingEntity) Bukkit.getEntity(UUID.fromString(args[1]));
            assert caster != null;

            if (target == null) {
                sender.sendMessage("target null");
                return true;
            }

            if(target instanceof Player){
                if(!((Player)target).isOnline()){
                    return true;
                }
            }

            if(profileManager.getAnyProfile(target).getIfDead()){
                return true;
            }

            String type = args[2];
            double amount = Double.parseDouble(args[3]);

            switch (type.toLowerCase()) {

                case "physical": {

                    int level = profileManager.getAnyProfile(caster).getStats().getLevel();

                    int skillLevel = (int) Math.ceil(((double) level / 5));

                    double damage = damageCalculator.calculateGettingDamaged(target, caster, "physical", amount * skillLevel);

                    changeResourceHandler.subtractHealthFromEntity(target, damage, caster);
                    return true;
                }
                case "magical": {

                    int level = profileManager.getAnyProfile(caster).getStats().getLevel();

                    int skillLevel = (int) Math.ceil(((double) level / 5));

                    double damage = damageCalculator.calculateGettingDamaged(target, caster, "magical", amount * skillLevel);

                    changeResourceHandler.subtractHealthFromEntity(target, damage, caster);
                    return true;
                }
                case "true":{

                    //Bukkit.getLogger().info("test " + target + " " + caster);

                    changeResourceHandler.subtractHealthFromEntity(target, amount, caster);
                    return true;
                }
                case "percent":{

                    //Bukkit.getLogger().info("tets");

                    double totalHealth = profileManager.getAnyProfile(target).getTotalHealth();

                    double damage = totalHealth * (amount * .01);

                    changeResourceHandler.subtractHealthFromEntity(target, damage, caster);
                    return true;
                }
                case "fury":{
                    int level = profileManager.getAnyProfile(caster).getStats().getLevel();
                    changeResourceHandler.subtractHealthFromEntity(target, amount * level, caster);
                    return true;
                }
                case "all":{
                    changeResourceHandler.killEntityNoMatterWhat(target);
                    return true;
                }
            }

        }

        return true;
    }
}
