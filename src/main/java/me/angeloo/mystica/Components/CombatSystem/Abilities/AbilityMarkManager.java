package me.angeloo.mystica.Components.CombatSystem.Abilities;

import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.entity.LivingEntity;

import java.util.*;

public class AbilityMarkManager {

    private final ProfileManager profileManager;

    private final Map<UUID, Set<LivingEntity>> marked = new HashMap<>();

    public AbilityMarkManager(Mystica main){
        profileManager = main.getProfileManager();
    }

    public void apply(LivingEntity caster, LivingEntity target){

        Set<LivingEntity> targets = marked.computeIfAbsent(caster.getUniqueId(), s->new HashSet<>());
        targets.add(target);
        marked.put(caster.getUniqueId(), targets);
    }

    public void applyAll(LivingEntity caster, Set<LivingEntity> targets){
        Set<LivingEntity> current = marked.computeIfAbsent(caster.getUniqueId(), s->new HashSet<>());
        current.addAll(targets);
        marked.put(caster.getUniqueId(), current);
    }

    public void removeTargets(LivingEntity caster){
        marked.remove(caster.getUniqueId());
    }

    public void removeSpecific(LivingEntity caster, LivingEntity target){
        Set<LivingEntity> targets = marked.computeIfAbsent(caster.getUniqueId(), s->new HashSet<>());
        targets.remove(target);
        marked.put(caster.getUniqueId(), targets);
    }

    public Set<LivingEntity> getTargets(LivingEntity caster){

        Set<LivingEntity> targets = marked.computeIfAbsent(caster.getUniqueId(),s->new HashSet<>());
        Set<LivingEntity> toRemove = new HashSet<>();

        for(LivingEntity target : targets){
            if(profileManager.getAnyProfile(target).getIfDead()){
                toRemove.add(target);
            }
        }
        targets.removeAll(toRemove);
        marked.put(caster.getUniqueId(), targets);
        return targets;

    }

}
