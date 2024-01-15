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
import java.util.UUID;

public class TheLindwyrm {

    private final ProfileManager profileManager;


    public TheLindwyrm(Mystica main){
        profileManager = main.getProfileManager();

    }

    public void makeProfile(UUID uuid){

        Entity entity = Bukkit.getEntity(uuid);

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

        int hp = 350 + (100 * (level-1));
        int atk = 1 * level;
        int mag = 1 * level;
        int def = 4 * level;
        int mdef = 4 * level;

        Stats stats = new Stats(level, atk, mag, hp, 0, 0, 0, def, mdef, 0);
        Boolean isMovable = false;
        Boolean mortal = false;
        Boolean object = false;
        Boolean passive = false;

        float xpYield = .01f;

        Yield yield = new Yield(xpYield, dropItems(level));
        NonPlayerProfile nonPlayerProfile = new NonPlayerProfile(hp, stats, isMovable, mortal, object, passive, yield) {

            @Override
            public Bal getBal() {
                return null;
            }

            @Override
            public Boolean getIfDead() {
                return false;
            }

            @Override
            public Boolean getIfInCombat() {
                return false;
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
            public void setGearStats(StatsFromGear statsFromGear) {

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
        };
        profileManager.addToNonPlayerProfiles(uuid, nonPlayerProfile);


    }

    public List<ItemStack> dropItems(int level){

        List<ItemStack> itemDrops = new ArrayList<>();

        for(int i = 0; i<(10 * level); i++){
            itemDrops.add(new SoulStone());
        }


        return itemDrops;
    }

}
