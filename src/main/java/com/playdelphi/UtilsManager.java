package com.playdelphi;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.playdelphi.exceptions.PlayerNotFoundException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;
import java.util.UUID;

public class UtilsManager {
    private final DelphiVote plugin;
    private Logger logger;


    // Constuctor
    public UtilsManager(DelphiVote plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

    // Lookup player UUID from Mojang based on player name
    public UUID mojangUUIDLookup(String playerName) 
    throws PlayerNotFoundException {
        try {

            URL url = new URL("https://api.minecraftservices.com/minecraft/profile/lookup/name/" + playerName);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
                String uuidString = jsonObject.get("id").getAsString();

                // logger.info("Found UUID for player " + playerName + ": " + uuidString);

                // Mojang API returns UUID without hyphens, so we need to add them
                return UUID.fromString(uuidString.replaceFirst(
                    "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"
                ));
            } else {
                // This will stack with the general exception below
                logger.warning("Failed to find UUID for player " + playerName + ". Response code: " + responseCode); 
                // return null;
                throw new PlayerNotFoundException("Player not found: " + playerName);
            } 
        } catch (Exception e) {
            // Required for IO, Protocol, and other exceptions    
            logger.warning("mojangUUIDLookup Error: " + e.getMessage());
            throw new PlayerNotFoundException(playerName);
        }
        // return null;  // This will never be reached
    }
}


