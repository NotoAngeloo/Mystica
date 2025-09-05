package me.angeloo.mystica.Components.Quests.Objectives;

import io.lumine.mythic.api.mobs.MythicMob;
import me.angeloo.mystica.Components.Quests.Progress.KillObjectiveProgress;
import me.angeloo.mystica.Components.Quests.Progress.ObjectiveProgress;
import me.angeloo.mystica.Components.Quests.QuestEnums.QuestType;
import org.bukkit.configuration.ConfigurationSection;

public class KillObjective implements QuestObjective{

    private final String id;
    private final MythicMob target;
    private final int required;

    public KillObjective(String id, MythicMob target, int required){
        this.id = id;
        this.target = target;
        this.required = required;
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
        return new KillObjectiveProgress(this);
    }

    @Override
    public ObjectiveProgress createProgressFromData(ConfigurationSection section) {
        KillObjectiveProgress progress = new KillObjectiveProgress(this);
        progress.deserialize(section);
        return progress;
    }

    public MythicMob getTarget(){
        return target;
    }

    public int getRequired(){
        return required;
    }


}
