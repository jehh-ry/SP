package com.test.servlets;

import com.framework.url.UrlFetcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class UrlClientServlet extends HttpServlet {

    private static final String SERVER_A_API = "http://localhost:8080/TestProject/urls?format=json";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        out.println("<html>");
        out.println("<head>");
        out.println("<title>Client - URLs du Serveur A</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; margin: 20px; }");
        out.println(".url-list { border: 1px solid #ccc; padding: 15px; margin: 10px 0; }");
        out.println(".url-item { margin: 5px 0; }");
        out.println(".error { color: red; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        
        out.println("<h2>Client - URLs disponibles</h2>");
        
        try {
            String jsonResult = UrlFetcher.fetch(SERVER_A_API);
            System.out.println("JSON reçu: " + jsonResult);
            
            // Parse le JSON et affiche proprement
            displayUrlsFromJson(out, jsonResult);
            
        } catch (Exception e) {
            out.println("<p class='error'>Erreur de connexion au serveur A: " + e.getMessage() + "</p>");
        }
        
        out.println("</body></html>");
    }

    /**
     * Affiche les URLs à partir du JSON
     */
    private void displayUrlsFromJson(PrintWriter out, String json) {
        if (json == null || json.trim().isEmpty()) {
            out.println("<p>Aucune donnée reçue du serveur A</p>");
            return;
        }
        
        try {
            // Nettoyage du JSON
            String cleanJson = json.trim().replaceAll("\\s+", " ");
            
            // Vérification de la structure JSON
            if (!cleanJson.contains("\"urls\"") || !cleanJson.contains("[")) {
                out.println("<p class='error'>Format de réponse invalide</p>");
                out.println("<pre>Réponse: " + cleanJson + "</pre>");
                return;
            }
            
            // Extraction des URLs
            String urlsSection = cleanJson.substring(cleanJson.indexOf("[\"") + 2);
            urlsSection = urlsSection.substring(0, urlsSection.indexOf("]"));
            
            String[] urls = urlsSection.split("\", \"");
            
            out.println("<div class='url-list'>");
            out.println("<h3>Liste des URLs (" + urls.length + ")</h3>");
            
            if (urls.length > 0 && !urls[0].isEmpty()) {
                out.println("<ul>");
                for (String url : urls) {
                    String cleanUrl = url.replace("\"", "").trim();
                    if (!cleanUrl.isEmpty()) {
                        out.println("<li class='url-item'>");
                        out.println("<a href='" + cleanUrl + "' target='_blank'>" + cleanUrl + "</a>");
                        out.println("</li>");
                    }
                }
                out.println("</ul>");
            } else {
                out.println("<p>📭 Aucune URL stockée sur le serveur A</p>");
            }
            out.println("</div>");
            
        } catch (Exception e) {
            out.println("<p class='error'>Erreur d'affichage des données: " + e.getMessage() + "</p>");
            out.println("<pre>Réponse brute: " + json + "</pre>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, 
                      "POST non supporté - Cette page est en lecture seule");
    }
}