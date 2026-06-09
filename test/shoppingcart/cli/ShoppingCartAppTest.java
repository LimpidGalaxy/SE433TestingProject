package shoppingcart.cli;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import shoppingcart.model.ShippingOption;
import shoppingcart.model.StateTax;
import shoppingcart.service.CartService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class ShoppingCartAppTest {
    private ByteArrayOutputStream outContent;
    private PrintStream originalOut;
    private java.io.InputStream originalIn;

    @BeforeEach
    void setUp() throws Exception {
        outContent = new ByteArrayOutputStream();
        originalOut = System.out;
        originalIn = System.in;
        System.setOut(new PrintStream(outContent));
        // Reset static fields via reflection
        resetStaticState();
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }


    // Resets the static state of ShoppingCartApp between tests.

    private void resetStaticState() throws Exception {
        // Create fresh scanner (will be replaced per test)
        setStaticField("scanner", new Scanner(System.in));
        setStaticField("cartService", new CartService());
        setStaticField("customerName", null);
        setStaticField("customerState", null);
        setStaticField("shippingOption", null);
    }

    private void setStaticField(String fieldName, Object value) throws Exception {
        Field field = ShoppingCartApp.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(null, value);
    }

    private Object getStaticField(String fieldName) throws Exception {
        Field field = ShoppingCartApp.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(null);
    }

    private void setInput(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        // Re-create scanner with new input
        try {
            setStaticField("scanner", new Scanner(System.in));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object invokePrivateStatic(String methodName, Class<?>[] paramTypes, Object[] args) throws Exception {
        Method method = ShoppingCartApp.class.getDeclaredMethod(methodName, paramTypes);
        method.setAccessible(true);
        return method.invoke(null, args);
    }

    private Object invokePrivateStatic(String methodName) throws Exception {
        return invokePrivateStatic(methodName, new Class<?>[0], new Object[0]);
    }

    // main() integration tests
    @Test
    void testConstructor(){
        // This silly test is added because PITest tells me the
        // "public class ShoppingCartApp {" line is not covered in the test.
        ShoppingCartApp app = new ShoppingCartApp();
        assertNotNull(app);
    }


    @Test
    void testMainAddItemAndCheckout() {
        String input = String.join("\n",
            "TestUser",  // name
            "IL",                  // state
            "STANDARD",            // shipping
            "1",                   // menu: add item
            "Apple",               // item name
            "3",                   // quantity
            "1.50",                // price
            "6"                    // menu: checkout
        ) + "\n";
        setInput(input);

        ShoppingCartApp.main(new String[0]);

        String output = outContent.toString();
        assertTrue(output.contains("Welcome to the Shopping Cart Application!"));
        assertTrue(output.contains("Item added."));
        assertTrue(output.contains("Transaction completed."));
    }

    @Test
    void testMainViewCartContents() {
        String input = String.join("\n",
            "TestUser", "IL", "STANDARD",
            "1", "Apple", "2", "1.00",      // add item
            "2",                            // view contents
            "6"                             // checkout
        ) + "\n";
        setInput(input);

        ShoppingCartApp.main(new String[0]);

        String output = outContent.toString();
        assertTrue(output.contains("Shopping Cart Contents:"));
        assertTrue(output.contains("Apple"));
    }

    @Test
    void testMainEditQuantity() {
        String input = String.join("\n",
            "TestUser", "IL", "STANDARD",
            "1", "Banana", "2", "0.99",    // add
            "3", "Banana", "5",            // edit quantity
            "6"                            // checkout
        ) + "\n";
        setInput(input);

        ShoppingCartApp.main(new String[0]);

        String output = outContent.toString();
        assertTrue(output.contains("Quantity updated."));
    }

    @Test
    void testMainRemoveItem() {
        String input = String.join("\n",
            "TestUser", "IL", "STANDARD",
            "1", "Orange", "1", "2.00",    // add
            "4", "Orange",                 // remove
            "6"                            // checkout
        ) + "\n";
        setInput(input);

        ShoppingCartApp.main(new String[0]);

        String output = outContent.toString();
        assertTrue(output.contains("Item removed."));
    }

    @Test
    void testMainGetCurrentTotal() {
        String input = String.join("\n",
            "TestUser", "IL", "STANDARD",
            "1", "Grape", "2", "5.00",      // add: raw = 10
            "5",                            // get total
            "6"                             // checkout
        ) + "\n";
        setInput(input);

        ShoppingCartApp.main(new String[0]);

        String output = outContent.toString();
        assertTrue(output.contains("Current total:"));
    }

    @Test
    void testMainInvalidMenuOption() {
        String input = String.join("\n",
            "TestUser", "IL", "STANDARD",
            "99",    // invalid menu
            "6"      // checkout
        ) + "\n";
        setInput(input);

        ShoppingCartApp.main(new String[0]);

        String output = outContent.toString();
        assertTrue(output.contains("Menu:"));       // kills displayMenu removal
        assertTrue(output.contains("Invalid option."));
    }

    @Test
    void testMainInvalidStateRetry() {
        String input = String.join("\n",
            "TestUser",
            "XX",    // invalid state
            "IL",    // valid state
            "STANDARD",
            "6"
        ) + "\n";
        setInput(input);

        ShoppingCartApp.main(new String[0]);

        String output = outContent.toString();
        assertTrue(output.contains("Invalid state abbreviation."));
    }

    @Test
    void testMainInvalidShipping() {
        String input = String.join("\n",
            "TestUser", "IL",
            "INVALID_SHIPPING",  // invalid shipping
            "6"
        ) + "\n";
        setInput(input);

        ShoppingCartApp.main(new String[0]);

        String output = outContent.toString();
        assertTrue(output.contains("Defaulting to STANDARD."));
    }

    @Test
    void testMainNextDayShipping() {
        String input = String.join("\n",
            "TestUser", "CA",
            "NEXT_DAY",
            "1", "Laptop", "1", "100.00",
            "6"
        ) + "\n";
        setInput(input);

        ShoppingCartApp.main(new String[0]);

        String output = outContent.toString();
        assertTrue(output.contains("Transaction completed."));
    }

    // addItem() branches

    @Test
    void testAddItemInvalidQuantity() throws Exception {
        setStaticField("cartService", new CartService());
        setInput("TestItem\n0\n");
        invokePrivateStatic("addItem");

        String output = outContent.toString();
        assertTrue(output.contains("Enter item name:"));
        assertTrue(output.contains("Error: Quantity must be at least 1."));
    }

    @Test
    void testAddItemInvalidPrice() throws Exception {
        setStaticField("cartService", new CartService());
        setInput("TestItem\n1\n0\n");
        invokePrivateStatic("addItem");

        String output = outContent.toString();
        assertTrue(output.contains("Enter item name:"));
        assertTrue(output.contains("Enter item price:"));
        assertTrue(output.contains("Error: Price must be positive."));
    }

    @Test
    void testAddItemNegativePrice() throws Exception {
        setStaticField("cartService", new CartService());
        setInput("TestItem\n1\n-5.00\n");
        invokePrivateStatic("addItem");

        String output = outContent.toString();
        assertTrue(output.contains("Error: Price must be positive."));
    }

    @Test
    void testAddItemServiceReturnsFalse() throws Exception {
        // Inject a CartService that always rejects items
        CartService rejectingService = new CartService() {
            @Override
            public boolean addItem(String name, int quantity, BigDecimal price) {
                return false;
            }
        };
        setStaticField("cartService", rejectingService);
        setInput("Widget\n5\n9.99\n");
        invokePrivateStatic("addItem");

        String output = outContent.toString();
        assertTrue(output.contains("Error adding item."));
    }

    // editQuantity() branches

    @Test
    void testEditQuantityItemNotFound() throws Exception {
        setStaticField("cartService", new CartService());
        setInput("NotFound\n5\n");
        invokePrivateStatic("editQuantity");

        String output = outContent.toString();
        assertTrue(output.contains("Enter item name to edit:"));
        assertTrue(output.contains("Item not found."));
    }

    @Test
    void testEditQuantityInvalidQuantity() throws Exception {
        CartService cs = new CartService();
        cs.addItem("Apple", 1, BigDecimal.ONE);
        setStaticField("cartService", cs);
        setInput("Apple\n0\n");
        invokePrivateStatic("editQuantity");

        String output = outContent.toString();
        assertTrue(output.contains("Error: Quantity must be at least 1."));
    }

    // removeItem() branches

    @Test
    void testRemoveItemNotFound() throws Exception {
        setStaticField("cartService", new CartService());
        setInput("NotFound\n");
        invokePrivateStatic("removeItem");

        String output = outContent.toString();
        assertTrue(output.contains("Enter item name to remove:"));
        assertTrue(output.contains("Item not found."));
    }

    @Test
    void testRemoveItemSuccess() throws Exception {
        CartService cs = new CartService();
        cs.addItem("Apple", 1, BigDecimal.ONE);
        setStaticField("cartService", cs);
        setInput("Apple\n");
        invokePrivateStatic("removeItem");

        String output = outContent.toString();
        assertTrue(output.contains("Item removed."));
    }

    // getCurrentTotal()

    @Test
    void testGetCurrentTotalMethod() throws Exception {
        CartService cs = new CartService();
        cs.addItem("Test", 1, BigDecimal.valueOf(10.00));
        setStaticField("cartService", cs);
        setStaticField("customerState", StateTax.OTHER);
        setStaticField("shippingOption", ShippingOption.STANDARD);

        BigDecimal result = (BigDecimal) invokePrivateStatic("getCurrentTotal");

        assertNotNull(result);
        String output = outContent.toString();
        assertTrue(output.contains("Current total:"));
    }

    // checkout()

    @Test
    void testCheckoutBelowMinimum() throws Exception {
        // Use a CartService subclass that forces getCurrentTotal to return < $1
        CartService cs = new CartService() {
            @Override
            public java.math.BigDecimal getCurrentTotal(StateTax st, ShippingOption so) {
                return BigDecimal.ZERO;
            }
        };
        setStaticField("cartService", cs);
        setStaticField("customerState", StateTax.OTHER);
        setStaticField("shippingOption", ShippingOption.STANDARD);

        invokePrivateStatic("checkout");

        String output = outContent.toString();
        assertTrue(output.contains("The minimum purchase amount is $1.00"));
    }

    @Test
    void testCheckoutExceedsMaximum() throws Exception {
        CartService cs = new CartService();
        // Add an extremely expensive item
        cs.addItem("MegaItem", 1, BigDecimal.valueOf(100000.00));
        setStaticField("cartService", cs);
        setStaticField("customerState", StateTax.OTHER);
        setStaticField("shippingOption", ShippingOption.NEXT_DAY);

        invokePrivateStatic("checkout");

        String output = outContent.toString();
        // rawTotal=100000, tax=0, shipping=25, total=100025 > 99999.99
        assertTrue(output.contains("Total exceeds maximum allowed."));
    }

    @Test
    void testCheckoutSuccess() throws Exception {
        CartService cs = new CartService();
        cs.addItem("Apple", 1, BigDecimal.valueOf(5.00));
        setStaticField("cartService", cs);
        setStaticField("customerState", StateTax.OTHER);
        setStaticField("shippingOption", ShippingOption.STANDARD);

        invokePrivateStatic("checkout");

        String output = outContent.toString();
        assertTrue(output.contains("Transaction completed."));
        assertEquals(0, cs.getItemCount());
    }

    // getIntInput()

    @Test
    void testGetIntInputValid() throws Exception {
        setInput("42\n");
        int result = (Integer) invokePrivateStatic("getIntInput",
            new Class<?>[]{String.class}, new Object[]{"Enter: "});
        assertEquals(42, result);
        String output = outContent.toString();
        assertTrue(output.contains("Enter:"));
    }

    @Test
    void testGetIntInputInvalidThenValid() throws Exception {
        setInput("abc\n100\n");
        int result = (Integer) invokePrivateStatic("getIntInput",
            new Class<?>[]{String.class}, new Object[]{"Enter: "});
        assertEquals(100, result);

        String output = outContent.toString();
        assertTrue(output.contains("Invalid input."));
    }

    // getDoubleInput() branches

    @Test
    void testGetDoubleInputValid() throws Exception {
        setInput("3.14\n");
        double result = (Double) invokePrivateStatic("getDoubleInput",
            new Class<?>[]{String.class}, new Object[]{"Enter: "});
        assertEquals(3.14, result, 0.001);
        String output = outContent.toString();
        assertTrue(output.contains("Enter:"));
    }

    @Test
    void testGetDoubleInputInvalidThenValid() throws Exception {
        setInput("xyz\n99.99\n");
        double result = (Double) invokePrivateStatic("getDoubleInput",
            new Class<?>[]{String.class}, new Object[]{"Enter: "});
        assertEquals(99.99, result, 0.001);

        String output = outContent.toString();
        assertTrue(output.contains("Invalid input."));
    }

    // collectCustomerInfo()

    @Test
    void testCollectCustomerInfoValid() throws Exception {
        setInput("Alice\nCA\nSTANDARD\n");
        invokePrivateStatic("collectCustomerInfo");

        String output = outContent.toString();
        assertTrue(output.contains("Enter your name:"));
        assertTrue(output.contains("Enter your state (e.g., IL, CA, NY):"));
        assertTrue(output.contains("Choose shipping option (STANDARD/NEXT_DAY): "));
        // NegateConditionals killer: valid state should NOT trigger error message
        assertFalse(output.contains("Invalid state abbreviation."));
        assertEquals("Alice", getStaticField("customerName"));
        assertEquals(StateTax.CA, getStaticField("customerState"));
        assertEquals(ShippingOption.STANDARD, getStaticField("shippingOption"));
    }

    @Test
    void testCollectCustomerInfoInvalidState() throws Exception {
        setInput("Bob\nZZ\nNY\nNEXT_DAY\n");
        invokePrivateStatic("collectCustomerInfo");

        String output = outContent.toString();
        assertTrue(output.contains("Invalid state abbreviation."));
        assertEquals(StateTax.NY, getStaticField("customerState"));
        assertEquals(ShippingOption.NEXT_DAY, getStaticField("shippingOption"));
    }

    @Test
    void testCollectCustomerInfoInvalidShipping() throws Exception {
        setInput("Charlie\nIL\nBAD_OPTION\n");
        invokePrivateStatic("collectCustomerInfo");

        String output = outContent.toString();
        assertTrue(output.contains("Defaulting to STANDARD."));
        assertEquals(ShippingOption.STANDARD, getStaticField("shippingOption"));
    }

    @Test
    void testCollectCustomerInfoLowercaseState() throws Exception {
        setInput("Diana\nil\nSTANDARD\n");
        invokePrivateStatic("collectCustomerInfo");

        assertEquals(StateTax.IL, getStaticField("customerState"));
    }

    // displayMenu()

    @Test
    void testDisplayMenu() throws Exception {
        invokePrivateStatic("displayMenu");

        String output = outContent.toString();
        assertTrue(output.contains("Menu:"));
        assertTrue(output.contains("Add item to cart"));
        assertTrue(output.contains("See contents of shopping cart"));
        assertTrue(output.contains("Edit quantity"));
        assertTrue(output.contains("Remove items"));
        assertTrue(output.contains("Get current total"));
        assertTrue(output.contains("Checkout"));
    }

    // mutation-killing boundary tests

    @Test
    void testCheckoutExactOneDollar() throws Exception {
        // total exactly 1.00: original < 0 is false (passes), mutant <= 0 is true (enters min branch)
        CartService cs = new CartService() {
            @Override
            public java.math.BigDecimal getCurrentTotal(StateTax st, ShippingOption so) {
                return BigDecimal.valueOf(1.00);
            }
        };
        setStaticField("cartService", cs);
        setStaticField("customerState", StateTax.OTHER);
        setStaticField("shippingOption", ShippingOption.STANDARD);

        invokePrivateStatic("checkout");

        String output = outContent.toString();
        // Should go to normal (success) path, NOT the "minimum purchase" path
        assertTrue(output.contains("Transaction completed."));
    }

    @Test
    void testCheckoutExactMaxTotal() throws Exception {
        // total exactly 99999.99: original > 0 is false, mutant >= 0 is true
        CartService cs = new CartService() {
            @Override
            public java.math.BigDecimal getCurrentTotal(StateTax st, ShippingOption so) {
                return BigDecimal.valueOf(99999.99);
            }
        };
        setStaticField("cartService", cs);
        setStaticField("customerState", StateTax.OTHER);
        setStaticField("shippingOption", ShippingOption.STANDARD);

        invokePrivateStatic("checkout");

        String output = outContent.toString();
        // Should go to normal (success) path, NOT the "exceeds maximum" path
        assertTrue(output.contains("Transaction completed."));
    }

    @Test
    void testEditQuantityBoundaryOne() throws Exception {
        CartService cs = new CartService();
        cs.addItem("Apple", 1, BigDecimal.ONE);
        setStaticField("cartService", cs);
        // newQuantity = 1: original 1 < 1 is false, mutant 1 <= 1 is true
        setInput("Apple\n1\n");
        invokePrivateStatic("editQuantity");

        String output = outContent.toString();
        // Should succeed, not show error
        assertTrue(output.contains("Quantity updated."));
    }

    @Test
    void testScannerClosedAfterMain() throws Exception {
        String input = String.join("\n",
            "TestUser", "IL", "STANDARD", "6"
        ) + "\n";
        setInput(input);

        ShoppingCartApp.main(new String[0]);

        // After main(), scanner should be closed
        Scanner sc = (Scanner) getStaticField("scanner");
        assertNotNull(sc);
        // Verify scanner is closed by checking its readable state
        // If scanner.close() was removed by mutation, this won't throw
        try {
            sc.nextLine();
            fail("Expected scanner to be closed after main()");
        } catch (IllegalStateException e) {
            // Expected: scanner is closed
        }
    }
}
