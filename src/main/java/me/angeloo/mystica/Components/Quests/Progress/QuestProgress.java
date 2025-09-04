package me.angeloo.mystica.Components.Quests.Progress;

import me.angeloo.mystica.Components.Quests.Objectives.QuestObjective;
import me.angeloo.mystica.Components.Quests.Progress.ObjectiveProgress;
import me.angeloo.mystica.Components.Quests.Quest;
import me.angeloo.mystica.Components.Quests.QuestEnums.QuestType;

import java.util.ArrayList;
import java.util.List;

public class QuestProgress {

    private final Quest quest;

    private final List<ObjectiveProgress> objectives;

    public QuestProgress(Quest quest){
        this.quest = quest;
        this.objectives = new ArrayList<>();

        for(QuestObjective template : quest.getObjectives()){
            objectives.add(template.createProcess());
        }
    }

    public Quest getQuest(){
        return quest;
    }

    public List<ObjectiveProgress> getObjectives(){
        return objectives;
    }

    public void updateProgress(QuestType type, Object data){

        for(ObjectiveProgress progress : objectives){

            if(progress.getObjective().getType().equals(type)){
                progress.update(data);
            }

        }

    }

    public boolean isComplete(){
        for (ObjectiveProgress progress : objectives){

            if(!progress.isComplete()){
                return false;
            }

        }
        return true;

    }

}
