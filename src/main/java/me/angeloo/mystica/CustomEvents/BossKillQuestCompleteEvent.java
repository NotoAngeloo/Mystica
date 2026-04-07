package me.angeloo.mystica.CustomEvents;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Set;

public class BossKillQuestCompleteEvent extends Event {

    public static final HandlerList handlers = new HandlerList();

    private final Set<Player> players;
    private final String objectiveOf;

    public BossKillQuestCompleteEvent(Set<Player> players, String objectiveOf){
        this.players = players;
        this.objectiveOf = objectiveOf;
    }

    public Set<Player> getPlayers(){return players;}
    public String getObjectiveOf(){return objectiveOf;}

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }
}
