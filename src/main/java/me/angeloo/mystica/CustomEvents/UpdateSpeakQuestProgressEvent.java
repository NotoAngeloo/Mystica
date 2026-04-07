package me.angeloo.mystica.CustomEvents;

import io.lumine.mythic.api.mobs.MythicMob;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UpdateSpeakQuestProgressEvent extends Event {

    public static final HandlerList handlers = new HandlerList();

    private final Player player;

    private final MythicMob mob;

    public UpdateSpeakQuestProgressEvent(Player player, MythicMob mob){
        this.player = player;
        this.mob = mob;
    }

    public Player getPlayer() {
        return player;
    }

    public MythicMob getMob() {
        return mob;
    }


    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }

}
