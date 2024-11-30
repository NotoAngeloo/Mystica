package me.angeloo.mystica.Managers;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.angeloo.mystica.Components.FakePlayerProfile;
import me.angeloo.mystica.Components.ProfileComponents.NonPlayerStuff.Yield;
import me.angeloo.mystica.CustomEvents.AiSignalEvent;
import me.angeloo.mystica.CustomEvents.TargetBarShouldUpdateEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Components.NonPlayerProfile;
import me.angeloo.mystica.Components.PlayerProfile;
import me.angeloo.mystica.Components.Profile;
import me.angeloo.mystica.Components.ProfileComponents.*;
import me.angeloo.mystica.Utility.DisplayWeapons;
import me.angeloo.mystica.Utility.ProfileFileWriter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
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

    private final Map<UUID, PlayerProfile> playerProfiles = new HashMap<>();
    private final Map<UUID, FakePlayerProfile> fakePlayerProfileMap = new HashMap<>();
    private final Map<UUID, NonPlayerProfile> nonPlayerProfiles = new HashMap<>();

    private final Map<Player, List<LivingEntity>> companionMap = new HashMap<>();

    private final Map<String, Player> playerNameMap = new HashMap<>();
    private final Map<UUID, String> nonPlayerNameMap = new HashMap<>();
    private final Map<UUID, Location> bossHomes = new HashMap<>();
    private final Map<UUID, BukkitTask> furyTasks = new HashMap<>();
    private final Map<UUID, Boolean> resetProcessing = new HashMap<>();

    public ProfileManager(Mystica main) {
        this.main = main;
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

            String playerClass = profile.getPlayerClass();
            String playerSubclass = profile.getPlayerSubclass();

            ItemStack[] savedInv = profile.getSavedInv();

            PlayerBag playerBag = profile.getPlayerBag();
            PlayerEquipment playerEquipment = profile.getPlayerEquipment();

            Skill_Level skillLevel = profile.getSkillLevels();
            EquipSkills equipSkills = profile.getEquipSkills();

            PlayerBossLevel playerBossLevel = profile.getPlayerBossLevel();

            String playername = Bukkit.getOfflinePlayer(uuid).getName();

            YamlConfiguration config = profileFileWriter.createOrLoadProfileFile(uuid);

            config.set(id + ".name", playername);

            config.set(id + ".stats.level", stats.getLevel());

            config.set(id + ".class", playerClass);
            config.set(id + ".subclass", playerSubclass);

            config.set(id + ".savedInv", savedInv);

            //bag
            config.set(id + ".bag", playerBag.getItems());
            config.set(id + ".bagUnlocks", playerBag.getNumUnlocks());

            config.set(id + ".equipment", playerEquipment.getEquipment());

            config.set(id + ".skill_level.skill1_bonus", skillLevel.getSkill_1_Level_Bonus());
            config.set(id + ".skill_level.skill2_bonus", skillLevel.getSkill_2_Level_Bonus());
            config.set(id + ".skill_level.skill3_bonus", skillLevel.getSkill_3_Level_Bonus());
            config.set(id + ".skill_level.skill4_bonus", skillLevel.getSkill_4_Level_Bonus());
            config.set(id + ".skill_level.skill5_bonus", skillLevel.getSkill_5_Level_Bonus());
            config.set(id + ".skill_level.skill6_bonus", skillLevel.getSkill_6_Level_Bonus());
            config.set(id + ".skill_level.skill7_bonus", skillLevel.getSkill_7_Level_Bonus());
            config.set(id + ".skill_level.skill8_bonus", skillLevel.getSkill_8_Level_Bonus());

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

                    //player points
                    int bal = config.getInt(id + ".points.bal");
                    Bal points = new Bal(bal);

                    String playerClass = config.getString(id + ".class");
                    String playerSubclass = config.getString(id + ".subclass");

                    //stats
                    int level = config.getInt(id + ".stats.level");

                    Stats stats = new Stats(level,50,100,50,50, 1);
                    assert playerClass != null;
                    assert playerSubclass != null;
                    stats.setLevelStats(level, playerClass, playerSubclass);

                    int hp = stats.getHealth();

                    ItemStack[] savedInv = ((List<ItemStack>) config.get(id + ".savedInv")).toArray(new ItemStack[41]);

                    ArrayList<ItemStack> items = (ArrayList<ItemStack>) config.getList(id + ".bag", new ArrayList<ItemStack>());
                    int bagUnlocks = config.getInt(id + ".bagUnlocks");
                    PlayerBag playerBag = new PlayerBag(items, bagUnlocks);

                    //equipment
                    ItemStack[] equipment = ((List<ItemStack>) config.get(id + ".equipment")).toArray(new ItemStack[5]);
                    PlayerEquipment playerEquipment = new PlayerEquipment(equipment);

                    StatsFromGear gearStats = new StatsFromGear(0, 0, 0,  0, 0, 0);

                    int skill1_bonus = config.getInt(id + ".skill_level.skill1_bonus");
                    int skill2_bonus = config.getInt(id + ".skill_level.skill2_bonus");
                    int skill3_bonus = config.getInt(id + ".skill_level.skill3_bonus");
                    int skill4_bonus = config.getInt(id + ".skill_level.skill4_bonus");
                    int skill5_bonus = config.getInt(id + ".skill_level.skill5_bonus");
                    int skill6_bonus = config.getInt(id + ".skill_level.skill6_bonus");
                    int skill7_bonus = config.getInt(id + ".skill_level.skill7_bonus");
                    int skill8_bonus = config.getInt(id + ".skill_level.skill8_bonus");

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

                    Skill_Level skillLevel = new Skill_Level(
                            skill1_bonus, skill2_bonus, skill3_bonus, skill4_bonus, skill5_bonus, skill6_bonus, skill7_bonus, skill8_bonus);

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
                    PlayerProfile profile = new PlayerProfile(false, false, hp,
                            stats,
                            gearStats,
                            playerClass,
                            playerSubclass,
                            savedInv,
                            playerBag,
                            playerEquipment,
                            skillLevel,
                            equipSkills,
                            playerBossLevel,
                            milestones,
                            points) {

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

        CreaturesAndCharactersManager creaturesAndCharactersManager = new CreaturesAndCharactersManager(main);

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
        StatsFromGear gearStats = new StatsFromGear( 0, 0,0,0,0,0);

        int currentHealth = 1;

        PlayerBag playerBag = new PlayerBag(new ArrayList<>(), 0);
        PlayerEquipment playerEquipment = new PlayerEquipment(new ItemStack[5]);

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

        Bal bal = new Bal(0);

        return new PlayerProfile(false, false, currentHealth,
                stats,
                gearStats,
                "none",
                "none",
                new ItemStack[41],
                playerBag,
                playerEquipment,
                skillLevel,
                equipSkills,
                playerBossLevel,
                milestones,
                bal) {


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
        StatsFromGear gearStats = new StatsFromGear( 0, 0,0,0,0,0);

        int currentHealth = 20;

        PlayerBag playerBag = new PlayerBag(new ArrayList<>(), 0);
        PlayerEquipment playerEquipment = new PlayerEquipment(new ItemStack[5]);

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

        Bal bal = new Bal(0);

        PlayerProfile profile = new PlayerProfile(false, false, currentHealth,
                stats,
                gearStats,
                "none",
                "none",
                new ItemStack[41],
                playerBag,
                playerEquipment,
                skillLevel,
                equipSkills,
                playerBossLevel,
                milestones,
                bal) {


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

        newPlayer.setLevel(1);
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
        new DisplayWeapons(main).displayWeapons(newPlayer);
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
        }.runTaskLater(main, 5);

    }

    public void createNewDefaultNonPlayerProfile(UUID uuid){
        Stats stats = new Stats(1,1,1,1,1,0);
        double currentHealth = 1;
        Yield yield = new Yield(0, null);
        NonPlayerProfile nonPlayerProfile = new NonPlayerProfile(currentHealth, stats, true, false, false, false, yield) {
            @Override
            public Bal getBal() {
                return null;
            }

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
            public String getPlayerClass() {
                return "None";
            }

            @Override
            public void setPlayerClass(String playerClass) {

            }

            @Override
            public String getPlayerSubclass() {
                return "none";
            }

            @Override
            public void setPlayerSubclass(String playerSubclass) {

            }

            @Override
            public ItemStack[] getSavedInv() {
                return new ItemStack[0];
            }

            @Override
            public void setSavedInv(ItemStack[] inv) {

            }

            @Override
            public void removeSavedInv() {

            }

            @Override
            public PlayerBag getPlayerBag() {
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

                    if(MythicBukkit.inst().getAPIHelper().isMythicMob(uuid)){
                        AbstractEntity abstractEntity = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(Bukkit.getEntity(uuid)).getEntity();
                        MythicBukkit.inst().getAPIHelper().getMythicMobInstance(Bukkit.getEntity(uuid)).signalMob(abstractEntity, "fury");
                    }
                    this.cancel();
                    return;
                }

                count++;
            }
        }.runTaskTimer(main, 20, 20);

        furyTasks.put(uuid, furyTask);

    }

    public boolean resetBoss(UUID uuid){

        if(!bossHomes.containsKey(uuid)){
            return false;
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

        Bukkit.getServer().getPluginManager().callEvent(new TargetBarShouldUpdateEvent(boss));

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


                    AbstractEntity abstractEntity = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(boss).getEntity();

                    if(abstractEntity == null){
                        this.cancel();
                        return;
                    }

                    if(home.getWorld() == null){
                        this.cancel();
                        return;
                    }

                    boss.teleport(home);
                    MythicBukkit.inst().getAPIHelper().getMythicMobInstance(boss).signalMob(abstractEntity, "reset");

                }
            }.runTaskTimer(main, 0, 1);


        }

        return true;
    }

    private void processReset(LivingEntity entity){
        resetProcessing.put(entity.getUniqueId(), true);

        new BukkitRunnable(){
            @Override
            public void run(){
                resetProcessing.remove(entity.getUniqueId());
            }
        }.runTaskLater(main, 80);
    }

    public boolean getIfResetProcessing(LivingEntity entity){
        return resetProcessing.getOrDefault(entity.getUniqueId(), false);
    }

    public void addCompanion(Player player, LivingEntity companion){
        List<LivingEntity> currentCompanions = getCompanions(player);
        currentCompanions.add(companion);
        //Bukkit.getLogger().info(companion.getName() + " added to companions");
        companionMap.put(player, currentCompanions);
    }

    public List<LivingEntity> getCompanions(Player player){

        /*if(companionMap.containsKey(player)){
            Bukkit.getLogger().info(String.valueOf(companionMap.get(player).size()));
        }*/

        return companionMap.getOrDefault(player, new ArrayList<>());
    }

    public void updateCompanions(Player player){
        if(companionMap.containsKey(player)) {

            //Bukkit.getLogger().info("current companions " + companionMap.get(player));

            Set<LivingEntity> toRemove = new HashSet<>();
            for (LivingEntity companion : companionMap.get(player)) {
                if (companion.isDead()) {
                    toRemove.add(companion);
                }
            }

            for(LivingEntity companion : toRemove){
                removeCompanion(player, companion);
            }
        }
    }

    public Player getCompanionsPlayer(LivingEntity companion){
        for(Map.Entry<Player, List<LivingEntity>> entry : companionMap.entrySet()){
            Player player = entry.getKey();
            List<LivingEntity> companions = entry.getValue();
            if(companions.contains(companion)){
                return player;
            }
        }

        return null;
    }

    public void removeCompanion(Player player, LivingEntity companion){

        if(companionMap.containsKey(player)){
            List<LivingEntity> companions = new ArrayList<>(companionMap.get(player));
            companions.remove(companion);

            //Bukkit.getLogger().info("removing " + companion.getUniqueId());

            if(companions.isEmpty()){
                removeCompanions(player);
                return;
            }

            companionMap.put(player, companions);
        }

    }

    public void removeCompanions(Player player){
        for(LivingEntity companion : companionMap.get(player)){

            if(companion != null){
                companion.remove();
            }

        }
        //Bukkit.getLogger().info("removing from map");
        companionMap.remove(player);
    }



}
