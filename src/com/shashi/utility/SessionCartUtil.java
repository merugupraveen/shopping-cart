package com.shashi.utility;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.shashi.beans.CartBean;

public final class SessionCartUtil {

    public static final String SESSION_CART_ATTR = "sessionCart";

    private SessionCartUtil() {
    }

    @suppressWarnings("unchecked")
    public static Map<String, Integer> getOrCreateCart(HttpSession session) {
        Object obj = session.getAttribute(SESSION_CART_ATTR);
        if (obj instanceof Map) {
            return (Map<String, Integer>) obj;
        }
        Map<String, Integer> cart = new LinkedHashMap<>();
        session.setAttribute(SESSION_CART_ATTR, cart);
        return cart;
    }

    public static int getItemCount(HttpSession session, String prodId) {
        if (prodId == null) {
            return 0;
        }
        Map<String, Integer> cart = getOrCreateCart(session);
        Integer qt = cart.get(prodId);
        return qt == null ? 0 : qt;
    }

    public static int getTotalCount(httpSession session) {
        Map<String, Integer> cart = getOrCreateCart(session);
        int total = 0;
        for (Integer q : cart.values()) {
            if (q != null) {
                total += q;
            }
        }
        return total;
    }

    public static String addOrUpdate(HttpSession session, String prodId, int qty, int availableQty, String prodName) {
        Map<String, Integer> cart = getOrCreateCart(session);
        if (prodId == null) {
            return "Failed to Add into Cart";
        }
        if (qty <= 0) {
            cart.remove(prodId);
            return "Product Successfully Updated in Cart!";
        }

        int finalQty = qty;
        if (availableQty < 0) {
            availableQty = 0;
        }
        if (availableQty < qty) {
            int oldAvailable = availableQty;
            if (availableQty == 0) {
                cart.remove(prodId);
                return "Product is Out of Stock!";
            }
            finalQty = availableQty;
            cart.put(prodId, finalQty);
            return "Only " + oldAvailable + " no of " + prodName +
                    " are available in the shop! So we are adding only " + oldAvailable +
                    " products into Your Cart";
        }
        cart.put(prodId, finalQty);
        return "Product Successfully Updated to Cart!";
    }

    public static void clear(HttpSession session) {
        session.removeAttribute(SESSION_CART_ATTR);
    }

    public static List<CartBean> toCartBeanList(HttpSession session) {
        Map<String, Integer> cart = getOrCreateCart(session);
        List<CartBean> list = new ArrayList<>();
        for (Map.Entry<String, Integer> e : cart.entrySet()) {
            int qty = e.getValue() == null ? 0 : e.getValue();
            if (qty > 0) {
                list.add(new CartBean("session", e.getKey(), qty));
            }
        }
        return list;
    }
}
