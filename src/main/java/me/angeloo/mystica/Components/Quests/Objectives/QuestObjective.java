package me.angeloo.mystica.Components.Quests.Objectives;

import me.angeloo.mystica.Components.Quests.QuestEnums.QuestType;
import org.bukkit.entity.Player;

public interface QuestObjective {

    //public abstract boolean isComplete(Player player);

    boolean isComplete(Player player);

    QuestType getType();

}
