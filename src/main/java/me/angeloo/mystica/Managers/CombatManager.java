package me.angeloo.mystica.Managers;

import me.angeloo.mystica.Components.ClassSkillItems.AllSkillItems;
import me.angeloo.mystica.Components.ProfileComponents.EquipSkills;
import me.angeloo.mystica.Components.ProfileComponents.PlayerEquipment;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Components.Profile;
import me.angeloo.mystica.Utility.DisplayWeapons;
import me.angeloo.mystica.Utility.StatusDisplayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;


import java.util.*;
import java.util.List;

public class CombatManager {

    private final Mystica main;
    private final ProfileManager profileManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final AbilityManager abilityManager;
    private final DpsManager dpsManager;
    private final AllSkillItems allSkillItems;


    private final StatusDisplayer statusDisplayer;

    private final Map<UUID, Long> lastCalledCombat = new HashMap<>();
    private final Map<UUID, BukkitTask> combatTickForThisPlayer = new HashMap<>();

    public CombatManager(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        abilityManager = manager;
        dpsManager = main.getDpsManager();
        allSkillItems = new AllSkillItems(main);
        statusDisplayer = new StatusDisplayer(main, manager);
    }


    public void startCombatTimer(Player player){

        boolean combatStatus = profileManager.getAnyProfile(player).getIfInCombat();

        if (!combatStatus){
            player.sendMessage("You are now in combat");

            profileManager.getAnyProfile(player).setSavedInv(player.getInventory().getContents());
            player.getInventory().clear();

            PlayerEquipment playerEquipment = profileManager.getAnyProfile(player).getPlayerEquipment();

            if(playerEquipment.getWeapon() != null){
                player.getInventory().setItemInMainHand(playerEquipment.getWeapon());
            }

            if (playerEquipment.getOffhand() != null){
                player.getInventory().setItemInOffHand(playerEquipment.getOffhand());
            }

            DisplayWeapons displayWeapons = new DisplayWeapons(main);
            displayWeapons.displayArmor(player);


            player.getInventory().setItem(13, getItem(new ItemStack(Material.BARRIER), "Exit Combat"));
        }

        profileManager.getAnyProfile(player).setIfInCombat(true);
        lastCalledCombat.put(player.getUniqueId(), System.currentTimeMillis());
        startCombatTick(player);
    }

    private void startCombatTick(Player player){

        if(combatTickForThisPlayer.containsKey(player.getUniqueId())){
            return;
        }

        BukkitTask combatTask = new BukkitRunnable() {

            int clock = 0;

            @Override
            public void run() {

                boolean combatStatus = profileManager.getAnyProfile(player).getIfInCombat();

                if(!combatStatus){
                    this.cancel();
                    combatTickForThisPlayer.remove(player.getUniqueId());
                    return;
                }

                //do a thing once a second
                displayPlayerHealthPlusInfo(player);
                setPlayerAbilityItems(player);

                statusDisplayer.displayStatus(player);

                clock += 1;

                //every 20 in game ticks
                if(clock >= 10){
                    dpsManager.addPlayerSecondsInCombat(player);
                    clock = 0;
                }

            }
        }.runTaskTimer(main, 0, 2);

        combatTickForThisPlayer.put(player.getUniqueId(), combatTask);

    }


    private long getLastCalledCombat(Player player){

        if(!lastCalledCombat.containsKey(player.getUniqueId())){
            lastCalledCombat.put(player.getUniqueId(), System.currentTimeMillis());
        }

        return lastCalledCombat.get(player.getUniqueId());
    }


    //should happen every tick of cooldown
    private void setPlayerAbilityItems(Player player){


        boolean deathStatus = profileManager.getAnyProfile(player).getIfDead();

        if(deathStatus){
            return;
        }

        ItemStack weapon = player.getInventory().getItemInMainHand();

        if(weapon.getAmount() > 1){
            weapon.setAmount(1);
        }

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

    public ItemStack getOldItem(Player player, int slot){


        EquipSkills equipSkills = profileManager.getAnyProfile(player).getEquipSkills();

        ItemStack abilityItem;

        if(slot == 8){
            abilityItem = allSkillItems.getUltimate(player);
        }
        else{
            abilityItem = allSkillItems.getPlayerSkill(player, equipSkills.getAnySlot()[slot]);
        }

        return abilityItem;
    }


    //should happen every time health is changed OR cooldown updates
    public void displayPlayerHealthPlusInfo(Player player){

        StringBuilder fullBar = new StringBuilder();

        String shieldString = getShieldString(player);
        String manaString = getManaBar(player);
        String statusString = ChatColor.GRAY + getPlayerStatus(player);

        String colorlessString = statusString.replaceAll("§.", "");

        int amountStatusChar = colorlessString.length();
        //make sure the bar is always 25 chars long
        int leftToSpaceOnEachSide = 25 - amountStatusChar;

        /*if(leftToSpaceOnEachSide%2!=0){
            leftToSpaceOnEachSide = leftToSpaceOnEachSide + 1;
        }*/

        leftToSpaceOnEachSide = leftToSpaceOnEachSide/2;

        StringBuilder fillerString = new StringBuilder();
        for(int i=0;i<leftToSpaceOnEachSide;i++){
            fillerString.append(" ");
        }

        //replace " " with fillerString
        fullBar.append(shieldString).append(fillerString).append(statusString).append(fillerString).append(manaString);

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(String.valueOf(fullBar)));
    }

    private String getShieldString(Player player){

        Profile playerProfile = profileManager.getAnyProfile(player);

        double maxHp = playerProfile.getTotalHealth();
        double shieldAmount = buffAndDebuffManager.getGenericShield().getCurrentShieldAmount(player);
        int percent = (int) Math.floor((shieldAmount/maxHp) * 100);

        StringBuilder shieldBar = new StringBuilder().append(ChatColor.YELLOW);

        for(int i = 0; i<15 ; i++){
            if(percent > (i*((double) 100/15))){
                shieldBar.append("||");
            }
            else{
                shieldBar.append(" ");
            }
        }

        return String.valueOf(shieldBar);
    }


    private String getManaBar(Player player){

        Profile playerProfile = profileManager.getAnyProfile(player);

        double maxMp = playerProfile.getTotalMana();
        double currentMp = playerProfile.getCurrentMana();
        int percent = (int) Math.floor((currentMp/maxMp) * 100);

        StringBuilder manaBar = new StringBuilder().append(ChatColor.BLUE);

        for(int i = 0; i<15 ; i++){
            if(percent > (i*((double) 100/15))){
                manaBar.append("||");
            }
            else{
                manaBar.append(" ");
            }
        }

        return String.valueOf(manaBar);
    }

    private String getPlayerStatus(Player player){

        int hotBarSlot = player.getInventory().getHeldItemSlot();

        int cooldown;


        EquipSkills equipSkills = profileManager.getAnyProfile(player).getEquipSkills();

        if(hotBarSlot == 8){

            if(!allSkillItems.getUltimate(player).hasItemMeta()){
                return " ";
            }

            cooldown = abilityManager.getUltimateCooldown(player);

            if(cooldown <= 0){

                ItemStack ultimateItem = allSkillItems.getUltimate(player);

                if(ultimateItem.getType().equals(Material.AIR)){
                    return " ";
                }

                String abilityName = ultimateItem.getItemMeta().getDisplayName();

                abilityName = abilityName.replaceAll("§.", "");


                return abilityName;
            }

            return String.valueOf(cooldown);
        }

        int abilityNumber = equipSkills.getAnySlot()[hotBarSlot];

        if(abilityNumber <=0){
            return " ";
        }

        cooldown = abilityManager.getCooldown(player, abilityNumber);

        if(cooldown <=0){

            String abilityName = allSkillItems.getPlayerSkill(player, abilityNumber).getItemMeta().getDisplayName();
            abilityName = abilityName.replaceAll("§.", "");

            return abilityName;
        }

        return String.valueOf(cooldown);

    }


    public void tryToEndCombat(Player player){

        long currentTime = System.currentTimeMillis();
        long lastCalled = getLastCalledCombat(player);

        if(currentTime - lastCalled > 10000){
            forceCombatEnd(player);
        }
        else{
            player.sendMessage(((10000 -(currentTime - lastCalled))/1000) + " seconds left");
        }

    }

    public void forceCombatEnd(Player player){

        profileManager.getAnyProfile(player).setIfInCombat(false);

        player.getInventory().clear(13);

        player.sendMessage("You are no longer in combat");

        //and restore their inventories

        if(!profileManager.getAnyProfile(player).getIfDead()){

            player.setInvisible(false);

            player.getInventory().clear();

            ItemStack[] savedInv = profileManager.getAnyProfile(player).getSavedInv();

            boolean allNull = true;
            for(ItemStack item : savedInv){
                if(item != null){
                    allNull = false;
                    break;
                }
            }

            if(!allNull){
                player.getInventory().setContents(savedInv);
                profileManager.getAnyProfile(player).removeSavedInv();
            }
        }

        dpsManager.removeDps(player);
        abilityManager.resetAbilityBuffs(player);
    }

    private ItemStack getItem(ItemStack item, String name, String... lore){
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        List<String> lores = new ArrayList<>();

        for (String s : lore){
            lores.add(ChatColor.translateAlternateColorCodes('&', s));

        }
        meta.setLore(lores);
        item.setItemMeta(meta);
        return item;
    }



}
