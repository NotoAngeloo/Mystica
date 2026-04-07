package me.angeloo.mystica.Components.ProfileComponents;

import org.bukkit.Bukkit;

public class EquipSkills {

    private final int[] slots;

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
    }
}
