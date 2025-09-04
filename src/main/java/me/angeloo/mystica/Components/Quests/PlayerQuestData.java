package me.angeloo.mystica.Components.Quests;

import me.angeloo.mystica.Components.Quests.Progress.QuestProgress;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerQuestData {

    private final UUID playerId;

    private final Map<String, QuestProgress> activeQuests = new HashMap<>();

    public PlayerQuestData(UUID playerId){
        this.playerId = playerId;
    }

    public void accept(Quest quest){

        if(!activeQuests.containsKey(quest.getId())){
            activeQuests.put(quest.getId(), new QuestProgress(quest));
        }

    }

    public void abandon(String id){
        activeQuests.remove(id);
    }

    public QuestProgress getProgress(String id){
        return activeQuests.get(id);
    }

    public Collection<QuestProgress> getAllProgress(){
        return activeQuests.values();
    }

}
