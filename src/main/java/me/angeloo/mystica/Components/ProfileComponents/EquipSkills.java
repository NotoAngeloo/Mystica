package me.angeloo.mystica.Components.ProfileComponents;

import org.bukkit.Bukkit;

public class EquipSkills {

    public static final int EMPTY = 0;
    private static final int MAX_SLOTS = 8;

    private final int[] slots;

    public EquipSkills(int[] slots) {
        if (slots.length != MAX_SLOTS) {
            throw new IllegalArgumentException("Slots must be size " + MAX_SLOTS);
        }
        this.slots = slots;
    }

    public int getSkill(int slot) {
        validateSlot(slot);
        return slots[slot];
    }

    public void setSkill(int slot, int skillId) {
        validateSlot(slot);
        slots[slot] = skillId;
    }

    public boolean isEmpty(int slot) {
        return getSkill(slot) == EMPTY;
    }

    public int findSlot(int skillId) {
        for (int i = 0; i < MAX_SLOTS; i++) {
            if (slots[i] == skillId) {
                return i;
            }
        }
        return -1;
    }

    public int size() {
        return MAX_SLOTS;
    }

    private void validateSlot(int slot) {
        if (slot < 0 || slot >= MAX_SLOTS) {
            throw new IllegalArgumentException("Invalid slot: " + slot);
        }
    }

    /*private final int[] slots;

    //if taken by 0, there is no skill

    public EquipSkills(int[] slots) {
        this.slots = slots;
    }

    public int[] getAnySlot() {
        return slots;
    }

    private void setSlot0(int slot0){
        slots[0] = slot0;
    }

    private void setSlot1(int slot1){
        slots[1] = slot1;
    }

    private void setSlot2(int slot2){
        slots[2] = slot2;
    }

    private void setSlot3(int slot3){
        slots[3] = slot3;
    }

    private void setSlot4(int slot4){
        slots[4] = slot4;
    }

    private void setSlot5(int slot5){
        slots[5] = slot5;
    }

    private void setSlot6(int slot6){
        slots[6] = slot6;
    }

    private void setSlot7(int slot7){
        slots[7] = slot7;
    }

    public void setAnySlot(int slot, int toSkill){
        switch (slot) {
            case 0 -> {
                setSlot0(toSkill);
            }
            case 1 -> {
                setSlot1(toSkill);
            }
            case 2 -> {
                setSlot2(toSkill);
            }
            case 3 -> {
                setSlot3(toSkill);
            }
            case 4 -> {
                setSlot4(toSkill);
            }
            case 5 -> {
                setSlot5(toSkill);
            }
            case 6 -> {
                setSlot6(toSkill);
            }
            case 7 -> {
                setSlot7(toSkill);
            }
            default -> {
                Bukkit.getLogger().info("something went wrong when setting skill slots");
            }
        }
    }

    public int whichSlotIsTheSkillEquippedIn(int skillNumber){

        for(int i=0;i<=7; i++){

            if(skillNumber == slots[i]){
                return i;
            }
        }
        return -1;
    }*/
}
