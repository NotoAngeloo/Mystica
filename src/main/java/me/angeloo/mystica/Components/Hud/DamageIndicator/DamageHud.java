package me.angeloo.mystica.Components.Hud.DamageIndicator;

import org.bukkit.Bukkit;

public class DamageHud {

    private static final int MAX_SLOTS = 13;

    private static final long TICK_MS = 50L;

    //should be 20
    private static final long LIFETIME =
            40L * TICK_MS;

    private final DamageSlot[] slots =
            new DamageSlot[MAX_SLOTS];

    private int overwriteIndex = 0;

    private boolean dirty = false;

    public DamageHud() {

        for(int i = 0; i < MAX_SLOTS; i++) {
            slots[i] = new DamageSlot();
        }
    }

    public void tick() {

        long now = System.currentTimeMillis();

        for(DamageSlot slot : slots) {

            if(!slot.isActive(now) &&
                    slot.getEntry() != null) {

                slot.clear();

                dirty = true;
            }
        }
    }

    public void addDamage(DamageEntry entry) {

        long now = System.currentTimeMillis();

        // Find inactive slot first
        for(DamageSlot slot : slots) {

            if(!slot.isActive(now)) {

                slot.set(entry, now + LIFETIME);

                dirty = true;
                return;
            }
        }

        // Overwrite if all full
        slots[overwriteIndex].set(
                entry,
                now + LIFETIME
        );

        overwriteIndex++;
        overwriteIndex %= MAX_SLOTS;

        dirty = true;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void clearDirty() {
        dirty = false;
    }

    public DamageSlot getSlot(int id) {

        if(id < 0 || id >= slots.length) {
            return null;
        }

        return slots[id];
    }

    public DamageSlot[] getSlots() {
        return slots;
    }

}
