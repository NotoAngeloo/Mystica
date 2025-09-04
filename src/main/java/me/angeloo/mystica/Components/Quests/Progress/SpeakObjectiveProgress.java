package me.angeloo.mystica.Components.Quests.Progress;

import me.angeloo.mystica.Components.Quests.Objectives.SpeakObjective;

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

}
