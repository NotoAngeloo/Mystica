package me.angeloo.mystica.Components.Hud;

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
    private final Map<UUID, String> team0faceMap = new HashMap<>();
    private final Map<UUID, String> team1faceMap = new HashMap<>();
    private final Map<UUID, String> team2faceMap = new HashMap<>();
    private final Map<UUID, String> team3faceMap = new HashMap<>();

    private final Map<UUID, String> squad0Face = new HashMap<>();
    private final Map<UUID, String> squad1Face = new HashMap<>();
    private final Map<UUID, String> squad2Face = new HashMap<>();

    private final String[] facePixel = {"\ue0d0","\ue0d1","\ue0d2","\ue0d3","\ue0d4","\ue0d5","\ue0d6","\ue0d7"};

    private final String[] teamPixel0 = {"\ue180","\ue181","\ue182","\ue183","\ue184","\ue185","\ue186","\ue187"};

    private final String[] teamPixel1 = {"\ue188","\ue189","\ue18a","\ue18b","\ue18c","\ue18d","\ue18e","\ue18f"};

    private final String[] teamPixel2 = {"\ue190","\ue191","\ue192","\ue193","\ue194","\ue195","\ue196","\ue197"};

    private final String[] teamPixel3 = {"\ue198","\ue199","\ue19a","\ue19b","\ue19c","\ue19d","\ue19e","\ue19f"};

    public SkinGrabber(){

    }


    private void constructFace(Player player){

        BufferedImage skin = getBufferedImage(player);

        if(skin == null){
            //-8 to not mess up alignment
            faceMap.put(player.getUniqueId(), "\uE0B2" + "\uF808");
            return;
        }

        StringBuilder face = new StringBuilder();

        for(int y = 0; y<8; y++){

            String currentUnicode = facePixel[y];

            for(int x = 0; x<8;x++){

                int rbg = skin.getRGB(8 + x, 8 + y);

                face.append(ChatColor.of(new Color(rbg)));
                face.append(currentUnicode);
                face.append(ChatColor.RESET);
                //-1
                face.append("\uF801");
            }


            //-16
            face.append("\uF809");


        }

        faceMap.put(player.getUniqueId(), String.valueOf(face));

    }

    private void constructSquadFace(Player player, int squadSlot) {

        BufferedImage skin = getBufferedImage(player);

        //test alignment this when i have more than a handful of testers
        if (skin == null) {
            //TODO:have a default steve skin of different ascents
            //squad0Face.put(player.getUniqueId(), )
            return;
        }

        StringBuilder face = new StringBuilder();

        switch (squadSlot) {
            case 0, 1, 2 -> {
                String currentUnicode = "\uE22C";

                for (int y = 0; y < 8; y++) {

                    switch (y) {
                        case 0 -> {
                            currentUnicode = "\uE22C";
                        }
                        case 1 -> {
                            currentUnicode = "\uE22D";
                        }
                        case 2 -> {
                            currentUnicode = "\uE22E";
                        }
                        case 3 -> {
                            currentUnicode = "\uE22F";
                        }
                        case 4 -> {
                            currentUnicode = "\uE230";
                        }
                        case 5 -> {
                            currentUnicode = "\uE231";
                        }
                        case 6 -> {
                            currentUnicode = "\uE232";
                        }
                        case 7 -> {
                            currentUnicode = "\uE233";
                        }
                    }

                    for (int x = 0; x < 8; x++) {

                        int rbg = skin.getRGB(8 + x, 8 + y);

                        face.append(ChatColor.of(new Color(rbg)));
                        face.append(currentUnicode);
                        face.append(ChatColor.RESET);
                        //-1
                        face.append("\uF801");
                    }


                    //-8
                    face.append("\uF808");


                }

            }
            case 3, 4, 5 -> {
                String currentUnicode = "\uE234";

                for (int y = 0; y < 8; y++) {

                    switch (y) {
                        case 0 -> {
                            currentUnicode = "\uE234";
                        }
                        case 1 -> {
                            currentUnicode = "\uE235";
                        }
                        case 2 -> {
                            currentUnicode = "\uE236";
                        }
                        case 3 -> {
                            currentUnicode = "\uE237";
                        }
                        case 4 -> {
                            currentUnicode = "\uE238";
                        }
                        case 5 -> {
                            currentUnicode = "\uE239";
                        }
                        case 6 -> {
                            currentUnicode = "\uE23A";
                        }
                        case 7 -> {
                            currentUnicode = "\uE23B";
                        }
                    }

                    for (int x = 0; x < 8; x++) {

                        int rbg = skin.getRGB(8 + x, 8 + y);

                        face.append(ChatColor.of(new Color(rbg)));
                        face.append(currentUnicode);
                        face.append(ChatColor.RESET);
                        //-1
                        face.append("\uF801");
                    }


                    //-8
                    face.append("\uF808");


                }

            }
            case 6, 7, 8 -> {
                String currentUnicode = "\uE23C";

                for (int y = 0; y < 8; y++) {

                    switch (y) {
                        case 0 -> {
                            currentUnicode = "\uE23C";
                        }
                        case 1 -> {
                            currentUnicode = "\uE23D";
                        }
                        case 2 -> {
                            currentUnicode = "\uE23E";
                        }
                        case 3 -> {
                            currentUnicode = "\uE23F";
                        }
                        case 4 -> {
                            currentUnicode = "\uE240";
                        }
                        case 5 -> {
                            currentUnicode = "\uE241";
                        }
                        case 6 -> {
                            currentUnicode = "\uE242";
                        }
                        case 7 -> {
                            currentUnicode = "\uE243";
                        }
                    }

                    for (int x = 0; x < 8; x++) {

                        int rbg = skin.getRGB(8 + x, 8 + y);

                        face.append(ChatColor.of(new Color(rbg)));
                        face.append(currentUnicode);
                        face.append(ChatColor.RESET);
                        //-1
                        face.append("\uF801");
                    }


                    //-8
                    face.append("\uF808");


                }

            }
        }

        switch (squadSlot) {
            case 0, 1, 2 -> {
                squad0Face.put(player.getUniqueId(), String.valueOf(face));
                return;
            }
            case 3, 4, 5 -> {
                squad1Face.put(player.getUniqueId(), String.valueOf(face));
                return;
            }
            case 6, 7, 8 -> {
                squad2Face.put(player.getUniqueId(), String.valueOf(face));
                return;
            }
        }
    }

    private void constructTeamFace(Player player, int teamSlot){

        BufferedImage skin = getBufferedImage(player);

        if(skin == null){
            //have a default steve skin of different ascents
            //-16 not not mess with alignment
            team0faceMap.put(player.getUniqueId(), "\uE14E" + "\uF809");
            team1faceMap.put(player.getUniqueId(), "\uE179" + "\uF809");
            team2faceMap.put(player.getUniqueId(), "\uE1A4" + "\uF809");
            team3faceMap.put(player.getUniqueId(), "\uE1CF" + "\uF809");
            return;
        }

        StringBuilder face = new StringBuilder();

        switch (teamSlot) {
            case 0 -> {


                for (int y = 0; y < 8; y++) {

                    String currentUnicode = teamPixel0[y];

                    for (int x = 0; x < 8; x++) {

                        int rbg = skin.getRGB(8 + x, 8 + y);

                        face.append(ChatColor.of(new Color(rbg)));
                        face.append(currentUnicode);
                        face.append(ChatColor.RESET);
                        //-1
                        face.append("\uF801");
                    }


                    //-16
                    face.append("\uF809");


                }

            }
            case 1 -> {

                for (int y = 0; y < 8; y++) {

                    String currentUnicode = teamPixel1[y];

                    for (int x = 0; x < 8; x++) {

                        int rbg = skin.getRGB(8 + x, 8 + y);

                        face.append(ChatColor.of(new Color(rbg)));
                        face.append(currentUnicode);
                        face.append(ChatColor.RESET);
                        //-1
                        face.append("\uF801");
                    }


                    //-16
                    face.append("\uF809");

                }

            }
            case 2 -> {

                for (int y = 0; y < 8; y++) {

                    String currentUnicode = teamPixel2[y];

                    for (int x = 0; x < 8; x++) {

                        int rbg = skin.getRGB(8 + x, 8 + y);

                        face.append(ChatColor.of(new Color(rbg)));
                        face.append(currentUnicode);
                        face.append(ChatColor.RESET);
                        //-1
                        face.append("\uF801");
                    }


                    //-16
                    face.append("\uF809");


                }

            }
            case 3 -> {

                for (int y = 0; y < 8; y++) {

                    String currentUnicode = teamPixel3[y];

                    for (int x = 0; x < 8; x++) {

                        int rbg = skin.getRGB(8 + x, 8 + y);

                        face.append(ChatColor.of(new Color(rbg)));
                        face.append(currentUnicode);
                        face.append(ChatColor.RESET);
                        //-1
                        face.append("\uF801");
                    }


                    //-16
                    face.append("\uF809");


                }

            }
        }

        switch (teamSlot) {
            case 0 -> {
                team0faceMap.put(player.getUniqueId(), String.valueOf(face));
                return;
            }
            case 1 -> {
                team1faceMap.put(player.getUniqueId(), String.valueOf(face));
                return;
            }
            case 2 -> {
                team2faceMap.put(player.getUniqueId(), String.valueOf(face));
                return;
            }
            case 3 -> {
                team3faceMap.put(player.getUniqueId(), String.valueOf(face));
                return;
            }
        }

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


        if(skinMap.containsKey(player.getUniqueId())){

            if(skinMap.get(player.getUniqueId()) == null){

                Bukkit.getLogger().info("skin was null, trying again");

                try {
                    skinMap.put(player.getUniqueId(), requestSkin(player));
                }
                catch (IOException e){
                    skinMap.put(player.getUniqueId(), null);
                }

            }

        }

        if(!skinMap.containsKey(player.getUniqueId())){

            Bukkit.getLogger().info("skin not saved yet, requesting");

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

    public String getTeamFace(Player player, int teamSlot){

        switch (teamSlot) {
            case 0 -> {
                if (!team0faceMap.containsKey(player.getUniqueId())) {
                    constructTeamFace(player, teamSlot);
                }

                return team0faceMap.get(player.getUniqueId());
            }
            case 1 -> {
                if (!team1faceMap.containsKey(player.getUniqueId())) {
                    constructTeamFace(player, teamSlot);
                }

                return team1faceMap.get(player.getUniqueId());
            }
            case 2 -> {
                if (!team2faceMap.containsKey(player.getUniqueId())) {
                    constructTeamFace(player, teamSlot);
                }

                return team2faceMap.get(player.getUniqueId());
            }
            case 3 -> {
                if (!team3faceMap.containsKey(player.getUniqueId())) {
                    constructTeamFace(player, teamSlot);
                }

                return team3faceMap.get(player.getUniqueId());
            }
        }

        return null;
    }

    public String getSquadFace(Player player, int slot){

        switch (slot) {
            case 0, 1, 2 -> {
                if (!squad0Face.containsKey(player.getUniqueId())) {
                    constructSquadFace(player, slot);
                }
                return squad0Face.get(player.getUniqueId());
            }
            case 3, 4, 5 -> {

                if (!squad1Face.containsKey(player.getUniqueId())) {
                    constructSquadFace(player, slot);
                }

                return squad1Face.get(player.getUniqueId());
            }
            case 6, 7, 8 -> {

                if (!squad2Face.containsKey(player.getUniqueId())) {
                    constructSquadFace(player, slot);
                }

                return squad2Face.get(player.getUniqueId());
            }
        }

        return null;
    }

    private BufferedImage requestSkin(Player player) throws IOException{


        URL profileUrl = getProfileUrl(player);

        //Bukkit.getLogger().info("profile url: " + profileUrl);

        HttpURLConnection connection = getConnection(profileUrl);;

        connection.setRequestMethod("GET");

        if(connection.getResponseCode() != 200){
            Bukkit.getLogger().info("response code not 200, returning null");
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


        //Bukkit.getLogger().info("properties: " + properties);
        //Bukkit.getLogger().info("value: " + value);

        byte[] decodedBytes = Base64.getDecoder().decode(value);
        String decoded = new String(decodedBytes);

        JsonObject json2 = JsonParser.parseString(decoded).getAsJsonObject();
        JsonObject textures = json2.getAsJsonObject("textures");
        JsonObject skin = textures.getAsJsonObject("SKIN");
        String skinUrlString = skin.get("url").getAsString();

        URL skinUrl = getSkinUrl(skinUrlString);

        //Bukkit.getLogger().info("skin url: " + skinUrl);

        return ImageIO.read(skinUrl);
    }


}
