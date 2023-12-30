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
        allSkillItems = new AllSkillItems(main);
    }

    public void initializeItems(Player player){

        EquipSkills equipSkills = profileManager.getAnyProfile(player).getEquipSkills();

        for(int i=0; i<=7; i++){
            int slot = player.getInventory().getHeldItemSlot();

            if (slot == i){
                continue;
            }

            int skillNumber = equipSkills.getAnySlot()[i];

            int cooldown = abilityManager.getCooldown(player, skillNumber);

            ItemStack abilityItem = allSkillItems.getPlayerSkill(player, skillNumber);

            if(cooldown > 0){
                abilityItem.setAmount(cooldown);
            }

            if(cooldown == 1 && !abilityItem.getType().equals(Material.AIR)){

                ItemMeta meta = abilityItem.getItemMeta();

                assert meta != null;
                int modelData = meta.getCustomModelData();
                modelData++;

                meta.setCustomModelData(modelData);
                abilityItem.setItemMeta(meta);
            }

            player.getInventory().setItem(i, abilityItem);
        }

        if(allSkillItems.getUltimate(player).getType() != Material.AIR){

            int slot = player.getInventory().getHeldItemSlot();

            if(slot == 8){
                return;
            }

            int cooldown = abilityManager.getUltimateCooldown(player);

            ItemStack ultimateItem = allSkillItems.getUltimate(player);

            if(cooldown > 0){
                ultimateItem.setAmount(cooldown);
            }

            if(cooldown == 1 && !ultimateItem.getType().equals(Material.AIR)){

                ItemMeta meta = ultimateItem.getItemMeta();

                assert meta != null;
                int modelData = meta.getCustomModelData();
                modelData++;

                meta.setCustomModelData(modelData);
                ultimateItem.setItemMeta(meta);
            }

            player.getInventory().setItem(8, ultimateItem);
        }
    }

    public void displayCooldown(Player player, int abilityNumber){

        boolean combatStatus = profileManager.getAnyProfile(player).getIfInCombat();

        if(!combatStatus){
            return;
        }

        int hotBarSlot = player.getInventory().getHeldItemSlot();

        //get skillslot too
        EquipSkills equipSkills = profileManager.getAnyProfile(player).getEquipSkills();
        int equippedIn = equipSkills.whichSlotIsTheSkillEquippedIn(abilityNumber);

        if(hotBarSlot == equippedIn){
            return;
        }

        ItemStack abilityItem = allSkillItems.getPlayerSkill(player, abilityNumber);
        int cooldown = abilityManager.getCooldown(player, abilityNumber);
        if(cooldown > 0){
            abilityItem.setAmount(cooldown);
        }

        if(cooldown == 1){

            ItemMeta meta = abilityItem.getItemMeta();

            assert meta != null;
            int modelData = meta.getCustomModelData();
            modelData++;

            meta.setCustomModelData(modelData);
            abilityItem.setItemMeta(meta);
        }

        player.getInventory().setItem(equippedIn, abilityItem);
    }

    public void displayUltimateCooldown(Player player){

        boolean combatStatus = profileManager.getAnyProfile(player).getIfInCombat();

        if(!combatStatus){
            return;
        }

        int hotBarSlot = player.getInventory().getHeldItemSlot();

        if(hotBarSlot == 8){
            return;
        }

        ItemStack abilityItem = allSkillItems.getUltimate(player);
        int cooldown = abilityManager.getUltimateCooldown(player);
        if(cooldown > 0){
            abilityItem.setAmount(cooldown);
        }

        if(cooldown == 1){

            ItemMeta meta = abilityItem.getItemMeta();

            assert meta != null;
            int modelData = meta.getCustomModelData();
            modelData++;

            meta.setCustomModelData(modelData);
            abilityItem.setItemMeta(meta);
        }

        player.getInventory().setItem(8, abilityItem);

    }

    public void displayChaosMysticUltimateItem(Player player, boolean ready){

        boolean combatStatus = profileManager.getAnyProfile(player).getIfInCombat();

        if(!combatStatus){
            return;
        }

        ItemStack abilityItem = allSkillItems.getUltimate(player);

        if(ready){

            ItemMeta meta = abilityItem.getItemMeta();

            assert meta != null;
            int modelData = meta.getCustomModelData();
            modelData++;

            meta.setCustomModelData(modelData);
            abilityItem.setItemMeta(meta);
        }

        player.getInventory().setItem(8, abilityItem);
    }

    public ItemStack getOldItem(Player player, int slot){

        EquipSkills equipSkills = profileManager.getAnyProfile(player).getEquipSkills();

        ItemStack abilityItem;

        boolean ultimate = false;

        if(slot == 8){
            abilityItem = allSkillItems.getUltimate(player);
            ultimate = true;
        }
        else{
            abilityItem = allSkillItems.getPlayerSkill(player, equipSkills.getAnySlot()[slot]);
        }

        if(abilityItem.getType().equals(Material.AIR)){
            return abilityItem;
        }

        int cooldown;

        if(ultimate){
            cooldown = abilityManager.getUltimateCooldown(player);
        }
        else{
            cooldown = abilityManager.getCooldown(player, equipSkills.getAnySlot()[slot]);
        }

        if(cooldown > 0){
            abilityItem.setAmount(cooldown);
        }

        if(cooldown == 1){

            ItemMeta meta = abilityItem.getItemMeta();

            assert meta != null;
            int modelData = meta.getCustomModelData();
            modelData++;

            meta.setCustomModelData(modelData);
            abilityItem.setItemMeta(meta);
        }


        return abilityItem;
    }

}
