package me.angeloo.mystica.Components.Hud.DamageIndicator;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DamageHudManager {

    private final Map<UUID, DamageHud> huds =
            new HashMap<>();

    private final DamageHudRenderer renderer =
            new DamageHudRenderer();

    public DamageHud get(Player player) {

        return huds.computeIfAbsent(
                player.getUniqueId(),
                uuid -> new DamageHud()
        );
    }

    public void addDamage(
            Player player,
            DamageEntry entry
    ) {

        get(player).addDamage(entry);
    }

    public void tick() {

        for(Player player : Bukkit.getOnlinePlayers()) {

            DamageHud hud =
                    huds.get(player.getUniqueId());

            if(hud == null) {
                continue;
            }

            hud.tick();

            if(!hud.isDirty()) {
                continue;
            }

            String render =
                    renderer.build(hud);

            player.sendTitle(
                    " ",
                    render,
                    0,
                    999999,
                    0
            );

            hud.clearDirty();
        }
    }
}
