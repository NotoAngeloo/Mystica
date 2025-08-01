package me.angeloo.mystica.Utility.Listeners;

import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.events.bukkit.player.BukkitPartiesPlayerPostJoinEvent;
import com.alessiodp.parties.api.interfaces.Party;
import com.alessiodp.parties.api.interfaces.PartyPlayer;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicMobDespawnEvent;
import me.angeloo.mystica.Components.Inventories.Abilities.AbilityInventory;
import me.angeloo.mystica.Components.Inventories.Equipment.EquipmentInventory;
import me.angeloo.mystica.Components.Inventories.Party.PartyInventory;
import me.angeloo.mystica.Components.Items.PathToolItem;
import me.angeloo.mystica.Components.ProfileComponents.EquipSkills;
import me.angeloo.mystica.Components.ProfileComponents.NonPlayerStuff.Yield;
import me.angeloo.mystica.CustomEvents.*;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Tasks.AggroTick;
import me.angeloo.mystica.Tasks.RezTick;
import me.angeloo.mystica.Utility.*;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;
import me.angeloo.mystica.Utility.Enums.BarType;
import me.angeloo.mystica.Utility.Enums.PlayerClass;
import me.angeloo.mystica.Utility.Hud.CooldownDisplayer;
import me.angeloo.mystica.Utility.Logic.StealthTargetBlacklist;
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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.*;

import static me.angeloo.mystica.Utility.Enums.BarType.*;

public class GeneralEventListener implements Listener {

    private final Mystica main;
    private final HudManager hudManager;
    private final FakePlayerAiManager fakePlayerAiManager;
    private final DailyData dailyData;
    private final ProfileManager profileManager;
    private final MysticaPartyManager mysticaPartyManager;
    private final PathingManager pathingManager;
    private final StealthTargetBlacklist stealthTargetBlacklist;
    private final AggroTick aggroTick;
    private final DpsManager dpsManager;
    private final AggroManager aggroManager;
    private final PvpManager pvpManager;
    private final InventoryItemGetter itemGetter;
    private final TargetManager targetManager;
    private final CombatManager combatManager;
    private final CooldownDisplayer cooldownDisplayer;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final AbilityManager abilityManager;
    private final DeathManager deathManager;
    private final EquipmentInventory equipmentInventory;
    private final AbilityInventory abilityInventory;
    private final DisplayWeapons displayWeapons;
    private final GearReader gearReader;
    private final ClassSetter classSetter;
    private final GravestoneManager gravestoneManager;

    private final PartyInventory partyInventory;

    private final DamageCalculator damageCalculator;
    private final ChangeResourceHandler changeResourceHandler;

    private final FirstClearManager firstClearManager;

    private final RezTick rezTick;

    private final Map<UUID, Boolean> dropCheck = new HashMap<>();

    private final Map<UUID, Long> breakawayCooldown = new HashMap<>();
    private final Map<UUID, Long> damageSoundCooldown = new HashMap<>();

    private final Map<UUID, Location> previousLocations = new HashMap<>();
    private final Set<UUID> combatLogs = new HashSet<>();

    public GeneralEventListener(Mystica main) {
        this.main = main;
        hudManager = main.getHudManager();
        dailyData = main.getDailyData();
        profileManager = main.getProfileManager();
        pathingManager = main.getPathingManager();
        itemGetter = main.getItemGetter();
        fakePlayerAiManager = main.getFakePlayerAiManager();
        stealthTargetBlacklist = main.getStealthTargetBlacklist();
        aggroTick = main.getAggroTick();
        dpsManager = main.getDpsManager();
        aggroManager = main.getAggroManager();
        pvpManager = main.getPvpManager();
        targetManager = main.getTargetManager();
        combatManager = main.getCombatManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        abilityManager = main.getAbilityManager();
        deathManager = main.getDeathManager();
        equipmentInventory = main.getEquipmentInventory();
        abilityInventory = main.getAbilityInventory();
        displayWeapons = main.getDisplayWeapons();
        damageCalculator = main.getDamageCalculator();
        changeResourceHandler = main.getChangeResourceHandler();
        gearReader = new GearReader(main);
        classSetter = new ClassSetter(main);
        firstClearManager = main.getFirstClearManager();
        gravestoneManager = main.getGravestoneManager();
        mysticaPartyManager = main.getMysticaPartyManager();
        partyInventory = main.getPartyInventory();
        cooldownDisplayer = main.getCooldownDisplayer();
        rezTick = main.getRezTick();
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.closeInventory();
        }

        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {

                if (entity instanceof LivingEntity) {
                    if (entity instanceof Player) {
                        continue;
                    }

                    entity.remove();
                }


                if (entity.getType() == EntityType.TEXT_DISPLAY) {
                    entity.remove();
                }
            }


        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        hudManager.innitHud(player);
        Bukkit.getServer().getPluginManager().callEvent(new SetMenuItemsEvent(player));

        if (combatLogs.contains(player.getUniqueId())) {
            deathManager.playerNowDead(player);
            combatLogs.remove(player.getUniqueId());
        }

        profileManager.getAnyProfile(player);

        player.getInventory().setHeldItemSlot(8);


        targetManager.setPlayerTarget(player, null);
        gearReader.setGearStats(player);

        if(!profileManager.getAnyProfile(player).getIfDead()){
            displayWeapons.displayArmor(player);
            cooldownDisplayer.initializeItems(player);
        }



        if (!profileManager.getPlayerNameMap().containsKey(player.getName())) {

            AttributeInstance maxHealthAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            assert maxHealthAttribute != null;

            if (maxHealthAttribute.getBaseValue() != 20) {
                maxHealthAttribute.setBaseValue(20);
            }

            player.setHealth(20);
            player.setGlowing(false);
            player.setInvisible(false);
            player.setFireTicks(0);
            player.setVisualFire(false);
            player.setLevel(0);


            changeResourceHandler.healPlayerToFull(player);
        }

        profileManager.addToPlayerNameMap(player);

    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {

        Player player = event.getPlayer();

        mysticaPartyManager.removeFromParty(player);


        boolean combatStatus = profileManager.getAnyProfile(player).getIfInCombat();
        boolean deathStatus = profileManager.getAnyProfile(player).getIfDead();

        if (combatStatus) {
            if (!deathStatus) {
                combatLogs.add(player.getUniqueId());
                //deathManager.playerNowDead(player);
            }
        }

        targetManager.getPlayerTarget(player);


        targetManager.setPlayerTarget(player, null);

        if (player.getWorld().getName().startsWith("tutorial_")) {
            //remove class
            if (!profileManager.getAnyProfile(player).getMilestones().getMilestone("tutorial")) {
                classSetter.setClass(player, PlayerClass.NONE);
            }
        }
    }

    //maybe ill make items to be added to a bag, when implemented. just to stop items being deleted when combat end
    @EventHandler
    public void noItemPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {

            boolean combatStatus = profileManager.getAnyProfile(player).getIfInCombat();
            boolean deathStatus = profileManager.getAnyProfile(player).getIfDead();

            if (combatStatus) {
                event.setCancelled(true);
            }

            if (deathStatus) {
                event.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void noBreakBlocks(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE && player.isOp() && !profileManager.getAnyProfile(player).getIfDead()) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void displayPathsWithTool(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();

        if (!player.isOp()) {
            return;
        }

        if (player.getGameMode() != GameMode.CREATIVE) {
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();

        ItemStack pathTool = new CustomItemConverter().convert(new PathToolItem(), 1);

        if (!item.isSimilar(pathTool)) {
            return;
        }

        pathingManager.displayAllNearbyPaths(player);
    }

    @EventHandler
    public void onPathTool(PlayerInteractEvent event) {

        Player player = event.getPlayer();

        if (!player.isOp()) {
            return;
        }

        if (player.getGameMode() != GameMode.CREATIVE) {
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();

        ItemStack pathTool = new CustomItemConverter().convert(new PathToolItem(), 1);

        if (!item.isSimilar(pathTool)) {
            return;
        }

        event.setCancelled(true);

        //depending on click add to list
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            assert block != null;
            Location location = block.getLocation();
            pathingManager.createPath(location);
            pathingManager.displayAllNearbyPaths(player);
            return;
        }

        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
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

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (event.getAction() == Action.PHYSICAL && event.getClickedBlock().getType() == Material.FARMLAND) {
            event.setCancelled(true);
        }

        if (player.getInventory().getItemInMainHand().getType().equals(Material.BOW) || player.getInventory().getItemInOffHand().getType().equals(Material.BOW)) {
            event.setCancelled(true);
        }

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            //figure out allows here

            Block block = event.getClickedBlock();
            if (block != null) {
                Material blockType = block.getType();

                //maybe have a list later
                if (blockType == Material.DARK_OAK_SIGN) {
                    return;
                }
            }

            event.setCancelled(true);
        }


    }


    @EventHandler
    public void noPlaceBlocks(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void menuClick(InventoryClickEvent event) {
        Inventory clickedInv = event.getClickedInventory();
        if (clickedInv == null) {
            return;
        }

        String title = event.getView().getTitle();

        if (!title.equalsIgnoreCase("crafting")) {
            return;
        }

        if (clickedInv.getType() == InventoryType.PLAYER) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        abilityInventory.openAbilityInventory(player, -1);
    }


    @EventHandler
    public void noOffHandEquip(InventoryClickEvent event) {
        Inventory clickedInv = event.getClickedInventory();
        if (clickedInv == null) {
            return;
        }

        if (clickedInv.getType() != InventoryType.PLAYER) {
            return;
        }

        String title = event.getView().getTitle();

        if (!title.equalsIgnoreCase("crafting")) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        if (player.getGameMode().equals(GameMode.CREATIVE)) {
            return;
        }

        if (event.getClick().equals(ClickType.SWAP_OFFHAND)) {
            event.setCancelled(true);
        }

        if (event.getSlotType() != InventoryType.SlotType.QUICKBAR) {
            return;
        }

        if (event.getSlot() == 40) {
            event.setCancelled(true);
            ItemStack item = player.getItemOnCursor();
            ItemStack tempItem = item.clone();
            event.setCancelled(true);
            player.setItemOnCursor(null);
            equipmentInventory.openEquipmentInventory(player);
            player.getInventory().addItem(tempItem);
        }
    }

    @EventHandler
    public void invOpen(InventoryOpenEvent event){
        Player player = (Player) event.getPlayer();

        if(event.getInventory().getType().equals(InventoryType.CRAFTING)){
            Bukkit.getLogger().info("open?");
        }
    }

    @EventHandler
    public void menuOpen(SetMenuItemsEvent event){

        Player player = event.getPlayer();

        if(profileManager.getAnyProfile(player).getIfDead()){
            return;
        }

        cooldownDisplayer.initializeItems(player);

        if(profileManager.getAnyProfile(player).getIfInCombat()){
            return;
        }

        new BukkitRunnable(){
            @Override
            public void run(){
                Inventory inventory = player.getOpenInventory().getTopInventory();
                if(inventory.getType().equals(InventoryType.CRAFTING)){
                    setMenuItems(player);
                }
            }
        }.runTaskLaterAsynchronously(main, 1);


    }

    private void setMenuItems(Player player){

        //check to see what player has unlocked
        player.getInventory().setItem(27, itemGetter.getItem(Material.LEATHER, 1, "Bag"));
        player.getInventory().setItem(29, itemGetter.getItem(Material.AMETHYST_SHARD, 1, "Skills"));
        player.getInventory().setItem(31, itemGetter.getItem(Material.BROWN_BANNER, 1, "Team"));

    }

    @EventHandler
    public void noArmorEquip(InventoryClickEvent event) {

        Inventory clickedInv = event.getClickedInventory();
        if (clickedInv == null) {
            return;
        }
        Player player = (Player) event.getWhoClicked();

        if (event.getSlotType() == InventoryType.SlotType.ARMOR) {
            ItemStack item = player.getItemOnCursor();

            event.setCancelled(true);
            ItemStack tempItem = item.clone();
            event.setCancelled(true);
            player.setItemOnCursor(null);
            displayWeapons.displayArmor(player);

            equipmentInventory.openEquipmentInventory(player);
            player.getInventory().addItem(tempItem);
            return;
        }

        if (clickedInv.getType() == InventoryType.PLAYER) {

            String title = event.getView().getTitle();

            if (!title.equalsIgnoreCase("crafting")) {
                return;
            }

            ClickType clickType = event.getClick();
            if (clickType.isShiftClick()) {
                ItemStack item = event.getCurrentItem();
                assert item != null;
                if (item.getType().name().endsWith("_HELMET") ||
                        item.getType().name().endsWith("_CHESTPLATE") ||
                        item.getType().name().endsWith("_LEGGINGS") ||
                        item.getType().name().endsWith("_BOOTS")) {


                    ItemStack tempItem = item.clone();
                    player.getInventory().setItem(event.getSlot(), null);
                    displayWeapons.displayArmor(player);

                    equipmentInventory.openEquipmentInventory(player);
                    player.getInventory().addItem(tempItem);
                }
            }
        }
    }

    @EventHandler
    public void partyUpdateWhenObserving(PartyUpdateWhenObservingEvent event){

        List<LivingEntity> mParty = event.getMParty();

        for(LivingEntity member : mParty){

            if(member instanceof Player player){

                InventoryView view = player.getOpenInventory();;

                if(view.getTitle().contains("\uE05E") ||
                        view.getTitle().contains("\uE05F") ||
                        view.getTitle().contains("\uE060")){


                    Bukkit.getLogger().info("player should see this be updated");
                    partyInventory.openPartyInventory(player);

                }

            }

        }

    }

    @EventHandler
    public void maxHealthChange(MaxHealthChangeOutOfCombatEvent event){

        Player player = event.getPlayer();

        if(profileManager.getAnyProfile(player).getIfDead()){
            return;
        }

        if(profileManager.getAnyProfile(player).getIfInCombat()){
            return;
        }

        changeResourceHandler.healPlayerToFull(player);
    }

    @EventHandler
    public void noOpenInvCombatOrDead(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();

        boolean combatStatus = profileManager.getAnyProfile(player).getIfInCombat();
        boolean deathStatus = profileManager.getAnyProfile(player).getIfDead();

        if (!combatStatus) {
            return;
        }

        if (!deathStatus) {
            return;
        }
        event.setCancelled(true);
        player.sendMessage("you can't do that right now");
        player.closeInventory();
    }

    @EventHandler
    public void noCombatInvInteraction(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        boolean combatStatus = profileManager.getAnyProfile(player).getIfInCombat();
        if (!combatStatus) {
            return;
        }
        event.setCancelled(true);

        player.closeInventory();
        player.sendMessage("you can't do that right now");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        event.setKeepInventory(true);
        event.setKeepLevel(true);
        event.getDrops().clear();
        event.setDeathMessage(null);

        event.setDroppedExp(0);


        deathManager.playerNowDead(player);

    }

    @EventHandler
    public void normalEntityDeathDeath(EntityDeathEvent event) {
        event.getDrops().clear();
        event.setDroppedExp(0);

        LivingEntity entity = event.getEntity();

        if (entity instanceof Player) {
            return;
        }

        LivingEntity lastCaster = aggroManager.getLastPlayer(entity);

        if (lastCaster == null) {
            return;
        }

        Bukkit.getServer().getPluginManager().callEvent(new MysticaEnemyDeathEvent(lastCaster, entity));

    }

    @EventHandler
    public void mysticaPlayerDeath(MysticaPlayerDeathEvent event) {

        LivingEntity mysticaPlayer = event.getMysticaPlayer();

        //check for team wipe
        List<LivingEntity> mParty = new ArrayList<>(mysticaPartyManager.getMysticaParty(mysticaPlayer));

        for (LivingEntity member : mParty) {
            if (!profileManager.getAnyProfile(member).getIfDead()) {
                return;
            }
        }

        //Bukkit.getLogger().info("team wipe");

        for (LivingEntity member : mParty) {
            dpsManager.removeDps(member);

            if (member instanceof Player player) {
                hudManager.getDamageBoardPlaceholders().clearPlaceholders(player);
                rezTick.ableRezCountdown(player);
            }

        }

    }

    @EventHandler
    public void onLevelup(PlayerLevelChangeEvent event) {
        Player player = event.getPlayer();

        int newLevel = event.getNewLevel();

        if (newLevel > dailyData.getMaxLevel()) {
            newLevel = dailyData.getMaxLevel();
            player.setLevel(newLevel);
        }

        profileManager.getAnyProfile(player).getStats().setLevel(newLevel);

        player.closeInventory();

        //heal them
        changeResourceHandler.healPlayerToFull(player);
    }

    @EventHandler
    public void customDeathEvent(MysticaEnemyDeathEvent event) {

        LivingEntity caster = event.getPlayerWhoKilled();

        Player companionPlayer;

        if (caster instanceof Player) {
            companionPlayer = (Player) caster;
        } else {
            companionPlayer = profileManager.getCompanionsPlayer(caster);
        }

        if (!profileManager.getCompanions(companionPlayer).isEmpty()) {
            for (UUID companion : profileManager.getCompanions(companionPlayer)) {

                LivingEntity livingEntity = (LivingEntity) Bukkit.getEntity(companion);

                if (livingEntity != null) {
                    if (MythicBukkit.inst().getAPIHelper().isMythicMob(companion)) {
                        AbstractEntity abstractEntity = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(livingEntity).getEntity();
                        MythicBukkit.inst().getAPIHelper().getMythicMobInstance(livingEntity).signalMob(abstractEntity, "reset");
                    }
                }


            }
        }

        LivingEntity entity = event.getEntityWhoDied();

        Yield yield = profileManager.getAnyProfile(entity).getYield();

        float xpYield = yield.getXpYield();
        List<ItemStack> itemDrops = yield.getItemYield();

        Set<Player> victors = new HashSet<>();

        List<LivingEntity> mParty = new ArrayList<>(mysticaPartyManager.getMysticaParty(caster));
        for (LivingEntity member : mParty) {
            if (member instanceof Player) {
                changeResourceHandler.addXpToPlayer((Player) member, xpYield / mParty.size());
                victors.add((Player) member);
            }
        }

        //check bosshomes
        if (profileManager.getIfEntityIsBoss(entity.getUniqueId())) {

            //check if mm too
            if (MythicBukkit.inst().getAPIHelper().isMythicMob(entity.getUniqueId())) {
                String bossType = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(entity).getMobType();
                String bossName = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(entity).getDisplayName();

                int level = profileManager.getAnyProfile(entity).getStats().getLevel();

                //check if the boss has been cleared at this level yet
                if (!firstClearManager.getIfBossHasBeenClearedAtThisLevel(bossType, level)) {

                    //build a string
                    StringBuilder announcement = new StringBuilder();

                    announcement.append(ChatColor.of(new java.awt.Color(127, 0, 255))).append("Server First Clear!\n");
                    announcement.append(ChatColor.RESET).append("Congratulations to ");

                    for (Player victor : victors) {
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

        profileManager.getAnyProfile(entity).getVoidsOnDeath(victors);

    }


    @EventHandler
    public void targetTooFar(PlayerMoveEvent event) {

        Player player = event.getPlayer();

        player.setSaturation(1);


        if (targetManager.getPlayerTarget(player) == null) {
            return;
        }

        LivingEntity playerTarget = targetManager.getPlayerTarget(player);

        World playerWorld = player.getWorld();
        World targetWorld = playerTarget.getWorld();
        if (playerWorld != targetWorld) {
            targetManager.setPlayerTarget(player, null);
            return;
        }

        double distance = playerTarget.getLocation().distance(player.getLocation());
        if (distance > 35) {
            targetManager.setPlayerTarget(player, null);
            return;
        }

        if (playerTarget.isDead()) {
            targetManager.setPlayerTarget(player, null);
            return;
        }

    }

    @EventHandler
    public void rangerLoseFocus(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!profileManager.getAnyProfile(player).getPlayerClass().equals(PlayerClass.Ranger)) {
            return;
        }

        Location from = event.getFrom();
        Location to = event.getTo();
        assert to != null;
        if (from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ()) {
            abilityManager.getRangerAbilities().getFocus().loseFocus(player);
        }

    }

    @EventHandler
    public void noImmobile(PlayerMoveEvent event) {
        Player player = event.getPlayer();


        boolean immobile = buffAndDebuffManager.getImmobile().getImmobile(player);

        if (!immobile) {
            return;
        }

        Block block = player.getLocation().subtract(0, .1, 0).getBlock();

        if (block.getType() == Material.AIR) {
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
    public void noTakeArmorstand(PlayerArmorStandManipulateEvent event) {
        Player player = event.getPlayer();


        if (player.getGameMode() == GameMode.CREATIVE && !profileManager.getAnyProfile(player).getIfDead()) {
            return;
        }

        event.setCancelled(true);
    }


    @EventHandler
    public void whenHealthChanged(HealthChangeEvent event) {
        LivingEntity defender = event.getEntity();

        if (defender instanceof ArmorStand) {
            return;
        }

        if (defender instanceof ItemFrame) {
            return;
        }

        boolean immortal = false;
        boolean immune = buffAndDebuffManager.getImmune().getImmune(defender);

        if (!(defender instanceof Player)) {

            if (!profileManager.getAnyProfile(defender).fakePlayer()) {
                immortal = profileManager.getAnyProfile(defender).getImmortality();

                if (defender.isDead()) {
                    return;
                }

                if (profileManager.getAnyProfile(defender).getIfObject()) {
                    return;
                }

                if (profileManager.getIfResetProcessing(defender)) {
                    return;
                }

                if (!event.getIfPositive()) {
                    aggroTick.startAggroTaskFor(defender);
                }

                List<LivingEntity> attackers = aggroManager.getAttackerList(defender);
                for (LivingEntity attacker : attackers) {
                    if (!(attacker instanceof Player attackerPlayer)) {
                        continue;
                    }
                    Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(attackerPlayer, Target, false));
                }

            }

        }

        if (defender instanceof Player defenderPlayer) {

            boolean deathStatus = profileManager.getAnyProfile(defender).getIfDead();

            if (deathStatus) {
                return;
            }


            if (!event.getIfPositive()) {

                InventoryView openInv = defenderPlayer.getOpenInventory();

                if (!openInv.getTitle().equalsIgnoreCase("crafting")) {
                    defenderPlayer.closeInventory();
                }

                combatManager.startCombatTimer(defenderPlayer);
            }


        }

        if (defender instanceof Player || profileManager.getAnyProfile(defender).fakePlayer()) {

            List<LivingEntity> mysticaParty = new ArrayList<>(mysticaPartyManager.getMysticaParty(defender));

            for (LivingEntity member : mysticaParty) {

                if (member instanceof Player player) {
                    Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, Team, false));
                }

            }
        }


        if (immortal || immune) {
            return;
        }

        if (!event.getIfPositive()) {


            if (damageSoundCooldown.get(defender.getUniqueId()) == null) {
                damageSoundCooldown.put(defender.getUniqueId(), (System.currentTimeMillis() / 1000) - 1);
            }

            long currentTime = System.currentTimeMillis() / 1000;

            if ((defender instanceof Player) || profileManager.getAnyProfile(defender).fakePlayer()) {

                if (defender instanceof Player) {
                    if (currentTime - damageSoundCooldown.get(defender.getUniqueId()) > 0.5) {

                        ((Player) defender).playSound(defender, Sound.ENTITY_PLAYER_HURT, 1, 1);
                        damageSoundCooldown.put(defender.getUniqueId(), (System.currentTimeMillis() / 1000));
                    }
                }

                abilityManager.getWarriorAbilities().getSearingChains().tryToDecreaseCooldown(defender);
                abilityManager.getAssassinAbilities().getStealth().stealthBonusCheck(defender, null);

                if (profileManager.getAnyProfile(defender).getPlayerClass().equals(PlayerClass.Warrior)) {
                    abilityManager.getWarriorAbilities().getRage().addRageToEntity(defender, 10);
                }

            }

            buffAndDebuffManager.getSleep().forceWakeUp(defender);


        }

        if (!profileManager.getAnyProfile(defender).fakePlayer()) {
            if (!defender.hasAI()) {
                defender.setAI(true);
            }
        }


        if (MythicBukkit.inst().getAPIHelper().isMythicMob(defender.getUniqueId())) {
            AbstractEntity abstractEntity = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(defender).getEntity();
            MythicBukkit.inst().getAPIHelper().getMythicMobInstance(defender).signalMob(abstractEntity, "damage");
        }

    }

    @EventHandler
    public void hudUpdate(HudUpdateEvent event) {

        Player player = event.getPlayer();
        BarType barType = event.getBarType();
        boolean forced = event.getIfForced();

        switch (barType) {

            case Resource -> {
                hudManager.editResourceBar(player);
                return;
            }

            case Target -> {
                hudManager.editTargetBar(player, forced);
                return;
            }
            case Team -> {
                hudManager.editTeamBar(player, forced);
                return;
            }
            case Status -> {
                hudManager.editStatusBar(player);
                return;
            }
            case Cast -> {
                hudManager.displayCastBar(player);
                return;
            }
            case Dps -> {
                hudManager.getDamageBoardPlaceholders().updateDamageBoardValues(player);
                return;
            }
        }


    }




    @EventHandler
    public void ultimateStatusChange(UltimateStatusChageEvent event){
        Player player = event.getPlayer();

        if(rezTick.running(player)){
            return;
        }

        hudManager.displayUltimate(player);
    }


    @EventHandler
    public void allEntityDamage(EntityDamageEvent event){


        double damage = event.getDamage();

        event.setCancelled(true);

        if((event.getEntity() instanceof Player player)){

            event.setCancelled(true);


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
                changeResourceHandler.subtractHealthFromEntity(player, actualDamage, attacker, false);
            }
            else{
                changeResourceHandler.subtractHealthFromEntity(player, damage, null, false);
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

        if(profileManager.getAnyProfile(player).getIfDead()){
            return;
        }

        event.setCancelled(true);


        int newSlot = event.getNewSlot();

        EquipSkills equipSkills = profileManager.getAnyProfile(player).getEquipSkills();
        int abilityNumber = equipSkills.getAnySlot()[newSlot];
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

        LivingEntity caster = event.getCaster();

        aggroManager.addAttacker(entity, caster);

        World playerWorld = caster.getWorld();

        List<LivingEntity> mParty = new ArrayList<>(mysticaPartyManager.getMysticaParty(caster));
        for(LivingEntity member : mParty){
            if(member instanceof Player){
                if(((Player) member).isOnline()){
                    continue;
                }

                boolean deathStatus = profileManager.getAnyProfile(member).getIfDead();

                if(deathStatus){
                    continue;
                }
            }

            if(member == caster){
                continue;
            }

            World memberWorld = member.getWorld();

            if(playerWorld != memberWorld){
                continue;
            }

            double distance = caster.getLocation().distance(member.getLocation());

            if(distance > 100){
                continue;
            }

            aggroManager.addAttacker(entity, member);
        }


    }

    @EventHandler
    public void removeTargetOrTeamTarget(PlayerDropItemEvent event){

        Player player = event.getPlayer();
        abilityManager.interruptBasic(player);

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
        abilityManager.interruptBasic(player);

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

                if(!(entity instanceof LivingEntity)){
                    continue;
                }

                if(MythicBukkit.inst().getAPIHelper().isMythicMob(entity.getUniqueId())){
                    String mobType = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(entity).getMobType();

                    if(mobType.equalsIgnoreCase("safezone")){
                        continue;
                    }
                }

                LivingEntity livingEntity = (LivingEntity) entity;

                if(profileManager.getAnyProfile(livingEntity).getIfDead()){
                    continue;
                }

                if(entity instanceof Player || profileManager.getAnyProfile(livingEntity).fakePlayer() ){

                    if(entity instanceof Player){
                        if(pvpManager.pvpLogic(player, (Player) entity)){
                            if(stealthTargetBlacklist.get((Player) entity)){
                                continue;
                            }
                        }
                    }


                    double distanceSquared = entity.getLocation().distanceSquared(player.getLocation());
                    if(distanceSquared < closestDistanceSquaredPlayer){
                        theClosestPlayer = entity;
                        closestDistanceSquaredPlayer = distanceSquared;
                    }
                }

                if(!(entity instanceof Player)){

                    boolean object = profileManager.getAnyProfile(player).getIfObject();

                    if(object){

                        if(!gravestoneManager.isGravestone(entity)){
                            continue;
                        }
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

                    if(MythicBukkit.inst().getAPIHelper().isMythicMob(entity.getUniqueId())){
                        String mobType = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(entity).getMobType();

                        if(mobType.equalsIgnoreCase("safezone")){
                            continue;
                        }
                    }

                    LivingEntity livingEntity = (LivingEntity) entity;

                    if(profileManager.getAnyProfile(livingEntity).getIfDead()){
                        continue;
                    }

                    if(entity instanceof Player || profileManager.getAnyProfile(livingEntity).fakePlayer()){

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



                    }

                    if(!(entity instanceof Player)){

                        boolean object = profileManager.getAnyProfile(livingEntity).getIfObject();

                        if(object){

                            if(!gravestoneManager.isGravestone(entity)){
                                continue;
                            }
                        }

                        if(profileManager.getAnyProfile((LivingEntity) entity).fakePlayer()){
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

                    if(MythicBukkit.inst().getAPIHelper().isMythicMob(entity.getUniqueId())){
                        String mobType = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(entity).getMobType();

                        if(mobType.equalsIgnoreCase("safezone")){
                            continue;
                        }
                    }

                    LivingEntity livingEntity = (LivingEntity) entity;

                    if(profileManager.getAnyProfile(livingEntity).getIfDead()){
                        continue;
                    }

                    boolean object = profileManager.getAnyProfile(livingEntity).getIfObject();

                    if(object){
                        if(!gravestoneManager.isGravestone(entity)){
                            continue;
                        }
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
    public void OnTeleport(PlayerTeleportEvent event){
        Player player = event.getPlayer();
        if (!(event.getFrom().getWorld() == event.getTo().getWorld())) {
            // Store the player's previous location before the teleport
            previousLocations.put(player.getUniqueId(), event.getFrom());
        }
    }

    @EventHandler
    public void WorldChange(PlayerChangedWorldEvent event){

        Player player = event.getPlayer();

        if(profileManager.getAnyProfile(player).getIfDead()){
            deathManager.playerNowLive(player, false, null);
        }

        targetManager.setPlayerTarget(player, null);
        combatManager.forceCombatEnd(player);

        displayWeapons.displayArmor(player);

        gravestoneManager.removeGravestone(player);

        if(!profileManager.getCompanions(player).isEmpty()){
            profileManager.removeCompanions(player);
        }

        if(!player.getWorld().equals(Bukkit.getWorld("world"))){
            player.getWorld().setSpawnLocation(player.getLocation());
        }

        if(event.getFrom().getName().contains("tutorial_")){
            Location previousLoc = previousLocations.get(player.getUniqueId());

            Location newLoc = new Location(player.getWorld(), previousLoc.getX(), previousLoc.getY(), previousLoc.getZ(), previousLoc.getYaw(), previousLoc.getPitch());

            //but check the hidden interaction first (player dies in tutorial)
            player.teleport(newLoc);

        }

    }


    @EventHandler
    public void onMMRemoval(MythicMobDespawnEvent event){

        Entity entity = event.getMob().getEntity().getBukkitEntity();

        fakePlayerAiManager.stopAiTask(entity.getUniqueId());

        if(!(entity instanceof LivingEntity livingEntity)){
            return;
        }

        profileManager.getAnyProfile(livingEntity).setIfDead(true);

        if(profileManager.getAnyProfile(livingEntity).fakePlayer()){
            Player companionPlayer = profileManager.getCompanionsPlayer(livingEntity);
            profileManager.removeCompanion(companionPlayer, livingEntity.getUniqueId());
            mysticaPartyManager.removeFromMysticaPartyMap(livingEntity);
            profileManager.clearCompanionFaces(entity.getUniqueId());

        }

    }


    @EventHandler
    public void companionSignalEvent(AiSignalEvent event){

        LivingEntity companion = event.getCompanion();
        String signal = event.getSignal();

        if(signal.equalsIgnoreCase("stop")){
            fakePlayerAiManager.stopAiTask(companion.getUniqueId());
            return;
        }

        fakePlayerAiManager.signal(companion, signal);

    }

    @EventHandler
    public void onPartyJoin(BukkitPartiesPlayerPostJoinEvent event){

        Player player = Bukkit.getPlayer(event.getPartyPlayer().getPartyId());
        Player newLeader = Bukkit.getPlayer(event.getParty().getLeader());

        Bukkit.getScheduler().runTask(main, () -> profileManager.transferCompanionsToLeader(player, newLeader));

    }

    @EventHandler
    public void rezPlayer(PlayerInteractEvent event){

        Player player = event.getPlayer();

        if(player.getGameMode() != GameMode.SPECTATOR){
            return;
        }

        boolean deathStatus = profileManager.getAnyProfile(player).getIfDead();

        if(!deathStatus){
            return;
        }

        if(!rezTick.ableToRez(player)){
            return;
        }

        event.setCancelled(true);

        if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK){

            deathManager.playerNowLive(player, false, null);
            displayWeapons.displayArmor(player);
        }

    }

    @EventHandler
    public void spectatorMove(PlayerMoveEvent event){

        Player player = event.getPlayer();

        // Only apply logic if the player is in spectator mode
        if (player.getGameMode() != GameMode.SPECTATOR) {
            return;
        }

        Location to = event.getTo();
        Location from = event.getFrom();

        // Check if the player is actually moving to a new block
        if (to != null && (to.getBlockX() != from.getBlockX() || to.getBlockY() != from.getBlockY() || to.getBlockZ() != from.getBlockZ())) {

            // Check if the target block is not air (or other passable blocks if needed)
            if (!to.getBlock().isPassable()) {
                event.setCancelled(true);
                return;
            }
        }

        if(to == null){
            return;
        }

        Entity gravestone = gravestoneManager.getGravestone(player);

        if(gravestone == null){
            return;
        }

        Location gravestoneLoc = gravestone.getLocation();
        double distance = to.distance(gravestoneLoc);

        if (distance > 10) {
            player.teleport(gravestoneLoc);
            return;
        }

    }



    @EventHandler
    public void onMPartyUpdate(UpdateMysticaPartyEvent event){

        LivingEntity entity = event.getEntity();

        List<LivingEntity> oldMParty = new ArrayList<>(mysticaPartyManager.getMysticaParty(entity));

        for(LivingEntity member : oldMParty){
            mysticaPartyManager.updateMysticaParty(member);
        }

        for(LivingEntity member : oldMParty){

            if(member == null){
                continue;
            }

            if(member instanceof Player player){
                Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, Team, true));
                Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, Dps, true));
            }
        }

    }

    @EventHandler
    public void onCompanionSpawn(CompanionSpawnEvent event){

        LivingEntity companion = event.getCompanion();

        double max = profileManager.getAnyProfile(companion).getTotalHealth();
        changeResourceHandler.addHealthToEntity(companion, max, null);

        Player player = profileManager.getCompanionsPlayer(companion);

        Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, Team, true));

    }

}
