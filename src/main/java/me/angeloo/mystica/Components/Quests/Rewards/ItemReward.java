package me.angeloo.mystica.Components.Quests.Rewards;

import me.angeloo.mystica.Components.Items.MysticaItem;
import me.angeloo.mystica.Components.Quests.QuestEnums.RewardType;
import net.minecraft.network.PacketListener;
import org.bukkit.entity.Player;

public class ItemReward implements QuestReward{

    private final MysticaItem mysticaItem;

    public ItemReward(MysticaItem item){
        this.mysticaItem = item;
    }

    @Override
    public RewardType getType(){
        return RewardType.Item;
    }

    @Override
    public void give(Player player){

        //add to mysticabag

    }

}
