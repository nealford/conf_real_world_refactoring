<%@ page import="java.util.*, com.nealford.art.memento.emotherearth.entity.Product" %>
<html>
<head>
<title>
CatalogView
</title>
</head>
<jsp:useBean id="product" scope="session"
             class="com.nealford.art.memento.emotherearth.entity.Product" />
<body>
<h1>
<%
    Integer start = (Integer) request.getAttribute("start");
    int s = start.intValue();
%>
Catalog of Items
</h1>
<TABLE border=1>
<TR><TH><a href="catalog?sort=id&start=<%= s %>">ID</a>
<TH><a href="catalog?sort=name&start=<%= s %>">NAME</a>
<TH><a href="catalog?sort=price&start=<%= s %>">PRICE</a>
<TH>Buy</TR>
<%
    List outputList = (List) request.getAttribute("outputList");
    Iterator iterator = outputList.iterator();
    while (iterator.hasNext()) {
        Product p = (Product) iterator.next();
        pageContext.setAttribute("p", p);
%>
<TR><TD><jsp:getProperty name="p" property="id" /></td>
<TD><jsp:getProperty name="p" property="name" /></td>
<TD align='right'><jsp:getProperty name="p" property="priceAsCurrency"/></td>
<TD><form action="showcart" method="post">
    Qty: <input id='<%= "qty" + p.getId() %>' type="text" size="3" name="quantity">
    <input type="hidden" name="id" value='<%= p.getId() %>'>
    <input id='<%= "submit" + p.getId() %>' type="submit" value="Add to cart"></form></TR>
<%
    }
%>
</TABLE>
<%-- show page links --%>
<p> Pages: &nbsp;
<%
    String[] pageList = (String[]) request.getAttribute("pageList");
    if (pageList != null) {
        for (int i = 0; i < pageList.length; i++) {
            out.println(pageList[i]);
        }
    }
%>
</body>
</html>
