package me.angeloo.mystica.Components.Quests.Objectives;

import me.angeloo.mystica.Components.Quests.Progress.ObjectiveProgress;
import me.angeloo.mystica.Components.Quests.QuestEnums.QuestType;
import org.bukkit.configuration.ConfigurationSection;

public interface QuestObjective {

    String getId();
    QuestType getType();
    ObjectiveProgress createProgress();

    default ObjectiveProgress createProgressFromData(ConfigurationSection section) {
        return createProgress();
    }

}
