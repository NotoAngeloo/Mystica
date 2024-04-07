package me.angeloo.mystica.Utility;

import me.angeloo.mystica.Components.Profile;
import me.angeloo.mystica.Components.ProfileComponents.PlayerEquipment;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ClassSwapper {

    private final ProfileManager profileManager;

    public ClassSwapper(Mystica main){
        profileManager = main.getProfileManager();
    }

    //step 1, make sure they don't have equipment
    public boolean hasEquipment(Player player){

        PlayerEquipment equipment = profileManager.getAnyProfile(player).getPlayerEquipment();

        return (equipment.getWeapon() != null && !equipment.getWeapon().getType().isAir())
                || (equipment.getOffhand() != null && !equipment.getOffhand().getType().isAir())
                || (equipment.getHelmet() != null && !equipment.getHelmet().getType().isAir())
                || (equipment.getChestPlate() != null && !equipment.getChestPlate().getType().isAir())
                || (equipment.getLeggings() != null && !equipment.getLeggings().getType().isAir())
                || (equipment.getBoots() != null && !equipment.getBoots().getType().isAir());
    }


    public void swapClass(Player player, String clazz){
        Profile playerProfile = profileManager.getAnyProfile(player);

        playerProfile.setPlayerClass(clazz);
        playerProfile.setPlayerSubclass("none");
        profileManager.getAnyProfile(player).getStats().setLevelStats(profileManager.getAnyProfile(player).getStats().getLevel(), clazz, "none");
        player.sendMessage("You are now a(n) " + clazz);
        player.sendMessage("");
        ComponentBuilder classGuideMessage = new ComponentBuilder(ChatColor.of(new java.awt.Color(255, 128, 0)) + "Click here " +
                ChatColor.RESET + "to see a brief class guide")
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/classguide"));

        player.spigot().sendMessage(classGuideMessage.create());

    }

}
