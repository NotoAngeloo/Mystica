package me.angeloo.mystica.Components.Items.Equipment;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class EquipmentStats {

    private final Map<StatType, Integer> stats;

    public EquipmentStats() {
        this.stats = new EnumMap<>(StatType.class);
    }

    public void add(
            StatType type,
            int amount
    ) {

        stats.merge(
                type,
                amount,
                Integer::sum
        );
    }

    public int get(
            StatType type
    ) {

        return stats.getOrDefault(type, 0);
    }

    public Map<StatType, Integer> asMap() {
        return Collections.unmodifiableMap(stats);
    }
}
