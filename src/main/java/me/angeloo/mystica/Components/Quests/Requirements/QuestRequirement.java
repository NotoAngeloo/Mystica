package me.angeloo.mystica.Components.Quests.Requirements;

import me.angeloo.mystica.Components.PlayerProfile;
import me.angeloo.mystica.Components.Quests.Progress.QuestProgress;
import me.angeloo.mystica.Components.Quests.QuestEnums.RequirementType;

public class QuestRequirement implements Requirement{

    private final String questId;

    public QuestRequirement(String questId){
        this.questId = questId;
    }

    @Override
    public boolean isMet(PlayerProfile profile){

        QuestProgress progress = profile.getQuestProgressMap().get(questId);

        return progress != null && progress.isRewarded();
    }

    @Override
    public RequirementType getType(){
        return RequirementType.Quest;
    }

}
