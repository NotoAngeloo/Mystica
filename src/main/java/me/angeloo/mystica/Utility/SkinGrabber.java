package me.angeloo.mystica.Utility;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import com.google.gson.*;

import javax.imageio.ImageIO;
import java.net.URI;
import java.net.URL;
import java.util.*;

public class SkinGrabber {

    private final Map<UUID, BufferedImage> skinMap = new HashMap<>();

    public SkinGrabber(){

    }

    public void grabSkin(Player player){

        BufferedImage skin = getBufferedImage(player);

        if(skin == null){
            Bukkit.getLogger().info("skin null");
            return;
        }


        int rbg = skin.getRGB(8, 8);

        player.sendMessage(new Color(rbg, true) + " test");

        Bukkit.getLogger().info(new Color(rbg, true) + " test");
    }

    private URL getProfileUrl(Player player) throws IOException {
        return URI.create("https://sessionserver.mojang.com/session/minecraft/profile/" + player.getUniqueId()).toURL();
    }

    private HttpURLConnection getConnection(URL url) throws IOException {
        return (HttpURLConnection) url.openConnection();
    }

    private URL getSkinUrl(String urlString) throws IOException{
        return URI.create(urlString).toURL();
    }

    private BufferedImage getBufferedImage(Player player){

        if(!skinMap.containsKey(player.getUniqueId())){

            try {
                skinMap.put(player.getUniqueId(), requestSkin(player));
            }
            catch (IOException e){
                skinMap.put(player.getUniqueId(), null);
            }

        }

        return skinMap.get(player.getUniqueId());
    }

    private BufferedImage requestSkin(Player player) throws IOException{

        URL profileUrl = getProfileUrl(player);

        HttpURLConnection connection = getConnection(profileUrl);;

        connection.setRequestMethod("GET");


        if(connection.getResponseCode() != 200){
            return null;
        }

        Scanner scanner = new Scanner(connection.getInputStream());

        //this is the json, as a string. need to isolate the base 64 string
        String response = scanner.useDelimiter("\\A").next();
        scanner.close();


        JsonObject json = JsonParser.parseString(response).getAsJsonObject();
        JsonArray properties = json.getAsJsonArray("properties");
        JsonObject first = properties.get(0).getAsJsonObject();
        String value = first.get("value").getAsString();


        byte[] decodedBytes = Base64.getDecoder().decode(value);
        String decoded = new String(decodedBytes);

        JsonObject json2 = JsonParser.parseString(decoded).getAsJsonObject();
        JsonObject textures = json2.getAsJsonObject("textures");
        JsonObject skin = textures.getAsJsonObject("SKIN");
        String skinUrlString = skin.get("url").getAsString();

        URL skinUrl = getSkinUrl(skinUrlString);


        return ImageIO.read(skinUrl);
    }


}
