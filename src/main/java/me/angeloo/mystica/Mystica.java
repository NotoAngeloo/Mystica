package me.angeloo.mystica;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import me.angeloo.mystica.Components.Commands.*;
import me.angeloo.mystica.Components.Inventories.BagInventory;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Tasks.AggroTick;
import me.angeloo.mystica.Tasks.NaturalRegenTick;
import me.angeloo.mystica.Tasks.RezTick;
import me.angeloo.mystica.Tasks.TargetDistanceTick;
import me.angeloo.mystica.Utility.*;
import me.angeloo.mystica.Utility.Listeners.GeneralEventListener;
import me.angeloo.mystica.Utility.Listeners.InventoryEventListener;
import me.angeloo.mystica.Utility.Listeners.MMListeners;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public final class Mystica extends JavaPlugin {

    private static Mystica plugin;

    private ProfileManager profileManager;
    private ProfileFileWriter profileFileWriter;

    private ClassSetter classSetter;
    private StealthTargetBlacklist stealthTargetBlacklist;
    private TargetManager targetManager;
    private AggroTick aggroTick;
    private AggroManager aggroManager;
    private DpsManager dpsManager;
    private PvpManager pvpManager;
    private CombatManager combatManager;
    private BuffAndDebuffManager buffAndDebuffManager;
    private AbilityManager abilityManager;
    private DeathManager deathManager;
    private InventoryIndexingManager inventoryIndexingManager;
    private PveChecker pveChecker;
    private DamageCalculator damageCalculator;
    private ChangeResourceHandler changeResourceHandler;

    private BagInventory bagInventory;

    private ProtocolManager protocolManager;

    @Override
    public void onEnable() {

        plugin = this;

        protocolManager = ProtocolLibrary.getProtocolManager();

        profileFileWriter = new ProfileFileWriter(this);

        profileManager = new ProfileManager(this);
        profileManager.loadProfilesFromConfig();

        classSetter = new ClassSetter(this);

        pveChecker = new PveChecker(this);
        pvpManager = new PvpManager(this);
        stealthTargetBlacklist = new StealthTargetBlacklist();
        aggroManager = new AggroManager();
        buffAndDebuffManager = new BuffAndDebuffManager(this);
        targetManager = new TargetManager(this);

        changeResourceHandler = new ChangeResourceHandler(this);

        dpsManager = new DpsManager();

        damageCalculator = new DamageCalculator(this);

        abilityManager = new AbilityManager(this);
        combatManager = abilityManager.getCombatManager();

        deathManager = new DeathManager(this);

        inventoryIndexingManager = new InventoryIndexingManager();
        bagInventory = new BagInventory(this);


        aggroTick = new AggroTick(this);

        getCommand("ToggleGlobalPvp").setExecutor(new ToggleGlobalPvp(this));
        getCommand("SeeRawDamage").setExecutor(new SeeRawDamage(this));
        getCommand("MysticaDamage").setExecutor(new MysticaDamage(this));
        getCommand("Bag").setExecutor(new Bag(this));
        getCommand("Equipment").setExecutor(new Equipment(this));
        getCommand("Trash").setExecutor(new Trash());
        getCommand("ClassSelect").setExecutor(new ClassSelect(this));
        getCommand("WhatAreMyStats").setExecutor(new WhatAreMyStats(this));
        getCommand("Reforge").setExecutor(new Reforge(this));
        getCommand("Upgrade").setExecutor(new Upgrade(this));
        getCommand("Generate").setExecutor(new Generate(this));
        getCommand("ManualSave").setExecutor(new ManualSave(this));
        getCommand("DeleteProfile").setExecutor(new DeleteProfile(this));

        this.getServer().getPluginManager().registerEvents(new InventoryEventListener(this), this);
        this.getServer().getPluginManager().registerEvents(new GeneralEventListener(this), this);
        this.getServer().getPluginManager().registerEvents(new MMListeners(this), this);

        NaturalRegenTick regenTick = new NaturalRegenTick(this, abilityManager);
        regenTick.runTaskTimer(this, 0, 40);
        RezTick rezTick = new RezTick(this);
        rezTick.runTaskTimer(this, 0, 20);
        TargetDistanceTick targetDistanceTick = new TargetDistanceTick(this);
        targetDistanceTick.runTaskTimer(this, 0, 20);

        Bukkit.getLogger().info("Mystica Enabled");

        World island = Bukkit.getWorld("world");
        assert island != null;
        WorldBorder border = island.getWorldBorder();
        Location center = new Location(island, 94, 66, 118);
        border.setCenter(center);
        double size = 1775;
        border.setSize(size);

    }

    @Override
    public void onDisable() {

        for (Player player : Bukkit.getOnlinePlayers()){

            boolean combatStatus = profileManager.getAnyProfile(player).getIfInCombat();
            boolean deathStatus = profileManager.getAnyProfile(player).getIfDead();

            if(deathStatus){
                deathManager.playerNowLive(player, false, null);
            }
            if(combatStatus){
                combatManager.forceCombatEnd(player);
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
        }

        profileManager.saveProfilesToConfig();
        Bukkit.getLogger().info("Mystica Disabled");
    }

    public static Mystica getPlugin(){return plugin;}

    public ProfileManager getProfileManager(){
        return profileManager;
    }

    public ClassSetter getClassSetter(){
        return classSetter;
    }

    public StealthTargetBlacklist getStealthTargetBlacklist(){return stealthTargetBlacklist;}

    public TargetManager getTargetManager(){
        return targetManager;
    }

    public DpsManager getDpsManager() {
        return dpsManager;
    }

    public AggroTick getAggroTick(){
        return aggroTick;
    }

    public AggroManager getAggroManager(){
        return aggroManager;
    }

    public CombatManager getCombatManager(){
        return combatManager;
    }

    public BuffAndDebuffManager getBuffAndDebuffManager(){
        return buffAndDebuffManager;
    }

    public AbilityManager getAbilityManager(){
        return abilityManager;
    }

    public DeathManager getDeathManager(){
        return deathManager;
    }

    public ChangeResourceHandler getChangeResourceHandler(){
        return changeResourceHandler;
    }

    public DamageCalculator getDamageCalculator(){
        return damageCalculator;
    }

    public PvpManager getPvpManager() {
        return pvpManager;
    }

    public PveChecker getPveChecker(){
        return pveChecker;
    }

    public InventoryIndexingManager getInventoryIndexingManager(){
        return inventoryIndexingManager;
    }

    public BagInventory getBagInventory(){
        return bagInventory;
    }

    public ProfileFileWriter getProfileFileWriter(){
        return profileFileWriter;
    }

    public ProtocolManager getProtocolManager(){return protocolManager;}

}
