package me.angeloo.mystica;

import io.lumine.mythic.api.exceptions.InvalidMobTypeException;
import me.angeloo.mystica.Components.ClassSkillItems.AllSkillItems;
import me.angeloo.mystica.Components.Commands.*;
import me.angeloo.mystica.Components.Inventories.*;
import me.angeloo.mystica.Components.Items.BagItem;
import me.angeloo.mystica.Components.Items.SoulStone;
import me.angeloo.mystica.Components.Items.StackableItemRegistry;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.NMS.Common.PacketInterface;
import me.angeloo.mystica.NMS.NMSVersion;
import me.angeloo.mystica.Tasks.*;
import me.angeloo.mystica.Utility.*;
import me.angeloo.mystica.Utility.DamageIndicator.DamageIndicatorApi;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;
import me.angeloo.mystica.Utility.Listeners.GeneralEventListener;
import me.angeloo.mystica.Utility.Listeners.InventoryEventListener;
import me.angeloo.mystica.Utility.Listeners.MMListeners;
import me.angeloo.mystica.Utility.Logic.DamageBoardPlaceholders;
import me.angeloo.mystica.Utility.Logic.PveChecker;
import me.angeloo.mystica.Utility.Logic.StealthTargetBlacklist;
import net.playavalon.mythicdungeons.api.MythicDungeonsService;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;


import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public final class Mystica extends JavaPlugin{

    private static Mystica plugin;

    private PacketInterface packetManager;

    private CreaturesAndCharactersManager creaturesAndCharactersManager;

    private ProfileManager profileManager;
    private ProfileFileWriter profileFileWriter;

    private MysticaPartyManager mysticaPartyManager;
    private MatchMakingManager matchMakingManager;

    private DailyData dailyData;
    private PathingManager pathingManager;

    private DamageBoardPlaceholders damageBoardPlaceholders;
    private HudManager hudManager;

    private DisplayWeapons displayWeapons;
    private ClassSetter classSetter;
    private GearReader gearReader;
    private InventoryItemGetter inventoryItemGetter;
    private AllSkillItems allSkillItems;
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
    private CustomInventoryManager customInventoryManager;
    private PveChecker pveChecker;
    private DamageCalculator damageCalculator;
    private ChangeResourceHandler changeResourceHandler;
    private Locations locations;
    
    private AbilityInventory abilityInventory;
    private SpecInventory specInventory;
    private IdentifyInventory identifyInventory;
    private ReforgeInventory reforgeInventory;
    private RefineInventory refineInventory;
    private UpgradeInventory upgradeInventory;
    private EquipmentInventory equipmentInventory;
    private MatchmakingInventory matchmakingInventory;

    private FirstClearManager firstClearManager;

    public static final List<Integer> TASKS_ID = new ArrayList<>();
    private DamageIndicatorApi api;

    public static Color assassinColor = new java.awt.Color(214, 61, 207);
    public static Color elementalistColor = new Color(52, 151, 219);
    public static Color mysticColor = new Color(155, 120, 197);
    public static Color paladinColor = new Color(154, 125, 10);
    public static Color rangerColor = new Color(34, 111, 80);
    public static Color shadowKnightColor = new Color(213, 33, 3);
    public static Color warriorColor = new Color(214, 126, 61);

    public static Color menuColor = new Color(176, 159, 109);
    public static Color levelColor = new Color(0,102,0);


    public static Color commonColor = new Color(137, 141, 173);
    public static Color uncommonColor = new Color(138, 221, 31);
    public static Color rareColor = new Color(57, 164, 179);

    public static MythicDungeonsService dungeonsApi(){
        return Bukkit.getServer().getServicesManager().load(MythicDungeonsService.class);
    }

    @Override
    public void onEnable() {

        plugin = this;

        this.packetManager = NMSVersion.getCurrentVersion().getVersionFactory().create();

        StackableItemRegistry.register("SOUL STONE", SoulStone::deserialize);
        StackableItemRegistry.register("BAG", BagItem::deserialize);


        pathingManager = new PathingManager(this);
        pathingManager.createOrLoadFolder();

        dailyData = new DailyData(this);
        dailyData.createOrLoadFolder();

        profileFileWriter = new ProfileFileWriter(this);

        profileManager = new ProfileManager(this);
        profileManager.loadProfilesFromConfig();
        creaturesAndCharactersManager = profileManager.getCreaturesAndCharactersManager();

        mysticaPartyManager = new MysticaPartyManager(this);
        matchMakingManager = new MatchMakingManager(this);
        matchmakingInventory = matchMakingManager.getMatchmakingInventory();

        locations = new Locations(this);
        locations.initializeLocationals();

        inventoryItemGetter = new InventoryItemGetter();
        displayWeapons = new DisplayWeapons(this);

        gearReader = new GearReader(this);
        classSetter = new ClassSetter(this);
        pvpManager = new PvpManager(this);
        pveChecker = new PveChecker(this);

        stealthTargetBlacklist = new StealthTargetBlacklist();
        aggroManager = new AggroManager(this);
        bossCastingManager = new BossCastingManager(this);
        buffAndDebuffManager = new BuffAndDebuffManager(this);

        fakePlayerTargetManager = new FakePlayerTargetManager(this);
        targetManager = new TargetManager(this);

        gravestoneManager = new GravestoneManager();
        dpsManager = new DpsManager(this);
        changeResourceHandler = new ChangeResourceHandler(this);


        damageCalculator = new DamageCalculator(this);

        abilityManager = new AbilityManager(this);
        combatManager = abilityManager.getCombatManager();

        deathManager = new DeathManager(this);
        allSkillItems = abilityManager.getAllSkillItems();


        hudManager = new HudManager(this);
        damageBoardPlaceholders = hudManager.getDamageBoardPlaceholders();

        fakePlayerAiManager = new FakePlayerAiManager(this);

        customInventoryManager = new CustomInventoryManager();
        abilityInventory = new AbilityInventory(this);
        specInventory = abilityInventory.getSpecInventory();
        identifyInventory = new IdentifyInventory(this);
        reforgeInventory = new ReforgeInventory(this);
        refineInventory = new RefineInventory(this);
        upgradeInventory = new UpgradeInventory(this);
        equipmentInventory = new EquipmentInventory(this);

        firstClearManager = new FirstClearManager(this);
        firstClearManager.createOrLoadFolder();

        aggroTick = new AggroTick(this);

        getCommand("ToggleGlobalPvp").setExecutor(new ToggleGlobalPvp(this));
        getCommand("SeeRawDamage").setExecutor(new SeeRawDamage(this));
        getCommand("MysticaDamage").setExecutor(new MysticaDamage(this));
        getCommand("MysticaEffect").setExecutor(new MysticaEffect(this));
        getCommand("StartFuryTimer").setExecutor(new StartFuryTimer(this));
        getCommand("Equipment").setExecutor(new Equipment(this));
        getCommand("Trash").setExecutor(new Trash());
        getCommand("ClassSelect").setExecutor(new ClassSelect(this));
        getCommand("GearSwap").setExecutor(new GearSwap());
        getCommand("WhatAreMyStats").setExecutor(new WhatAreMyStats(this));
        getCommand("ToggleImmunity").setExecutor(new ToggleImmunity(this));
        getCommand("Reforge").setExecutor(new Reforge(this));
        getCommand("Refine").setExecutor(new Refine(this));
        getCommand("Upgrade").setExecutor(new Upgrade(this));
        getCommand("Identify").setExecutor(new Identify(this));
        getCommand("ManualSave").setExecutor(new ManualSave(this));
        getCommand("DeleteProfile").setExecutor(new DeleteProfile(this));
        getCommand("SetMileStone").setExecutor(new SetMilestone(this));
        getCommand("MysticaInteractions").setExecutor(new MysticaInteractions(this));
        getCommand("PathTool").setExecutor(new PathTool());
        getCommand("DisplayPath").setExecutor(new DisplayPath(this));
        getCommand("SavePaths").setExecutor(new SavePaths(this));
        getCommand("BossLevel").setExecutor(new BossLevel(this));
        getCommand("HitValidCheck").setExecutor(new HitValidCheck(this));
        getCommand("SetCaution").setExecutor(new SetCaution(this));
        getCommand("SignalNearbyNpc").setExecutor(new SignalNearbyNpc());
        getCommand("StopCompanionRotation").setExecutor(new StopCompanionRotation(this));
        getCommand("DisplayInterruptBar").setExecutor(new DisplayInterruptBar(this));
        getCommand("CompanionNeedsToInterrupt").setExecutor(new CompanionNeedsToInterrupt(this));
        getCommand("Matchmaking").setExecutor(new Matchmaking(this));
        getCommand("MysticaItem").setExecutor(new MysticaItem(this));


        this.getServer().getPluginManager().registerEvents(new ClassSelectInventory(this), this);
        this.getServer().getPluginManager().registerEvents(matchmakingInventory, this);


        //SpecInventory specInventory = abilityInventory.getSpecInventory();
        //this.getServer().getPluginManager().registerEvents(specInventory, this);

        this.getServer().getPluginManager().registerEvents(abilityInventory, this);
        this.getServer().getPluginManager().registerEvents(specInventory, this);
        this.getServer().getPluginManager().registerEvents(identifyInventory, this);
        this.getServer().getPluginManager().registerEvents(reforgeInventory, this);
        this.getServer().getPluginManager().registerEvents(refineInventory, this);
        this.getServer().getPluginManager().registerEvents(upgradeInventory, this);
        this.getServer().getPluginManager().registerEvents(equipmentInventory, this);

        this.getServer().getPluginManager().registerEvents(new InventoryEventListener(this), this);
        this.getServer().getPluginManager().registerEvents(new GeneralEventListener(this), this);
        this.getServer().getPluginManager().registerEvents(new MMListeners(this), this);




        //NaturalRegenTick regenTick = new NaturalRegenTick(this);
        //fregenTick.runTaskTimer(this, 0, 40);
        RezTick rezTick = new RezTick(this);
        rezTick.runTaskTimerAsynchronously(this, 0, 20);
        DailyTick dailyTick = new DailyTick(this);
        dailyTick.runTaskTimerAsynchronously(this, 0, 1200);

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




        try {
            creaturesAndCharactersManager.spawnAllNpcs();
        } catch (InvalidMobTypeException e) {
            throw new RuntimeException(e);
        }


        this.api = new DamageIndicatorApi(this);
    }


    @Override
    public void onDisable() {

        for (Player player : Bukkit.getOnlinePlayers()){

            boolean combatStatus = profileManager.getAnyProfile(player).getIfInCombat();
            boolean deathStatus = profileManager.getAnyProfile(player).getIfDead();

            if(deathStatus){
                deathManager.playerNowLive(player, false, null);
            }
            if(combatStatus) {
                combatManager.forceCombatEnd(player);
            }

            if(player.getWorld().getName().startsWith("tutorial_") && !profileManager.getAnyProfile(player).getMilestones().getMilestone("tutorial")){
                classSetter.setClass(player, PlayerClass.NONE);
            }

        }

        profileManager.saveProfilesToConfig();
        pathingManager.saveFolder();
        dailyData.saveFolder();
        Bukkit.getLogger().info("Mystica Disabled");
    }

    public static Mystica getPlugin(){return plugin;}

    public static List<Integer> getTasksId() {
        return TASKS_ID;
    }

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

    public CustomInventoryManager getInventoryManager(){
        return customInventoryManager;
    }

    public ProfileFileWriter getProfileFileWriter(){
        return profileFileWriter;
    }

    public FirstClearManager getFirstClearManager(){return firstClearManager;}

    public Locations getLocations(){return locations;}

    public DailyData getDailyData(){return dailyData;}

    public GravestoneManager getGravestoneManager(){return gravestoneManager;}

    public BossCastingManager getBossCastingManager(){return bossCastingManager;}

    public MatchmakingInventory getMatchmakingInventory(){return matchmakingInventory;}

    public MysticaPartyManager getMysticaPartyManager(){return mysticaPartyManager;}

    public MatchMakingManager getMatchMakingManager(){return matchMakingManager;}

    public InventoryItemGetter getItemGetter(){return inventoryItemGetter;}


    public IdentifyInventory getIdentifyInventory(){return identifyInventory;}

    public ReforgeInventory getReforgeInventory(){return reforgeInventory;}

    public RefineInventory getRefineInventory(){return refineInventory;}

    public UpgradeInventory getUpgradeInventory(){return upgradeInventory;}

    public EquipmentInventory getEquipmentInventory(){return equipmentInventory;}

    public GearReader getGearReader(){return gearReader;}

    public DisplayWeapons getDisplayWeapons(){return displayWeapons;}

    public AbilityInventory getAbilityInventory(){return abilityInventory;}

    public AllSkillItems getAllSkillItems(){return allSkillItems;}

    public HudManager getHudManager() {
        return hudManager;
    }



    @NotNull
    public PacketInterface getPacketInterface(){
        return packetManager;
    }
}
