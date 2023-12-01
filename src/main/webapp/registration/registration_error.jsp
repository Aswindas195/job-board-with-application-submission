<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Error Page</title>
</head>
<body>

<%
    if (request.getAttribute("error") != null && (boolean) request.getAttribute("error")) {
%>
        <h2>Error:</h2>
        <p><%= request.getAttribute("errorMessage") %></p>
<%
    } else {
%>
        <h2>Unknown Error</h2>
<%
    }
%>

</body>
</html>
