package me.angeloo.mystica.Components.Quests.Rewards;

import me.angeloo.mystica.Components.Quests.QuestEnums.RewardType;
import org.bukkit.entity.Player;

public interface QuestReward {

    RewardType getType();

    void give(Player player);

}
