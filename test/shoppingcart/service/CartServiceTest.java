package shoppingcart.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import shoppingcart.model.ShippingOption;
import shoppingcart.model.StateTax;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class CartServiceTest {

    private CartService cartService;

    @BeforeEach
    void setUp() {
        cartService = new CartService();
    }

    // addItem()

    @Test
    void testAddItemValid() {
        boolean result = cartService.addItem("Apple", 3, BigDecimal.valueOf(1.50));
        assertTrue(result);
        assertEquals(1, cartService.getItemCount());
    }

    @Test
    void testAddItemInvalidQuantityZero() {
        boolean result = cartService.addItem("Apple", 0, BigDecimal.valueOf(1.50));
        assertFalse(result);
        assertEquals(0, cartService.getItemCount());
    }

    @Test
    void testAddItemInvalidQuantityNegative() {
        boolean result = cartService.addItem("Apple", -5, BigDecimal.valueOf(1.50));
        assertFalse(result);
        assertEquals(0, cartService.getItemCount());
    }

    @Test
    void testAddItemInvalidPriceZero() {
        boolean result = cartService.addItem("Apple", 1, BigDecimal.ZERO);
        assertFalse(result);
        assertEquals(0, cartService.getItemCount());
    }

    @Test
    void testAddItemInvalidPriceNegative() {
        boolean result = cartService.addItem("Apple", 1, BigDecimal.valueOf(-1.00));
        assertFalse(result);
        assertEquals(0, cartService.getItemCount());
    }

    @Test
    void testAddMultipleItems() {
        cartService.addItem("A", 1, BigDecimal.ONE);
        cartService.addItem("B", 2, BigDecimal.TEN);
        assertEquals(2, cartService.getItemCount());
    }

    // removeItem()

    @Test
    void testRemoveItemExisting() {
        cartService.addItem("Apple", 1, BigDecimal.ONE);
        assertTrue(cartService.removeItem("Apple"));
        assertEquals(0, cartService.getItemCount());
    }

    @Test
    void testRemoveItemNonExisting() {
        cartService.addItem("Apple", 1, BigDecimal.ONE);
        assertFalse(cartService.removeItem("Banana"));
        assertEquals(1, cartService.getItemCount());
    }

    @Test
    void testRemoveItemEmptyCart() {
        assertFalse(cartService.removeItem("Anything"));
    }

    // updateQuantity()

    @Test
    void testUpdateQuantityValid() {
        cartService.addItem("Apple", 1, BigDecimal.ONE);
        assertTrue(cartService.updateQuantity("Apple", 10));
    }

    @Test
    void testUpdateQuantityInvalidZero() {
        cartService.addItem("Apple", 1, BigDecimal.ONE);
        assertFalse(cartService.updateQuantity("Apple", 0));
    }

    @Test
    void testUpdateQuantityInvalidNegative() {
        cartService.addItem("Apple", 1, BigDecimal.ONE);
        assertFalse(cartService.updateQuantity("Apple", -1));
    }

    @Test
    void testUpdateQuantityNonExisting() {
        assertFalse(cartService.updateQuantity("NotFound", 5));
    }

    // getCartContents()

    @Test
    void testGetCartContentsEmpty() {
        assertEquals("Shopping cart is empty.", cartService.getCartContents());
    }

    @Test
    void testGetCartContentsWithItems() {
        cartService.addItem("Apple", 2, BigDecimal.valueOf(1.50));
        String contents = cartService.getCartContents();
        assertTrue(contents.contains("Shopping Cart Contents:"));
        assertTrue(contents.contains("Apple"));
    }

    // getCurrentTotal()

    @Test
    void testGetCurrentTotalEmpty() {
        // rawTotal = 0; tax = 0 * 0.0 = 0; shipping = 10.0 (STANDARD below 50)
        BigDecimal total = cartService.getCurrentTotal(StateTax.OTHER, ShippingOption.STANDARD);
        assertEquals(0, BigDecimal.valueOf(10.0).compareTo(total));
    }

    @Test
    void testGetCurrentTotalWithItems() {
        cartService.addItem("Apple", 2, BigDecimal.valueOf(5.00)); // 10.00
        // raw = 10, tax = 10 * 0.06 = 0.6, shipping = 10.0 (below 50)
        BigDecimal total = cartService.getCurrentTotal(StateTax.IL, ShippingOption.STANDARD);
        assertEquals(0, BigDecimal.valueOf(20.6).compareTo(total));
    }

    @Test
    void testGetCurrentTotalNextDay() {
        cartService.addItem("Laptop", 1, BigDecimal.valueOf(100.00));
        // raw = 100, tax = 100 * 0.06 = 6, shipping = 25
        BigDecimal total = cartService.getCurrentTotal(StateTax.NY, ShippingOption.NEXT_DAY);
        assertEquals(0, BigDecimal.valueOf(131.0).compareTo(total));
    }

    @Test
    void testGetCurrentTotalFreeShipping() {
        cartService.addItem("Expensive", 1, BigDecimal.valueOf(100.00));
        // raw = 100 >= 50 → free shipping; tax = 100 * 0.0 = 0
        BigDecimal total = cartService.getCurrentTotal(StateTax.OTHER, ShippingOption.STANDARD);
        assertEquals(0, BigDecimal.valueOf(100.0).compareTo(total));
    }

    // getItemCount()

    @Test
    void testGetItemCountInitial() {
        assertEquals(0, cartService.getItemCount());
    }

    @Test
    void testGetItemCountAfterAdd() {
        cartService.addItem("A", 1, BigDecimal.ONE);
        assertEquals(1, cartService.getItemCount());
        cartService.addItem("B", 1, BigDecimal.ONE);
        assertEquals(2, cartService.getItemCount());
    }

    @Test
    void testGetItemCountAfterRemove() {
        cartService.addItem("A", 1, BigDecimal.ONE);
        cartService.addItem("B", 1, BigDecimal.ONE);
        cartService.removeItem("A");
        assertEquals(1, cartService.getItemCount());
    }

    // checkout()

    @Test
    void testCheckoutClearsCart() {
        cartService.addItem("A", 1, BigDecimal.ONE);
        cartService.addItem("B", 1, BigDecimal.ONE);
        cartService.checkout();
        assertEquals(0, cartService.getItemCount());
        assertEquals("Shopping cart is empty.", cartService.getCartContents());
    }

    @Test
    void testCheckoutEmptyCart() {
        assertDoesNotThrow(() -> cartService.checkout());
        assertEquals(0, cartService.getItemCount());
    }
}
