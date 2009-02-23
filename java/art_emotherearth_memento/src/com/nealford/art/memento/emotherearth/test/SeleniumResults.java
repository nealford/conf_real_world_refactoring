package com.nealford.art.memento.emotherearth.test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

public class SeleniumResults extends HttpServlet {
    private final String[] COLUMNS = { "Category", "Results" };


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        out.println("<html><head></head></body>");
        out.println("<h1>Selenium Results</h1>");
        out.println("<table border='1'>");
        out.println("<tr>");
        for (int i = 0; i < COLUMNS.length; i++)
            out.println("<th>" + COLUMNS[i] + "</th>");
        out.println("</tr>");
        Enumeration paramEnum = request.getParameterNames();
        while (paramEnum.hasMoreElements()) {
            String param = (String) paramEnum.nextElement();
            out.println("<tr>");
            out.println("<td>" + param + "</td>");
            out.println("<td>" + request.getParameter(param) + "</td>");
            out.println("</tr>");
        }
        out.println("</body></html>");
    }
}
