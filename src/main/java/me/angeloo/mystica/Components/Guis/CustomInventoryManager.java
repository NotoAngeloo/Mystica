package me.angeloo.mystica.Components.Guis;

import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Enums.EquipmentEnhancementType;
import me.angeloo.mystica.Utility.Enums.Role;
import me.angeloo.mystica.Utility.Enums.SubClass;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CustomInventoryManager {

    private final ProfileManager profileManager;
    private final InventoryTextGenerator textGenerator;
    private final Map<UUID, EquipmentEnhancementType> enhancementTypeIndex = new HashMap<>();
    private final Map<UUID, Integer> bagIndex = new HashMap<>();
    private final Map<UUID, Integer> classIndex = new HashMap<>();
    private final Map<UUID, Integer> dungeonIndex = new HashMap<>();
    private final Map<UUID, Integer> partyIndex = new HashMap<>();
    private final Map<UUID, Role> roleFilter= new HashMap<>();

    public CustomInventoryManager(Mystica main){
        profileManager = main.getProfileManager();
        textGenerator = new InventoryTextGenerator();
    }

    public InventoryTextGenerator getTextGenerator(){
        return this.textGenerator;
    }

    public int getBagIndex(Player player){

        if(!bagIndex.containsKey(player.getUniqueId())){
            bagIndex.put(player.getUniqueId(), 0);
        }

        return bagIndex.get(player.getUniqueId());
    }

    public void setBagIndex(Player player, Integer index){
        bagIndex.put(player.getUniqueId(), index);
    }

    public String addBagPng(String originalTitle){

        StringBuilder newTitle = new StringBuilder();

        newTitle.append(originalTitle);


        //negative space before this

        //-256
        newTitle.append("\uF80D");

        //+78
        newTitle.append("\uF82B\uF828\uF826");

        newTitle.append("\uE05C");

        return String.valueOf(newTitle);
    }

    public int getClassIndex(Player player){

        if(!classIndex.containsKey(player.getUniqueId())){
            classIndex.put(player.getUniqueId(), 0);
        }

        return classIndex.get(player.getUniqueId());
    }


    public void setClassIndex(Player player, Integer index){
        classIndex.put(player.getUniqueId(), index);
    }


    public int getDungeonIndex(Player player){

        if(!dungeonIndex.containsKey(player.getUniqueId())){
            dungeonIndex.put(player.getUniqueId(), 0);
        }

        return dungeonIndex.get(player.getUniqueId());
    }

    public void dungeonLeft(Player player){

        if(getDungeonIndex(player) <= 0){
            return;
        }

        setDungeonIndex(player, getDungeonIndex(player) - 1);
    }

    public void dungeonRight(Player player){

        //change this later when i add more dungeons
        if(getDungeonIndex(player) >= 3){
            return;
        }

        setDungeonIndex(player, getDungeonIndex(player) + 1);
    }

    public void setDungeonIndex(Player player, Integer index){
        dungeonIndex.put(player.getUniqueId(), index);
    }

    public int getPartyIndex(Player player){
        if(!partyIndex.containsKey(player.getUniqueId())){
            partyIndex.put(player.getUniqueId(), 0);
        }
        return partyIndex.get(player.getUniqueId());
    }

    public void setPartyIndex(Player player, Integer index){
        partyIndex.put(player.getUniqueId(), index);
    }

    public Role getRoleFilter(Player player){
        return roleFilter.getOrDefault(player.getUniqueId(), Role.None);
    }

    public void setRoleFilter(Player player, Role role){
        roleFilter.put(player.getUniqueId(), role);
    }

    public Role getRole(LivingEntity partyMember){

        SubClass subClass = profileManager.getAnyProfile(partyMember).getPlayerSubclass();

        switch (subClass){
            case Shepard, Divine -> {
                return Role.Healer;
            }
            case Gladiator, Blood, Templar ->{
                return Role.Tank;
            }
            default -> {
                return Role.Damage;
            }
        }

    }

    public void setEnhancementTypeIndex(Player player, EquipmentEnhancementType type){
        enhancementTypeIndex.put(player.getUniqueId(), type);
    }

    public EquipmentEnhancementType getEnhancementTypeIndex(Player player){

        if(!enhancementTypeIndex.containsKey(player.getUniqueId())){
            enhancementTypeIndex.put(player.getUniqueId(), EquipmentEnhancementType.Identify);
        }

        return enhancementTypeIndex.get(player.getUniqueId());
    }

}
