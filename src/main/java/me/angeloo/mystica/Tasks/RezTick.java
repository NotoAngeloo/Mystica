package me.angeloo.mystica.Tasks;

import me.angeloo.mystica.Managers.MysticaPartyManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class RezTick{

    private final Mystica main;
    private final ProfileManager profileManager;
    private final MysticaPartyManager mysticaPartyManager;

    private final Map<UUID, BukkitTask> rezTasks = new HashMap<>();
    private final Map<UUID, BukkitTask> countDownTasks = new HashMap<>();
    private final Map<UUID, Integer> ableToRez = new HashMap<>();

    public RezTick(Mystica main){
        this.main = main;
        profileManager = main.getProfileManager();
        mysticaPartyManager = main.getMysticaPartyManager();
    }

    public void startRezTickFor(Player player){

        if(rezTasks.containsKey(player.getUniqueId())){
            return;
        }

        if(!partyInCombat(player)){
            ableRezCountdown(player);
        }


        BukkitTask rezTask = new BukkitRunnable(){
            @Override
            public void run(){

                if(this.isCancelled()){
                    rezTasks.remove(player.getUniqueId());
                    return;
                }

                //start it back up on rejoin
                if(!player.isOnline()){
                    rezTasks.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                boolean deathStatus = profileManager.getAnyProfile(player).getIfDead();

                if(!deathStatus){
                    rezTasks.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                String rezMessage = "Left Click to Respawn";


                if(partyInCombat(player)){
                    rezMessage = "Party in Combat";
                }

                if(!partyInCombat(player)){
                    if(!ableToRez(player)){
                        rezMessage = "Able to revive in " + getRezTime(player);
                    }
                }

                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(rezMessage));



            }

        }.runTaskTimerAsynchronously(main, 0, 20);



        rezTasks.put(player.getUniqueId(), rezTask);

    }

    public void stopAll(){

        for(Map.Entry<UUID, BukkitTask> task : rezTasks.entrySet()){
            UUID key = task.getKey();
            rezTasks.get(key).cancel();
        }

    }


    public boolean running(Player player){
        return rezTasks.containsKey(player.getUniqueId());
    }

    public boolean ableToRez(Player player){

        if(partyInCombat(player)){
            return false;
        }

        if(ableToRez.containsKey(player.getUniqueId())){
            return getRezTime(player) <= 0;
        }

        return true;
    }

    public void ableRezCountdown(Player player){

        if(countDownTasks.containsKey(player.getUniqueId())){
            return;
        }

        ableToRez.put(player.getUniqueId(), 3);

        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){


                if(partyInCombat(player) && inParty(player)){
                    this.cancel();
                    ableToRez.put(player.getUniqueId(), 3);
                    countDownTasks.remove(player.getUniqueId());
                    return;
                }

                int time = getRezTime(player);

                time--;

                ableToRez.put(player.getUniqueId(), time);

                if(time <= 0){
                    this.cancel();
                    ableToRez.remove(player.getUniqueId());
                    countDownTasks.remove(player.getUniqueId());
                }
            }
        }.runTaskTimerAsynchronously(main, 20, 20);

        countDownTasks.put(player.getUniqueId(), task);
    }

    private int getRezTime(Player player){
        return ableToRez.getOrDefault(player.getUniqueId(), 3);
    }

    private boolean partyInCombat(Player player){

        List<LivingEntity> mParty = new ArrayList<>(mysticaPartyManager.getMysticaParty(player));

        for(LivingEntity member : mParty){

            boolean partyMemberDeathStatus = profileManager.getAnyProfile(member).getIfDead();

            if(partyMemberDeathStatus){
                continue;
            }

            //check to see how companion members in combat
            boolean partyMemberCombatStatus = profileManager.getAnyProfile(member).getIfInCombat();

            if(partyMemberCombatStatus){
                return true;
            }

        }

        return false;
    }

    private boolean inParty(Player player){
        List<LivingEntity> mParty = new ArrayList<>(mysticaPartyManager.getMysticaParty(player));
        return mParty.size() != 1;
    }

}
