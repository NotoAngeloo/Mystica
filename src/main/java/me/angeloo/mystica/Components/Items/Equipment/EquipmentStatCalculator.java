package me.angeloo.mystica.Components.Items.Equipment;

public class EquipmentStatCalculator {

    public EquipmentStats getBaseStats(
            MysticaEquipment item
    ) {

        EquipmentStats stats =
                new EquipmentStats();

        int level = item.getLevel();

        switch (item.getSlot()) {

            case WEAPON -> {

                stats.add(
                        StatType.ATTACK,
                        3 * level
                );

                stats.add(
                        StatType.HEALTH,
                        18 * level
                );

                stats.add(
                        StatType.DEFENSE,
                        4 * level
                );

                stats.add(
                        StatType.MAGIC_DEFENSE,
                        4 * level
                );
            }

            case HEAD -> {

                stats.add(
                        StatType.HEALTH,
                        50 * level
                );
            }

            case CHEST -> {

                stats.add(
                        StatType.HEALTH,
                        31 * level
                );

                stats.add(
                        StatType.DEFENSE,
                        4 * level
                );

                stats.add(
                        StatType.MAGIC_DEFENSE,
                        4 * level
                );
            }

            case LEGS -> {

                stats.add(
                        StatType.ATTACK,
                        4 * level
                );
            }

            case BOOTS -> {

                stats.add(
                        StatType.ATTACK,
                        2 * level
                );
            }
        }

        return stats;
    }

}
