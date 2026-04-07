package me.angeloo.mystica.Components.Quests.Progress;

import me.angeloo.mystica.Components.Quests.Objectives.KillObjective;
import me.angeloo.mystica.Components.Quests.Objectives.QuestObjective;
import me.angeloo.mystica.Components.Quests.Objectives.SpeakObjective;

public abstract class ObjectiveProgress {

    private final QuestObjective objective;


    public ObjectiveProgress(QuestObjective objective){
        this.objective = objective;
    }

    public QuestObjective getObjective(){
        return objective;
    }

    public abstract void update(Object data);

    public abstract boolean isComplete();

}
