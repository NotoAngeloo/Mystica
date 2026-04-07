package me.angeloo.mystica.Components.Quests;

import me.angeloo.mystica.Components.PlayerProfile;
import me.angeloo.mystica.Components.Quests.Objectives.QuestObjective;
import me.angeloo.mystica.Components.Quests.Progress.QuestProgress;
import me.angeloo.mystica.Components.Quests.Requirements.Requirement;
import me.angeloo.mystica.Components.Quests.Rewards.QuestReward;

import java.util.List;

public class Quest {

    private final String id;
    private final String name;
    private final String giverId;
    private final List<String> description;
    private final List<String> progress;
    private final List<String> completed;
    private final List<QuestObjective> objectives;
    private final List<QuestReward> rewards;
    private final List<Requirement> requirements;


public Quest(String id, String name, String giverId, List<String> description, List<String> progress, List<String> completed,List<QuestObjective> objectives, List<QuestReward> rewards, List<Requirement> requirements) {
        this.id = id;
        this.name = name;
        this.giverId = giverId;
        this.description = description;
        this.progress = progress;
        this.completed = completed;
        this.objectives = objectives;
        this.rewards = rewards;
        this.requirements = requirements;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getGiverId(){return giverId;}
    public List<String> getDescription() { return description; }
    public List<String> getProgress(){ return progress;}
    public List<String> getCompleted(){return completed;}
    public List<QuestObjective> getObjectives(){return this.objectives;}
    public List<QuestReward> getRewards(){return this.rewards;}
    public List<Requirement> getRequirements(){return this.requirements;}

    public boolean canStart(PlayerProfile profile){
        for (Requirement req : this.requirements){
            if(!req.isMet(profile)){
                return false;
            }
        }
        return true;
    }



}
