package shoppingcart.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class CartItemTest {

    @Test
    void testConstructor() {
        CartItem item = new CartItem("Apple", 3, BigDecimal.valueOf(1.50));
        assertEquals("Apple", item.getName());
        assertEquals(3, item.getQuantity());
        assertEquals(0, BigDecimal.valueOf(1.50).compareTo(item.getPrice()));
    }

    @Test
    void testGetName() {
        CartItem item = new CartItem("Banana", 1, BigDecimal.TEN);
        assertEquals("Banana", item.getName());
    }

    @Test
    void testGetQuantity() {
        CartItem item = new CartItem("Orange", 5, BigDecimal.ONE);
        assertEquals(5, item.getQuantity());
    }

    @Test
    void testSetQuantity() {
        CartItem item = new CartItem("Grape", 2, BigDecimal.valueOf(3.0));
        item.setQuantity(10);
        assertEquals(10, item.getQuantity());
    }

    @Test
    void testGetPrice() {
        CartItem item = new CartItem("Melon", 1, BigDecimal.valueOf(4.99));
        assertEquals(0, BigDecimal.valueOf(4.99).compareTo(item.getPrice()));
    }

    @Test
    void testGetTotal() {
        CartItem item = new CartItem("Kiwi", 3, BigDecimal.valueOf(2.00));
        assertEquals(0, BigDecimal.valueOf(6.00).compareTo(item.getTotal()));
    }

    @Test
    void testGetTotalWithOneQuantity() {
        CartItem item = new CartItem("Peach", 1, BigDecimal.valueOf(5.50));
        assertEquals(0, BigDecimal.valueOf(5.50).compareTo(item.getTotal()));
    }

    @Test
    void testGetTotalWithLargeQuantity() {
        CartItem item = new CartItem("Bulk Item", 100, BigDecimal.valueOf(0.99));
        assertEquals(0, BigDecimal.valueOf(99.00).compareTo(item.getTotal()));
    }

    @Test
    void testToString() {
        CartItem item = new CartItem("Apple", 2, BigDecimal.valueOf(1.50));
        String result = item.toString();
        assertTrue(result.contains("Apple"));
        assertTrue(result.contains("Qty: 2"));
        assertTrue(result.contains("Price: $1.50"));
        assertTrue(result.contains("Total: $3.00"));
    }
}
