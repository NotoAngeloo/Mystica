package me.angeloo.mystica.Components.CombatSystem.Abilities.BasicAttacks;

import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BasicAttackEngine {

    private final Mystica main;

    private final Map<UUID, BasicAttackState> states = new HashMap<>();

    public BasicAttackEngine(Mystica main){
        this.main = main;
    }

    public void start(LivingEntity caster, BasicAttackDefinition def){

        UUID uuid = caster.getUniqueId();

        if(states.containsKey(uuid)){
            return;
        }

        if(!def.canStart(caster)){
            return;
        }

        BasicAttackState state = new BasicAttackState();
        //start at 1
        state.stage = 1;
        state.definition = def;
        states.put(uuid, state);

        executeStage(caster, state);
    }

    private void executeStage(LivingEntity caster, BasicAttackState state){

        //Bukkit.getLogger().info("basic stage " + state.stage);

        if(!state.definition.canContinue(caster, state.stage)){
            stop(caster);
            return;
        }

        boolean success = state.definition.performStage(caster, state.stage);

        int nextStage = state.stage+1;
        int maxStages = state.definition.getMaxStages(caster);
        int delay = state.definition.getStageDelay(caster, state.stage);
        int postDelay = state.definition.getStageDelay(caster, nextStage);

        if(success){
            state.stage++;

            if(state.stage > maxStages){
                state.task = new BukkitRunnable(){
                    @Override
                    public void run(){
                        stop(caster);
                    }
                }.runTaskLater(main, postDelay);

                return;
            }
        }


        //try to do next stage
        state.task = new BukkitRunnable(){
            @Override
            public void run(){
                executeStage(caster, state);
            }
        }.runTaskLater(main, delay);
    }

    public void stop(LivingEntity caster) {
        BasicAttackState state = states.remove(caster.getUniqueId());

        if (state != null && state.task != null) {
            state.task.cancel();
        }
    }

    public boolean isRunning(LivingEntity caster) {
        return states.containsKey(caster.getUniqueId());
    }

}
