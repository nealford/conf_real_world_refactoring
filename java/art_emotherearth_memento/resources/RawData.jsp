<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.DriverManager" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.Statement" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>Raw Data (for testing purposes)</title></head>
<body>

<table>
    <tr>
        <td><label>User Name</label></td>
        <td><input type="text" id="user_name" value="Homer"/></td>
    </tr>
    <tr>
        <td><label>Ocean Quantity</label></td>
        <td><input type="text" id="ocean_quantity" value="2"/></td>
    </tr>
    <tr>
        <td><label>cc number</label></td>
        <td><input type="text" id="cc_num" value="1234-5678-9012-3456" /> </td>
    </tr>
    <tr>
        <td><label>cc type</label></td>
        <td><input type="text" id="cc_type" value="Amex" /> </td>
    </tr>
    <tr>
        <td><label>cc exp date</label></td>
        <td><input type="text" id="cc_exp_date" value="01/10" /> </td>        
    </tr>
</table>

</body>
</html>