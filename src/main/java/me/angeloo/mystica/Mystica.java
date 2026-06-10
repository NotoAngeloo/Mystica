package me.angeloo.mystica;

import io.lumine.mythic.api.exceptions.InvalidMobTypeException;
import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.CombatContext;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.*;
import me.angeloo.mystica.Components.Commands.*;
import me.angeloo.mystica.Components.Creatures.CreaturesAndCharactersManager;
import me.angeloo.mystica.Components.EntityBehavior.AggroManager;
import me.angeloo.mystica.Components.EntityBehavior.AggroTick;
import me.angeloo.mystica.Components.EntityBehavior.FakePlayerAiManager;
import me.angeloo.mystica.Components.OldGuis.Equipment.EquipmentUpgradeManager;
import me.angeloo.mystica.Components.Hud.BossCastingManager;
import me.angeloo.mystica.Components.Hud.DamageIndicator.DamageHudManager;
import me.angeloo.mystica.Components.Hud.HudManager;
import me.angeloo.mystica.Components.Items.Equipment.EquipmentDisplayRenderer;
import me.angeloo.mystica.Components.MysticaGui.Assemble.GuiAssembler;
import me.angeloo.mystica.Components.MysticaGui.GuiListener;
import me.angeloo.mystica.Components.MysticaGui.GuiManager;
import me.angeloo.mystica.Components.MysticaGui.Render.GuiRenderer;
import me.angeloo.mystica.Components.MysticaGui.TestGuiCommand;
import me.angeloo.mystica.Components.Parties.MysticaPartyManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Components.ProfileComponents.StatCalculator;
import me.angeloo.mystica.Components.Quests.QuestManager;
import me.angeloo.mystica.Tasks.DailyTick;
import me.angeloo.mystica.Tasks.RezTick;
import me.angeloo.mystica.Utility.*;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;
import me.angeloo.mystica.Utility.Listeners.GeneralEventListener;
import me.angeloo.mystica.Utility.Listeners.InventoryEventListener;
import me.angeloo.mystica.Utility.Listeners.MMListeners;
import me.angeloo.mystica.Utility.Logic.PveChecker;
import me.angeloo.mystica.Utility.Logic.StealthTargetBlacklist;
import me.angeloo.mystica.Utility.MechanicCircle.CircleCommand;
import me.angeloo.mystica.Utility.ShapeRenderer.*;
import me.angeloo.mystica.Utility.ShapeRenderer.Gradient.GradientRenderers;
import me.angeloo.mystica.Utility.ShapeRenderer.Text.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;
import java.util.Map;

public final class Mystica extends JavaPlugin{

    private static Mystica plugin;

    private ProfileManager profileManager;
    private ProfileFileWriter profileFileWriter;

    private LayoutEngine layoutEngine;
    private StringRenderer stringRenderer;
    private GradientRenderers gradientRenderers;

    private CreaturesAndCharactersManager creaturesAndCharactersManager;

    private QuestManager questManager;

    private MysticaPartyManager mysticaPartyManager;

    private DailyData dailyData;
    private PathingManager pathingManager;

    private BossManager bossManager;

    private HudManager hudManager;
    private DamageHudManager damageHudManager;

    private ClassSetter classSetter;
    private GearReader gearReader;
    private InventoryItemGetter inventoryItemGetter;
    private StealthTargetBlacklist stealthTargetBlacklist;
    private FakePlayerTargetManager fakePlayerTargetManager;
    private TargetManager targetManager;
    private FakePlayerAiManager fakePlayerAiManager;
    private AggroTick aggroTick;
    private AggroManager aggroManager;
    private RezTick rezTick;
    private DpsManager dpsManager;
    private PvpManager pvpManager;
    private CombatManager combatManager;

    private StatusEffectManager statusEffectManager;

    private GravestoneManager gravestoneManager;
    private BossCastingManager bossCastingManager;
    private AbilityManager abilityManager;
    private CooldownManager cooldownManager;
    private DeathManager deathManager;

    private PveChecker pveChecker;
    private DamageCalculator damageCalculator;
    private ChangeResourceHandler changeResourceHandler;

    private EquipmentUpgradeManager equipmentUpgradeManager;

    private FirstClearManager firstClearManager;

    private StatCalculator statCalculator;

    private EquipmentDisplayRenderer equipmentDisplayRenderer;


    public static Color menuColor = new Color(176, 159, 109);
    public static Color levelColor = new Color(0,102,0);


    public static Color commonColor = new Color(137, 141, 173);
    public static Color uncommonColor = new Color(138, 221, 31);
    public static Color rareColor = new Color(57, 164, 179);
    public static Color epicColor = new Color(72, 18, 143);

    public Mystica() {
    }

    /*public static MythicDungeonsService dungeonsApi(){
        return Bukkit.getServer().getServicesManager().load(MythicDungeonsService.class);
    }*/

    @Override
    public void onEnable() {

        plugin = this;

        //StackableItemRegistry.register("SOUL STONE", SoulStone::deserialize);
        //StackableItemRegistry.register("BAG", BagItem::deserialize);
        //StackableItemRegistry.register("MYSTICAL CRYSTAL", MysticalCrystal::deserialize);

        PixelGlyphRegistry pixelGlyphRegistry = new PixelGlyphRegistry();
        AsciiFontAtlas fontAtlas = new AsciiFontAtlas();
        Map<Character, PixelMatrix> glyphMap = fontAtlas.getGlyphs();
        CharGlyphAtlas charGlyphAtlas = new CharGlyphAtlas();

        for(Character c : glyphMap.keySet()){
            charGlyphAtlas.register(c, glyphMap.get(c));
        }
        //Bukkit.getLogger().info(glyphMap.keySet().size() + " chars registered");

        CharGlyphPreComputer.precomputeAll(charGlyphAtlas, pixelGlyphRegistry);

        gradientRenderers = new GradientRenderers(pixelGlyphRegistry);

        layoutEngine = new LayoutEngine(charGlyphAtlas);
        stringRenderer = new StringRenderer();

        pathingManager = new PathingManager(this);
        pathingManager.createOrLoadFolder();

        dailyData = new DailyData(this);
        dailyData.createOrLoadFolder();

        statCalculator = new StatCalculator();

        profileFileWriter = new ProfileFileWriter(this);

        questManager = new QuestManager(this);
        questManager.loadQuests();

        profileManager = new ProfileManager(this);
        profileManager.loadProfilesFromConfig();
        bossManager = profileManager.getBossManager();
        creaturesAndCharactersManager = profileManager.getCreaturesAndCharactersManager();


        mysticaPartyManager = profileManager.getMysticaPartyManager();


        inventoryItemGetter = new InventoryItemGetter();
        //displayWeapons = new DisplayWeapons(this);

        gearReader = new GearReader(this);
        classSetter = new ClassSetter(this);
        pvpManager = new PvpManager(this);
        pveChecker = new PveChecker(this);

        stealthTargetBlacklist = new StealthTargetBlacklist();
        aggroManager = new AggroManager(this);
        bossCastingManager = new BossCastingManager(this);

        statusEffectManager = new StatusEffectManager(this);

        fakePlayerTargetManager = new FakePlayerTargetManager(this);
        gravestoneManager = new GravestoneManager();
        targetManager = new TargetManager(this);
        dpsManager = new DpsManager(this);

        damageHudManager = new DamageHudManager();

        changeResourceHandler = new ChangeResourceHandler(this);
        damageCalculator = new DamageCalculator(this);
        cooldownManager = new CooldownManager();

        CombatContext combatContext = new CombatContext(damageCalculator, changeResourceHandler, cooldownManager, pvpManager, pveChecker);
        statusEffectManager.setCombatContext(combatContext);

        equipmentDisplayRenderer = new EquipmentDisplayRenderer(this);
        abilityManager = new AbilityManager(this);
        combatManager = abilityManager.getCombatManager();


        rezTick = new RezTick(this);
        deathManager = new DeathManager(this);


        hudManager = new HudManager(this);

        fakePlayerAiManager = new FakePlayerAiManager(this);



        // = new CustomInventoryManager(this);

        //abilityInventory = new AbilityInventory(this);
        //specInventory = abilityInventory.getSpecInventory();
        //bagEquipmentFunctions = new BagEquipmentFunctions(this);
        //genericDiscard = new GenericDiscard(this);
        //devBoxInventory = new DevBoxInventory(this);

        //equipmentUpgradeManager = new EquipmentUpgradeManager(this);


        /*equipmentInventory = new EquipmentInventory(this);
        matchMakingManager = new MatchMakingManager(this);
        invitedInventory = new InvitedInventory(this);
        partyInventory = new PartyInventory(this);
        questAcceptInventory = new QuestAcceptInventory(this);
        pickQuestInventory = new PickQuestInventory(this, questAcceptInventory);
        dungeonSelect = new DungeonSelect(this);
        shopOrQuest = new ShopOrQuest(this);*/

        firstClearManager = new FirstClearManager(this);
        firstClearManager.createOrLoadFolder();

        aggroTick = new AggroTick(this);

        GuiAssembler guiAssembler = new GuiAssembler(this);
        GuiRenderer guiRenderer = new GuiRenderer(guiAssembler);
        GuiManager guiManager = new GuiManager(guiRenderer);
        GuiListener guiListener = new GuiListener(this, guiManager);

        getCommand("ToggleGlobalPvp").setExecutor(new ToggleGlobalPvp(this));
        getCommand("SeeRawDamage").setExecutor(new SeeRawDamage(this));
        getCommand("MysticaDamage").setExecutor(new MysticaDamage(this));
        getCommand("MysticaEffect").setExecutor(new MysticaEffect(this));
        getCommand("StartFuryTimer").setExecutor(new StartFuryTimer(this));
        getCommand("Equipment").setExecutor(new Equipment(this));
        getCommand("ClassSelect").setExecutor(new ClassSelectCommand(guiManager));
        getCommand("GearSwap").setExecutor(new GearSwap());
        getCommand("ToggleImmunity").setExecutor(new ToggleImmunity(this));
        getCommand("Reforge").setExecutor(new Reforge(this));
        getCommand("Refine").setExecutor(new Refine(this));
        getCommand("Upgrade").setExecutor(new Upgrade(this));
        getCommand("Identify").setExecutor(new Identify(this));
        getCommand("ManualSave").setExecutor(new ManualSave(this));
        getCommand("DeleteProfile").setExecutor(new DeleteProfile(this));
        getCommand("HitValidCheck").setExecutor(new HitValidCheck(this));
        getCommand("SetCaution").setExecutor(new SetCaution(this));
        getCommand("StopCompanionRotation").setExecutor(new StopCompanionRotation(this));
        getCommand("DisplayInterruptBar").setExecutor(new DisplayInterruptBar(this));
        getCommand("CompanionNeedsToInterrupt").setExecutor(new CompanionNeedsToInterrupt(this));
        getCommand("DungeonSelect").setExecutor(new DungeonFinder(this));
        getCommand("DevBox").setExecutor(new DevBox(this));
        getCommand("StarterKit").setExecutor(new StarterKit(this));
        getCommand("BossWarn").setExecutor(new BossWarn(this));
        getCommand("MysticaQuest").setExecutor(new MysticaQuest(this));
        getCommand("OpenNpcGui").setExecutor(new OpenNpcGui(this));
        getCommand("circle").setExecutor(new CircleCommand(this));


        //this.getServer().getPluginManager().registerEvents(dungeonSelect, this);


        getCommand("testgui").setExecutor(new TestGuiCommand(guiManager));

        this.getServer().getPluginManager().registerEvents(guiListener, this);

        /*this.getServer().getPluginManager().registerEvents(abilityInventory, this);
        this.getServer().getPluginManager().registerEvents(specInventory, this);
        this.getServer().getPluginManager().registerEvents(equipmentUpgradeManager.getIdentifyInventory(), this);
        this.getServer().getPluginManager().registerEvents(equipmentUpgradeManager.getReforgeInventory(), this);
        this.getServer().getPluginManager().registerEvents(equipmentUpgradeManager.getRefineInventory(), this);
        this.getServer().getPluginManager().registerEvents(equipmentUpgradeManager.getUpgradeInventory(), this);
        this.getServer().getPluginManager().registerEvents(equipmentInventory, this);
        this.getServer().getPluginManager().registerEvents(partyInventory, this);
        this.getServer().getPluginManager().registerEvents(invitedInventory, this);
        this.getServer().getPluginManager().registerEvents(questAcceptInventory,this);
        this.getServer().getPluginManager().registerEvents(shopOrQuest, this);
        this.getServer().getPluginManager().registerEvents(pickQuestInventory,this);
        this.getServer().getPluginManager().registerEvents(bagEquipmentFunctions, this);
        this.getServer().getPluginManager().registerEvents(genericDiscard, this);
        this.getServer().getPluginManager().registerEvents(devBoxInventory, this);*/

        this.getServer().getPluginManager().registerEvents(new InventoryEventListener(this), this);
        this.getServer().getPluginManager().registerEvents(new GeneralEventListener(this), this);
        this.getServer().getPluginManager().registerEvents(new MMListeners(this), this);




        DailyTick dailyTick = new DailyTick(this);
        dailyTick.runTaskTimerAsynchronously(this, 0, 1200);

        startStatusEffectTicker();
        startActionBarTicker();
        startDamageHudTicker();

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

    }

    public void startStatusEffectTicker(){
        Bukkit.getScheduler().runTaskTimer(this, statusEffectManager::tick, 1L,1L);
    }

    public void startActionBarTicker(){
        Bukkit.getScheduler().runTaskTimer(this, hudManager::hudTicker, 1L, 1L);
    }
    public void startDamageHudTicker(){Bukkit.getScheduler().runTaskTimer(this, damageHudManager::tick, 1L, 1L);}

    @Override
    public void onDisable() {

        rezTick.stopAll();

        for (Player player : Bukkit.getOnlinePlayers()){


            player.getInventory().clear();

            boolean combatStatus = profileManager.getAnyProfile(player).getIfInCombat();
            boolean deathStatus = profileManager.getAnyProfile(player).getIfDead();

            if(deathStatus){
                deathManager.playerNowLive(player, false, null);
            }
            if(combatStatus) {
                combatManager.forceCombatEnd(player);
            }

            player.setGameMode(GameMode.SURVIVAL);

        }

        profileManager.saveProfilesToConfig();
        pathingManager.saveFolder();
        dailyData.saveFolder();
        creaturesAndCharactersManager.cancelSpawnTasks();
        Bukkit.getLogger().info("Mystica Disabled");
    }

    public static Mystica getPlugin(){return plugin;}


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

    public RezTick getRezTick(){
        return rezTick;
    }

    public CombatManager getCombatManager(){
        return combatManager;
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

    public ProfileFileWriter getProfileFileWriter(){
        return profileFileWriter;
    }

    public FirstClearManager getFirstClearManager(){return firstClearManager;}

    public DailyData getDailyData(){return dailyData;}

    public GravestoneManager getGravestoneManager(){return gravestoneManager;}

    public BossCastingManager getBossCastingManager(){return bossCastingManager;}

    public MysticaPartyManager getMysticaPartyManager(){return mysticaPartyManager;}


    public InventoryItemGetter getItemGetter(){return inventoryItemGetter;}

    public EquipmentUpgradeManager getEquipmentUpgradeManager(){return equipmentUpgradeManager;}

    public GearReader getGearReader(){return gearReader;}

    public HudManager getHudManager() {
        return hudManager;
    }

    public QuestManager getQuestManager(){return questManager;}


    public StatusEffectManager getStatusEffectManager(){return statusEffectManager;}

    public BossManager getBossManager(){
        return bossManager;
    }

    public CooldownManager getCooldownManager(){
        return cooldownManager;
    }

    public StringRenderer getStringRenderer(){
        return stringRenderer;
    }

    public LayoutEngine getLayoutEngine(){
        return layoutEngine;
    }

    public DamageHudManager getDamageHudManager(){
        return damageHudManager;
    }

    public StatCalculator getStatCalculator(){
        return statCalculator;
    }

    public EquipmentDisplayRenderer getEquipmentDisplayRenderer(){
        return equipmentDisplayRenderer;
    }

    public GradientRenderers getGradientRenderers() {
        return gradientRenderers;
    }
}
