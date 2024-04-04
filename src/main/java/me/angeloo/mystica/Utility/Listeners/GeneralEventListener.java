package me.angeloo.mystica.Utility.Listeners;

import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.interfaces.PartiesAPI;
import com.alessiodp.parties.api.interfaces.Party;
import com.alessiodp.parties.api.interfaces.PartyPlayer;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.angeloo.mystica.Components.Inventories.AbilityInventory;
import me.angeloo.mystica.Components.Inventories.BagInventory;
import me.angeloo.mystica.Components.Inventories.EquipmentInventory;
import me.angeloo.mystica.Components.Items.PathToolItem;
import me.angeloo.mystica.Components.Items.RezItem;
import me.angeloo.mystica.Components.ProfileComponents.EquipSkills;
import me.angeloo.mystica.Components.ProfileComponents.NonPlayerStuff.Yield;
import me.angeloo.mystica.CustomEvents.*;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Tasks.AggroTick;
import me.angeloo.mystica.Utility.*;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.*;

public class GeneralEventListener implements Listener {

    private final Mystica main;
    private final ProfileManager profileManager;
    private final PathingManager pathingManager;
    private final StealthTargetBlacklist stealthTargetBlacklist;
    private final AggroTick aggroTick;
    private final AggroManager aggroManager;
    private final PvpManager pvpManager;
    private final TargetManager targetManager;
    private final CombatManager combatManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final AbilityManager abilityManager;
    private final DeathManager deathManager;
    private final InventoryIndexingManager inventoryIndexingManager;
    private final EquipmentInventory equipmentInventory;
    private final AbilityInventory abilityInventory;
    private final EquipmentInformation equipmentInformation;
    private final DisplayWeapons displayWeapons;
    private final StatusDisplayer statusDisplayer;
    private final ShieldAbilityManaDisplayer shieldAbilityManaDisplayer;
    private final GearReader gearReader;
    private final BagInventory bagInventory;
    private final ClassSetter classSetter;
    private final DamageHealthBoard damageHealthBoard;
    private final CustomItemConverter customItemConverter;
    private final Locations locations;

    private final DamageCalculator damageCalculator;
    private final ChangeResourceHandler changeResourceHandler;

    private final FirstClearManager firstClearManager;

    private final Map<UUID, BukkitTask> countdownTasks = new HashMap<>();
    private final Map<UUID, Boolean> dropCheck = new HashMap<>();

    private final Map<UUID, Long> breakawayCooldown = new HashMap<>();
    private final Map<UUID, Long> damageSoundCooldown = new HashMap<>();

    public GeneralEventListener(Mystica main){
        this.main = main;
        profileManager = main.getProfileManager();
        pathingManager = main.getPathingManager();
        stealthTargetBlacklist = main.getStealthTargetBlacklist();
        aggroTick = main.getAggroTick();
        aggroManager = main.getAggroManager();
        pvpManager = main.getPvpManager();
        targetManager = main.getTargetManager();
        combatManager = main.getCombatManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        abilityManager = main.getAbilityManager();
        deathManager = main.getDeathManager();
        inventoryIndexingManager = main.getInventoryIndexingManager();
        equipmentInventory = new EquipmentInventory(main);
        abilityInventory = new AbilityInventory(main);
        equipmentInformation = new EquipmentInformation();
        displayWeapons = new DisplayWeapons(main);
        statusDisplayer = new StatusDisplayer(main, abilityManager);
        shieldAbilityManaDisplayer = new ShieldAbilityManaDisplayer(main, abilityManager);
        damageCalculator = main.getDamageCalculator();
        changeResourceHandler = main.getChangeResourceHandler();
        bagInventory = main.getBagInventory();
        gearReader = new GearReader(main);
        classSetter = new ClassSetter(main);
        damageHealthBoard = main.getDamageHealthBoard();
        customItemConverter = new CustomItemConverter();
        firstClearManager = main.getFirstClearManager();
        locations = new Locations(main);
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event){
        for(Player player : Bukkit.getOnlinePlayers()){
            player.closeInventory();
        }

        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {

                if(entity instanceof LivingEntity){
                    if(entity instanceof Player){
                        continue;
                    }

                    entity.remove();
                }


                if(entity.getType() == EntityType.TEXT_DISPLAY){
                    entity.remove();
                }
            }


        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        profileManager.getAnyProfile(player);

        if(!profileManager.getPlayerNameMap().containsKey(player.getName())){

            AttributeInstance maxHealthAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            assert maxHealthAttribute != null;

            if(maxHealthAttribute.getBaseValue() != 20){
                maxHealthAttribute.setBaseValue(20);
            }

            player.setHealth(20);
            player.setGlowing(false);
            player.setInvisible(false);
            player.setFireTicks(0);
            player.setVisualFire(false);

            ItemStack[] savedInv = profileManager.getAnyProfile(player).getSavedInv();

            boolean allNull = true;
            for(ItemStack item : savedInv){
                if(item != null){
                    allNull = false;
                    break;
                }
            }

            boolean hasBadItem = false;

            PlayerInventory playerInventory = player.getInventory();

            if(playerInventory.contains(Material.BARRIER)){
                hasBadItem = true;
            }

            ItemStack rezItem = customItemConverter.convert(new RezItem(), 1);

            if(playerInventory.contains(rezItem)){
                hasBadItem = true;
            }

            if(!allNull || hasBadItem){
                player.getInventory().setContents(savedInv);
                profileManager.getAnyProfile(player).removeSavedInv();
            }

        }

        /*if(profileManager.getAnyProfile(player).getPlayerClass().equalsIgnoreCase("None")){
            player.openInventory(new ClassSelectInventory().openClassSelect("none"));
        }*/

        profileManager.addToPlayerNameMap(player);
        inventoryIndexingManager.innitBagIndex(player);
        inventoryIndexingManager.innitClassIndex(player);
        targetManager.setPlayerTarget(player, null);
        buffAndDebuffManager.removeAllBuffsAndDebuffs(player);
        gearReader.setGearStats(player);


        new BukkitRunnable(){
            @Override
            public void run(){

                if(profileManager.getAnyProfile(player).getMilestones().getMilestone("tutorial")
                && !profileManager.getAnyProfile(player).getMilestones().getMilestone("firstdungeon")
                && !player.getWorld().getName().startsWith("tutorial_")){
                    Bukkit.getServer().getPluginManager().callEvent(new HelpfulHintEvent(player, "npcspeak"));
                }

            }
        }.runTaskLater(main, 60);

    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){

        Player player = event.getPlayer();

        boolean combatStatus = profileManager.getAnyProfile(player).getIfInCombat();
        boolean deathStatus = profileManager.getAnyProfile(player).getIfDead();

        if(combatStatus){
            if(!deathStatus){
                deathManager.playerNowDead(player);
            }
        }

        targetManager.getPlayerTarget(player);

        ArrayList<ItemStack> allPlayerItems = profileManager.getAnyProfile(player).getPlayerBag().getItems();
        ArrayList<ItemStack> itemsMinusTemp = new ArrayList<>();
        for(ItemStack item : allPlayerItems){
            if(item.getType() == Material.AIR){
                continue;
            }
            itemsMinusTemp.add(item);
        }
        profileManager.getAnyProfile(player).getPlayerBag().setItems(itemsMinusTemp);

        for(Map.Entry<UUID, LivingEntity> entry: targetManager.getTargetMap().entrySet()){
            UUID playerID = entry.getKey();
            Player thisPlayer = Bukkit.getPlayer(playerID);
            Entity target = entry.getValue();

            if(target != null && target.equals(player)){
                assert thisPlayer != null;
                targetManager.removeAllBars(thisPlayer);

            }
        }

        targetManager.setPlayerTarget(player, null);

        if(player.getWorld().getName().startsWith("tutorial_")){
            //remove class
            if(!profileManager.getAnyProfile(player).getMilestones().getMilestone("tutorial")){
                classSetter.setClass(player, "none");
            }
        }
    }

    //maybe ill make items to be added to a bag, when implemented. just to stop items being deleted when combat end
    @EventHandler
    public void noItemPickup(EntityPickupItemEvent event){
        if(event.getEntity() instanceof Player){
            Player player = (Player) event.getEntity();

            boolean combatStatus = profileManager.getAnyProfile(player).getIfInCombat();
            boolean deathStatus = profileManager.getAnyProfile(player).getIfDead();

            if(combatStatus){
                event.setCancelled(true);
            }

            if(deathStatus){
                event.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void noBreakBlocks(BlockBreakEvent event){
        Player player =  event.getPlayer();

        if(player.getGameMode() == GameMode.CREATIVE){
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void displayPathsWithTool(PlayerToggleSneakEvent event){
        Player player = event.getPlayer();

        if(!player.isOp()){
            return;
        }

        if(player.getGameMode() != GameMode.CREATIVE) {
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();

        ItemStack pathTool = new CustomItemConverter().convert(new PathToolItem(), 1);

        if(!item.isSimilar(pathTool)){
            return;
        }

        pathingManager.displayAllNearbyPaths(player);
    }

    @EventHandler
    public void onPathTool(PlayerInteractEvent event){

        Player player = event.getPlayer();

        if(!player.isOp()){
            return;
        }

        if(player.getGameMode() != GameMode.CREATIVE) {
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();

        ItemStack pathTool = new CustomItemConverter().convert(new PathToolItem(), 1);

        if(!item.isSimilar(pathTool)){
            return;
        }

        event.setCancelled(true);

        //depending on click add to list
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
            Block block = event.getClickedBlock();
            assert block != null;
            Location location = block.getLocation();
            pathingManager.createPath(location);
            pathingManager.displayAllNearbyPaths(player);
            return;
        }

        if(event.getAction() == Action.LEFT_CLICK_BLOCK){
            Block block = event.getClickedBlock();
            assert block != null;
            Location location = block.getLocation();
            pathingManager.deletePath(location);
            pathingManager.displayAllNearbyPaths(player);
            return;
        }

    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();

        if(player.getGameMode() == GameMode.CREATIVE){
            return;
        }

        if (event.getAction() == Action.PHYSICAL && event.getClickedBlock().getType() == Material.FARMLAND) {
            event.setCancelled(true);
        }

        if(player.getInventory().getItemInMainHand().getType().equals(Material.BOW) || player.getInventory().getItemInOffHand().getType().equals(Material.BOW)){
            event.setCancelled(true);
        }

        if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
            //figure out allows here

            Block block = event.getClickedBlock();
            if(block != null){
                Material blockType = block.getType();

                //maybe have a list later
                if(blockType == Material.DARK_OAK_SIGN){
                    return;
                }
            }

            event.setCancelled(true);
        }


    }

    @EventHandler
    public void noEquipArmor(PlayerInteractEvent event) {

        Player player = event.getPlayer();

        if(player.getGameMode().equals(GameMode.CREATIVE)){
            return;
        }

        ItemStack itemInMain = player.getInventory().getItemInMainHand();
        boolean combatStatus = profileManager.getAnyProfile(player).getIfInCombat();

        if (!combatStatus) {

            List<Material> validEquipment = equipmentInformation.getAllEquipmentTypes();
            Material itemType = itemInMain.getType();

            if(!validEquipment.contains(itemType)){
                return;
            }

            player.openInventory(equipmentInventory.openEquipmentInventory(player, null, false));
            event.setCancelled(true);
            player.getInventory().setHelmet(null);
            player.getInventory().setChestplate(null);
            player.getInventory().setLeggings(null);
            player.getInventory().setBoots(null);
        }

    }

    @EventHandler
    public void noPlaceBlocks(BlockPlaceEvent event){
        Player player = event.getPlayer();

        if(player.getGameMode() == GameMode.CREATIVE){
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void menuInitialization(PlayerJoinEvent event){
        Player player = event.getPlayer();
        setMenuItems(player.getOpenInventory().getTopInventory());
    }

    @EventHandler
    public void menuClose(InventoryCloseEvent event){
        String title = event.getView().getTitle();

        if(title.equalsIgnoreCase("crafting")){
            event.getView().getTopInventory().clear();
        }

        Player player = (Player) event.getPlayer();


        new BukkitRunnable(){
            @Override
            public void run(){

                if(player.getOpenInventory().getTitle().equalsIgnoreCase("crafting")
                        && player.getGameMode().equals(GameMode.SURVIVAL)){

                    this.cancel();
                    Inventory crafting = event.getView().getTopInventory();
                    setMenuItems(crafting);
                    player.updateInventory();
                }

            }
        }.runTaskTimer(main, 0, 20);

    }

    private void setMenuItems(Inventory inventory){

        inventory.setItem(1, getItem(Material.OAK_SAPLING, 0, ChatColor.of(new java.awt.Color(0,102,0)) + "Skills",
                "click to open skill menu"));
        inventory.setItem(2, getItem(Material.CHEST, 0, ChatColor.of(new java.awt.Color(176, 159, 109)) + "Bag",
                "click to open bag"));
        inventory.setItem(3, getItem(Material.PAPER, 0, "Coming Soon",
                "to own on dvd and vhs"));
        inventory.setItem(4, getItem(Material.SKELETON_SKULL, 0, ChatColor.of(new java.awt.Color(176, 0, 0)) + "Stuck",
                "click to teleport to your spawn"));
    }

    private ItemStack getItem(Material material, int modelData, String name, String ... lore){

        ItemStack item = new ItemStack(material);

        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        List<String> lores = new ArrayList<>();

        for (String s : lore){
            lores.add(ChatColor.translateAlternateColorCodes('&', s));
        }

        meta.setLore(lores);
        meta.setCustomModelData(modelData);

        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void menuClick(InventoryClickEvent event){
        Inventory clickedInv = event.getClickedInventory();
        if (clickedInv == null) {
            return;
        }

        String title = event.getView().getTitle();

        if(!title.equalsIgnoreCase("crafting")){
            return;
        }

        if(clickedInv.getType() == InventoryType.PLAYER){
            return;
        }

        Player player = (Player) event.getWhoClicked();

        ItemStack item = player.getItemOnCursor();
        ItemStack tempItem = item.clone();
        event.setCancelled(true);
        player.setItemOnCursor(null);
        player.getInventory().addItem(tempItem);


        switch (event.getSlot()){
            case 1:{
                player.openInventory(abilityInventory.openAbilityInventory(player, new ItemStack(Material.AIR), false));
                break;
            }
            case 2:{
                player.openInventory(bagInventory.openBagInventory(player, inventoryIndexingManager.getBagIndex(player)));
                break;
            }
            case 4:{
                if(profileManager.getAnyProfile(player).getIfInCombat()){
                    player.sendMessage("cannot use in combat");
                    return;
                }

                if(profileManager.getAnyProfile(player).getIfDead()){
                    player.sendMessage("cannot use while dead");
                    return;
                }

                if(breakawayCooldown.get(player.getUniqueId()) == null){
                    breakawayCooldown.put(player.getUniqueId(), (System.currentTimeMillis() / 1000) - 300);
                }

                long currentTime = System.currentTimeMillis() / 1000;
                if(currentTime - breakawayCooldown.get(player.getUniqueId()) < 300){
                    player.sendMessage("command on cooldown");
                    return;
                }
                breakawayCooldown.put(player.getUniqueId(), currentTime);

                player.teleport(player.getWorld().getSpawnLocation());
                break;
            }
        }


    }

    @EventHandler
    public void noOffHandEquip(InventoryClickEvent event){
        Inventory clickedInv = event.getClickedInventory();
        if (clickedInv == null) {
            return;
        }

        if(clickedInv.getType() != InventoryType.PLAYER){
            return;
        }

        String title = event.getView().getTitle();

        if(!title.equalsIgnoreCase("crafting")){
            return;
        }

        Player player = (Player) event.getWhoClicked();

        if(player.getGameMode().equals(GameMode.CREATIVE)){
            return;
        }

        if(event.getClick().equals(ClickType.SWAP_OFFHAND)){
            event.setCancelled(true);
        }

        if(event.getSlotType() != InventoryType.SlotType.QUICKBAR){
            return;
        }

        if(event.getSlot() == 40){
            event.setCancelled(true);
            ItemStack item = player.getItemOnCursor();
            ItemStack tempItem = item.clone();
            event.setCancelled(true);
            player.setItemOnCursor(null);
            player.openInventory(equipmentInventory.openEquipmentInventory(player, new ItemStack(Material.AIR), false));
            player.getInventory().addItem(tempItem);
        }
    }

    @EventHandler
    public void noArmorEquip(InventoryClickEvent event){

        Inventory clickedInv = event.getClickedInventory();
        if (clickedInv == null) {
            return;
        }
        Player player = (Player) event.getWhoClicked();

        if(event.getSlotType() == InventoryType.SlotType.ARMOR){
            ItemStack item = player.getItemOnCursor();

            event.setCancelled(true);
            ItemStack tempItem = item.clone();
            event.setCancelled(true);
            player.setItemOnCursor(null);
            player.getInventory().setHelmet(null);
            player.getInventory().setChestplate(null);
            player.getInventory().setLeggings(null);
            player.getInventory().setBoots(null);

            player.openInventory(equipmentInventory.openEquipmentInventory(player, null, false));
            player.getInventory().addItem(tempItem);
            return;
        }

        if(clickedInv.getType() == InventoryType.PLAYER){

            String title = event.getView().getTitle();

            if(!title.equalsIgnoreCase("crafting")){
                return;
            }

            ClickType clickType = event.getClick();
            if(clickType.isShiftClick()){
                ItemStack item = event.getCurrentItem();
                if (item.getType().name().endsWith("_HELMET") ||
                        item.getType().name().endsWith("_CHESTPLATE") ||
                        item.getType().name().endsWith("_LEGGINGS") ||
                        item.getType().name().endsWith("_BOOTS")) {


                    ItemStack tempItem = item.clone();
                    player.getInventory().setItem(event.getSlot(), null);
                    player.getInventory().setHelmet(null);
                    player.getInventory().setChestplate(null);
                    player.getInventory().setLeggings(null);
                    player.getInventory().setBoots(null);

                    player.openInventory(equipmentInventory.openEquipmentInventory(player, null, false));
                    player.getInventory().addItem(tempItem);
                }
            }
        }
    }


    @EventHandler
    public void noOpenInvCombatOrDead(InventoryOpenEvent event){
        Player player = (Player) event.getPlayer();

        boolean combatStatus = profileManager.getAnyProfile(player).getIfInCombat();
        boolean deathStatus = profileManager.getAnyProfile(player).getIfDead();

        if(!combatStatus){
            return;
        }

        if(!deathStatus){
            return;
        }
        event.setCancelled(true);
        player.sendMessage("you can't do that right now");
        player.closeInventory();
    }

    @EventHandler
    public void noCombatInvInteraction(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();

        boolean combatStatus = profileManager.getAnyProfile(player).getIfInCombat();
        if(!combatStatus){
            return;
        }
        event.setCancelled(true);

        ItemStack item = event.getCurrentItem();

        if(item != null && item.hasItemMeta()){

            ItemMeta meta = item.getItemMeta();

            assert meta != null;
            String name = meta.getDisplayName();

            if(name.equalsIgnoreCase("Exit Combat")){
                combatManager.tryToEndCombat(player);
                return;
            }

        }

        player.closeInventory();
        player.sendMessage("you can't do that right now");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        Player player = event.getEntity();

        event.setKeepInventory(true);
        event.setKeepLevel(true);
        event.getDrops().clear();
        event.setDeathMessage(null);

        event.setDroppedExp(0);

        for(Map.Entry<UUID, LivingEntity> entry: targetManager.getTargetMap().entrySet()){

            Player thisPlayer = Bukkit.getPlayer(entry.getKey());

            Entity target = entry.getValue();

            if(target == player){
                assert thisPlayer != null;
                targetManager.updateTargetBar(thisPlayer);
            }
        }

        deathManager.playerNowDead(player);

    }

    @EventHandler
    public void normalEntityDeathDeath(EntityDeathEvent event){
        event.getDrops().clear();
        event.setDroppedExp(0);

        LivingEntity entity = event.getEntity();

        if(entity instanceof Player){
            return;
        }

        Player lastPlayer = aggroManager.getLastPlayer(entity);

        if(lastPlayer == null){
            return;
        }

        Bukkit.getServer().getPluginManager().callEvent(new CustomDeathEvent(lastPlayer, entity));

    }

    @EventHandler
    public void customDeathEvent(CustomDeathEvent event){

        Player player = event.getPlayerWhoKilled();

        LivingEntity entity = event.getEntityWhoDied();

        Yield yield = profileManager.getAnyProfile(entity).getYield();

        float xpYield = yield.getXpYield();
        List<ItemStack> itemDrops = yield.getItemYield();

        Set<Player> victors = new HashSet<>();

        PartiesAPI api = Parties.getApi();
        PartyPlayer partyPlayer = api.getPartyPlayer(player.getUniqueId());
        assert partyPlayer != null;
        if(partyPlayer.isInParty()){

            Party party = api.getParty(partyPlayer.getPartyId());

            assert party != null;
            Set<UUID> partyMemberList = party.getMembers();

            //List<Player> partyList = new ArrayList<>();

            for(UUID partyMemberId : partyMemberList){
                Player partyMember = Bukkit.getPlayer(partyMemberId);

                if(partyMember==null){
                    continue;
                }

                changeResourceHandler.addXpToPlayer(partyMember, (xpYield / partyMemberList.size()));
                bagInventory.addItemsToPlayerBagByPickup(partyMember, itemDrops);
                victors.add(partyMember);
            }

        }
        else {
            changeResourceHandler.addXpToPlayer(player, xpYield);
            bagInventory.addItemsToPlayerBagByPickup(player, itemDrops);
            victors.add(player);
        }



        //check bosshomes
        if(profileManager.getIfEntityIsBoss(entity.getUniqueId())){

            //check if mm too
            if(MythicBukkit.inst().getAPIHelper().isMythicMob(entity.getUniqueId())){
                String bossType = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(entity).getMobType();
                String bossName = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(entity).getDisplayName();

                int level = profileManager.getAnyProfile(entity).getStats().getLevel();

                //check if the boss has been cleared at this level yet
                if(!firstClearManager.getIfBossHasBeenClearedAtThisLevel(bossType, level)){

                    //build a string
                    StringBuilder announcement = new StringBuilder();

                    announcement.append(ChatColor.of(new java.awt.Color(127, 0, 255))).append("Server First Clear!\n");
                    announcement.append(ChatColor.RESET).append("Congratulations to ");

                    for(Player victor : victors){
                        announcement.append(ChatColor.of(new java.awt.Color(0, 102, 204)));
                        announcement.append(victor.getName()).append(" ");
                    }

                    announcement.append(ChatColor.RESET);
                    announcement.append("for defeating ");
                    announcement.append(ChatColor.of(new java.awt.Color(102, 0, 0)));
                    announcement.append(bossName);
                    announcement.append(ChatColor.RESET);
                    announcement.append(" at level ");
                    announcement.append(ChatColor.of(new java.awt.Color(0, 102, 0)));
                    announcement.append(level);

                    Bukkit.getServer().broadcastMessage(String.valueOf(announcement));

                    //and mark it as cleared
                    firstClearManager.markCleared(bossType, level, victors);

                    //perhaps give all the players an achievement as well
                    //somthing something victors
                }

            }



        }

    }

    @EventHandler
    public void teleportPlayer(PlayerInteractEvent event){
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        boolean deathStatus = profileManager.getAnyProfile(player).getIfDead();

        if(deathStatus){
            return;
        }

        if(item == null){
            return;
        }

        if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK){
            return;
        }


        String colorlessName = item.getItemMeta().getDisplayName().replaceAll("§.", "");
        ItemStack singleItem = customItemConverter.convert(item, 1);

        //add a cooldown
        switch (colorlessName.toLowerCase()){
            case "teleport: stonemont":{
                event.setCancelled(true);
                player.getInventory().remove(singleItem);
                player.teleport(locations.stonemont());
                break;
            }
            case "teleport: cave of the lindwyrm":{
                event.setCancelled(true);
                player.getInventory().remove(singleItem);
                player.teleport(locations.caveOfLindwyrm());
                break;
            }
            case "teleport: windbluff prison":{
                event.setCancelled(true);
                player.getInventory().remove(singleItem);
                player.teleport(locations.windbluff());
                break;
            }
            case "teleport: traders outpost":{
                event.setCancelled(true);
                player.getInventory().remove(singleItem);
                player.teleport(locations.outpost());
                break;
            }
        }
    }

    @EventHandler
    public void rezPlayer(PlayerInteractEvent event){

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        boolean deathStatus = profileManager.getAnyProfile(player).getIfDead();

        if(!deathStatus){
            return;
        }

        if(item == null){
            return;
        }

        if(!item.isSimilar(customItemConverter.convert(new RezItem(), 1))) {
            return;
        }

        if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK){
            return;
        }

        event.setCancelled(true);

        PartiesAPI api = Parties.getApi();
        PartyPlayer partyPlayer = api.getPartyPlayer(player.getUniqueId());

        assert partyPlayer != null;
        if(partyPlayer.isInParty()){
            Party party = api.getParty(partyPlayer.getPartyId());

            assert party != null;
            Set<UUID> partyMemberList = party.getMembers();

            party.getMembers().remove(partyPlayer.getPartyId());

            //excludes the player
            for(UUID memberID : partyMemberList){

                //exclude dead players from this
                Player member = Bukkit.getPlayer(memberID);

                if(member == null){
                    continue;
                }

                boolean partyMemberDeathStatus = profileManager.getAnyProfile(member).getIfDead();

                if(partyMemberDeathStatus){
                    continue;
                }

                boolean partyMemberCombatStatus = profileManager.getAnyProfile(member).getIfInCombat();

                if(partyMemberCombatStatus){
                    event.setCancelled(true);
                    
                    if(combatManager.canLeaveCombat(member)){
                        //request them
                        member.sendMessage(ChatColor.of(new java.awt.Color(0, 153, 0)) + player.getName() + ChatColor.RESET + " requests that you exit combat.");
                    }

                    return;
                }
            }
        }

        deathManager.playerNowLive(player, false, null);
        displayWeapons.displayWeapons(player);

    }

    @EventHandler
    public void onManualCombatStart(PlayerAnimationEvent event) {

        Player player = event.getPlayer();

        if(!player.isSneaking()){
            return;
        }

        if (event.getAnimationType() == PlayerAnimationType.ARM_SWING && event.getPlayer().getInventory().getItemInMainHand().getType().isAir()) {

            boolean combatStatus = profileManager.getAnyProfile(player).getIfInCombat();

            //make sure not in combat
            if(combatStatus){
                return;
            }

            UUID uuid = player.getUniqueId();
            if(countdownTasks.containsKey(uuid)){
                countdownTasks.get(uuid).cancel();
            }

            BukkitTask task = new BukkitRunnable(){
                private int secondsLeft = 3;

                @Override
                public void run(){

                    if(player.isSneaking()){
                        if (secondsLeft > 0){
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("Starting Combat in " + secondsLeft));
                            secondsLeft --;
                        }
                        else{
                            this.cancel();
                            combatManager.startCombatTimer(player);

                        }
                    }else{
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("Cancelled combat"));
                        this.cancel();
                    }

                }
            }.runTaskTimer(main, 0, 20);

            countdownTasks.put(uuid, task);

        }
    }


    @EventHandler
    public void noDeadMovement(PlayerMoveEvent event){
        Player player = event.getPlayer();

        boolean deathStatus = profileManager.getAnyProfile(player).getIfDead();

        if(!deathStatus){
            return;
        }

        Block block = player.getLocation().subtract(0,.1,0).getBlock();

        if(block.getType() == Material.AIR){
            return;
        }

        Location from = event.getFrom();
        Location to = event.getTo();
        assert to != null;
        if (from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ()) {
            event.setCancelled(true);
            player.teleport(from);
        }
    }

    @EventHandler
    public void noImmobile(PlayerMoveEvent event){
        Player player = event.getPlayer();

        boolean immobile = buffAndDebuffManager.getImmobile().getImmobile(player);

        if(!immobile){
            return;
        }

        Block block = player.getLocation().subtract(0,.1,0).getBlock();

        if(block.getType() == Material.AIR){
            return;
        }

        Location from = event.getFrom();
        Location to = event.getTo();
        assert to != null;
        if (from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ()) {
            event.setCancelled(true);
            player.teleport(from);
        }
    }


    @EventHandler
    public void noTakeArmorstand(PlayerArmorStandManipulateEvent event){
        Player player = event.getPlayer();

        if(player.getGameMode() == GameMode.CREATIVE){
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void whenHealthChanged(HealthChangeEvent event){
        LivingEntity defender = event.getEntity();

        if(defender instanceof ArmorStand){
            return;
        }

        if(defender instanceof ItemFrame){
            return;
        }

        boolean immortal = false;
        boolean immune = buffAndDebuffManager.getImmune().getImmune(defender);

        if(!(defender instanceof Player)){

            immortal = profileManager.getAnyProfile(defender).getImmortality();

            if(defender.isDead()){
                return;
            }

            if(profileManager.getAnyProfile(defender).getIfObject()){
                return;
            }

            if(profileManager.getIfResetProcessing(defender)){
                return;
            }

            aggroTick.startAggroTaskFor(defender);
        }

        if(defender instanceof Player){

            boolean deathStatus = profileManager.getAnyProfile(defender).getIfDead();

            if(deathStatus){
                return;
            }

            Player defenderPlayer = (Player) defender;

            shieldAbilityManaDisplayer.displayPlayerHealthPlusInfo(defenderPlayer);

            if(!event.getIfPositive()){

                InventoryView openInv = defenderPlayer.getOpenInventory();

                if(!openInv.getTitle().equalsIgnoreCase("crafting")){
                    defenderPlayer.closeInventory();
                }

                combatManager.startCombatTimer(defenderPlayer);
            }


        }

        //targeting bar
        Bukkit.getServer().getPluginManager().callEvent(new TargetBarShouldUpdateEvent(defender));

        if(immortal || immune){
            return;
        }

        if(!event.getIfPositive()){


            if(damageSoundCooldown.get(defender.getUniqueId()) == null){
                damageSoundCooldown.put(defender.getUniqueId(), (System.currentTimeMillis() / 1000) - 1);
            }

            long currentTime = System.currentTimeMillis() / 1000;

            if(defender instanceof Player){
                if(currentTime - damageSoundCooldown.get(defender.getUniqueId()) > 0.5){

                    ((Player) defender).playSound(defender, Sound.ENTITY_PLAYER_HURT, 1, 1);
                    damageSoundCooldown.put(defender.getUniqueId(), (System.currentTimeMillis() / 1000));
                }

                abilityManager.getWarriorAbilities().getSearingChains().tryToDecreaseCooldown((Player) defender);
                abilityManager.getAssassinAbilities().getStealth().stealthBonusCheck((Player) defender, null);

            }


            buffAndDebuffManager.getSleep().forceWakeUp(defender);


        }


        if(!defender.hasAI()){
            defender.setAI(true);
        }

        if(MythicBukkit.inst().getAPIHelper().isMythicMob(defender.getUniqueId())){
            AbstractEntity abstractEntity = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(defender).getEntity();
            MythicBukkit.inst().getAPIHelper().getMythicMobInstance(defender).signalMob(abstractEntity, "damage");
        }

    }

    @EventHandler
    public void targetBarUpdate(TargetBarShouldUpdateEvent event){

        LivingEntity target = event.getTarget();

        for(Map.Entry<UUID, LivingEntity> entry: targetManager.getTargetMap().entrySet()){
            UUID playerID = entry.getKey();
            Player player = Bukkit.getPlayer(playerID);
            Entity playerTarget = entry.getValue();

            if(playerTarget != null && playerTarget.equals(target)){
                assert player != null;
                targetManager.updateTargetBar(player);

            }
        }

    }

    @EventHandler
    public void boardUpdate(BoardValueUpdateEvent event){

        Player player = event.getPlayer();

        PartiesAPI api = Parties.getApi();

        PartyPlayer partyPlayer = api.getPartyPlayer(player.getUniqueId());

        assert partyPlayer != null;
        if(partyPlayer.isInParty()){
            Party party = api.getParty(partyPlayer.getPartyId());
            assert party != null;
            Set<UUID> partyMemberList = party.getMembers();
            for (UUID memberId : partyMemberList){

                Player partyMember = Bukkit.getPlayer(memberId);

                if(partyMember == null){
                    continue;
                }

                damageHealthBoard.update(partyMember);
            }
        }

    }

    @EventHandler
    public void allEntityDamage(EntityDamageEvent event){

        double damage = event.getDamage();

        event.setCancelled(true);

        if((event.getEntity() instanceof Player)){

            event.setCancelled(true);

            Player player = (Player) event.getEntity();


            boolean deathStatus = profileManager.getAnyProfile(player).getIfDead();

            if(deathStatus){
                return;
            }

            if(event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK ){
                LivingEntity attacker = (LivingEntity) ((EntityDamageByEntityEvent) event).getDamager();

                if(attacker instanceof Player){
                    return;
                }

                damage = 1.0;
                Double actualDamage = damageCalculator.calculateGettingDamaged(player, attacker, "Physical", damage);
                changeResourceHandler.subtractHealthFromEntity(player, actualDamage, attacker);
            }
            else{
                changeResourceHandler.subtractHealthFromEntity(player, damage, null);
            }

        }

    }

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event){

        if(event.getDamager() instanceof Player){
            Player player = (Player) event.getDamager();
            if(player.getGameMode().equals(GameMode.CREATIVE)){
                return;
            }
        }


        event.setCancelled(true);
        //player hit someting
        event.setDamage(0);

        if(!(event.getEntity() instanceof LivingEntity)){
            return;
        }

        if(event.getDamager() instanceof Player){

            Player player = (Player) event.getDamager();

            if (event.getEntity() instanceof Player) {
                if(!(pvpManager.pvpLogic((Player) event.getDamager(), (Player) event.getEntity()))){
                    return;
                }
            }


            boolean deathStatus = profileManager.getAnyProfile(player).getIfDead();

            if(deathStatus){
                return;
            }

            combatManager.startCombatTimer(player);
            abilityManager.useBasic(player);
        }

    }

    @EventHandler
    public void basicAttackAir(PlayerAnimationEvent event){

        Player player = event.getPlayer();

        if(event.getAnimationType() != PlayerAnimationType.ARM_SWING){
            return;
        }


        boolean deathStatus = profileManager.getAnyProfile(player).getIfDead();

        if(deathStatus){
            return;
        }

        boolean combatStatus = profileManager.getAnyProfile(player).getIfInCombat();

        if (!combatStatus) {
            return;
        }


        if(dropCheck.containsKey(player.getUniqueId())){
            if(dropCheck.get(player.getUniqueId())){
                return;
            }
        }

        //cast basic attack
        abilityManager.useBasic(player);

    }

    @EventHandler
    public void useAbility(PlayerItemHeldEvent event){

        Player player = event.getPlayer();

        if(!profileManager.getAnyProfile(player).getIfInCombat()){
            return;
        }

        if(profileManager.getAnyProfile(player).getIfDead()){
            return;
        }

        event.setCancelled(true);
        int newSlot = event.getNewSlot();

        EquipSkills equipSkills = profileManager.getAnyProfile(player).getEquipSkills();
        int abilityNumber = equipSkills.getAnySlot()[newSlot-1];
        abilityManager.useAbility(player, abilityNumber);

    }

    @EventHandler
    public void useUltimate(PlayerSwapHandItemsEvent event){
        event.setCancelled(true);

        Player player = event.getPlayer();

        boolean combatStatus = profileManager.getAnyProfile(player).getIfInCombat();
        boolean deathStatus = profileManager.getAnyProfile(player).getIfDead();

        if(!combatStatus){
            targetManager.setPlayerTarget(player, null);
            return;
        }

        if(deathStatus){
            targetManager.setPlayerTarget(player, null);
            return;
        }

        abilityManager.useUltimate(player);
    }

    @EventHandler
    public void playerCastSkillOnEnemy(SkillOnEnemyEvent event){


        LivingEntity entity = event.getEntity();


        if(entity instanceof Player){
            return;
        }

        Player player = event.getPlayer();

        aggroManager.addAttacker(entity, player);

        World playerWorld = player.getWorld();

        PartiesAPI api = Parties.getApi();

        PartyPlayer partyPlayer = api.getPartyPlayer(player.getUniqueId());

        assert partyPlayer != null;
        if(partyPlayer.isInParty()){

            Party party = api.getParty(partyPlayer.getPartyId());

            assert party != null;
            Set<UUID> partyMemberList = party.getMembers();

            for(UUID partyMemberId : partyMemberList){

                Player partyMember = Bukkit.getPlayer(partyMemberId);

                if(partyMember == null){
                    continue;
                }

                if(!partyMember.isOnline()){
                    continue;
                }

                if(partyMember == player){
                    continue;
                }

                boolean deathStatus = profileManager.getAnyProfile(partyMember).getIfDead();

                if(deathStatus){
                    continue;
                }

                World memberWorld = partyMember.getWorld();

                if(playerWorld != memberWorld){
                    continue;
                }

                double distance = player.getLocation().distance(partyMember.getLocation());

                if(distance > 20){
                    continue;
                }

                aggroManager.addAttacker(entity, partyMember);

            }
        }
    }

    @EventHandler
    public void removeTargetOrTeamTarget(PlayerDropItemEvent event){

        Player player = event.getPlayer();

        boolean teamTarget = player.isSneaking();

        if(teamTarget){
            targetManager.setTeamTarget(player);
        }
        else{
            targetManager.setPlayerTarget(player, null);
        }



        if(player.getGameMode() != GameMode.CREATIVE){
            event.setCancelled(true);
        }

        dropCheck.put(player.getUniqueId(), true);

        Bukkit.getScheduler().runTask(main, () -> dropCheck.put(player.getUniqueId(), false));
    }

    @EventHandler
    public void targetEntity(PlayerInteractEvent event){

        if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK){
            return;
        }

        Player player = event.getPlayer();

        double baseRange = 20;
        double bonusRange = buffAndDebuffManager.getTotalRangeModifier(player);
        double totalRange = baseRange + bonusRange;

        boolean prioritizePlayer = player.isSneaking();

        if(prioritizePlayer){
            Vector direction = player.getEyeLocation().getDirection();
            double x = direction.getX() * totalRange;
            double y = direction.getY() * totalRange;
            double z = direction.getZ() * totalRange;
            Location start = player.getEyeLocation();
            Location end = start.clone().add(x, y, z);
            BoundingBox boundingBox = BoundingBox.of(start, end);

            double closestDistanceSquaredMob = Double.MAX_VALUE;
            double closestDistanceSquaredPlayer = Double.MAX_VALUE;
            Entity theClosestMob = null;
            Entity theClosestPlayer = null;
            for(Entity entity: player.getWorld().getNearbyEntities(boundingBox)){
                //only players and mobs
                if(entity == player){
                    continue;
                }

                if(entity instanceof Player){

                    if(pvpManager.pvpLogic(player, (Player) entity)){
                        if(stealthTargetBlacklist.get((Player) entity)){
                            continue;
                        }
                    }


                    double distanceSquared = entity.getLocation().distanceSquared(player.getLocation());
                    if(distanceSquared < closestDistanceSquaredPlayer){
                        theClosestPlayer = entity;
                        closestDistanceSquaredPlayer = distanceSquared;
                    }
                }

                if(entity instanceof LivingEntity && !(entity instanceof Player)){

                    boolean object = profileManager.getAnyProfile(player).getIfObject();

                    if(object){
                        continue;
                    }

                    double distanceSquared = entity.getLocation().distanceSquared(player.getLocation());
                    if(distanceSquared < closestDistanceSquaredMob){
                        theClosestMob = entity;
                        closestDistanceSquaredMob = distanceSquared;
                    }
                }
            }

            if(theClosestPlayer == null){
                if(theClosestMob != null){
                    targetManager.setPlayerTarget(player, (LivingEntity) theClosestMob);
                }
            }

            if(theClosestPlayer != null){
                targetManager.setPlayerTarget(player, (LivingEntity) theClosestPlayer);
            }

        }
        else{
            boolean inCombat = profileManager.getAnyProfile(player).getIfInCombat();

            Vector direction = player.getEyeLocation().getDirection();
            double x = direction.getX() * totalRange;
            double y = direction.getY() * totalRange;
            double z = direction.getZ() * totalRange;
            Location start = player.getEyeLocation();
            Location end = start.clone().add(x, y, z);
            BoundingBox boundingBox = BoundingBox.of(start, end);

            if(inCombat){

                double closestDistanceSquaredMob = Double.MAX_VALUE;
                double closestDistanceSquaredPlayer = Double.MAX_VALUE;
                Entity theClosestMob = null;
                LivingEntity theClosestPlayer = null;
                for(Entity entity: player.getWorld().getNearbyEntities(boundingBox)){
                    //only players and mobs
                    if(entity == player){
                        continue;
                    }

                    if(!(entity instanceof LivingEntity)){
                        continue;
                    }

                    if(entity instanceof Player){

                        if(pvpManager.pvpLogic(player, (Player) entity)){
                            if(stealthTargetBlacklist.get((Player) entity)){
                                continue;
                            }
                        }

                        double distanceSquared = entity.getLocation().distanceSquared(player.getLocation());

                        Player entityPlayer = (Player) entity;

                        if(pvpManager.pvpLogic(player, entityPlayer)){

                            if(distanceSquared < closestDistanceSquaredMob){
                                theClosestMob = entityPlayer;
                                closestDistanceSquaredMob = distanceSquared;
                            }
                        }

                        if(distanceSquared < closestDistanceSquaredPlayer){
                            theClosestPlayer = entityPlayer;
                            closestDistanceSquaredPlayer = distanceSquared;
                        }
                    }

                    if(!(entity instanceof Player)){

                        LivingEntity livingEntity = (LivingEntity) entity;

                        boolean object = profileManager.getAnyProfile(livingEntity).getIfObject();

                        if(object){
                            continue;
                        }

                        double distanceSquared = entity.getLocation().distanceSquared(player.getLocation());
                        if(distanceSquared < closestDistanceSquaredMob){
                            theClosestMob = entity;
                            closestDistanceSquaredMob = distanceSquared;
                        }
                    }
                }

                if(theClosestMob == null){
                    if(theClosestPlayer != null){
                        targetManager.setPlayerTarget(player, theClosestPlayer);
                    }
                }

                if(theClosestMob != null){
                    targetManager.setPlayerTarget(player, (LivingEntity) theClosestMob);
                }
            }
            else{

                LivingEntity theClosest = null;
                double closestDistanceSquared = Double.MAX_VALUE;

                for(Entity entity: player.getWorld().getNearbyEntities(boundingBox)){
                    //only players and mobs
                    if(entity == player){
                        continue;
                    }

                    if(!(entity instanceof LivingEntity)){
                        continue;
                    }

                    LivingEntity livingEntity = (LivingEntity) entity;

                    boolean object = profileManager.getAnyProfile(livingEntity).getIfObject();

                    if(object){
                        continue;
                    }

                    if(entity instanceof Player){
                        if(pvpManager.pvpLogic(player, (Player) entity)){
                            if(stealthTargetBlacklist.get((Player) entity)){
                                continue;
                            }
                        }
                    }

                    double distanceSquared = entity.getLocation().distanceSquared(player.getLocation());

                    if(distanceSquared < closestDistanceSquared){
                        theClosest = livingEntity;
                        closestDistanceSquared = distanceSquared;
                    }

                    if(theClosest != null){
                        targetManager.setPlayerTarget(player, theClosest);
                    }
                }
            }
        }

    }

    @EventHandler
    public void StatusChange(StatusUpdateEvent event){
        Player player = event.getPlayer();
        statusDisplayer.displayStatus(player);
    }

    @EventHandler
    public void WorldChange(PlayerChangedWorldEvent event){

        Player player = event.getPlayer();

        targetManager.setPlayerTarget(player, null);
        combatManager.forceCombatEnd(player);

        displayWeapons.displayArmor(player);
        displayWeapons.displayWeapons(player);
    }

    @EventHandler
    public void LeaveTutorial(PlayerChangedWorldEvent event){

        World world = event.getFrom();

        if(!world.getName().startsWith("tutorial_")){
            return;
        }

        Player player = event.getPlayer();

        if(profileManager.getAnyProfile(player).getMilestones().getMilestone("tutorial")){
            return;
        }

        classSetter.setClass(player, "none");
    }

    @EventHandler
    public void onHelpfulHint(HelpfulHintEvent event){

        Player player = event.getPlayer();

        String whatHint = event.getWhatHint();;


        switch (whatHint.toLowerCase()){
            case "combatend":{
                player.sendMessage(ChatColor.of(new java.awt.Color(255, 128, 0)) + "Helpful Hint: " +
                        ChatColor.RESET + "Open your inventory to exit combat");
                return;
            }
        }

    }


}
