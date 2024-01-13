package me.angeloo.mystica.Utility.Listeners;

import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.interfaces.PartiesAPI;
import com.alessiodp.parties.api.interfaces.Party;
import com.alessiodp.parties.api.interfaces.PartyPlayer;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.angeloo.mystica.Components.Inventories.AbilityInventory;
import me.angeloo.mystica.Components.Inventories.ClassSelectInventory;
import me.angeloo.mystica.Components.Inventories.EquipmentInventory;
import me.angeloo.mystica.Components.ProfileComponents.EquipSkills;
import me.angeloo.mystica.CustomEvents.CustomDeathEvent;
import me.angeloo.mystica.CustomEvents.HealthChangeEvent;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.CustomEvents.StatusUpdateEvent;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Tasks.AggroTick;
import me.angeloo.mystica.Utility.*;
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
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.*;

public class GeneralEventListener implements Listener {

    private final Mystica main;
    private final ProfileManager profileManager;
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
    private final CooldownDisplayer cooldownDisplayer;
    private final ShieldAbilityManaDisplayer shieldAbilityManaDisplayer;

    private final DamageCalculator damageCalculator;
    private final ChangeResourceHandler changeResourceHandler;

    private final Map<UUID, BukkitTask> countdownTasks = new HashMap<>();
    private final Map<UUID, Boolean> dropCheck = new HashMap<>();

    public GeneralEventListener(Mystica main){
        this.main = main;
        profileManager = main.getProfileManager();
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
        cooldownDisplayer = new CooldownDisplayer(main, abilityManager);
        shieldAbilityManaDisplayer = new ShieldAbilityManaDisplayer(main, abilityManager);
        damageCalculator = main.getDamageCalculator();
        changeResourceHandler = main.getChangeResourceHandler();
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event){
        for(Player player : Bukkit.getOnlinePlayers()){
            player.closeInventory();
        }

        for (World world : Bukkit.getWorlds()) {
            for (LivingEntity entity : world.getLivingEntities()) {

                if(entity instanceof Player){
                    continue;
                }

                entity.remove();
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

            ItemStack rezItem = new ItemStack(Material.ENDER_EYE);
            ItemMeta rezMeta = rezItem.getItemMeta();
            assert rezMeta != null;
            rezMeta.setDisplayName("Revive");
            List<String> lore = new ArrayList<>();
            lore.add("Right Click to Revive");
            rezMeta.setLore(lore);
            rezItem.setItemMeta(rezMeta);

            if(playerInventory.contains(rezItem)){
                hasBadItem = true;
            }

            if(!allNull || hasBadItem){
                player.getInventory().setContents(savedInv);
                profileManager.getAnyProfile(player).removeSavedInv();
            }

        }

        if(profileManager.getAnyProfile(player).getPlayerClass().equalsIgnoreCase("None")){
            player.openInventory(new ClassSelectInventory().openClassSelect("none"));
        }

        profileManager.addToPlayerNameMap(player);
        inventoryIndexingManager.innitBagIndex(player);
        targetManager.setPlayerTarget(player, null);
        buffAndDebuffManager.removeAllBuffsAndDebuffs(player);
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
                targetManager.removeAllBars(thisPlayer);

            }
        }

        targetManager.setPlayerTarget(player, null);
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
    public void noOffHandEquip(InventoryClickEvent event){
        Inventory clickedInv = event.getClickedInventory();
        if (clickedInv == null) {
            return;
        }

        if(clickedInv.getType() == InventoryType.PLAYER){
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
                player.setItemOnCursor(null);
                player.openInventory(abilityInventory.openAbilityInventory(player, null, false));
            }

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
    public void combatFollow(PlayerItemHeldEvent event){
        Player player = event.getPlayer();

        boolean combatStatus = profileManager.getAnyProfile(player).getIfInCombat();
        boolean deathStatus = profileManager.getAnyProfile(player).getIfDead();

        if(!combatStatus){
            return;
        }

        if(deathStatus){
            return;
        }

        ItemStack weapon = player.getInventory().getItemInMainHand();
        int newSlot = event.getNewSlot();
        int oldSlot = event.getPreviousSlot();
        player.getInventory().setItem(newSlot, weapon);
        player.getInventory().setItem(oldSlot, cooldownDisplayer.getOldItem(player, oldSlot));
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
    public void rezPlayer(PlayerInteractEvent event){

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        boolean deathStatus = profileManager.getAnyProfile(player).getIfDead();

        if(!deathStatus){
            return;
        }


        if(event.getAction() == Action.RIGHT_CLICK_AIR
                || event.getAction() == Action.RIGHT_CLICK_BLOCK){
            if(item != null && item.getType() == Material.ENDER_EYE && item.getItemMeta().getDisplayName().equalsIgnoreCase("Revive")){
                event.setCancelled(true);
                deathManager.playerNowLive(player, false, null);
                displayWeapons.displayWeapons(player);
            }
        }
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

            aggroTick.startAggroTaskFor(defender);
        }

        if(defender instanceof Player){

            boolean deathStatus = profileManager.getAnyProfile(defender).getIfDead();

            if(deathStatus){
                return;
            }

            shieldAbilityManaDisplayer.displayPlayerHealthPlusInfo((Player) defender);

            if(!event.getIfPositive()){
                Player defenderPlayer = (Player)defender;

                InventoryView openInv = defenderPlayer.getOpenInventory();

                if(!openInv.getTitle().equalsIgnoreCase("crafting")){
                    defenderPlayer.closeInventory();
                }

            }


            if(!event.getIfPositive()){
                combatManager.startCombatTimer((Player) defender);
            }
        }

        //targeting bar
        for(Map.Entry<UUID, LivingEntity> entry: targetManager.getTargetMap().entrySet()){
            UUID playerID = entry.getKey();
            Player player = Bukkit.getPlayer(playerID);
            Entity target = entry.getValue();

            if(target != null && target.equals(defender)){
                assert player != null;
                targetManager.updateTargetBar(player);

            }
        }

        if(immortal || immune){
            return;
        }

        if(!event.getIfPositive()){

            if(!(defender instanceof Player)){

                if(!profileManager.getIfEntityIsBoss(defender.getUniqueId())){
                    defender.getWorld().spawnParticle(Particle.REDSTONE, defender.getLocation(), 50, 0.5, 0.5, 0.5, new Particle.DustOptions(Color.RED, 1));
                }
            }
            else{
                ((Player) defender).playSound(defender, Sound.ENTITY_PLAYER_HURT, 1, 1);
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
    public void useAbility(PlayerSwapHandItemsEvent event){
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

        //use ability

        int slot = player.getInventory().getHeldItemSlot();

        if(slot == 8){
            abilityManager.useUltimate(player);
        }
        else{
            EquipSkills equipSkills = profileManager.getAnyProfile(player).getEquipSkills();
            int abilityNumber = equipSkills.getAnySlot()[slot];
            abilityManager.useAbility(player, abilityNumber);
        }



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
    public void removeTarget(PlayerDropItemEvent event){

        Player player = event.getPlayer();

        targetManager.setPlayerTarget(player, null);

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


}
