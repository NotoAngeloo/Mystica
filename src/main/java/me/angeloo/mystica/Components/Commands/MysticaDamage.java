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

    private final DamageCalculator damageCalculator;
    private final ChangeResourceHandler changeResourceHandler;
    private final ProfileManager profileManager;

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

                Player player = (Player) target;

                boolean deathStatus = profileManager.getAnyProfile(player).getIfDead();

                if(deathStatus){
                    return true;
                }
            }


            String type = args[2];
            double amount = Double.parseDouble(args[3]);

            switch (type.toLowerCase()) {

                case "physical": {

                    Player player = Bukkit.getPlayer(UUID.fromString(args[0]));

                    if (player == null) {
                        sender.sendMessage("no");
                        return true;
                    }

                    assert caster != null;
                    int level = profileManager.getAnyProfile(caster).getStats().getLevel();

                    int skillLevel = (int) Math.ceil(((double) level / 5));

                    double damage = damageCalculator.calculateGettingDamaged(player, caster, "physical", amount * skillLevel);

                    changeResourceHandler.subtractHealthFromEntity(player, damage, caster);
                    return true;
                }
                case "magical": {

                    Player player = Bukkit.getPlayer(UUID.fromString(args[0]));

                    if (player == null) {
                        sender.sendMessage("no");
                        return true;
                    }

                    assert caster != null;
                    int level = profileManager.getAnyProfile(caster).getStats().getLevel();

                    int skillLevel = (int) Math.ceil(((double) level / 5));

                    double damage = damageCalculator.calculateGettingDamaged(player, caster, "magical", amount * skillLevel);

                    changeResourceHandler.subtractHealthFromEntity(player, damage, caster);
                    return true;
                }
                case "crush":{

                    Player player = Bukkit.getPlayer(UUID.fromString(args[0]));

                    if (player == null) {
                        sender.sendMessage("no");
                        return true;
                    }

                    boolean resist = false;

                    String subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

                    if(subclass.equalsIgnoreCase("gladiator")
                    || subclass.equalsIgnoreCase("templar")
                    || subclass.equalsIgnoreCase("blood")){
                        resist = true;
                    }

                    if(resist){
                        return true;
                    }

                    double percent = profileManager.getAnyProfile(player).getTotalHealth() * (amount/100);

                    changeResourceHandler.subtractHealthFromEntity(player, percent, caster);

                    return true;
                }
            }


        }

        return false;
    }
}
