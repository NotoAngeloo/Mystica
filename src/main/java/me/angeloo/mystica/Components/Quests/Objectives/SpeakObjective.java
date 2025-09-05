package me.angeloo.mystica.Components.Quests.Objectives;

import io.lumine.mythic.api.mobs.MythicMob;
import me.angeloo.mystica.Components.Quests.Progress.KillObjectiveProgress;
import me.angeloo.mystica.Components.Quests.Progress.ObjectiveProgress;
import me.angeloo.mystica.Components.Quests.Progress.SpeakObjectiveProgress;
import me.angeloo.mystica.Components.Quests.QuestEnums.QuestType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class SpeakObjective implements QuestObjective {

    private final String id;
    private final MythicMob target;

    public SpeakObjective(String id, MythicMob target){
        this.id = id;
        this.target = target;

    }

    @Override
    public String getId(){
        return id;
    }

    @Override
    public QuestType getType(){
        return QuestType.Speak;
    }

    @Override
    public ObjectiveProgress createProgress() {
        return new SpeakObjectiveProgress(this);
    }

    @Override
    public ObjectiveProgress createProgressFromData(ConfigurationSection section) {
        SpeakObjectiveProgress progress = new SpeakObjectiveProgress(this);
        progress.deserialize(section);
        return progress;
    }


    public MythicMob getTarget(){
        return target;
    }

}
