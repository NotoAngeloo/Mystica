package me.angeloo.mystica.Components.Quests.Objectives;

import io.lumine.mythic.api.mobs.MythicMob;
import me.angeloo.mystica.Components.Quests.QuestEnums.QuestType;
import org.bukkit.entity.Player;

public class SpeakObjective implements QuestObjective {

    private final MythicMob target;
    private boolean complete;

    public SpeakObjective(MythicMob target){
        this.target = target;
        this.complete = false;
    }

    public void complete(){
        this.complete = true;
    }

    @Override
    public QuestType getType(){
        return QuestType.Speak;
    }

    @Override
    public boolean isComplete(Player player){
        return this.complete;
    }

}
