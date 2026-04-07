package me.angeloo.mystica.NMS.Common;

@FunctionalInterface
public interface VersionFactory {

    PacketInterface create() throws InvalidVersionException;

    static VersionFactory InconfiguredVersion(){
        return () -> {
            throw new InvalidVersionException("Nms bullshit not properly configured for this version");
        };
    }

    public static class InconfiguredVersionException extends Exception{

    }


}
