package me.angeloo.mystica.NMS;

import me.angeloo.mystica.NMS.Common.PacketInterface;
import me.angeloo.mystica.NMS.Common.VersionFactory;
import me.angeloo.mystica.NMS.v1_20_3.PacketsV1_20_3;
import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum NMSVersion {


    /* 1.21.3 - 1.21.4 */ v1_21_R3(() -> new me.angeloo.mystica.NMS.v1_20_3.PacketsV1_20_3()),
    /* Other versions  */ UNKNOWN(VersionFactory.InconfiguredVersion());



    private static NMSVersion CURRENT_VERSION;

    static{
        CURRENT_VERSION = detectVersion();
    }

    NMSVersion(VersionFactory packetInterface) {
        this.packetInterface = packetInterface;
    }

    final VersionFactory packetInterface;

    public static NMSVersion getCurrentVersion() {
        return CURRENT_VERSION;
    }

    public VersionFactory getVersionFactory(){
        return packetInterface;
    }


    private static NMSVersion detectVersion(){
        String bukkitVersion = Bukkit.getServer().getBukkitVersion();

        Bukkit.getLogger().info("bukkit version is " + bukkitVersion);

        int majorVersion = Integer.parseInt(bukkitVersion.split("[.-]")[1]);

        if(majorVersion >= 20){
            switch (bukkitVersion){
                case "1.20.4-R0.1-SNAPSHOT":
                    return v1_21_R3;
                default:
                    return UNKNOWN;
            }
        }

        Matcher matcher = Pattern.compile("v\\d+_\\d+_R\\d+").matcher(Bukkit.getServer().getClass().getPackage().getName());

        if (!matcher.find()) {
            return UNKNOWN;
        }

        String nmsVersionName = matcher.group();
        try {
            return valueOf(nmsVersionName);
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }

    }

}
