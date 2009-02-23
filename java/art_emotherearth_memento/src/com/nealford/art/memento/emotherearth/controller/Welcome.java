package com.nealford.art.memento.emotherearth.controller;

import com.nealford.art.memento.emotherearth.util.DBPool;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Welcome extends HttpServlet {

    public void init() throws ServletException {
        killExistingDatabaseConnection();
        String driverClass =
                getServletContext().getInitParameter("driverClass");
        String dbUrl =
                getServletContext().getInitParameter("dbUrl");
        DBPool dbPool =
                createConnectionPool(driverClass, dbUrl);
        getServletContext().setAttribute("dbPool", dbPool);
    }

    private DBPool createConnectionPool(String driverClass,
                                        String dbUrl) {
        DBPool dbPool = null;
        try {
            dbPool = new DBPool(driverClass, dbUrl);
        } catch (SQLException sqlx) {
            System.out.println("Unable to create connection pool!");
            getServletContext().log(new java.util.Date() +
                                    ":Connection pool error", sqlx);
        }
        return dbPool;
    }

    private void killExistingDatabaseConnection() {
        try {
            DriverManager.getConnection("jdbc:derby:;shutdown=true");
        } catch (SQLException purposelyIgnored) {
        }
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher dispather =
                request.getRequestDispatcher("/WelcomeView.jsp");
        dispather.forward(request, response);
    }
}