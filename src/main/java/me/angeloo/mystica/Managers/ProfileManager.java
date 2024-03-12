package me.angeloo.mystica.Managers;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.angeloo.mystica.Components.ProfileComponents.NonPlayerStuff.Yield;
import me.angeloo.mystica.CustomEvents.HealthChangeEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Components.NonPlayerProfile;
import me.angeloo.mystica.Components.PlayerProfile;
import me.angeloo.mystica.Components.Profile;
import me.angeloo.mystica.Components.ProfileComponents.*;
import me.angeloo.mystica.Utility.GearReader;
import me.angeloo.mystica.Utility.ProfileFileWriter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
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

    private final File dataFolder;
    private final ProfileFileWriter profileFileWriter;

    private final Map<UUID, PlayerProfile> playerProfiles = new HashMap<>();
    private final Map<UUID, NonPlayerProfile> nonPlayerProfiles = new HashMap<>();

    private final Map<String, Player> playerNameMap = new HashMap<>();
    private final Map<UUID, String> nonPlayerNameMap = new HashMap<>();
    private final Map<UUID, Location> bossHomes = new HashMap<>();
    private final Map<UUID, BukkitTask> furyTasks = new HashMap<>();
    private final Map<UUID, Boolean> resetProcessing = new HashMap<>();


    public ProfileManager(Mystica main) {
        this.main = main;
        dataFolder = main.getDataFolder();
        profileFileWriter = main.getProfileFileWriter();
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

            config.set(id + ".skill_level.skill1", skillLevel.getSkill_1_Level());
            config.set(id + ".skill_level.skill2", skillLevel.getSkill_2_Level());
            config.set(id + ".skill_level.skill3", skillLevel.getSkill_3_Level());
            config.set(id + ".skill_level.skill4", skillLevel.getSkill_4_Level());
            config.set(id + ".skill_level.skill5", skillLevel.getSkill_5_Level());
            config.set(id + ".skill_level.skill6", skillLevel.getSkill_6_Level());
            config.set(id + ".skill_level.skill7", skillLevel.getSkill_7_Level());
            config.set(id + ".skill_level.skill8", skillLevel.getSkill_8_Level());

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

        File[] profileFiles = dataFolder.listFiles();

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

                    Stats stats = new Stats(level,50,50,100,100,1,1,50,50, 1);
                    assert playerSubclass != null;
                    stats.setLevelStats(level, playerSubclass);

                    int hp = stats.getHealth();
                    int mana = stats.getMana();

                    ItemStack[] savedInv = ((List<ItemStack>) config.get(id + ".savedInv")).toArray(new ItemStack[41]);

                    ArrayList<ItemStack> items = (ArrayList<ItemStack>) config.getList(id + ".bag", new ArrayList<ItemStack>());
                    int bagUnlocks = config.getInt(id + ".bagUnlocks");
                    PlayerBag playerBag = new PlayerBag(items, bagUnlocks);

                    //equipment
                    ItemStack[] equipment = ((List<ItemStack>) config.get(id + ".equipment")).toArray(new ItemStack[6]);
                    PlayerEquipment playerEquipment = new PlayerEquipment(equipment);

                    StatsFromGear gearStats = new StatsFromGear(0, 0, 0, 0, 0, 0, 0, 0, 0);


                    int skill1 = config.getInt(id + ".skill_level.skill1");
                    int skill2 = config.getInt(id + ".skill_level.skill2");
                    int skill3 = config.getInt(id + ".skill_level.skill3");
                    int skill4 = config.getInt(id + ".skill_level.skill4");
                    int skill5 = config.getInt(id + ".skill_level.skill5");
                    int skill6 = config.getInt(id + ".skill_level.skill6");
                    int skill7 = config.getInt(id + ".skill_level.skill7");
                    int skill8 = config.getInt(id + ".skill_level.skill8");

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

                    Skill_Level skillLevel = new Skill_Level(skill1, skill2, skill3, skill4, skill5, skill6, skill7, skill8,
                            skill1_bonus, skill2_bonus, skill3_bonus, skill4_bonus, skill5_bonus, skill6_bonus, skill7_bonus, skill8_bonus);

                    int bossLevel = config.getInt(id + ".boss_level");

                    Map<String, Boolean> allMilestones = new HashMap<>();

                    allMilestones.put("tutorial", config.getBoolean(id + ".milestones.tutorial"));
                    allMilestones.put("divive", config.getBoolean(id + ".milestones.divine"));
                    allMilestones.put("chaos", config.getBoolean(id + ".milestones.chaos"));
                    allMilestones.put("firstdungeon", config.getBoolean(id + ".milestones.firstdungeon"));

                    Milestones milestones = new Milestones(allMilestones);

                    PlayerBossLevel playerBossLevel = new PlayerBossLevel(bossLevel);
                    PlayerProfile profile = new PlayerProfile(false, false, hp, mana,
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
                        public Bal getBal() {
                            return null;
                        }

                        @Override
                        public Boolean getIsPassive() {
                            return false;
                        }

                        @Override
                        public Boolean getIsMovable() {
                            return true;
                        }

                        @Override
                        public void setIsMovable(Boolean which) {

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
                        public void setImmortality(boolean immortality) {

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

        if(nonPlayerProfiles.get(entity.getUniqueId()) == null){

            if(nonPlayerNameMap.containsKey(entity.getUniqueId())){
                String name = nonPlayerNameMap.get(entity.getUniqueId());

                creaturesAndCharactersManager.makeNpcProfile(name, entity.getUniqueId());
            }
            else{
                createNewDefaultNonPlayerProfile(entity.getUniqueId());
            }


        }

        return nonPlayerProfiles.get(entity.getUniqueId());
    }


    private Profile getPlayerProfile(UUID uuid){
        //returns null if doesnt exist

        if(playerProfiles.get(uuid) == null){
            createNewPlayerProfile(uuid);
        }

        return playerProfiles.get(uuid);
    }


    public void createNewPlayerProfile(UUID uuid){

        Stats stats = new Stats(1,50,50,100,100,1,1,50,50, 1);
        StatsFromGear gearStats = new StatsFromGear( 0, 0,0,0,0,0,0,0,0);

        int currentHealth = 20;
        int currentMana = 20;

        PlayerBag playerBag = new PlayerBag(new ArrayList<>(), 0);
        PlayerEquipment playerEquipment = new PlayerEquipment(new ItemStack[6]);

        Skill_Level skillLevel = new Skill_Level(1,1,1,1,1,1,1,1,
                0,0,0,0,0,0,0,0);

        EquipSkills equipSkills = new EquipSkills(new int[8]);

        PlayerBossLevel playerBossLevel = new PlayerBossLevel(1);

        Milestones milestones = new Milestones(new HashMap<>());

        Bal bal = new Bal(0);

        PlayerProfile profile = new PlayerProfile(false, false, currentHealth, currentMana,
                stats,
                gearStats,
                "None",
                "None",
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
            public void setIsMovable(Boolean which) {

            }

            @Override
            public Boolean getImmortality() {
                return false;
            }

            @Override
            public void setImmortality(boolean immortality) {

            }

            @Override
            public Boolean getIfObject() {
                return false;
            }

            @Override
            public Yield getYield() {
                return null;
            }
        };
        playerProfiles.put(uuid, profile);

        Player newPlayer = Bukkit.getPlayer(uuid);
        assert newPlayer != null;
        newPlayer.setLevel(1);
        newPlayer.setExp(0);

        AttributeInstance maxHealthAttribute = newPlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        assert maxHealthAttribute != null;
        maxHealthAttribute.setBaseValue(20);

        World world = Bukkit.getWorld("world");
        Location spawnLoc = new Location(world,74,100,-357,90,7);

        newPlayer.teleport(spawnLoc);
        newPlayer.getWorld().setSpawnLocation(spawnLoc);
        newPlayer.getInventory().clear();
        newPlayer.sendMessage("You are playing a pre-release\nYour items and progress are subjected to being removed");

        //interactions stuff
        PluginManager pluginManager = Bukkit.getPluginManager();
        Plugin interactions =  pluginManager.getPlugin("interactions");
        if (interactions != null && interactions.isEnabled()) {
            Server server = Bukkit.getServer();

            server.dispatchCommand(server.getConsoleSender(), "interactions resetplayer " + newPlayer.getName() + " newPlayer");
        }

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
        }.runTaskLater(main, 20);

    }

    public void createNewDefaultNonPlayerProfile(UUID uuid){
        Stats stats = new Stats(1,1,1,1,0,0,0,1,1,0);
        double currentHealth = 1;
        Yield yield = new Yield(0, null);
        NonPlayerProfile nonPlayerProfile = new NonPlayerProfile(currentHealth, stats, true, false, false, false, yield) {
            @Override
            public Bal getBal() {
                return null;
            }

            @Override
            public Boolean getIfDead() {
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
            public double getCurrentMana() {
                return 0;
            }

            @Override
            public void setCurrentMana(double currentMana) {

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
            public int getTotalMana() {
                return getStats().getMana();
            }

            @Override
            public int getTotalAttack() {
                return getStats().getAttack();
            }

            @Override
            public int getTotalMagic() {
                return getStats().getMagic();
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
            public int getTotalRegen() {
                return getStats().getRegen();
            }

            @Override
            public int getTotalManaRegen() {
                return getStats().getMana_Regen();
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
            public Milestones getMilestones() {
                return null;
            }

        };
        addToNonPlayerProfiles(uuid, nonPlayerProfile);
    }

    public void addToNonPlayerProfiles(UUID uuid, NonPlayerProfile profile){
        nonPlayerProfiles.put(uuid, profile);
    }

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

        if(MythicBukkit.inst().getAPIHelper().isMythicMob(uuid)){

            AbstractEntity abstractEntity = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(boss).getEntity();

            new BukkitRunnable(){
                @Override
                public void run(){
                    MythicBukkit.inst().getAPIHelper().getMythicMobInstance(boss).signalMob(abstractEntity, "reset");
                    if(!getIfResetProcessing(boss)){
                        this.cancel();
                    }
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

}
