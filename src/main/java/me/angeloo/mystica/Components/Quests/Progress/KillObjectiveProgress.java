package me.angeloo.mystica.Components.Quests.Progress;

import me.angeloo.mystica.Components.Quests.Objectives.KillObjective;

public class KillObjectiveProgress extends ObjectiveProgress{

    private int kills = 0;

    public KillObjectiveProgress(KillObjective killObjective){
        super(killObjective);
    }

    @Override
    public void update(Object data){

        if(data.equals(((KillObjective) getObjective()).getTarget())){
            kills ++;
        }

    }

    @Override
    public boolean isComplete(){
        return kills >= ((KillObjective) getObjective()).getRequired();
    }

}
