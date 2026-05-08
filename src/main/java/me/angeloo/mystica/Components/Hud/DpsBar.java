package me.angeloo.mystica.Components.Hud;

import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Enums.PlayerClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.LivingEntity;

import static me.angeloo.mystica.Mystica.*;

public class DpsBar {

    private final ProfileManager profileManager;

    public DpsBar(Mystica main){
        profileManager = main.getProfileManager();
    }

    public String getBar(LivingEntity entity, double top, double dps){

        StringBuilder bar = new StringBuilder();

        PlayerClass playerClass = profileManager.getAnyProfile(entity).getPlayerClass();

        switch (playerClass) {
            case Assassin -> {
                bar.append(ChatColor.of(assassinColor));
            }
            case Elementalist -> {
                bar.append(ChatColor.of(elementalistColor));
            }
            case Mystic -> {
                bar.append(ChatColor.of(mysticColor));
            }
            case Paladin -> {
                bar.append(ChatColor.of(paladinColor));
            }
            case Ranger -> {
                bar.append(ChatColor.of(rangerColor));
            }
            case Shadow_Knight -> {
                bar.append(ChatColor.of(shadowKnightColor));
            }
            case Warrior -> {
                bar.append(ChatColor.of(warriorColor));
            }
        }


        double ratio =  dps /  top;
        int amount = (int) Math.ceil(ratio * 10);

        if(amount <= 0){
            amount = 1;
        }

        if(amount >= 10){
            amount = 10;
        }

        //Bukkit.getLogger().info(entity.getName() + "top: " + top + " dps: " + dps +" ratio: "+ratio +  " amount: " + amount);

        switch (amount) {
            case 10 -> {
                bar.append("\ue3aa");
            }
            case 9 -> {
                bar.append("\ue3ab");
            }
            case 8 -> {
                bar.append("\ue3ac");
            }
            case 7 -> {
                bar.append("\ue3ad");
            }
            case 6 -> {
                bar.append("\ue3ae");
            }
            case 5 -> {
                bar.append("\ue3af");
            }
            case 4 -> {
                bar.append("\ue3b0");
            }
            case 3 -> {
                bar.append("\ue3b1");
            }
            case 2 -> {
                bar.append("\ue3b2");
            }
            case 1 -> {
                bar.append("\ue3b3");
            }
        }



        return String.valueOf(bar);

    }


}
