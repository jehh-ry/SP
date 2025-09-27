package com.framework.url;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UrlFetcher {
    
    public static String fetch(String serverUrl) {
        // Valide l'URL avant de faire la requête
        if (!isValidUrl(serverUrl)) {
            return "{\"error\": \"URL invalide: " + serverUrl + "\"}";
        }
        
        StringBuilder result = new StringBuilder();
        try {
            System.out.println("🔗 Tentative de connexion: " + serverUrl);
            URL url = new URL(serverUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            
            // Ajoute des headers pour identification
            con.setRequestProperty("User-Agent", "ServerA-Client/1.0");
            con.setRequestProperty("Accept", "application/json");

            int status = con.getResponseCode();
            System.out.println("📊 Status code: " + status);

            if (status == 200) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        result.append(line);
                    }
                }
                System.out.println("Données reçues: " + result.length() + " caractères");
            } else {
                result.append("{\"error\": \"Erreur HTTP: ").append(status).append("\"}");
                System.out.println("Erreur HTTP: " + status);
            }
        } catch (Exception e) {
            System.out.println("Erreur dans UrlFetcher: " + e.getMessage());
            return "{\"error\": \"Erreur de connexion: " + e.getMessage() + "\"}";
        }
        return result.toString();
    }
    
    private static boolean isValidUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        try {
            new URL(url.trim());
            return url.startsWith("http://") || url.startsWith("https://");
        } catch (Exception e) {
            return false;
        }
    }
}