package me.angeloo.mystica.Components.Guis.Abilities;

import me.angeloo.mystica.Components.CombatSystem.ClassSkillItems.AllSkillItems;
import me.angeloo.mystica.Components.ProfileComponents.EquipSkills;
import me.angeloo.mystica.Utility.DisplayWeapons;
import me.angeloo.mystica.Utility.InventoryItemGetter;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
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
    private final DisplayWeapons displayWeapons;
    private final AllSkillItems allSkillItems;
    private final InventoryItemGetter inventoryItemGetter;
    private final SpecInventory specInventory;

    public AbilityInventory(Mystica main){
        profileManager = main.getProfileManager();
        allSkillItems = main.getAllSkillItems();
        inventoryItemGetter = main.getItemGetter();
        displayWeapons = main.getDisplayWeapons();
        specInventory = new SpecInventory(main, this);
    }

    public void openAbilityInventory(Player player, int selectedSlot){
        EquipSkills equipSkills = profileManager.getAnyProfile(player).getEquipSkills();

        //skill selector should be part of the title

        StringBuilder title = new StringBuilder(ChatColor.WHITE + "\uF807" + "\uE064");


        if(selectedSlot != -1){
            //here for testing
            //-128
            title.append("\uF80C");
            //-38
            title.append("\uF80A\uF806");

            String selectorUnicode = "\uE063";

            int column = selectedSlot;

            //depending if over a certain number, when i have more skills in future

            //+18 for each +1 slot
            title.append("\uF829\uF822".repeat(Math.max(0, column)));



            //change the selector unicode ascent for different rows
            title.append(selectorUnicode);
        }





        //\uE064 is the inv itself,
        Inventory inv = Bukkit.createInventory(null, 9*6, String.valueOf(title));

        for(int i = 0; i<55;i++){

            ItemStack abilityItem = allSkillItems.getPlayerSkill(player, i+1);

            if(abilityItem.getType() == Material.AIR){
                break;
            }

            inv.setItem(i, abilityItem);

        }



        player.openInventory(inv);
        player.getInventory().clear();
        displayWeapons.displayArmor(player);

        player.getInventory().setItem(27, allSkillItems.getPlayerSkill(player, equipSkills.getAnySlot()[0]));
        player.getInventory().setItem(28, allSkillItems.getPlayerSkill(player, equipSkills.getAnySlot()[1]));
        player.getInventory().setItem(29, allSkillItems.getPlayerSkill(player, equipSkills.getAnySlot()[2]));
        player.getInventory().setItem(30, allSkillItems.getPlayerSkill(player, equipSkills.getAnySlot()[3]));
        player.getInventory().setItem(31, allSkillItems.getPlayerSkill(player, equipSkills.getAnySlot()[4]));
        player.getInventory().setItem(32, allSkillItems.getPlayerSkill(player, equipSkills.getAnySlot()[5]));
        player.getInventory().setItem(33, allSkillItems.getPlayerSkill(player, equipSkills.getAnySlot()[6]));
        player.getInventory().setItem(34, allSkillItems.getPlayerSkill(player, equipSkills.getAnySlot()[7]));
        player.getInventory().setItem(35, allSkillItems.getBasic(player));



    }

    @EventHandler
    public void abilityClicks(InventoryClickEvent event){
        if(event.getView().getTitle().contains(ChatColor.WHITE + "\uF807" + "\uE064")){
            event.setCancelled(true);


            Player player = (Player) event.getWhoClicked();
            EquipSkills equipSkills = profileManager.getAnyProfile(player).getEquipSkills();
            int slot = event.getSlot();

            Inventory topInv = event.getView().getTopInventory();

            Inventory bottomInv = event.getView().getBottomInventory();

            if(event.getClickedInventory() == topInv){
                //add skills

                ItemStack item = event.getCurrentItem();

                if(item == null){
                    return;
                }

                if(equipSkills.whichSlotIsTheSkillEquippedIn(slot+1) != -1){
                    //Bukkit.getLogger().info("ability " + (slot + 1) + " is equipped in slot "+ equipSkills.whichSlotIsTheSkillEquippedIn(slot+1));
                    equipSkills.setAnySlot(equipSkills.whichSlotIsTheSkillEquippedIn(slot+1), 0);
                }

                openAbilityInventory(player, slot);

                return;
            }

            if(event.getClickedInventory() == bottomInv){
                //remove old skills and add new ones
                String title = event.getView().getTitle();
                String selectorUnicode = "\uE063";


                List<Integer> equipSlots = new ArrayList<>();
                for(int i=27;i<=34;i++){
                    equipSlots.add(i);
                }


                if(equipSlots.contains(slot)){

                    //check if putting in a skill or not
                    if(!title.contains(selectorUnicode)){
                        //remove
                        if(event.getCurrentItem() == null){
                            return;
                        }

                        //Bukkit.getLogger().info("ability " + (slot - 26) + " is equipped in slot "+ equipSkills.whichSlotIsTheSkillEquippedIn(slot-26));
                        equipSkills.setAnySlot(equipSkills.whichSlotIsTheSkillEquippedIn(slot-26), 0);
                        openAbilityInventory(player, -1);
                        return;
                    }


                    int selectSlot = -1;


                    //when i have different rows, the selector will be different depending

                    if(title.contains(selectorUnicode)){
                        String spacer = "\uF829\uF822";
                        int column = countOccurrences(title, spacer);
                        selectSlot = column;
                    }


                    if(selectSlot != -1){
                        int skillNumber = selectSlot+1;
                        int putTheSkillHere = slot - 27;
                        //Bukkit.getLogger().info("put skill " + skillNumber + " in slot " + putTheSkillHere);
                        equipSkills.setAnySlot(putTheSkillHere, skillNumber);
                        openAbilityInventory(player, -1);
                    }

                    return;
                }

                List<Integer> pathSlots = new ArrayList<>();
                pathSlots.add(8);
                pathSlots.add(7);
                pathSlots.add(6);

                if(pathSlots.contains(slot)){
                    specInventory.openSpecInventory(player);
                }

                return;
            }



        }
    }

    public SpecInventory getSpecInventory(){return specInventory;}

    private int countOccurrences(String text, String pattern) {
        int count = 0;
        int index = 0;

        while ((index = text.indexOf(pattern, index)) != -1) {
            count++;
            index += pattern.length();
        }

        return count;
    }

}
