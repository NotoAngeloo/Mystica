package me.angeloo.mystica;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import io.lumine.mythic.api.exceptions.InvalidMobTypeException;
import me.angeloo.mystica.Components.Commands.*;
import me.angeloo.mystica.Components.Inventories.*;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Tasks.*;
import me.angeloo.mystica.Utility.*;
import me.angeloo.mystica.Utility.Listeners.GeneralEventListener;
import me.angeloo.mystica.Utility.Listeners.InventoryEventListener;
import me.angeloo.mystica.Utility.Listeners.MMListeners;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;


import java.awt.Color;
import java.util.ArrayList;

public final class Mystica extends JavaPlugin{

    private static Mystica plugin;

    private ProtocolManager protocolManager;

    private ProfileManager profileManager;
    private ProfileFileWriter profileFileWriter;

    private DailyData dailyData;
    private PathingManager pathingManager;

    private ClassSetter classSetter;
    private StealthTargetBlacklist stealthTargetBlacklist;
    private FakePlayerTargetManager fakePlayerTargetManager;
    private TargetManager targetManager;
    private FakePlayerAiManager fakePlayerAiManager;
    private AggroTick aggroTick;
    private AggroManager aggroManager;
    private DpsManager dpsManager;
    private PvpManager pvpManager;
    private CombatManager combatManager;
    private BuffAndDebuffManager buffAndDebuffManager;
    private GravestoneManager gravestoneManager;
    private BossCastingManager bossCastingManager;
    private AbilityManager abilityManager;
    private DeathManager deathManager;
    private InventoryIndexingManager inventoryIndexingManager;
    private PveChecker pveChecker;
    private DamageCalculator damageCalculator;
    private ChangeResourceHandler changeResourceHandler;
    private DamageHealthBoard damageHealthBoard;
    private Locations locations;
    private QuestManager questManager;
    private DailyEventManager dailyEventManager;

    private BagInventory bagInventory;
    private QuestInventory questInventory;

    private FirstClearManager firstClearManager;

    public static Color assassinColor = new java.awt.Color(214, 61, 207);
    public static Color elementalistColor = new Color(153, 204, 255);
    public static Color mysticColor = new Color(155, 120, 197);
    public static Color paladinColor = new Color(207, 214, 61);
    public static Color rangerColor = new Color(34, 111, 80);
    public static Color shadowKnightColor = new Color(213, 33, 3);
    public static Color warriorColor = new Color(214, 126, 61);

    public static Color menuColor = new Color(176, 159, 109);
    public static Color levelColor = new Color(0,102,0);
    public static Color soulstoneColor = new Color(23, 32, 112);

    public static Color questColor = new Color(255, 128, 0);



    @Override
    public void onEnable() {

        plugin = this;

        protocolManager = ProtocolLibrary.getProtocolManager();

        pathingManager = new PathingManager(this);
        pathingManager.createOrLoadFolder();

        dailyData = new DailyData(this);
        dailyData.createOrLoadFolder();

        profileFileWriter = new ProfileFileWriter(this);
        profileManager = new ProfileManager(this);
        profileManager.loadProfilesFromConfig();

        locations = new Locations(this);
        locations.initializeLocationals();

        questManager = new QuestManager(this);

        classSetter = new ClassSetter(this);
        pvpManager = new PvpManager(this);
        pveChecker = new PveChecker(this);

        bossCastingManager = new BossCastingManager(this);
        stealthTargetBlacklist = new StealthTargetBlacklist();
        aggroManager = new AggroManager();
        buffAndDebuffManager = new BuffAndDebuffManager(this);

        fakePlayerTargetManager = new FakePlayerTargetManager(this);
        targetManager = new TargetManager(this);


        gravestoneManager = new GravestoneManager();
        dpsManager = new DpsManager(this);
        changeResourceHandler = new ChangeResourceHandler(this);

        damageHealthBoard = new DamageHealthBoard(this);

        damageCalculator = new DamageCalculator(this);

        abilityManager = new AbilityManager(this);
        combatManager = abilityManager.getCombatManager();
        deathManager = new DeathManager(this);

        fakePlayerAiManager = new FakePlayerAiManager(this);

        inventoryIndexingManager = new InventoryIndexingManager();
        bagInventory = new BagInventory(this);
        questInventory = new QuestInventory(this);

        firstClearManager = new FirstClearManager(this);
        firstClearManager.createOrLoadFolder();

        aggroTick = new AggroTick(this);

        dailyEventManager = new DailyEventManager(this);

        getCommand("ToggleGlobalPvp").setExecutor(new ToggleGlobalPvp(this));
        getCommand("SeeRawDamage").setExecutor(new SeeRawDamage(this));
        getCommand("MysticaDamage").setExecutor(new MysticaDamage(this));
        getCommand("MysticaEffect").setExecutor(new MysticaEffect(this));
        getCommand("StartFuryTimer").setExecutor(new StartFuryTimer(this));
        getCommand("Bag").setExecutor(new Bag(this));
        getCommand("Equipment").setExecutor(new Equipment(this));
        getCommand("Trash").setExecutor(new Trash());
        getCommand("ClassSelect").setExecutor(new ClassSelect(this));
        getCommand("GearSwap").setExecutor(new GearSwap());
        getCommand("WhatAreMyStats").setExecutor(new WhatAreMyStats(this));
        getCommand("ToggleImmunity").setExecutor(new ToggleImmunity(this));
        getCommand("Reforge").setExecutor(new Reforge(this));
        getCommand("Upgrade").setExecutor(new Upgrade(this));
        getCommand("Generate").setExecutor(new Generate(this));
        getCommand("Identify").setExecutor(new Identify());
        getCommand("ManualSave").setExecutor(new ManualSave(this));
        getCommand("ForcePortals").setExecutor(new ForcePortals(this));
        getCommand("DeleteProfile").setExecutor(new DeleteProfile(this));
        getCommand("SetMileStone").setExecutor(new SetMilestone(this));
        getCommand("MysticaInteractions").setExecutor(new MysticaInteractions(this));
        getCommand("PathTool").setExecutor(new PathTool());
        getCommand("DisplayPath").setExecutor(new DisplayPath(this));
        getCommand("SavePaths").setExecutor(new SavePaths(this));
        getCommand("ToggleBoardType").setExecutor(new ToggleBoardType(this));
        getCommand("Cosmetic").setExecutor(new Cosmetic(this));
        getCommand("BossLevel").setExecutor(new BossLevel(this));
        getCommand("GiveSoulStone").setExecutor(new GiveSoulStone(this));
        getCommand("MysticaQuest").setExecutor(new MysticaQuest(this));
        getCommand("HitValidCheck").setExecutor(new HitValidCheck(this));
        getCommand("SetCaution").setExecutor(new SetCaution(this));
        getCommand("SignalNearbyNpc").setExecutor(new SignalNearbyNpc());
        getCommand("StopCompanionRotation").setExecutor(new StopCompanionRotation(this));
        getCommand("DisplayInterruptBar").setExecutor(new DisplayInterruptBar(this));

        AbilityInventory abilityInventory;
        this.getServer().getPluginManager().registerEvents(abilityInventory = new AbilityInventory(this), this);
        this.getServer().getPluginManager().registerEvents(new EquipmentInventory(this), this);
        this.getServer().getPluginManager().registerEvents(new ClassSelectInventory(this), this);

        SpecInventory specInventory = abilityInventory.getSpecInventory();
        this.getServer().getPluginManager().registerEvents(specInventory, this);

        this.getServer().getPluginManager().registerEvents(new InventoryEventListener(this), this);
        this.getServer().getPluginManager().registerEvents(new GeneralEventListener(this), this);
        this.getServer().getPluginManager().registerEvents(new MMListeners(this), this);

        NaturalRegenTick regenTick = new NaturalRegenTick(this, abilityManager);
        regenTick.runTaskTimer(this, 0, 40);
        RezTick rezTick = new RezTick(this);
        rezTick.runTaskTimer(this, 0, 20);
        TargetDistanceTick targetDistanceTick = new TargetDistanceTick(this);
        targetDistanceTick.runTaskTimer(this, 0, 20);
        DailyTick dailyTick = new DailyTick(this);
        dailyTick.runTaskTimer(this, 0, 1200);

        Bukkit.getLogger().info("Mystica Enabled");

        if(getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
            PapiHook.registerHook(this);
        }


        World island = Bukkit.getWorld("world");
        assert island != null;
        // WorldBorder border = island.getWorldBorder();
        //Location center = new Location(island, 94, 66, 118);
        //border.setCenter(center);
        //double size = 1775;
        //border.setSize(size);



        CreaturesAndCharactersManager creaturesAndCharactersManager = new CreaturesAndCharactersManager(this);
        try {
            creaturesAndCharactersManager.spawnAllNpcs();
        } catch (InvalidMobTypeException e) {
            throw new RuntimeException(e);
        }

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

            if(player.getWorld().getName().startsWith("tutorial_") && !profileManager.getAnyProfile(player).getMilestones().getMilestone("tutorial")){
                classSetter.setClass(player, "none");
            }

        }

        profileManager.saveProfilesToConfig();
        pathingManager.saveFolder();
        dailyData.saveFolder();
        Bukkit.getLogger().info("Mystica Disabled");
    }

    public static Mystica getPlugin(){return plugin;}

    public ProtocolManager getProtocolManager(){return protocolManager;}

    public ProfileManager getProfileManager(){
        return profileManager;
    }

    public PathingManager getPathingManager(){return pathingManager;}

    public ClassSetter getClassSetter(){
        return classSetter;
    }

    public StealthTargetBlacklist getStealthTargetBlacklist(){return stealthTargetBlacklist;}

    public FakePlayerTargetManager getFakePlayerTargetManager(){return fakePlayerTargetManager;}

    public TargetManager getTargetManager(){
        return targetManager;
    }

    public FakePlayerAiManager getFakePlayerAiManager(){return fakePlayerAiManager;}

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

    public QuestInventory getQuestInventory(){return questInventory;}

    public DamageHealthBoard getDamageHealthBoard(){return damageHealthBoard;}

    public ProfileFileWriter getProfileFileWriter(){
        return profileFileWriter;
    }

    public FirstClearManager getFirstClearManager(){return firstClearManager;}

    public Locations getLocations(){return locations;}

    public QuestManager getQuestManager(){return questManager;}

    public DailyData getDailyData(){return dailyData;}

    public DailyEventManager getDailyEventManager(){return dailyEventManager;}

    public GravestoneManager getGravestoneManager(){return gravestoneManager;}

    public BossCastingManager getBossCastingManager(){return bossCastingManager;}

}
