package shoppingcart.model;

import java.math.BigDecimal;

public class CartItem {
    private String name;
    private int quantity;
    private BigDecimal price; // per unit 

    public CartItem(String name, int quantity, BigDecimal price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getTotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public String toString() {
        return String.format("%s (Qty: %d, Price: $%.2f, Total: $%.2f)", name, quantity, price.doubleValue(), getTotal().doubleValue());
    }
}