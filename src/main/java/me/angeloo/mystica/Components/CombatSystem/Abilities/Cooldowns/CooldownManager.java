package me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {


    private final Map<UUID, Long> globalCooldowns = new HashMap<>();
    //int is ability number
    private final Map<UUID, Map<Integer, CooldownData>> cooldowns = new HashMap<>();

    public void start(UUID uuid, int abilityNumber, long baseCooldown){

        cooldowns
                .computeIfAbsent(uuid, k -> new HashMap<>())
                .put(abilityNumber, new CooldownData(baseCooldown));

    }


    public long getRemaining(UUID playerId, int abilityNumber, double haste, long now) {

        Map<Integer, CooldownData> playerMap = cooldowns.get(playerId);
        if (playerMap == null) return 0;

        CooldownData data = playerMap.get(abilityNumber);
        if (data == null) return 0;

        // --- Lazy update ---
        long delta = now - data.getLastUpdateTime();
        if (delta > 0) {
            double rate = (1.0 + haste) / data.getBaseCooldown();
            double progressGain = rate * delta;

            data.setProgress(Math.min(1.0, data.getProgress() + progressGain));
            data.setLastUpdateTime(now);
        }

        // --- Finished ---
        if (data.getProgress() >= 1.0) {
            playerMap.remove(abilityNumber);

            // optional cleanup
            if (playerMap.isEmpty()) {
                cooldowns.remove(playerId);
            }

            return 0;
        }

        // --- Remaining time ---
        double remainingProgress = 1.0 - data.getProgress();
        double rate = (1.0 + haste) / data.getBaseCooldown();

        return (long) (remainingProgress / rate);
    }

    public void reduceCooldownFlat(UUID playerId, int abilityNumber, double seconds) {
        Map<Integer, CooldownData> playerMap = cooldowns.get(playerId);
        if (playerMap == null) return;

        CooldownData data = playerMap.get(abilityNumber);
        if (data == null) return;

        long reductionMs = (long) (seconds * 1000);

        // simulate time passing
        double rate = 1.0 / data.getBaseCooldown();
        double progressGain = rate * reductionMs;

        data.setProgress(Math.min(1.0, data.getProgress() + progressGain));
    }

    public boolean isReady(UUID playerId, int abilityNumber, double haste) {
        long now = System.currentTimeMillis();
        return getRemaining(playerId, abilityNumber, haste, now) <= 0;
    }

    public void clear(UUID uuid, int abilityNumber){
        Map<Integer, CooldownData> cooldownDataMap = cooldowns.get(uuid);

        if(cooldownDataMap!=null){
            cooldownDataMap.remove(abilityNumber);
        }

    }

    public void clearAll(UUID uuid){
        cooldowns.remove(uuid);
    }

    //why did i make this again?
    /*public CooldownSnapshot getSnapshot(UUID playerId, int abilityNumber, double haste, long now) {

        Map<Integer, CooldownData> playerMap = cooldowns.get(playerId);
        if (playerMap == null) return new CooldownSnapshot(0, 1.0, true);

        CooldownData data = playerMap.get(abilityNumber);
        if (data == null) return new CooldownSnapshot(0, 1.0, true);

        // update progress (same logic as before)
        long delta = now - data.getLastUpdateTime();
        if (delta > 0) {
            double rate = (1.0 + haste) / data.getBaseCooldown();
            data.setProgress(Math.min(1.0, data.getProgress() + rate * delta));
            data.setLastUpdateTime(now);
        }

        if (data.getProgress() >= 1.0) {
            playerMap.remove(abilityNumber);
            return new CooldownSnapshot(0, 1.0, true);
        }

        double remainingProgress = 1.0 - data.getProgress();
        double rate = (1.0 + haste) / data.getBaseCooldown();
        long remaining = (long)(remainingProgress / rate);

        return new CooldownSnapshot(remaining, data.getProgress(), false);
    }*/

    public boolean isOnGlobalCooldown(UUID playerId, long now) {
        Long end = globalCooldowns.get(playerId);
        return end != null && end > now;
    }

    public long getGlobalRemaining(UUID playerId, long now) {
        Long end = globalCooldowns.get(playerId);
        if (end == null) return 0;
        return Math.max(0, end - now);
    }

    public void applyGlobalCooldown(UUID playerId, double haste, long now, int baseGcdMillis) {

        double adjusted = baseGcdMillis / (1.0 + haste);

        //.25 sec
        adjusted = Math.max(250, adjusted);

        long endTime = now + (long) adjusted;

        globalCooldowns.put(playerId, endTime);
    }

}
