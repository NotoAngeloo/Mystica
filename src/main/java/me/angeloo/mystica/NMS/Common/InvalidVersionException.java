package me.angeloo.mystica.NMS.Common;

import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;

public class InvalidVersionException  extends RuntimeException{

    public InvalidVersionException(String message){
        super(message);
    }

    @Override
    public void printStackTrace(){

        super.printStackTrace();

        Bukkit.getServer().shutdown();
    }

}
