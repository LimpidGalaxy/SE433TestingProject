package shoppingcart.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ShoppingCartTest {

    private ShoppingCart cart;

    @BeforeEach
    void setUp() {
        cart = new ShoppingCart();
    }

    @Test
    void testConstructor() {
        assertEquals(0, cart.getItemCount());
        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    void testAddItem() {
        CartItem item = new CartItem("Apple", 2, BigDecimal.valueOf(1.50));
        cart.addItem(item);
        assertEquals(1, cart.getItemCount());
    }

    @Test
    void testAddMultipleItems() {
        cart.addItem(new CartItem("A", 1, BigDecimal.ONE));
        cart.addItem(new CartItem("B", 1, BigDecimal.ONE));
        cart.addItem(new CartItem("C", 1, BigDecimal.ONE));
        assertEquals(3, cart.getItemCount());
    }

    @Test
    void testRemoveItem() {
        cart.addItem(new CartItem("Apple", 1, BigDecimal.ONE));
        assertTrue(cart.removeItem("Apple"));
        assertEquals(0, cart.getItemCount());
    }

    @Test
    void testRemoveItemCaseInsensitive() {
        cart.addItem(new CartItem("Apple", 1, BigDecimal.ONE));
        assertTrue(cart.removeItem("apple"));
        assertEquals(0, cart.getItemCount());
        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    void testRemoveItemNotFound() {
        cart.addItem(new CartItem("Apple", 1, BigDecimal.ONE));
        assertFalse(cart.removeItem("Banana"));
        assertEquals(1, cart.getItemCount());
    }

    @Test
    void testRemoveItemFromEmptyCart() {
        assertFalse(cart.removeItem("Anything"));
    }

    @Test
    void testUpdateQuantity() {
        cart.addItem(new CartItem("Apple", 1, BigDecimal.ONE));
        assertTrue(cart.updateQuantity("Apple", 5));
        assertEquals(5, cart.getItems().get(0).getQuantity());
    }

    @Test
    void testUpdateQuantityCaseInsensitive() {
        cart.addItem(new CartItem("Apple", 1, BigDecimal.ONE));
        assertTrue(cart.updateQuantity("apple", 10));
        assertEquals(10, cart.getItems().get(0).getQuantity());
    }

    @Test
    void testUpdateQuantityNotFound() {
        cart.addItem(new CartItem("Apple", 1, BigDecimal.ONE));
        assertFalse(cart.updateQuantity("Banana", 5));
        assertEquals(1, cart.getItems().get(0).getQuantity());
    }

    @Test
    void testUpdateQuantityEmptyCart() {
        assertFalse(cart.updateQuantity("Anything", 5));
    }

    @Test
    void testGetItemsDefensiveCopy() {
        cart.addItem(new CartItem("Apple", 1, BigDecimal.ONE));
        List<CartItem> items = cart.getItems();
        items.clear(); // modify the returned list
        assertEquals(1, cart.getItemCount()); // original should be unaffected
    }

    @Test
    void testGetItems() {
        CartItem item1 = new CartItem("A", 1, BigDecimal.ONE);
        CartItem item2 = new CartItem("B", 2, BigDecimal.TEN);
        cart.addItem(item1);
        cart.addItem(item2);
        List<CartItem> items = cart.getItems();
        assertEquals(2, items.size());
        assertTrue(items.contains(item1));
        assertTrue(items.contains(item2));
    }

    @Test
    void testGetRawTotalEmpty() {
        assertEquals(BigDecimal.ZERO, cart.getRawTotal());
    }

    @Test
    void testGetRawTotal() {
        // 1 * 10.00 = 10.00
        cart.addItem(new CartItem("A", 1, BigDecimal.valueOf(10.00)));
        // 2 * 5.00 = 10.00 → total = 20.00
        cart.addItem(new CartItem("B", 2, BigDecimal.valueOf(5.00)));
        assertEquals(BigDecimal.valueOf(20.00), cart.getRawTotal());
    }

    @Test
    void testGetRawTotalAfterRemoval() {
        cart.addItem(new CartItem("A", 1, BigDecimal.valueOf(10.00)));
        cart.addItem(new CartItem("B", 2, BigDecimal.valueOf(5.00)));
        cart.removeItem("A");
        assertEquals(BigDecimal.valueOf(10.00), cart.getRawTotal());
    }

    @Test
    void testGetItemCountEmpty() {
        assertEquals(0, cart.getItemCount());
    }

    @Test
    void testGetItemCount() {
        assertEquals(0, cart.getItemCount());
        cart.addItem(new CartItem("A", 1, BigDecimal.ONE));
        assertEquals(1, cart.getItemCount());
        cart.addItem(new CartItem("B", 1, BigDecimal.ONE));
        assertEquals(2, cart.getItemCount());
        cart.removeItem("A");
        assertEquals(1, cart.getItemCount());
    }

    @Test
    void testClear() {
        cart.addItem(new CartItem("A", 1, BigDecimal.ONE));
        cart.addItem(new CartItem("B", 1, BigDecimal.ONE));
        cart.clear();
        assertEquals(0, cart.getItemCount());
        assertEquals(BigDecimal.ZERO, cart.getRawTotal());
        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    void testClearEmptyCart() {
        assertDoesNotThrow(() -> cart.clear());
        assertEquals(0, cart.getItemCount());
    }

    @Test
    void testToStringEmpty() {
        assertEquals("Shopping cart is empty.", cart.toString());
    }

    @Test
    void testToStringWithItems() {
        cart.addItem(new CartItem("Apple", 2, BigDecimal.valueOf(1.50)));
        String result = cart.toString();
        assertTrue(result.contains("Shopping Cart Contents:"));
        assertTrue(result.contains("Apple"));
    }

    @Test
    void testToStringWithMultipleItems() {
        cart.addItem(new CartItem("Apple", 2, BigDecimal.valueOf(1.50)));
        cart.addItem(new CartItem("Banana", 1, BigDecimal.valueOf(0.99)));
        String result = cart.toString();
        assertTrue(result.contains("Apple"));
        assertTrue(result.contains("Banana"));
    }
}
