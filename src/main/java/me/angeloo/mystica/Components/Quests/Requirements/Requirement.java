package me.angeloo.mystica.Components.Quests.Requirements;

import me.angeloo.mystica.Components.ProfileComponents.PlayerProfile;
import me.angeloo.mystica.Components.Quests.QuestEnums.RequirementType;

public interface Requirement {

    RequirementType getType();
    boolean isMet(PlayerProfile profile);

}
