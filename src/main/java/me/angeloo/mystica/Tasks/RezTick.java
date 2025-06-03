package me.angeloo.mystica.Tasks;

import me.angeloo.mystica.Managers.FakePlayerAiManager;
import me.angeloo.mystica.Managers.MysticaPartyManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class RezTick extends BukkitRunnable {

    private final ProfileManager profileManager;
    private final MysticaPartyManager mysticaPartyManager;
    private final FakePlayerAiManager fakePlayerAiManager;

    public RezTick(Mystica main){
        profileManager = main.getProfileManager();
        mysticaPartyManager = main.getMysticaPartyManager();
        fakePlayerAiManager = main.getFakePlayerAiManager();
    }

    @Override
    public void run(){
        for(Player player : Bukkit.getOnlinePlayers()){

            boolean deathStatus = profileManager.getAnyProfile(player).getIfDead();

            if(!deathStatus){
                continue;
            }

            String rezMessage = "Left Click to Respawn";

            List<LivingEntity> mParty = new ArrayList<>(mysticaPartyManager.getMysticaParty(player));

            for(LivingEntity member : mParty){

                if(member instanceof Player){
                    boolean partyMemberDeathStatus = profileManager.getAnyProfile(member).getIfDead();

                    if(partyMemberDeathStatus){
                        continue;
                    }
                }


                boolean partyMemberCombatStatus = profileManager.getAnyProfile(member).getIfInCombat();

                if(partyMemberCombatStatus){
                    rezMessage = "Party in Combat";
                    break;
                }

                if(!(member instanceof Player)){
                    if(fakePlayerAiManager.getIfRotationRunning(member)){
                        rezMessage = "Party in Combat";
                        break;
                    }
                }


            }



            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(rezMessage));
        }
    }
}
