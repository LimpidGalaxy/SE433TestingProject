package shoppingcart.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
    private List<CartItem> items;

    public ShoppingCart() {
        this.items = new ArrayList<>();
    }

    public void addItem(CartItem item) {
        items.add(item);
    }

    public boolean removeItem(String itemName) {
        return items.removeIf(item -> item.getName().equalsIgnoreCase(itemName));
    }

    public boolean updateQuantity(String itemName, int newQuantity) {
        for (CartItem item : items) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                item.setQuantity(newQuantity);
                return true;
            }
        }
        return false;
    }

    public List<CartItem> getItems() {
        return new ArrayList<>(items); // defensive copy
    }

    public BigDecimal getRawTotal() {
        return items.stream().map(CartItem::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getItemCount() {
        return items.size();
    }

    public void clear() {
        items.clear();
    }

    @Override
    public String toString() {
        if (items.isEmpty()) {
            return "Shopping cart is empty.";
        }
        StringBuilder sb = new StringBuilder("Shopping Cart Contents:\n");
        for (CartItem item : items) {
            sb.append("- ").append(item.toString()).append("\n");
        }
        return sb.toString();
    }
}