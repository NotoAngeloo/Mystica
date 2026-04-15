package me.angeloo.mystica.Components.Hud;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.ProfileComponents.EquipSkills;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownDisplayCache {

    private final ProfileManager profileManager;
    private final AbilityManager abilityManager;

    private final Map<UUID, String> ultimateCache = new ConcurrentHashMap<>();
    private final Map<UUID, String> slotCache0 = new ConcurrentHashMap<>();
    private final Map<UUID, String> slotCache1 = new ConcurrentHashMap<>();
    private final Map<UUID, String> slotCache2 = new ConcurrentHashMap<>();
    private final Map<UUID, String> slotCache3 = new ConcurrentHashMap<>();
    private final Map<UUID, String> slotCache4 = new ConcurrentHashMap<>();
    private final Map<UUID, String> slotCache5 = new ConcurrentHashMap<>();
    private final Map<UUID, String> slotCache6 = new ConcurrentHashMap<>();
    private final Map<UUID, String> slotCache7 = new ConcurrentHashMap<>();

    public CooldownDisplayCache(Mystica main, AbilityManager manager){
        profileManager = main.getProfileManager();
        this.abilityManager = manager;
    }

    public void updateSkill(Player player, int slot){

        if(slot == -1){
            updateUltimate(player);
            return;
        }

        EquipSkills equipSkills = profileManager.getAnyProfile(player).getEquipSkills();

        int skillNumber = equipSkills.getAnySlot()[slot];

        //int cooldown =


    }

    private void updateUltimate(Player player){

    }


}
