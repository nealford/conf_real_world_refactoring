<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.DriverManager" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.Statement" %>
<%@ page import="com.nealford.art.memento.emotherearth.entity.Product" %>
<%
    String driverClass =
            getServletContext().getInitParameter("driverClass");
    String dbUrl =
            getServletContext().getInitParameter("dbUrl");

    try {
        Class.forName(driverClass).newInstance();
    } catch (Exception e) {
        e.printStackTrace();
    }
    Connection connection = DriverManager.getConnection(dbUrl);
    try {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM PRODUCTS");

%>
<html>
  <head><title>Data Test</title></head>
  <body>
  <table border="1" cellpadding="1" cellspacing="1">

    <thead>
    <tr>
      <td rowspan="1" colspan="3">Data Test</td>
    </tr>

    </thead><tbody>

      <tr>

        <td>open</td>

        <td>/art_emotherearth_memento/welcome</td>

        <td></td>

      </tr>

      <tr>

        <td>type</td>

        <td>user</td>

        <td>Homer</td>

      </tr>

      <tr>

        <td>clickAndWait</td>

        <td>//input[@id='submitButton']</td>

        <td></td>

      </tr>

      <tr>
        <td>verifyTitle</td>
        <td>CatalogView</td>
        <td></td>
      </tr>
<%
    for (int row = 0; row < 6; row++) {
        resultSet.next();
        Product p = new Product();
        p.setId(resultSet.getInt("ID"));
        p.setName(resultSet.getString("NAME"));
        p.setPrice(resultSet.getDouble("PRICE"));
%>
      <tr>
           <td>assertTable</td>
           <td>//html/body/table.<%= row + 1 %>.0</td>
           <td><%= p.getId() %></td>
      </tr>
      <tr>
          <td>assertTable</td>
          <td>//html/body/table.<%= row + 1 %>.1</td>
          <td><%= p.getName() %></td>
      </tr>
      <tr>
          <td>assertTable</td>
          <td>//html/body/table.<%= row + 1 %>.2</td>
          <td><%= p.getPriceAsCurrency() %></td>
       </tr>
<%
    }
    } finally {
        connection.close();
    }
%>

    </tbody>
  </table>


  </body>
</html>