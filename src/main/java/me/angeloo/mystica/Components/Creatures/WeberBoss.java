package me.angeloo.mystica.Components.Creatures;

import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.interfaces.PartiesAPI;
import com.alessiodp.parties.api.interfaces.Party;
import com.alessiodp.parties.api.interfaces.PartyPlayer;
import me.angeloo.mystica.Components.Items.SoulStone;
import me.angeloo.mystica.Components.NonPlayerProfile;
import me.angeloo.mystica.Components.ProfileComponents.*;
import me.angeloo.mystica.Components.ProfileComponents.NonPlayerStuff.Yield;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class WeberBoss {

    private final ProfileManager profileManager;

    public WeberBoss(Mystica main, ProfileManager profileManager){
        this.profileManager = profileManager;
    }

    public void makeProfile(UUID uuid){

        Entity entity = Bukkit.getEntity(uuid);

        if(entity == null){
            return;
        }

        Player theClosestPlayersLeader = null;

        double closestDistanceSquared = Double.MAX_VALUE;
        Player closestPlayer = null;


        for (Player player : entity.getWorld().getPlayers()) {
            double distanceSquared = player.getLocation().distanceSquared(entity.getLocation());
            if (distanceSquared < closestDistanceSquared) {
                closestDistanceSquared = distanceSquared;
                closestPlayer = player;
            }
        }

        PartiesAPI api = Parties.getApi();

        if(closestPlayer != null){
            PartyPlayer partyPlayer = api.getPartyPlayer(closestPlayer.getUniqueId());

            assert partyPlayer != null;
            if(partyPlayer.isInParty()){

                Party party = api.getParty(partyPlayer.getPartyId());

                assert party != null;
                UUID partyLeaderId = party.getLeader();

                assert partyLeaderId != null;

                theClosestPlayersLeader = Bukkit.getPlayer(partyLeaderId);


            }
            else{
                theClosestPlayersLeader = closestPlayer;
            }

            assert theClosestPlayersLeader != null;
            //Bukkit.getLogger().info(theClosestPlayersLeader.getName());

        }

        int level = 1;

        if(theClosestPlayersLeader != null){
            level = profileManager.getAnyProfile(theClosestPlayersLeader).getPlayerBossLevel().getBossLevel();
        }

        int hp = 10000 + (150 * (level-1));
        int atk = 60 + (35 * level-1);
        int def = 40 + (35 * level-1);
        int mdef = 50 + (45 * level-1);

        Stats stats = new Stats(level, atk, hp,  def, mdef, 0);
        Boolean isMovable = false;
        Boolean immortal = false;
        Boolean object = false;
        Boolean passive = false;

        float xpYield = 1.5f;

        Yield yield = new Yield(xpYield, dropItems(level));
        NonPlayerProfile nonPlayerProfile = new NonPlayerProfile(false, hp, stats, isMovable, immortal, passive, object, yield) {


            @Override
            public Boolean getIfInCombat() {
                return false;
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
            public void setGearStats(StatsFromGear statsFromGear) {

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
            public String getPlayerClass() {
                return null;
            }

            @Override
            public void setPlayerClass(String playerClass) {

            }

            @Override
            public String getPlayerSubclass() {
                return null;
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
        profileManager.addToNonPlayerProfiles(uuid, nonPlayerProfile);


    }

    public List<ItemStack> dropItems(int level){

        List<ItemStack> itemDrops = new ArrayList<>();

        /*for(int i = 0; i<=(10 * level); i++){
            itemDrops.add(new SoulStone());
        }*/

        //itemDrops.add(new OldUnidentifiedHelmet(level + 1));

        return itemDrops;
    }

}
