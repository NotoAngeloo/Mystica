package me.angeloo.mystica.Components.Inventories;

import me.angeloo.mystica.Components.ClassSkillItems.AllSkillItems;
import me.angeloo.mystica.Components.ProfileComponents.EquipSkills;
import me.angeloo.mystica.Utility.InventoryItemGetter;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class AbilityInventory implements Listener {

    private final ProfileManager profileManager;
    private final AllSkillItems allSkillItems;
    private final InventoryItemGetter inventoryItemGetter;
    private final SpecInventory specInventory;

    public AbilityInventory(Mystica main){
        profileManager = main.getProfileManager();
        allSkillItems = main.getAllSkillItems();
        inventoryItemGetter = main.getItemGetter();
        specInventory = new SpecInventory(main, this);
    }

    public Inventory openAbilityInventory(Player player, int selectedSlot){
        EquipSkills equipSkills = profileManager.getAnyProfile(player).getEquipSkills();

        //\uE064 is the inv itself,
        Inventory inv = Bukkit.createInventory(null, 9*6, ChatColor.WHITE + "\uF807" + "\uE064");

        inv.setItem(9, allSkillItems.getPlayerSkill(player, 1));
        inv.setItem(10, allSkillItems.getPlayerSkill(player, 2));
        inv.setItem(11, allSkillItems.getPlayerSkill(player, 3));
        inv.setItem(12, allSkillItems.getPlayerSkill(player, 4));
        inv.setItem(13, allSkillItems.getPlayerSkill(player, 5));
        inv.setItem(14, allSkillItems.getPlayerSkill(player, 6));
        inv.setItem(15, allSkillItems.getPlayerSkill(player, 7));
        inv.setItem(16, allSkillItems.getPlayerSkill(player, 8));

        //the select item
        if(selectedSlot != -1){

            ItemStack selector;

            if(selectedSlot >=0 && selectedSlot<=8){

                selector = inventoryItemGetter.getItem(Material.DIAMOND, 1, " ");

                inv.setItem(selectedSlot, selector);
            }

            if(selectedSlot>=9 && selectedSlot <=16){
                selector = inventoryItemGetter.getItem(Material.DIAMOND, 2, " ");
                inv.setItem(selectedSlot + 9, selector);
            }


        }


        inv.setItem(36, allSkillItems.getPlayerSkill(player, equipSkills.getAnySlot()[0]));
        inv.setItem(37, allSkillItems.getPlayerSkill(player, equipSkills.getAnySlot()[1]));
        inv.setItem(38, allSkillItems.getPlayerSkill(player, equipSkills.getAnySlot()[2]));
        inv.setItem(39, allSkillItems.getPlayerSkill(player, equipSkills.getAnySlot()[3]));
        inv.setItem(40, allSkillItems.getPlayerSkill(player, equipSkills.getAnySlot()[4]));
        inv.setItem(41, allSkillItems.getPlayerSkill(player, equipSkills.getAnySlot()[5]));
        inv.setItem(42, allSkillItems.getPlayerSkill(player, equipSkills.getAnySlot()[6]));
        inv.setItem(43, allSkillItems.getPlayerSkill(player, equipSkills.getAnySlot()[7]));
        inv.setItem(44, allSkillItems.getBasic(player));


        inv.setItem(49, allSkillItems.getUltimate(player));


        return inv;
    }

    @EventHandler
    public void abilityClicks(InventoryClickEvent event){
        if(event.getView().getTitle().contains(ChatColor.WHITE + "\uF807" + "\uE064")){
            event.setCancelled(true);

            Inventory topInv = event.getView().getTopInventory();

            if(event.getClickedInventory() != topInv){
                return;
            }

            Player player = (Player) event.getWhoClicked();

            int slot = event.getSlot();

            EquipSkills equipSkills = profileManager.getAnyProfile(player).getEquipSkills();

            List<Integer> skillSlots = new ArrayList<>();
            for(int i=9;i<=16;i++){
                skillSlots.add(i);
            }

            if(skillSlots.contains(slot)){
                slot-=9;

                if(equipSkills.whichSlotIsTheSkillEquippedIn(slot+1) != -1){
                    equipSkills.setAnySlot(equipSkills.whichSlotIsTheSkillEquippedIn(slot+1), 0);
                }

                player.openInventory(openAbilityInventory(player, slot));
                return;
            }

            List<Integer> equipSlots = new ArrayList<>();
            for(int i=36;i<=43;i++){
                equipSlots.add(i);
            }

            if(equipSlots.contains(slot)){
                //Bukkit.getLogger().info(String.valueOf(slot));

                int selectSlot = -1;
                ItemStack[] contents = topInv.getContents();

                for (int i = 0; i < contents.length; i++) {
                    if (contents[i] != null && contents[i].getType().equals(Material.DIAMOND)) {
                        selectSlot = i;
                        break;
                    }
                }

                if(selectSlot != -1){
                    if(selectSlot <=7){
                        int skillNumber = selectSlot+1;
                        int putTheSkillHere = slot - 36;
                        equipSkills.setAnySlot(putTheSkillHere, skillNumber);
                        player.openInventory(openAbilityInventory(player, -1));
                    }
                }

                if(selectSlot == -1){
                    int putTheSkillHere = slot - 36;
                    equipSkills.setAnySlot(putTheSkillHere, 0);
                    player.openInventory(openAbilityInventory(player, -1));
                }

            }

            List<Integer> pathSlots = new ArrayList<>();
            for(int i=51;i<=53;i++){
                pathSlots.add(i);
            }

            if(pathSlots.contains(slot)){
                player.openInventory(specInventory.openSpecInventory(player));
            }
        }
    }

    public SpecInventory getSpecInventory(){return specInventory;}

}
