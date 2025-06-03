package me.angeloo.mystica.Utility;

import net.md_5.bungee.api.ChatColor;
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
    private final Map<UUID, String> faceMap = new HashMap<>();

    public SkinGrabber(){

    }


    private void constructFace(Player player){

        BufferedImage skin = getBufferedImage(player);

        if(skin == null){
            Bukkit.getLogger().info("skin null");
            //have a default stave skin
            return;
        }

        StringBuilder face = new StringBuilder();

        String currentUnicode = "\uE13B";

        for(int y = 0; y<8; y++){

            switch (y){
                case 0:{
                    currentUnicode = "\uE13B";
                    break;
                }
                case 1:{
                    currentUnicode = "\uE13C";
                    break;
                }
                case 2:{
                    currentUnicode = "\uE13D";
                    break;
                }
                case 3:{
                    currentUnicode = "\uE13E";
                    break;
                }
                case 4:{
                    currentUnicode = "\uE13F";
                    break;
                }
                case 5:{
                    currentUnicode = "\uE140";
                    break;
                }
                case 6:{
                    currentUnicode = "\uE141";
                    break;
                }
                case 7:{
                    currentUnicode = "\uE142";
                    break;
                }

            }

            for(int x = 0; x<8;x++){

                int rbg = skin.getRGB(8 + x, 8 + y);

                face.append(ChatColor.of(new Color(rbg)));
                face.append(currentUnicode);
                face.append(ChatColor.RESET);
                //-1
                face.append("\uF801");
            }


            //-24
            face.append("\uF809\uF808");


        }

        faceMap.put(player.getUniqueId(), String.valueOf(face));

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

    public String getFace(Player player){

        if(!faceMap.containsKey(player.getUniqueId())){
            constructFace(player);
        }

        return faceMap.get(player.getUniqueId());
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
