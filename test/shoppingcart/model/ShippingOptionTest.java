package shoppingcart.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class ShippingOptionTest {

    @Test
    void testStandardValues() {
        ShippingOption option = ShippingOption.STANDARD;
        // getCost with total below threshold → cost applied
        assertEquals(BigDecimal.valueOf(10.0),
            option.getCost(BigDecimal.valueOf(0)));
        assertEquals(BigDecimal.valueOf(10.0),
            option.getCost(BigDecimal.valueOf(49.99)));
    }

    @Test
    void testStandardAtThreshold() {
        assertEquals(BigDecimal.ZERO,
            ShippingOption.STANDARD.getCost(BigDecimal.valueOf(50.0)));
    }

    @Test
    void testStandardAboveThreshold() {
        assertEquals(BigDecimal.ZERO,
            ShippingOption.STANDARD.getCost(BigDecimal.valueOf(100.0)));
    }

    @Test
    void testNextDayCost() {
        ShippingOption option = ShippingOption.NEXT_DAY;
        assertEquals(BigDecimal.valueOf(25.0),
            option.getCost(BigDecimal.valueOf(0)));
        assertEquals(BigDecimal.valueOf(25.0),
            option.getCost(BigDecimal.valueOf(50.0)));
        assertEquals(BigDecimal.valueOf(25.0),
            option.getCost(BigDecimal.valueOf(100.0)));
        assertEquals(BigDecimal.valueOf(25.0),
            option.getCost(BigDecimal.valueOf(99999.99)));
    }

    @Test
    void testValueOfStandard() {
        assertEquals(ShippingOption.STANDARD, ShippingOption.valueOf("STANDARD"));
    }

    @Test
    void testValueOfNextDay() {
        assertEquals(ShippingOption.NEXT_DAY, ShippingOption.valueOf("NEXT_DAY"));
    }

    @Test
    void testValues() {
        ShippingOption[] options = ShippingOption.values();
        assertEquals(2, options.length);
        assertTrue(java.util.Arrays.asList(options).contains(ShippingOption.STANDARD));
        assertTrue(java.util.Arrays.asList(options).contains(ShippingOption.NEXT_DAY));
    }
}
