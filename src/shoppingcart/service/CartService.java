package shoppingcart.service;

import java.math.BigDecimal;
import shoppingcart.model.CartItem;
import shoppingcart.model.ShippingOption;
import shoppingcart.model.ShoppingCart;
import shoppingcart.model.StateTax;

public class CartService {
    private ShoppingCart cart; 
    private PricingService pricingService;

    public CartService() {
        this.cart = new ShoppingCart();
        this.pricingService = new PricingService();
    }

    public boolean addItem(String name, int quantity, BigDecimal price) {
        if (!pricingService.isValidQuantity(quantity) || !pricingService.isValidItemPrice(price)) {
            return false;
        }
        CartItem item = new CartItem(name, quantity, price);
        cart.addItem(item);
        return true;
    }

    public boolean removeItem(String itemName) {
        return cart.removeItem(itemName);
    }

    public boolean updateQuantity(String itemName, int newQuantity) {
        if (!pricingService.isValidQuantity(newQuantity)) {
            return false;
        }
        return cart.updateQuantity(itemName, newQuantity);
    }

    public String getCartContents() {
        return cart.toString();
    }

    public BigDecimal getCurrentTotal(StateTax stateTax, ShippingOption shippingOption) {
        BigDecimal rawTotal = cart.getRawTotal();
        return pricingService.calculateTotal(rawTotal, stateTax, shippingOption);
    }

    public int getItemCount() {
        return cart.getItemCount();
    }

    public void checkout() {
        cart.clear();
    }
}