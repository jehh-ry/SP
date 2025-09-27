<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Test Project</title>
</head>
<body>
    <h2>Test Project - Page d'accueil</h2>
    <p>Serveur time: <%= new java.util.Date() %></p>
    
    <h3>Test des servlets :</h3>
    <ul>
        <li><a href="urls">Page URLs (Servlet)</a></li>
        <li><a href="client">Page Client (Servlet)</a></li>
    </ul>
    
    <h3>Test framework :</h3>
    <%
        try {
            Class.forName("com.framework.url.UrlStorage");
            out.println("<p style='color:green;'>✓ Framework détecté</p>");
        } catch (ClassNotFoundException e) {
            out.println("<p style='color:red;'>✗ Framework non trouvé: " + e.getMessage() + "</p>");
        }
    %>
</body>
</html>