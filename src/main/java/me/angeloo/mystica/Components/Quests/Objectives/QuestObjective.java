package me.angeloo.mystica.Components.Quests.Objectives;

import me.angeloo.mystica.Components.Quests.Progress.ObjectiveProgress;
import me.angeloo.mystica.Components.Quests.QuestEnums.QuestType;

public interface QuestObjective {

    String getId();
    QuestType getType();
    ObjectiveProgress createProcess();

}
