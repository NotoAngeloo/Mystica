package me.angeloo.mystica.Utility.MatchMaking;

import me.angeloo.mystica.Utility.Enums.Role;

import java.util.UUID;

public class MatchMakingPlayer {

    private final UUID uuid;
    private final Role role;

    public MatchMakingPlayer(UUID uuid, Role role){
        this.uuid = uuid;
        this.role = role;
    }

    public UUID getUuid(){
        return uuid;
    }

    public Role getRole(){
        return role;
    }

}
