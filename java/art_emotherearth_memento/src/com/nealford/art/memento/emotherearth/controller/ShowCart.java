package com.nealford.art.memento.emotherearth.controller;

import com.nealford.art.memento.emotherearth.boundary.ProductDb;
import com.nealford.art.memento.emotherearth.entity.CartItem;
import com.nealford.art.memento.emotherearth.entity.Product;
import com.nealford.art.memento.emotherearth.util.ShoppingCart;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Stack;

public class ShowCart extends HttpServlet {
    private static final String MEMENTO_STACK_ID = "mementoStack";

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws
            ServletException, IOException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws
            ServletException, IOException {
        RequestDispatcher dispatcher = null;
        HttpSession session = redirectIfSessionNotPresent(
                request, response, dispatcher);

        ShoppingCart cart = getOrCreateShoppingCart(session);
        Stack mementoStack = (Stack) session.getAttribute(
                MEMENTO_STACK_ID);

        if (request.getParameter("bookmark") != null)
            mementoStack = handleBookmark(cart, mementoStack);
        else if (request.getParameter("restore") != null)
            handleRestore(session, cart, mementoStack);
        else
            handleAddItemToCart(request, session, cart);

        if (mementoStack != null && !mementoStack.empty()) {
            request.setAttribute("bookmark", new Boolean(true));
            session.setAttribute(MEMENTO_STACK_ID, mementoStack);
        }

        dispatcher = request.getRequestDispatcher("/ShowCart.jsp");
        dispatcher.forward(request, response);

                                                                                                         
    }

    private void handleAddItemToCart(HttpServletRequest request,
                                     HttpSession session,
                                     ShoppingCart cart) throws
            NumberFormatException {
        ProductDb productDb = getProductBoundary(session);

        CartItem cartItem = buildCartItem(request, productDb,
                Integer.parseInt(request.
                                 getParameter("id")));
        cart.addItem(cartItem);
        session.setAttribute("cart", cart);
    }

    private void handleRestore(HttpSession session,
                               ShoppingCart cart,
                               Stack mementoStack) {
        if (mementoStack == null)
                return;
        cart.restoreFromBookmark(
                (ShoppingCart.ShoppingCartMemento)
                mementoStack.pop());
        if (mementoStack.empty()) {
            session.removeAttribute(MEMENTO_STACK_ID);
        }
    }

    private Stack handleBookmark(ShoppingCart cart,
                                 Stack mementoStack) {
        if (mementoStack == null) {
            mementoStack = new Stack();
        }
        mementoStack.push(cart.setBookmark());
        return mementoStack;
    }

    private CartItem buildCartItem(HttpServletRequest request,
                                   ProductDb productDb,
                                   int id) throws
            NumberFormatException {
        id = Integer.parseInt(request.getParameter("id"));
        int quantity =
                Integer.parseInt(request.getParameter("quantity"));
        Product product = productDb.getProduct(id);
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);
        return cartItem;
    }

    private ProductDb getProductBoundary(HttpSession session) {
        return (ProductDb) session.getAttribute("productList");
    }

    private ShoppingCart getOrCreateShoppingCart(
            HttpSession session) {
        ShoppingCart cart =
                (ShoppingCart) session.getAttribute("cart");
        if (cart == null) {
            cart = new ShoppingCart();
        }
        return cart;
    }

    private HttpSession redirectIfSessionNotPresent(
            HttpServletRequest request,
            HttpServletResponse response,
            RequestDispatcher dispatcher) throws ServletException,
            IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            dispatcher = request.getRequestDispatcher("/welcome");
            dispatcher.forward(request, response);
            return null;
        }
        return session;
    }
}