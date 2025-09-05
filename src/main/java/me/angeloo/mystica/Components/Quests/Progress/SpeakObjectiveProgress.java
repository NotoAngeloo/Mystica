package me.angeloo.mystica.Components.Quests.Progress;

import me.angeloo.mystica.Components.Quests.Objectives.SpeakObjective;
import org.bukkit.configuration.ConfigurationSection;

public class SpeakObjectiveProgress extends ObjectiveProgress{

    boolean spoken = false;

    public SpeakObjectiveProgress(SpeakObjective speakObjective){
        super(speakObjective);
    }

    @Override
    public void update(Object data){

        if(data.equals(((SpeakObjective) getObjective()).getTarget())){
            spoken = true;
        }

    }

    @Override
    public boolean isComplete(){
        return spoken;
    }

    public void serialize(ConfigurationSection section) {
        section.set("spoken", spoken);
    }

    public void deserialize(ConfigurationSection section) {
        this.spoken = section.getBoolean("spoken", false);
    }
}
