package me.angeloo.mystica.Components.Commands;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.CrowdControl.Fear;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.CrowdControl.Stun;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.DamageModifiers.ArmorBreak;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.DamageModifiers.Immune;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Components.CombatSystem.TargetManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.Enums.SubClass;
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
    private final StatusEffectManager statusEffectManager;
    private final TargetManager targetManager;

    public MysticaEffect(Mystica main) {
        profileManager = main.getProfileManager();
        changeResourceHandler = main.getChangeResourceHandler();
        statusEffectManager = main.getStatusEffectManager();
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
                //crush amount = %damage of their health

                case "crush" -> {

                    int bossLevel = profileManager.getAnyProfile(caster).getStats().getLevel();

                    //crush threshold increase by 10 each boss level
                    int crushThreshold = 100 + (10 * (bossLevel - 1));

                    SubClass subclass = profileManager.getAnyProfile(target).getPlayerSubclass();


                    if (subclass.equals(SubClass.Gladiator)
                            || subclass.equals(SubClass.Templar)
                            || subclass.equals(SubClass.Blood)) {
                        crushThreshold -= 100;
                    }

                    if (subclass.equals(SubClass.Executioner)) {
                        crushThreshold -= 50;
                    }

                    crushThreshold -= profileManager.getAnyProfile(target).getTotalDefense();

                    if (crushThreshold <= 0) {
                        return true;
                    }

                    double percent = profileManager.getAnyProfile(target).getTotalHealth() * ((double) amount / 100);

                    changeResourceHandler.subtractHealthFromEntity(target, percent, caster, true);
                    //Bukkit.getLogger().info("crush");

                    return true;
                }
                case "stun" -> {
                    statusEffectManager.applyEffect(target, new Stun(), amount, null);
                    return true;
                }
                case "unstun" -> {
                    statusEffectManager.removeEffect(target, "stun");
                    return true;
                }
                case "immune" -> {

                    if(statusEffectManager.hasEffect(target, "immune")){
                        statusEffectManager.removeEffect(target, "immune");
                        return true;
                    }

                    statusEffectManager.applyEffect(target, new Immune(), amount, null);

                    return true;
                }
                case "melt" -> {
                    statusEffectManager.applyEffect(target, new ArmorBreak(), null, null);
                    return true;
                }
                case "heal_percent" -> {
                    double totalHealth = profileManager.getAnyProfile(target).getTotalHealth();
                    double healed = totalHealth * (amount * .01);
                    changeResourceHandler.addHealthToEntity(target, healed, caster);
                    return true;
                }
                case "fear" -> {
                    statusEffectManager.applyEffect(target, new Fear(), amount, null);
                    return true;
                }
                case "fear_if_targeting" -> {

                    if (targetManager.isTargeting(target, caster)) {
                        statusEffectManager.applyEffect(target, new Fear(), amount, null);

                        if (target instanceof Player) {
                            if (!profileManager.getCompanions((Player) target).isEmpty()) {
                                for (UUID companion : profileManager.getCompanions((Player) target)) {
                                    LivingEntity entity = (LivingEntity) Bukkit.getEntity(companion);

                                    if (entity == null) {
                                        continue;
                                    }

                                    statusEffectManager.applyEffect(target, new Fear(), amount, null);
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
