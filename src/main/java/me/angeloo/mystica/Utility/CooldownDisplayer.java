package me.angeloo.mystica.Utility;

import me.angeloo.mystica.Components.ClassSkillItems.AllSkillItems;
import me.angeloo.mystica.Components.ProfileComponents.EquipSkills;
import me.angeloo.mystica.Managers.AbilityManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CooldownDisplayer {

    private final ProfileManager profileManager;
    private final AbilityManager abilityManager;
    private final AllSkillItems allSkillItems;

    public CooldownDisplayer(Mystica main, AbilityManager manager) {
        profileManager = main.getProfileManager();
        abilityManager = manager;
        allSkillItems = abilityManager.getAllSkillItems();
    }

    public void initializeItems(Player player){

        EquipSkills equipSkills = profileManager.getAnyProfile(player).getEquipSkills();

        for(int i=0; i<=7; i++){

            int skillNumber = equipSkills.getAnySlot()[i];

            int cooldown = abilityManager.getCooldown(player, skillNumber);
            int modelDataAddition = abilityManager.getModelDataAddition(player, skillNumber);

            ItemStack abilityItem = allSkillItems.getPlayerSkill(player, skillNumber);

            if(cooldown > 0){
                abilityItem.setAmount(cooldown);
            }

            if(!abilityItem.getType().equals(Material.AIR)){

                ItemMeta meta = abilityItem.getItemMeta();

                assert meta != null;
                int modelData = meta.getCustomModelData();
                modelData+=modelDataAddition;

                meta.setCustomModelData(modelData);
                abilityItem.setItemMeta(meta);
            }

            player.getInventory().setItem(i, abilityItem);
        }

    }

    public void displayCooldown(Player player, int abilityNumber){

        boolean deathStatus = profileManager.getAnyProfile(player).getIfDead();
        boolean combatStatus = profileManager.getAnyProfile(player).getIfInCombat();

        if(deathStatus || !combatStatus){
            return;
        }

        //get skillslot too
        EquipSkills equipSkills = profileManager.getAnyProfile(player).getEquipSkills();
        int equippedIn = equipSkills.whichSlotIsTheSkillEquippedIn(abilityNumber);

        ItemStack abilityItem = allSkillItems.getPlayerSkill(player, abilityNumber);
        int cooldown = abilityManager.getCooldown(player, abilityNumber);
        int modelDataAddition = abilityManager.getModelDataAddition(player, abilityNumber);

        if(cooldown > 0){
            abilityItem.setAmount(cooldown);
        }

        if(cooldown == 1){

            ItemMeta meta = abilityItem.getItemMeta();

            assert meta != null;
            int modelData = meta.getCustomModelData();
            modelData+=modelDataAddition;

            meta.setCustomModelData(modelData);
            abilityItem.setItemMeta(meta);
        }

        player.getInventory().setItem(equippedIn, abilityItem);
    }


}
