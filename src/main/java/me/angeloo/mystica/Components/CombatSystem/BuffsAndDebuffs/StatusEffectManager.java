package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs;

import me.angeloo.mystica.CustomEvents.HudUpdateEvent;
import me.angeloo.mystica.Utility.Enums.BarType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.*;

public class StatusEffectManager {

    private final Map<UUID, Map<String, StatusInstance>> active = new HashMap<>();

    public void applyEffect(LivingEntity entity, StatusEffect effect, @Nullable Integer duration, @Nullable Double magnitude) {

        if (effect.requireDurationDeclaration() && duration == null) {
            throw new IllegalArgumentException(
                    effect.getId() + " requires a custom duration, but none was provided."
            );
        }

        if (effect.requireMagnitudeDeclaration() && magnitude == null) {
            throw new IllegalArgumentException(
                    effect.getId() + " requires a custom magnitude, but none was provided."
            );
        }

        // --- Ensure the inner map exists ---
        Map<String, StatusInstance> map = active.computeIfAbsent(
                entity.getUniqueId(),
                id -> new HashMap<>()
        );

        StatusInstance existing = map.get(effect.getId());

        int resolvedDuration = (duration != null ? duration : effect.getDuration());
        double resolvedMagnitude = (magnitude != null ? magnitude : effect.getMagnitude());

        // --- Create new instance object now that correct values are known ---
        StatusInstance newInstance = effect.createInstance(resolvedDuration, resolvedMagnitude);

        // If none exists, simply add
        if (existing == null) {
            map.put(effect.getId(), newInstance);
            return;
        }

        // Apply stacking logic:
        switch (effect.stackType()) {

            case REPLACE -> {
                map.put(effect.getId(), newInstance);
                break;
            }

            case REPLACE_SMALLER -> {
                // use magnitude comparison using the resolved values
                if (existing.magnitude <= newInstance.magnitude) {
                    map.put(effect.getId(), newInstance);
                }
                break;
            }

            case REPLACE_LARGER -> {
                // use magnitude comparison using the resolved values
                if (existing.magnitude >= newInstance.magnitude) {
                    map.put(effect.getId(), newInstance);
                }
                break;
            }

            case ADDITIVE -> {
                existing.magnitude += newInstance.magnitude;
                existing.remainingTicks = Math.max(existing.remainingTicks, newInstance.remainingTicks);
                break;
            }
        }
    }

    public void removeEffect(LivingEntity entity, String id){

        Map<String, StatusInstance> map = active.get(entity.getUniqueId());
        if (map == null) return;

        StatusInstance inst = map.remove(id);
        if (inst == null) return;

        inst.endNow();
        inst.onRemove(entity);

    }

    public void clear(LivingEntity entity){

        Map<String, StatusInstance> effectMap = active.get(entity.getUniqueId());

        if(effectMap == null || effectMap.isEmpty()){
            return;
        }

        for(StatusInstance instance : effectMap.values()){
            instance.endNow();
            instance.onRemove(entity);
        }

        effectMap.clear();

        if(entity instanceof Player player){
            Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, BarType.Status));
        }

    }

    public void tick() {
        for (UUID id : active.keySet()) {

            LivingEntity entity = getEntity(id);
            if (entity == null) continue;

            Map<String, StatusInstance> map = active.get(id);
            Iterator<StatusInstance> it = map.values().iterator();

            while (it.hasNext()) {
                StatusInstance inst = it.next();
                inst.onTick(entity);

                // Only tick down if duration is positive
                if (inst.getRemainingTicks() > 0) {
                    boolean expired = inst.tickDown();
                    if (expired) {
                        inst.onRemove(entity);
                        it.remove();
                    }
                }
                // If duration <= 0, effect is "consumable" and stays until explicitly consumed
            }
        }
    }

    public boolean hasEffect(LivingEntity entity, String effectId) {
        Map<String, StatusInstance> map = active.get(entity.getUniqueId());
        return map != null && map.containsKey(effectId);
    }

    public boolean canCast(LivingEntity entity){

        return !(
                hasEffect(entity, "silence")
                || hasEffect(entity, "stun")
                || hasEffect(entity, "fear")
                || hasEffect(entity, "knock_up")
                || hasEffect(entity, "sleep")
                || hasEffect(entity, "interrupt")
                );

    }

    public boolean canMove(LivingEntity entity){
        return !(
                hasEffect(entity, "stun")
                || hasEffect(entity, "sleep")
                );

    }

    public boolean canBasic(LivingEntity entity){
        return !(
                hasEffect(entity, "stun")
                || hasEffect(entity, "sleep")
                || hasEffect(entity, "fear")
                || hasEffect(entity, "knock_up")
        );
    }

    public int getHasteLevel(LivingEntity entity){

        if(!hasEffect(entity, "haste")){
            return 0;
        }

        StatusInstance instance = getInstanceMap(entity).get("haste");
        return (int) instance.magnitude;
    }

    public double getAdditionalRange(LivingEntity entity){

        double additional = 0;

        if(hasEffect(entity, "conjuring_force")){
            additional += 10;
        }

        //whenever something else increase range put it here

        return additional;
    }

    public double getHealthBuffAmount(LivingEntity entity){

        double health = 0;

        if(hasEffect(entity, "burning_blessing")){
            health += getInstanceMap(entity).get("burning_blessing").magnitude;

        }

        if(hasEffect(entity, "flaming_sigil_health")){
            health += getInstanceMap(entity).get("flaming_sigil_health").magnitude;

        }

        return health;
    }

    //might need to pass in damager for reflect damage

    public void handleDamage(LivingEntity entity, double damage) {
        Map<String, StatusInstance> map = active.get(entity.getUniqueId());
        if (map == null) return;

        for (StatusInstance inst : map.values()) {
            inst.onDamage(entity, damage);
        }
    }

    private LivingEntity getEntity(UUID id) {
        Entity e = Bukkit.getEntity(id);
        return (e instanceof LivingEntity le) ? le : null;
    }

    public Map<String, StatusInstance> getInstanceMap(LivingEntity entity){
        return active.get(entity.getUniqueId());
    }


}
