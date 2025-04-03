package me.angeloo.mystica.Utility;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class MysticaParty {

    private Player leader;

    private final Map<LivingEntity, String> memberRole;


    public MysticaParty(Player leader){
        this.leader = leader;
        memberRole = new HashMap<>();
    }

    public Player getLeader(){
        return this.leader;
    }

    public void addOrChangeMemberRole(LivingEntity member, String role){
        memberRole.put(member, role);
    }

    public void removeMember(LivingEntity member){
        memberRole.remove(member);
    }

    public void changeLeader(Player player){
        this.leader = player;
    }

    public boolean hasTank(){

        for(Map.Entry<LivingEntity, String> key : memberRole.entrySet()){
            if(key.getValue().toLowerCase().contains("tank")){
                return true;
            }
        }
        return false;
    }

    public boolean hasHeal(){

        for(Map.Entry<LivingEntity, String> key : memberRole.entrySet()){
            if(key.getValue().toLowerCase().contains("heal")){
                return true;
            }
        }
        return false;
    }

    public boolean needsDamage(){

        if(hasTank() && hasHeal()){
            return numberRoleSelected() >= 2;
        }

        return false;
    }

    public void clearRoles(){
        memberRole.clear();
    }

    public int numberRoleSelected(){
        return memberRole.entrySet().size();
    }

}
