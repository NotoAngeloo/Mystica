package me.angeloo.mystica.Components.CombatSystem.Abilities;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerStateManager {

    private final Map<UUID, PlayerState> states = new HashMap<>();

    public PlayerState get(UUID player){
        return states.computeIfAbsent(player, id -> new PlayerState());
    }
}
