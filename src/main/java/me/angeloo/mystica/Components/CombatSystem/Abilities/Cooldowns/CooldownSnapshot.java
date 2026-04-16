package me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.ProfileComponents.EquipSkills;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownSnapshot {

    private final long remainingMs;
    private final double progress; // 0 → 1
    private final boolean ready;

    public CooldownSnapshot(long remainingMs, double progress, boolean ready) {
        this.remainingMs = remainingMs;
        this.progress = progress;
        this.ready = ready;
    }

    public long getRemainingMs() { return remainingMs; }
    public double getProgress() { return progress; }
    public boolean isReady() { return ready; }


}
