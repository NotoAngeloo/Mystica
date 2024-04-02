package me.angeloo.mystica.Components.Guides;


import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.awt.*;

import static me.angeloo.mystica.Mystica.*;

public class ClassGuides {

    private final ProfileManager profileManager;

    public ClassGuides(Mystica main){
        profileManager = main.getProfileManager();
    }

    public void openClassGuide(Player player){

        ItemStack guide = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) guide.getItemMeta();
        assert meta != null;
        meta.setTitle("Class Guide");
        meta.setAuthor("");
        addToMeta(player, meta);
        guide.setItemMeta(meta);
        player.openBook(guide);
    }

    private void addToMeta(Player player, BookMeta meta){

        switch (profileManager.getAnyProfile(player).getPlayerClass().toLowerCase()){
            case "assassin":{
                meta.addPage(ChatColor.of(assassinColor) + "Assassin" +
                        "\n\n" +
                        ChatColor.of(menuColor) + "Difficulty: " + ChatColor.RESET + "Challenging" +
                        "\n\n" +
                        ChatColor.of(menuColor) + "Strengths: " +
                        "\n" + ChatColor.RESET + "High single target damage" +
                        "\n" + ChatColor.RESET + "Mobility" +
                        "\n\n" +
                        ChatColor.of(menuColor) + "Weaknesses: " +
                        "\n" + ChatColor.RESET + "Range" +
                        "\n" + ChatColor.RESET + "Survivability"
                );

                meta.addPage(ChatColor.of(menuColor) + "How to play" +
                        "\n\n" +
                        ChatColor.RESET + "Build up combo points with\n" +
                        ChatColor.of(assassinColor) + "Assault, Blade Tempest, Laceration" +
                        "\n" +
                        "\uE009" + "\uE009" + "\uE008" + "\uE008" + "\uE008" +
                        "\n\n" +
                        ChatColor.RESET + "Spend your combo points with\n" +
                        ChatColor.of(assassinColor) + "Weakness Strike" +
                        "\n"+
                        "\uE009" + "\uE009" + "\uE009" + "\uE009" + "\uE009"

                );

                break;
            }
            case "elementalist":{
                meta.addPage(ChatColor.of(elementalistColor) + "Elementalist" +
                        "\n\n" +
                        ChatColor.of(menuColor) + "Difficulty: " + ChatColor.RESET + "Easy" +
                        "\n\n" +
                        ChatColor.of(menuColor) + "Strengths: " +
                        "\n" + ChatColor.RESET + "Range" +
                        "\n" + ChatColor.RESET + "Burst Damage" +
                        "\n\n" +
                        ChatColor.of(menuColor) + "Weaknesses: " +
                        "\n" + ChatColor.RESET + "Crowd Control"
                );

                meta.addPage(ChatColor.of(menuColor) + "How to play" +
                        "\n\n" +
                        ChatColor.RESET + "Keep your distance with " +
                        ChatColor.of(elementalistColor) + "Windrush Form" +
                        "\n\n" +
                        ChatColor.RESET + "Prioritize Casting " +
                        ChatColor.of(elementalistColor) + "Descending Inferno and Ice Bolt" +
                        ChatColor.RESET + "When " + ChatColor.of(elementalistColor) + "Elemental Breath " +
                        ChatColor.RESET + "is active."

                );

                break;
            }
            case "mystic":{
                meta.addPage(ChatColor.of(mysticColor) + "Mystic" +
                        "\n\n" +
                        ChatColor.of(menuColor) + "Difficulty: " + ChatColor.RESET + "Hard" +
                        "\n\n" +
                        ChatColor.of(menuColor) + "Strengths: " +
                        "\n" + ChatColor.RESET + "Survivability" +
                        "\n" + ChatColor.RESET + "Range" +
                        "\n\n" +
                        ChatColor.of(menuColor) + "Weaknesses: " +
                        "\n" + ChatColor.RESET + "Area of Effect"
                );

                meta.addPage(ChatColor.of(menuColor) + "How to play" +
                        "\n\n" +
                        ChatColor.RESET + "Cast " +
                        ChatColor.of(mysticColor) + "Dreadfall, Force of Will" +
                        ChatColor.RESET + " to damage your enemy." +
                        "\n\n" +
                        ChatColor.RESET + "Learn what time to use your support skills."
                );

                meta.addPage("Remember that holding (sneak) will prioritize allied players while selecting a target. " +
                        "\n\nPressing (q+shift) will target your lowest health ally as well.");

                break;
            }
            case "paladin":{
                meta.addPage(ChatColor.of(menuColor) + "Paladin" +
                        "\n\n" +
                        ChatColor.of(menuColor) + "Difficulty: " + ChatColor.RESET + "Average" +
                        "\n\n" +
                        ChatColor.of(menuColor) + "Strengths: " +
                        "\n" + ChatColor.RESET + "A little of everything" +
                        "\n\n" +
                        ChatColor.of(menuColor) + "Weaknesses: " +
                        "\n" + ChatColor.RESET + "Below average damage"
                );

                meta.addPage(ChatColor.of(menuColor) + "How to play" +
                        "\n\n" +
                        ChatColor.RESET + "A paladin has a skill for every situation. Learn what situation requires a specific skill");


                break;
            }
            case "ranger":{
                meta.addPage(ChatColor.of(rangerColor) + "Ranger" +
                        "\n\n" +
                        ChatColor.of(menuColor) + "Difficulty: " + ChatColor.RESET + "Easy" +
                        "\n\n" +
                        ChatColor.of(menuColor) + "Strengths: " +
                        "\n" + ChatColor.RESET + "Range" +
                        "\n" + ChatColor.RESET + "Sustained Damage" +
                        "\n\n" +
                        ChatColor.of(menuColor) + "Weaknesses: " +
                        "\n" + ChatColor.RESET + "Area of Effect"
                );

                meta.addPage(ChatColor.of(menuColor) + "How to play" +
                        "\n\n" +
                        ChatColor.RESET + "Keep your distance with " +
                        ChatColor.of(rangerColor) + "Roll" +
                        "\n\n" +
                        ChatColor.RESET + "But stay close enough to not interrupt your channeled skills"

                );

                break;
            }
            case "shadow knight":{
                meta.addPage(ChatColor.of(shadowKnightColor) + "Shadow Knight" +
                        "\n\n" +
                        ChatColor.of(menuColor) + "Difficulty: " + ChatColor.RESET + "Hard" +
                        "\n\n" +
                        ChatColor.of(menuColor) + "Strengths: " +
                        "\n" + ChatColor.RESET + "Survivability" +
                        "\n" + ChatColor.RESET + "Sustained Damage" +
                        "\n\n" +
                        ChatColor.of(menuColor) + "Weaknesses: " +
                        "\n" + ChatColor.RESET + "Area of Effect"
                );

                meta.addPage(ChatColor.of(menuColor) + "How to play" +
                        "\n\n" +
                        ChatColor.RESET + "Spend your energy with " +
                        ChatColor.of(shadowKnightColor) + "Spiritual Attack " +
                        ChatColor.RESET + " to deal damage." +
                        "\n\n" +
                        ChatColor.RESET + "Recover your energy with " +
                        ChatColor.of(shadowKnightColor) + "Burial Ground " + ChatColor.RESET + "and " +
                        ChatColor.of(shadowKnightColor) + "Soulcrack."

                );

                break;
            }
            case "warrior":{
                meta.addPage(ChatColor.of(warriorColor) + "Warrior" +
                        "\n\n" +
                        ChatColor.of(menuColor) + "Difficulty: " + ChatColor.RESET + "Average" +
                        "\n\n" +
                        ChatColor.of(menuColor) + "Strengths: " +
                        "\n" + ChatColor.RESET + "Crowd Control" +
                        "\n" + ChatColor.RESET + "Area of effect" +
                        "\n\n" +
                        ChatColor.of(menuColor) + "Weaknesses: " +
                        "\n" + ChatColor.RESET + "Range"
                );

                meta.addPage(ChatColor.of(menuColor) + "How to play" +
                        "\n\n" +
                        ChatColor.of(warriorColor) + "Tempest Rage " +
                        ChatColor.RESET + " will only do damage to nearby enemies." +
                        "\n\n" +
                        ChatColor.RESET + "Stay close to your target, but remember to dodge attacks."

                );

                break;
            }
        }

    }

}
