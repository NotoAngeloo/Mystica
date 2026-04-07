package me.angeloo.mystica.Components.Quests.Progress;

import me.angeloo.mystica.Components.Quests.Objectives.QuestObjective;
import me.angeloo.mystica.Components.Quests.Progress.ObjectiveProgress;
import me.angeloo.mystica.Components.Quests.Quest;
import me.angeloo.mystica.Components.Quests.QuestEnums.QuestType;
import me.angeloo.mystica.Components.Quests.QuestManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestProgress {


    private final Quest quest;
    private final List<ObjectiveProgress> objectiveProgresses = new ArrayList<>();
    private boolean rewarded;

    public QuestProgress(Quest quest) {
        this.quest = quest;

        // Convert each QuestObjective template into a mutable ObjectiveProgress
        for (QuestObjective obj : quest.getObjectives()) {
            objectiveProgresses.add(obj.createProgress());
        }

        rewarded = false;
    }

    public Quest getQuest(){
        return quest;
    }

    public List<ObjectiveProgress> getObjectiveProgresses() {
        return objectiveProgresses;
    }

    public void updateAllObjectiveProgress(Object data){

        for(ObjectiveProgress progress : this.objectiveProgresses){
            progress.update(data);
        }

    }

    public boolean isComplete(){
        return objectiveProgresses.stream().allMatch(ObjectiveProgress::isComplete);
    }

    public void setRewarded(){
        rewarded = true;
    }

    public boolean isRewarded(){
        return rewarded;
    }


    public static QuestProgress deserialize(QuestManager questManager, String questId, ConfigurationSection questSection){

        Quest quest = questManager.getQuest(questId);

        if(quest == null){
            throw new IllegalArgumentException("Quest with ID " + questId + " not found");
        }

        QuestProgress progress = new QuestProgress(quest);

        ConfigurationSection objectivesSection = questSection.getConfigurationSection("objectives");
        if (objectivesSection != null) {
            List<ObjectiveProgress> newProgresses = new ArrayList<>();


            for (QuestObjective objective : quest.getObjectives()) {
                ConfigurationSection objSec = objectivesSection.getConfigurationSection(objective.getId());
                if (objSec != null) {
                    newProgresses.add(objective.createProgressFromData(objSec));
                } else {
                    newProgresses.add(objective.createProgress());
                }

            }

            // Replace the default objective progress with the deserialized ones
            progress.getObjectiveProgresses().clear();
            progress.getObjectiveProgresses().addAll(newProgresses);
        }

        boolean rewarded = questSection.getBoolean("rewarded", false);

        if(rewarded){
            progress.setRewarded();
        }

        return progress;
    }



}
