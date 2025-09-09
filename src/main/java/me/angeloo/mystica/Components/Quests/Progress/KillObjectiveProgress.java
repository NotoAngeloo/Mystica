package me.angeloo.mystica.Components.Quests.Progress;

import io.lumine.mythic.api.mobs.MythicMob;
import me.angeloo.mystica.Components.Quests.Objectives.KillObjective;
import org.bukkit.configuration.ConfigurationSection;

public class KillObjectiveProgress extends ObjectiveProgress{

    private int kills = 0;

    public KillObjectiveProgress(KillObjective killObjective){
        super(killObjective);
    }

    @Override
    public void update(Object data){

        if(data instanceof MythicMob mob){
            if(mob.equals(((KillObjective) getObjective()).getTarget())){
                kills ++;
            }

        }


    }

    @Override
    public boolean isComplete(){
        return kills >= ((KillObjective) getObjective()).getRequired();
    }

    //not yet, ferb
    public void serialize(ConfigurationSection section) {
        section.set("kills", kills);
    }

    public void deserialize(ConfigurationSection section) {
        this.kills = section.getInt("kills", 0);
    }

}
