package me.angeloo.mystica.Components.Quests;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.angeloo.mystica.Components.Items.MysticaItem;
import me.angeloo.mystica.Components.Quests.Objectives.QuestObjective;
import me.angeloo.mystica.Components.Quests.Objectives.SpeakObjective;
import me.angeloo.mystica.Components.Quests.QuestEnums.QuestType;
import me.angeloo.mystica.Components.Quests.QuestEnums.RewardType;
import me.angeloo.mystica.Components.Quests.Rewards.ItemReward;
import me.angeloo.mystica.Components.Quests.Rewards.QuestReward;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

                        MythicMob mobType = MythicBukkit.inst().getAPIHelper().getMythicMob(mobName);


                        switch (questType){
                            case Speak -> {
                                objectives.add(new SpeakObjective(mobType));
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



                //do something with this
                Quest quest = new Quest(questId, name, description, objectives, rewards);
                registerQuest(quest);

                //Bukkit.getLogger().info("quest: " + questId + " " + name + " " + description + " " + objectives + " " + rewards);
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
