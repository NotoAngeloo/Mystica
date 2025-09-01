package me.angeloo.mystica.Components.Quests;

import me.angeloo.mystica.Components.Quests.Objectives.QuestObjective;
import me.angeloo.mystica.Components.Quests.Rewards.QuestReward;

import java.util.List;

public class Quest {

    private final String id;
    private final String name;
    private final List<String> description;
    private final List<QuestObjective> objectives;
    private final List<QuestReward> rewards;



    public Quest(String id, String name, List<String> description, List<QuestObjective> objectives, List<QuestReward> rewards) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.objectives = objectives;
        this.rewards = rewards;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public List<String> getDescription() { return description; } // lore-style
    public List<QuestObjective> getObjectives(){return this.objectives;}
    public List<QuestReward> getRewards(){return this.rewards;}



}
