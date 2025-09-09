package me.angeloo.mystica.Components.Quests;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.angeloo.mystica.Components.Items.MysticaItem;
import me.angeloo.mystica.Components.Quests.Objectives.KillObjective;
import me.angeloo.mystica.Components.Quests.Objectives.QuestObjective;
import me.angeloo.mystica.Components.Quests.Objectives.SpeakObjective;
import me.angeloo.mystica.Components.Quests.Progress.QuestProgress;
import me.angeloo.mystica.Components.Quests.QuestEnums.QuestType;
import me.angeloo.mystica.Components.Quests.QuestEnums.RewardType;
import me.angeloo.mystica.Components.Quests.Rewards.ItemReward;
import me.angeloo.mystica.Components.Quests.Rewards.QuestReward;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

public class QuestManager {

    private final Mystica main;

    private final Map<String, Quest> quests = new HashMap<>();

    public QuestManager(Mystica main){
        this.main = main;
    }

    public void loadQuests() {
        File questFolder = new File(main.getDataFolder(), "quests");
        if (!questFolder.exists()) {
            questFolder.mkdirs();

            //example file
            main.saveResource("quests/example_quests.yml", false);
        }

        File[] files = questFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for(File file : files){

            FileConfiguration config = YamlConfiguration.loadConfiguration(file);

            for(String questId : config.getKeys(false)){
                ConfigurationSection questSection = config.getConfigurationSection(questId);

                if(questSection == null){
                    continue;
                }

                String name = questSection.getString("name", questId);
                List<String> description = questSection.getStringList("description");
                List<String> progress = questSection.getStringList("description_progress");
                List<String> completed = questSection.getStringList("description_completed");

                List<QuestObjective> objectives = new ArrayList<>();
                ConfigurationSection objectiveSection = questSection.getConfigurationSection("objectives");

                if(objectiveSection != null){

                    for(String objId : objectiveSection.getKeys(false)){
                        ConfigurationSection objSection = objectiveSection.getConfigurationSection(objId);

                        if(objSection == null){
                            continue;
                        }

                        QuestType questType = QuestType.valueOf(objSection.getString("type"));

                        String mobName = objSection.getString("target");

                        int amount = objSection.getInt("amount");

                        MythicMob mobType = MythicBukkit.inst().getAPIHelper().getMythicMob(mobName);


                        switch (questType){
                            case Speak -> {
                                objectives.add(new SpeakObjective(objId, mobType));
                            }
                            case Kill -> {
                                objectives.add(new KillObjective(objId, mobType, amount));
                            }
                            default -> {
                                Bukkit.getLogger().info("unknown quest type: " + questType);
                            }
                        }

                    }
                }

                List<QuestReward> rewards = new ArrayList<>();

                ConfigurationSection rewardsSection = questSection.getConfigurationSection("rewards");
                if (rewardsSection != null) {
                    for (String rewardId : rewardsSection.getKeys(false)) {
                        ConfigurationSection rewardSection = rewardsSection.getConfigurationSection(rewardId);
                        if (rewardSection == null) continue;

                        RewardType rewardType = RewardType.valueOf(rewardSection.getString("type"));

                        switch (rewardType) {
                            case Item -> {

                                List<Map<?, ?>> itemMaps = rewardSection.getMapList("items");
                                for (Map<?, ?> rawMap : itemMaps) {
                                    Map<String, Object> itemMap = new HashMap<>();
                                    rawMap.forEach((k,v) -> itemMap.put(k.toString(), v));
                                    MysticaItem item = MysticaItem.deserialize(itemMap);

                                    //Bukkit.getLogger().info(String.valueOf(item.serialize()));


                                    rewards.add(new ItemReward(item));
                                }


                            }
                            default -> Bukkit.getLogger().info("Unknown reward type: " + rewardType);
                        }
                    }
                }

                //i think something else was supposed to be here
                Quest quest = new Quest(questId, name, description, progress, completed, objectives, rewards);
                registerQuest(quest);

            }


        }


        Bukkit.getLogger().info("loaded " + quests.entrySet().size() + " quests");
    }


    private void registerQuest(Quest quest){
        quests.put(quest.getId(), quest);
    }

    public Quest getQuest(String id){
        return quests.get(id);
    }


}
