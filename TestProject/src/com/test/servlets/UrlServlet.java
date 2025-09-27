package com.test.servlets;

import com.framework.url.UrlStorage;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.List;

public class UrlServlet extends HttpServlet {

    // Vérifie si l'URL est valide
    private boolean isValidUrl(String url) {
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

    // Vérifie si la requête vient du serveur A (localhost)
    private boolean isRequestFromServerA(HttpServletRequest req) {
        String clientIP = req.getRemoteAddr();
        String serverIP = req.getLocalAddr();
        
        System.out.println("Client IP: " + clientIP);
        System.out.println("Server IP: " + serverIP);
        
        // Accepte seulement depuis localhost (serveur A)
        return "127.0.0.1".equals(clientIP) || 
               "0:0:0:0:0:0:0:1".equals(clientIP) ||
               "localhost".equals(req.getServerName());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        // Vérifie si c'est une requête API (paramètre format=json)
        String format = req.getParameter("format");
        if ("json".equals(format)) {
            // Retourne les URLs en JSON pour le client
            sendUrlsAsJson(resp);
            return;
        }
        
        // Sinon, affiche la page HTML normale (serveur A seulement)
        if (!isRequestFromServerA(req)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Acces reserve");
            return;
        }
        
        showHtmlPage(resp);
    }

    /**
     * Retourne les URLs en JSON (pour le client)
     */
    private void sendUrlsAsJson(HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        List<String> urls = UrlStorage.getUrls();
        PrintWriter out = resp.getWriter();
        
        out.print("{\"urls\": [");
        for (int i = 0; i < urls.size(); i++) {
            if (i > 0) out.print(", ");
            out.print("\"" + urls.get(i).replace("\"", "\\\"") + "\"");
        }
        out.print("]}");
    }

    /**
     * Affiche la page HTML (pour le serveur A)
     */
    private void showHtmlPage(HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        out.println("<html><head><title>Serveur A - Gestion URLs</title></head><body>");
        out.println("<h2>Serveur A - Gestion des URLs</h2>");
        
        List<String> urls = UrlStorage.getUrls();
        if (urls.isEmpty()) {
            out.println("<p>Aucune URL pour le moment</p>");
        } else {
            out.println("<h3>URLs disponibles (" + urls.size() + ")</h3>");
            out.println("<ul>");
            for (String url : urls) {
                out.println("<li><a href='" + url + "' target='_blank'>" + url + "</a></li>");
            }
            out.println("</ul>");
        }

        // Formulaire réservé au serveur A
        out.println("<h3>Ajouter une nouvelle URL</h3>");
        out.println("<form method='post' action='urls'>");
        out.println("URL (http:// ou https://): <input type='text' name='url' size='50' required/>");
        out.println("<input type='submit' value='Ajouter'/>");
        out.println("</form>");
        
        out.println("<hr/>");
        //out.println("<p><small>Endpoint API: <a href='urls?format=json'>urls?format=json</a></small></p>");

        out.println("</body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        // Vérifie que la requête vient du serveur A
        if (!isRequestFromServerA(req)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Acces refuse");
            return;
        }

        String urlParam = req.getParameter("url");
        
        // Vérifie que l'URL est valide
        if (!isValidUrl(urlParam)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "URL invalide - doit commencer par http:// ou https://");
            return;
        }

        String url = urlParam.trim();
        UrlStorage.addUrl(url);
        System.out.println("URL ajoutée par serveur A: " + url);
        
        // Redirection vers la méthode GET
        resp.sendRedirect("urls");
    }
}