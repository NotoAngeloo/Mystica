package me.angeloo.mystica.Components.Guis.Equipment;

import me.angeloo.mystica.Mystica;

public class EquipmentUpgradeManager {

    private final IdentifyInventory identifyInventory;
    private final ReforgeInventory reforgeInventory;
    private final RefineInventory refineInventory;
    private final UpgradeInventory upgradeInventory;

    public EquipmentUpgradeManager(Mystica main){
        identifyInventory = new IdentifyInventory(main, this);
        reforgeInventory = new ReforgeInventory(main, this);
        refineInventory = new RefineInventory(main, this);
        upgradeInventory = new UpgradeInventory(main, this);

    }

    public IdentifyInventory getIdentifyInventory() {
        return identifyInventory;
    }

    public ReforgeInventory getReforgeInventory() {
        return reforgeInventory;
    }

    public RefineInventory getRefineInventory() {
        return refineInventory;
    }

    public UpgradeInventory getUpgradeInventory() {
        return upgradeInventory;
    }
}
