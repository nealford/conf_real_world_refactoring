<%@ page errorPage="SQLErrorPage.jsp" %>
<html>
<head>
<title>
CheckOutView
</title>
</head>
<body bgcolor="#ffffc0">
<h1>
<%= request.getAttribute("user") %>, Thank you for shopping at eMotherEarth.com
</h1>
<h3>
Your confirmation number is <%= request.getAttribute("confirmation") %>
</h3>
<p>
<P>
<a href="welcome"> Click here to return to the store</a>
</P>
</P>
</body>
</html>
