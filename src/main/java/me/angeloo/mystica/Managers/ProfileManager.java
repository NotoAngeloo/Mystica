package me.angeloo.mystica.Managers;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.angeloo.mystica.Components.FakePlayerProfile;
import me.angeloo.mystica.Components.Inventories.Storage.MysticaBag;
import me.angeloo.mystica.Components.Inventories.Storage.MysticaBagCollection;
import me.angeloo.mystica.Components.Items.MysticaEquipment;
import me.angeloo.mystica.Components.Items.MysticaItem;
import me.angeloo.mystica.Components.ProfileComponents.NonPlayerStuff.Yield;
import me.angeloo.mystica.CustomEvents.UpdateMysticaPartyEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Components.NonPlayerProfile;
import me.angeloo.mystica.Components.PlayerProfile;
import me.angeloo.mystica.Components.Profile;
import me.angeloo.mystica.Components.ProfileComponents.*;
import me.angeloo.mystica.Utility.EquipmentSlot;
import me.angeloo.mystica.Utility.PlayerClass;
import me.angeloo.mystica.Utility.ProfileFileWriter;
import me.angeloo.mystica.Utility.SubClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.*;

public class ProfileManager {

    private final Mystica main;

    private final File subFolder;
    private final ProfileFileWriter profileFileWriter;
    private final PathingManager pathingManager;

    private final CreaturesAndCharactersManager creaturesAndCharactersManager;

    private final Map<UUID, PlayerProfile> playerProfiles = new HashMap<>();
    private final Map<UUID, FakePlayerProfile> fakePlayerProfileMap = new HashMap<>();
    private final Map<UUID, NonPlayerProfile> nonPlayerProfiles = new HashMap<>();

    private final Map<UUID, BossBar> playerResourceBar = new HashMap<>();
    private final Map<UUID, BossBar> playerTargetBar = new HashMap<>();
    private final Map<UUID, BossBar> playerTeamBar = new HashMap<>();
    private final Map<UUID, BossBar> playerStatusBar = new HashMap<>();

    private final Map<UUID, Boolean> companionCombatMap = new HashMap<>();
    private final Map<Player, List<UUID>> companionMap = new HashMap<>();
    private final Map<UUID, Player> companionsPlayer = new HashMap<>();

    private final Map<UUID, String> companionFace = new HashMap<>();
    private final Map<UUID, String> companionFace0 = new HashMap<>();
    private final Map<UUID, String> companionFace1 = new HashMap<>();
    private final Map<UUID, String> companionFace2 = new HashMap<>();
    private final Map<UUID, String> companionFace3 = new HashMap<>();

    private final Map<UUID, String> bossIcons = new HashMap<>();

    private final Map<String, Player> playerNameMap = new HashMap<>();
    private final Map<UUID, String> nonPlayerNameMap = new HashMap<>();
    private final Map<UUID, Location> bossHomes = new HashMap<>();
    private final Map<UUID, BukkitTask> furyTasks = new HashMap<>();
    private final Map<UUID, Boolean> resetProcessing = new HashMap<>();

    public ProfileManager(Mystica main) {
        this.main = main;

        creaturesAndCharactersManager = new CreaturesAndCharactersManager(main, this);


        File dataFolder = main.getDataFolder();
        String subfolderName = "playerdata"; // Subfolder name
        File subfolder = new File(dataFolder, subfolderName); // Path to the subfolder
        if (!subfolder.exists()) {
            subfolder.mkdirs(); // Create the subfolder if it doesn't exist
        }
        this.subFolder = subfolder;
        profileFileWriter = main.getProfileFileWriter();
        pathingManager = main.getPathingManager();
    }

    public void saveProfilesToConfig(){
        for(UUID uuid : playerProfiles.keySet()) {
            String id = uuid.toString();
            Profile profile = playerProfiles.get(uuid);
            Stats stats = profile.getStats();

            PlayerClass playerClass = profile.getPlayerClass();
            SubClass playerSubclass = profile.getPlayerSubclass();

            PlayerEquipment playerEquipment = profile.getPlayerEquipment();

            EquipSkills equipSkills = profile.getEquipSkills();

            PlayerBossLevel playerBossLevel = profile.getPlayerBossLevel();

            String playername = Bukkit.getOfflinePlayer(uuid).getName();

            MysticaBagCollection collection = profile.getMysticaBagCollection();

            YamlConfiguration config = profileFileWriter.createOrLoadProfileFile(uuid);

            config.set(id + ".name", playername);

            config.set(id + ".stats.level", stats.getLevel());

            config.set(id + ".class", playerClass.toString());
            config.set(id + ".subclass", playerSubclass.toString());

            for(MysticaItem equipment : playerEquipment.getEquipment()){

                if(equipment == null){
                    continue;
                }

                Map<String, Object> equipmentData = equipment.serialize();

                config.createSection(id + ".equipment." + equipment.identifier());

                if(equipmentData == null){
                    continue;
                }

                for(Map.Entry<String, Object> entry : equipmentData.entrySet()){
                    config.set(id + ".equipment." + equipment.identifier() + "." + entry.getKey(), entry.getValue());
                }

            }



            config.set(id + ".skill_slots.0", equipSkills.getAnySlot()[0]);
            config.set(id + ".skill_slots.1", equipSkills.getAnySlot()[1]);
            config.set(id + ".skill_slots.2", equipSkills.getAnySlot()[2]);
            config.set(id + ".skill_slots.3", equipSkills.getAnySlot()[3]);
            config.set(id + ".skill_slots.4", equipSkills.getAnySlot()[4]);
            config.set(id + ".skill_slots.5", equipSkills.getAnySlot()[5]);
            config.set(id + ".skill_slots.6", equipSkills.getAnySlot()[6]);
            config.set(id + ".skill_slots.7", equipSkills.getAnySlot()[7]);

            config.set(id + ".boss_level", playerBossLevel.getBossLevel());

            for(Map.Entry<String, Boolean> entry : profile.getMilestones().getAllMilestones().entrySet()){
                config.set(id + ".milestones." + entry.getKey(), entry.getValue());
            }

            for(int i = 0; i<collection.getMysticaBagAmount(); i++){
                MysticaBag bag = collection.getBag(i);
                config.createSection(id + ".bag." + i);

                for(MysticaItem item : bag.getBag()){

                    config.createSection(id + ".bag." + i + "." + item.identifier());

                    Map<String, Object> itemData = item.serialize();

                    if(item.serialize() == null){
                        continue;
                    }

                    for(Map.Entry<String, Object> entry : itemData.entrySet()){
                        config.set(id + ".bag." + i + "." + item.identifier() + "." + entry.getKey(), entry.getValue());
                    }


                }


            }


            profileFileWriter.saveProfileFile(uuid, config);
        }
    }

    public void loadProfilesFromConfig(){

        File[] profileFiles = subFolder.listFiles();

        if(profileFiles != null){
            for(File file : profileFiles){

                if(file.isFile() && file.getName().endsWith(".yml")){
                    String fileName = file.getName();
                    String uuidString = fileName.replace(".yml", "");

                    UUID id;

                    try{
                        id = UUID.fromString(uuidString);
                    } catch (IllegalArgumentException exception){
                        continue;
                    }

                    YamlConfiguration config = profileFileWriter.createOrLoadProfileFile(id);


                    PlayerClass playerClass = PlayerClass.valueOf(config.getString(id + ".class"));
                    SubClass playerSubclass = SubClass.valueOf(config.getString(id + ".subclass"));

                    //stats
                    int level = config.getInt(id + ".stats.level");

                    Stats stats = new Stats(level,50,100,50,50, 1);
                    stats.setLevelStats(level, playerClass, playerSubclass);

                    int hp = stats.getHealth();

                    //equipment
                    PlayerEquipment playerEquipment = new PlayerEquipment(new MysticaItem[5]);


                    ConfigurationSection equipmentSection = config.getConfigurationSection(id + ".equipment");

                    if(equipmentSection != null){

                        for(String gearPiece : equipmentSection.getKeys(false)){

                            ConfigurationSection gearSection = config.getConfigurationSection(id + ".equipment." + gearPiece);
                            if(gearSection != null){
                                Map<String, Object> gearMap = new HashMap<>();

                                for(String key : gearSection.getKeys(false)){
                                    gearMap.put(key, gearSection.get(key));
                                }


                                MysticaEquipment mysticaEquipment = MysticaEquipment.deserialize(gearMap);

                                switch (mysticaEquipment.getEquipmentSlot()){
                                    case WEAPON -> {
                                        playerEquipment.setWeapon(mysticaEquipment);
                                    }
                                    case HEAD -> {
                                        playerEquipment.setHelmet(mysticaEquipment);
                                    }
                                    case CHEST -> {
                                        playerEquipment.setChestPlate(mysticaEquipment);
                                    }
                                    case LEGS -> {
                                        playerEquipment.setLeggings(mysticaEquipment);
                                    }
                                    case BOOTS -> {
                                        playerEquipment.setBoots(mysticaEquipment);
                                    }
                                }

                            }


                        }

                    }



                    StatsFromGear gearStats = new StatsFromGear(0,  0,  0, 0, 0);


                    //instead of this, get every individual skill
                    int[]slots = new int[8];
                    slots[0] = config.getInt(id + ".skill_slots.0");
                    slots[1] = config.getInt(id + ".skill_slots.1");
                    slots[2] = config.getInt(id + ".skill_slots.2");
                    slots[3] = config.getInt(id + ".skill_slots.3");
                    slots[4] = config.getInt(id + ".skill_slots.4");
                    slots[5] = config.getInt(id + ".skill_slots.5");
                    slots[6] = config.getInt(id + ".skill_slots.6");
                    slots[7] = config.getInt(id + ".skill_slots.7");

                    EquipSkills equipSkills = new EquipSkills(slots);

                    int bossLevel = config.getInt(id + ".boss_level");

                    Map<String, Boolean> allMilestones = new HashMap<>();

                    ConfigurationSection milestonesSection = config.getConfigurationSection(id + ".milestones");
                    if (milestonesSection != null) {
                        for (String milestone : milestonesSection.getKeys(false)) {
                            allMilestones.put(milestone, milestonesSection.getBoolean(milestone));
                        }
                    }

                    Milestones milestones = new Milestones(allMilestones);

                    PlayerBossLevel playerBossLevel = new PlayerBossLevel(bossLevel);

                    MysticaBagCollection mysticaBagCollection = new MysticaBagCollection(new ArrayList<>());
                    mysticaBagCollection.addBag();

                    //depending on the bags, add items???
                    ConfigurationSection allBagsSection = config.getConfigurationSection(id + ".bag");
                    if(allBagsSection != null){
                        for(String bagNumber : allBagsSection.getKeys(false)){
                            ConfigurationSection bagSection = config.getConfigurationSection(id + ".bag" + "." + bagNumber);
                            if(bagSection != null){
                                //check the number

                                if(Integer.parseInt(bagNumber) > mysticaBagCollection.getMysticaBagAmount()-1){
                                    mysticaBagCollection.addBag();
                                }

                                MysticaBag bag = mysticaBagCollection.getBag(Integer.parseInt(bagNumber));

                                //add items from config to this

                            }
                        }

                    }


                    PlayerProfile profile = new PlayerProfile(false, false, hp,
                            stats,
                            gearStats,
                            playerClass,
                            playerSubclass,
                            mysticaBagCollection,
                            playerEquipment,
                            new Skill_Level(0,0,0,0,0,0,0,0),
                            equipSkills,
                            playerBossLevel,
                            milestones) {

                        @Override
                        public Boolean getIsPassive() {
                            return false;
                        }

                        @Override
                        public Boolean getIsMovable() {
                            return true;
                        }

                        @Override
                        public Boolean getImmortality() {
                            return false;
                        }

                        @Override
                        public Boolean getIfObject() {
                            return false;
                        }

                        @Override
                        public Yield getYield() {
                            return null;
                        }

                        @Override
                        public Boolean fakePlayer() {
                            return false;
                        }

                        @Override
                        public void getVoidsOnDeath(Set<Player> players) {

                        }


                    };

                    playerProfiles.put(id, profile);

                }

            }
        }
    }

    public Profile getAnyProfile(LivingEntity entity){

        if(entity instanceof Player){
            return getPlayerProfile(entity.getUniqueId());
        }

        //check if its fake player as well
        if(nonPlayerProfiles.get(entity.getUniqueId()) == null && fakePlayerProfileMap.get(entity.getUniqueId()) == null){

            if(nonPlayerNameMap.containsKey(entity.getUniqueId())){
                String name = nonPlayerNameMap.get(entity.getUniqueId());

                creaturesAndCharactersManager.makeNpcProfile(name, entity.getUniqueId());
            }
            else{
                createNewDefaultNonPlayerProfile(entity.getUniqueId());
                //Bukkit.getLogger().info("creating new default profile for " + entity.getName());
            }

        }


        if(fakePlayerProfileMap.containsKey(entity.getUniqueId())){
            //Bukkit.getLogger().info("entity is fake player");
            return fakePlayerProfileMap.get(entity.getUniqueId());
        }

        return nonPlayerProfiles.get(entity.getUniqueId());
    }


    private Profile getPlayerProfile(UUID uuid){
        //returns null if doesnt exist

        if(playerProfiles.get(uuid) == null){
            createNewPlayerProfile(uuid);
        }

        return playerProfiles.getOrDefault(uuid, fallbackProfile());
    }

    private PlayerProfile fallbackProfile(){

        Stats stats = new Stats(1,1,1,1,1, 1);
        StatsFromGear gearStats = new StatsFromGear( 0, 0,0,0,0);

        int currentHealth = 1;


        MysticaBagCollection mysticaBagCollection = new MysticaBagCollection(new ArrayList<>());
        mysticaBagCollection.addBag();


        PlayerEquipment playerEquipment = new PlayerEquipment(new MysticaItem[5]);

        int[] defaultSkillSlots = new int[8];
        defaultSkillSlots[0] = 1;
        defaultSkillSlots[1] = 2;
        defaultSkillSlots[2] = 3;
        defaultSkillSlots[3] = 4;
        defaultSkillSlots[4] = 5;
        defaultSkillSlots[5] = 6;
        defaultSkillSlots[6] = 7;
        defaultSkillSlots[7] = 8;

        EquipSkills equipSkills = new EquipSkills(defaultSkillSlots);

        PlayerBossLevel playerBossLevel = new PlayerBossLevel(1);

        Milestones milestones = new Milestones(new HashMap<>());


        return new PlayerProfile(false, false, currentHealth,
                stats,
                gearStats,
                PlayerClass.NONE,
                SubClass.NONE,
                mysticaBagCollection,
                playerEquipment,
                new Skill_Level(0,0,0,0,0,0,0,0),
                equipSkills,
                playerBossLevel,
                milestones) {


            @Override
            public Boolean getIsPassive() {
                return false;
            }

            @Override
            public Boolean getIsMovable() {
                return true;
            }

            @Override
            public Boolean getImmortality() {
                return false;
            }

            @Override
            public Boolean getIfObject() {
                return false;
            }

            @Override
            public Yield getYield() {
                return null;
            }

            @Override
            public Boolean fakePlayer() {
                return false;
            }

            @Override
            public void getVoidsOnDeath(Set<Player> players) {

            }


        };
    }

    private void createNewPlayerProfile(UUID uuid){

        Player newPlayer = Bukkit.getPlayer(uuid);

        if(newPlayer == null){
            return;
        }

        Stats stats = new Stats(1,50,100,50,50, 1);
        StatsFromGear gearStats = new StatsFromGear( 0, 0,0,0,0);

        int currentHealth = 20;

        MysticaBagCollection mysticaBagCollection = new MysticaBagCollection(new ArrayList<>());
        mysticaBagCollection.addBag();
        PlayerEquipment playerEquipment = new PlayerEquipment(new MysticaItem[5]);

        Skill_Level skillLevel = new Skill_Level(
                0,0,0,0,0,0,0,0);

        int[] defaultSkillSlots = new int[8];
        defaultSkillSlots[0] = 1;
        defaultSkillSlots[1] = 2;
        defaultSkillSlots[2] = 3;
        defaultSkillSlots[3] = 4;
        defaultSkillSlots[4] = 5;
        defaultSkillSlots[5] = 6;
        defaultSkillSlots[6] = 7;
        defaultSkillSlots[7] = 8;

        EquipSkills equipSkills = new EquipSkills(defaultSkillSlots);

        PlayerBossLevel playerBossLevel = new PlayerBossLevel(1);

        Milestones milestones = new Milestones(new HashMap<>());

        PlayerProfile profile = new PlayerProfile(false, false, currentHealth,
                stats,
                gearStats,
                PlayerClass.NONE,
                SubClass.NONE,
                mysticaBagCollection,
                playerEquipment,
                skillLevel,
                equipSkills,
                playerBossLevel,
                milestones) {


            @Override
            public Boolean getIsPassive() {
                return false;
            }

            @Override
            public Boolean getIsMovable() {
                return true;
            }

            @Override
            public Boolean getImmortality() {
                return false;
            }

            @Override
            public Boolean getIfObject() {
                return false;
            }

            @Override
            public Yield getYield() {
                return null;
            }

            @Override
            public Boolean fakePlayer() {
                return false;
            }

            @Override
            public void getVoidsOnDeath(Set<Player> players) {

            }


        };
        playerProfiles.put(uuid, profile);

        newPlayer.setLevel(0);
        newPlayer.setExp(0);

        AttributeInstance maxHealthAttribute = newPlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        assert maxHealthAttribute != null;
        maxHealthAttribute.setBaseValue(20);


        World world = Bukkit.getWorld("world");
        Location spawnLoc = new Location(world,409,68,-564,25,5);
        newPlayer.teleport(spawnLoc);
        newPlayer.getWorld().setSpawnLocation(spawnLoc);

        newPlayer.getInventory().clear();
        newPlayer.sendMessage("You are playing a pre-release\nYour items and progress are subjected to being removed");


        //interactions stuff
        PluginManager pluginManager = Bukkit.getPluginManager();
        Plugin interactions =  pluginManager.getPlugin("interactions");
        if (interactions != null && interactions.isEnabled()) {
            Server server = Bukkit.getServer();

            server.dispatchCommand(server.getConsoleSender(), "interactions resetplayer " + newPlayer.getName() + " tutorial");

            //server.dispatchCommand(server.getConsoleSender(), "interactions resetplayer " + newPlayer.getName() + " newplayer");
            //server.dispatchCommand(server.getConsoleSender(), "interactions resetplayer " + newPlayer.getName() + " captain");
            //server.dispatchCommand(server.getConsoleSender(), "interactions resetplayer " + newPlayer.getName() + " HoLee");
        }

        //pathingManager.calculatePath(newPlayer, new Location(newPlayer.getWorld(), 64, 99, -350));
    }

    public void removePlayerProfile(Player player){

        if(player.isOnline()){
            player.kickPlayer("profile being removed");
        }

        new BukkitRunnable(){
            @Override
            public void run(){
                playerProfiles.remove(player.getUniqueId());
                playerNameMap.remove(player.getName());
            }
        }.runTaskLaterAsynchronously(main, 5);

    }

    public void createNewDefaultNonPlayerProfile(UUID uuid){
        Stats stats = new Stats(1,1,1,1,1,0);
        double currentHealth = 1;
        Yield yield = new Yield(0, null);
        NonPlayerProfile nonPlayerProfile = new NonPlayerProfile(false, currentHealth, stats, true, false, false, false, yield) {

            @Override
            public Boolean getIfDead() {

                Entity entity = Bukkit.getEntity(uuid);

                if(entity != null){
                    return entity.isDead();
                }

                return null;
            }

            @Override
            public Boolean getIfInCombat() {
                return null;
            }

            @Override
            public void setIfDead(Boolean ifDead) {

            }

            @Override
            public void setIfInCombat(Boolean ifInCombat) {

            }


            @Override
            public void setLevelStats(int level, String subclass) {

            }

            @Override
            public StatsFromGear getGearStats() {
                return null;
            }


            @Override
            public int getTotalHealth() {
                return getStats().getHealth();
            }

            @Override
            public int getTotalAttack() {
                return getStats().getAttack();
            }

            @Override
            public int getTotalDefense() {
                return getStats().getDefense();
            }

            @Override
            public int getTotalMagicDefense() {
                return getStats().getMagic_Defense();
            }

            @Override
            public int getTotalCrit() {
                return getStats().getCrit();
            }


            @Override
            public void setGearStats(StatsFromGear statsFromGear) {

            }

            @Override
            public PlayerClass getPlayerClass() {
                return PlayerClass.NONE;
            }

            @Override
            public void setPlayerClass(PlayerClass playerClass) {

            }

            @Override
            public SubClass getPlayerSubclass() {
                return SubClass.NONE;
            }

            @Override
            public void setPlayerSubclass(SubClass playerSubclass) {

            }

            @Override
            public MysticaBagCollection getMysticaBagCollection() {
                return null;
            }

            @Override
            public PlayerEquipment getPlayerEquipment() {
                return null;
            }

            @Override
            public PlayerBossLevel getPlayerBossLevel() {
                return null;
            }

            @Override
            public Skill_Level getSkillLevels() {
                return null;
            }

            @Override
            public EquipSkills getEquipSkills() {
                return null;
            }

            @Override
            public Boolean fakePlayer() {
                return false;
            }

            @Override
            public Milestones getMilestones() {
                return null;
            }

            @Override
            public void getVoidsOnDeath(Set<Player> players) {

            }


        };
        addToNonPlayerProfiles(uuid, nonPlayerProfile);
    }

    public void addToNonPlayerProfiles(UUID uuid, NonPlayerProfile profile){
        nonPlayerProfiles.put(uuid, profile);
    }
    public void addToFakePlayerProfileMap(UUID uuid, FakePlayerProfile profile){fakePlayerProfileMap.put(uuid, profile);}

    public Map<String, Player> getPlayerNameMap(){
        return playerNameMap;
    }
    public void addToPlayerNameMap(Player player){
        playerNameMap.put(player.getName(), player);
    }
    public boolean getIfEntityIsBoss(UUID uuid){return bossHomes.containsKey(uuid);}
    public void addToNonPlayerNameMap(String name, UUID id){
        nonPlayerNameMap.put(id, name);
    }


    public void setBossHome(UUID uuid){
        Entity entity = Bukkit.getEntity(uuid);

        LivingEntity boss = (LivingEntity) entity;

        assert boss != null;
        Location home = boss.getLocation();

        bossHomes.put(uuid, home);

    }

    public void startFuryTimer(UUID uuid, int time){

        if(furyTasks.containsKey(uuid)){
            furyTasks.get(uuid).cancel();
        }

        LivingEntity entity = (LivingEntity) Bukkit.getEntity(uuid);

        if(entity == null){
            return;
        }

        BukkitTask furyTask = new BukkitRunnable(){
            int count = 0;
            @Override
            public void run(){

                //check death here too
                if(entity.isDead()){
                    this.cancel();
                    return;
                }

                if(count>=time){

                    //Bukkit.getLogger().info("fury timer for " + uuid);
                    this.cancel();
                    Bukkit.getScheduler().runTask(main, ()->{
                        if(MythicBukkit.inst().getAPIHelper().isMythicMob(uuid)){
                            AbstractEntity abstractEntity = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(Bukkit.getEntity(uuid)).getEntity();
                            MythicBukkit.inst().getAPIHelper().getMythicMobInstance(Bukkit.getEntity(uuid)).signalMob(abstractEntity, "fury");
                        }
                    });
                    return;
                }

                count++;
            }
        }.runTaskTimerAsynchronously(main, 20, 20);

        furyTasks.put(uuid, furyTask);

    }

    public void resetBoss(UUID uuid){

        boolean isBoss = bossHomes.containsKey(uuid);

        if(!isBoss){
            return;
        }

        //Bukkit.getLogger().info("reseting boss");

        Entity entity = Bukkit.getEntity(uuid);

        LivingEntity boss = (LivingEntity) entity;

        Location home = bossHomes.get(uuid);

        assert boss != null;

        processReset(boss);

        if(furyTasks.containsKey(boss.getUniqueId())){
            furyTasks.get(boss.getUniqueId()).cancel();
            furyTasks.remove(boss.getUniqueId());
        }

        Profile profile = nonPlayerProfiles.get(uuid);
        double maxHealth = profile.getTotalHealth();
        profile.setCurrentHealth(maxHealth);
        AttributeInstance maxHealthAttribute = boss.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        assert maxHealthAttribute != null;
        boss.setHealth(maxHealthAttribute.getBaseValue());

        boss.setAI(false);
        boss.teleport(home);


        if(MythicBukkit.inst().getAPIHelper().isMythicMob(uuid)){

            new BukkitRunnable(){
                @Override
                public void run(){

                    if(!getIfResetProcessing(boss)){
                        this.cancel();
                        return;
                    }

                    Entity entity = Bukkit.getEntity(uuid);

                    if(entity == null){
                        this.cancel();
                        return;
                    }

                    LivingEntity boss = (LivingEntity) entity;

                    if(home.getWorld() == null){
                        this.cancel();
                        return;
                    }

                    AbstractEntity abstractEntity = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(boss).getEntity();
                    boss.teleport(home);
                    MythicBukkit.inst().getAPIHelper().getMythicMobInstance(boss).signalMob(abstractEntity, "reset");


                }
            }.runTaskTimer(main, 0, 2);

        }

    }

    private void processReset(LivingEntity entity){
        resetProcessing.put(entity.getUniqueId(), true);

        new BukkitRunnable(){
            @Override
            public void run(){
                resetProcessing.remove(entity.getUniqueId());
            }
        }.runTaskLaterAsynchronously(main, 80);
    }

    public boolean getIfResetProcessing(LivingEntity entity){
        return resetProcessing.getOrDefault(entity.getUniqueId(), false);
    }


    public void addCompanion(Player player, UUID companion){
        List<UUID> currentCompanions = getCompanions(player);
        currentCompanions.add(companion);
        companionMap.put(player, currentCompanions);
        companionsPlayer.put(companion, player);
        Bukkit.getLogger().info("Companion " + companion + " added to " + player.getName());
        Bukkit.getServer().getPluginManager().callEvent(new UpdateMysticaPartyEvent(player));

    }

    public List<UUID> getCompanions(Player player){
        return companionMap.getOrDefault(player, new ArrayList<>());
    }

    public Player getCompanionsPlayer(LivingEntity companion){
        return companionsPlayer.getOrDefault(companion.getUniqueId(), null);
    }

    public void removeCompanion(Player player, UUID companion){
        List<UUID> currentCompanions = getCompanions(player);
        currentCompanions.remove(companion);
        companionMap.put(player, currentCompanions);
        companionsPlayer.remove(companion);
        Bukkit.getLogger().info("Companion " + companion + " removed from " + player.getName());

        if(currentCompanions.isEmpty()){
            companionMap.remove(player);
        }

        Bukkit.getServer().getPluginManager().callEvent(new UpdateMysticaPartyEvent(player));
    }

    public void removeCompanions(Player player){


        List<UUID> currentCompanions = getCompanions(player);

        for(UUID companionId : currentCompanions){

            LivingEntity companion = (LivingEntity) Bukkit.getEntity(companionId);

            if(companion != null){
                companion.remove();
                companionsPlayer.remove(companionId);
                Bukkit.getLogger().info("Companion " + companion + " removed from " + player.getName());
            }

        }

        companionMap.remove(player);

        Bukkit.getServer().getPluginManager().callEvent(new UpdateMysticaPartyEvent(player));

    }

    public void transferCompanionsToLeader(Player player, Player newPlayer){
        List<UUID> companions = new ArrayList<>(getCompanions(player));

        if(!companions.isEmpty()){
            for(UUID companion : companions){
                addCompanion(newPlayer, companion);
                companionsPlayer.put(companion, newPlayer);
            }

            companionMap.remove(player);

        }

        Bukkit.getServer().getPluginManager().callEvent(new UpdateMysticaPartyEvent(player));
    }

    public void setPlayerResourceBar(Player player, BossBar resourceBar){
        playerResourceBar.put(player.getUniqueId(), resourceBar);
    }

    public void setPlayerTargetBar(Player player, BossBar targetBar){
        playerTargetBar.put(player.getUniqueId(), targetBar);
    }

    public void setPlayerTeamBar(Player player, BossBar teamBar){
        playerTeamBar.put(player.getUniqueId(), teamBar);
    }

    public void setPlayerStatusBar(Player player, BossBar statusBar){
        playerStatusBar.put(player.getUniqueId(), statusBar);
    }

    public BossBar getPlayerResourceBar(Player player){
        if(!playerResourceBar.containsKey(player.getUniqueId())){
            BossBar resourceBar = Bukkit.createBossBar("", BarColor.WHITE, BarStyle.SOLID);
            setPlayerResourceBar(player, resourceBar);
        }
        return playerResourceBar.get(player.getUniqueId());
    }

    public BossBar getPlayerTargetBar(Player player){
        if(!playerTargetBar.containsKey(player.getUniqueId())){
            BossBar targetBar = Bukkit.createBossBar("",BarColor.WHITE,BarStyle.SOLID);
            setPlayerTargetBar(player, targetBar);
        }
        return playerTargetBar.get(player.getUniqueId());
    }

    public BossBar getPlayerTeamBar(Player player){
        if(!playerTeamBar.containsKey(player.getUniqueId())){
            BossBar teamBar = Bukkit.createBossBar("",BarColor.WHITE,BarStyle.SOLID);
            setPlayerTeamBar(player, teamBar);
        }
        return playerTeamBar.get(player.getUniqueId());
    }

    public BossBar getPlayerStatusBar(Player player){
        if(!playerStatusBar.containsKey(player.getUniqueId())){
            BossBar statusBar = Bukkit.createBossBar("",BarColor.WHITE,BarStyle.SOLID);
            setPlayerStatusBar(player, statusBar);
        }
        return playerStatusBar.get(player.getUniqueId());
    }


    public boolean getIfCompanionInCombat(UUID companion){return companionCombatMap.getOrDefault(companion, false);}
    public void setCompanionCombat(UUID companion){companionCombatMap.put(companion, true);}
    public void removeCompanionCombat(UUID companion){companionCombatMap.remove(companion);}

    public void setCompanionFaces(UUID uuid, String companion){

        switch (companion.toLowerCase()){
            case "salmon":{
                companionFace.put(uuid, "\uE267");
                companionFace0.put(uuid, "\uE268");
                companionFace1.put(uuid, "\uE269");
                companionFace2.put(uuid, "\uE26A");
                companionFace3.put(uuid, "\uE26B");
                return;
            }
            case "slippy":{
                companionFace.put(uuid, "\uE26C");
                companionFace0.put(uuid, "\uE26D");
                companionFace1.put(uuid, "\uE26E");
                companionFace2.put(uuid, "\uE26F");
                companionFace3.put(uuid, "\uE270");
                return;
            }
            case "wings":{
                companionFace.put(uuid, "\uE271");
                companionFace0.put(uuid, "\uE272");
                companionFace1.put(uuid, "\uE273");
                companionFace2.put(uuid, "\uE274");
                companionFace3.put(uuid, "\uE275");
                return;
            }
            case "darwin":{
                companionFace.put(uuid, "\uE276");
                companionFace0.put(uuid, "\uE277");
                companionFace1.put(uuid, "\uE278");
                companionFace2.put(uuid, "\uE279");
                companionFace3.put(uuid, "\uE27A");
                return;
            }
        }

    }

    public String getCompanionFace(UUID uuid){
        return companionFace.getOrDefault(uuid, "\uE144");
    }

    public String getCompanionTeamFace(UUID uuid, int slot){

        switch (slot){
            case 0:{
                if(!companionFace0.containsKey(uuid)){
                    companionFace0.put(uuid, "\uE14E");
                }

                return companionFace0.get(uuid);
            }
            case 1:{
                if(!companionFace1.containsKey(uuid)){
                    companionFace1.put(uuid, "\uE179");
                }

                return companionFace1.get(uuid);
            }
            case 2:{
                if(!companionFace2.containsKey(uuid)){
                    companionFace2.put(uuid, "\uE1A4");
                }

                return companionFace2.get(uuid);
            }
            case 3:{
                if(!companionFace3.containsKey(uuid)){
                    companionFace3.put(uuid, "\uE1CF");
                }

                return companionFace3.get(uuid);
            }
        }

        return companionFace.getOrDefault(uuid, "\uE144");
    }

    public void clearCompanionFaces(UUID uuid){
        companionFace.remove(uuid);
        companionFace0.remove(uuid);
        companionFace1.remove(uuid);
        companionFace2.remove(uuid);
        companionFace3.remove(uuid);
    }

    public void setBossIcon(UUID uuid, String bossName){

        switch (bossName.toLowerCase()){
            case "lindwyrm":{
                bossIcons.put(uuid, "\uE04E");
                return;
            }
        }

    }

    public String getBossIcon(UUID uuid){
        return bossIcons.getOrDefault(uuid, "\uE1A3");
    }

    public CreaturesAndCharactersManager getCreaturesAndCharactersManager(){return creaturesAndCharactersManager;}
}
