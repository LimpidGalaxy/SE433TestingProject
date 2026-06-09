package shoppingcart.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import shoppingcart.model.ShippingOption;
import shoppingcart.model.StateTax;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class PricingServiceTest {

    private PricingService pricingService;

    @BeforeEach
    void setUp() {
        pricingService = new PricingService();
    }

    // calculateTotal()

    @Test
    void testCalculateTotalNoTaxFreeShipping() {
        // rawTotal 100 >= 50 threshold → STANDARD shipping is free
        // tax = 100 * 0.0 = 0
        BigDecimal result = pricingService.calculateTotal(
            BigDecimal.valueOf(100.0), StateTax.OTHER, ShippingOption.STANDARD);
        assertEquals(0, BigDecimal.valueOf(100.0).compareTo(result));
    }

    @Test
    void testCalculateTotalWithTaxAndShipping() {
        // rawTotal 30 < 50 threshold → STANDARD shipping = 10.0
        // tax = 30 * 0.06 = 1.8
        // total = 30 + 1.8 + 10 = 41.8
        BigDecimal result = pricingService.calculateTotal(
            BigDecimal.valueOf(30.0), StateTax.IL, ShippingOption.STANDARD);
        assertEquals(0, BigDecimal.valueOf(41.8).compareTo(result));
    }

    @Test
    void testCalculateTotalWithTaxAndNextDay() {
        // tax = 50 * 0.06 = 3.0
        // shipping = 25.0 (NEXT_DAY always)
        // total = 50 + 3 + 25 = 78
        BigDecimal result = pricingService.calculateTotal(
            BigDecimal.valueOf(50.0), StateTax.CA, ShippingOption.NEXT_DAY);
        assertEquals(0, BigDecimal.valueOf(78.0).compareTo(result));
    }

    @Test
    void testCalculateTotalZero() {
        BigDecimal result = pricingService.calculateTotal(
            BigDecimal.ZERO, StateTax.NY, ShippingOption.STANDARD);
        // tax = 0 * 0.06 = 0, shipping = 10 (below 50 threshold)
        assertEquals(0, BigDecimal.valueOf(10.0).compareTo(result));
    }

    @Test
    void testCalculateTotalZeroOtherStandard() {
        BigDecimal result = pricingService.calculateTotal(
            BigDecimal.ZERO, StateTax.OTHER, ShippingOption.STANDARD);
        assertEquals(0, BigDecimal.valueOf(10.0).compareTo(result));
    }

    @Test
    void testCalculateTotalZeroNextDay() {
        BigDecimal result = pricingService.calculateTotal(
            BigDecimal.ZERO, StateTax.OTHER, ShippingOption.NEXT_DAY);
        assertEquals(0, BigDecimal.valueOf(25.0).compareTo(result));
    }

    @Test
    void testCalculateTotalAtStandardThreshold() {
        // 50 → free STANDARD shipping, tax = 50 * 0.06 = 3
        BigDecimal result = pricingService.calculateTotal(
            BigDecimal.valueOf(50.0), StateTax.IL, ShippingOption.STANDARD);
        assertEquals(0, BigDecimal.valueOf(53.0).compareTo(result));
    }

    @Test
    void testCalculateTotalNYStandardAboveThreshold() {
        // 200 > 50, free shipping; tax = 200 * 0.06 = 12
        BigDecimal result = pricingService.calculateTotal(
            BigDecimal.valueOf(200.0), StateTax.NY, ShippingOption.STANDARD);
        assertEquals(0, BigDecimal.valueOf(212.0).compareTo(result));
    }

    // isValidQuantity()

    @Test
    void testIsValidQuantityOne() {
        assertTrue(pricingService.isValidQuantity(1));
    }

    @Test
    void testIsValidQuantityPositive() {
        assertTrue(pricingService.isValidQuantity(100));
    }

    @Test
    void testIsValidQuantityZero() {
        assertFalse(pricingService.isValidQuantity(0));
    }

    @Test
    void testIsValidQuantityNegative() {
        assertFalse(pricingService.isValidQuantity(-1));
        assertFalse(pricingService.isValidQuantity(-100));
    }

    @Test
    void testIsValidQuantityMaxValue() {
        assertTrue(pricingService.isValidQuantity(Integer.MAX_VALUE));
    }

    // isValidTotal()

    @Test
    void testIsValidTotalAtMin() {
        assertTrue(pricingService.isValidTotal(PricingService.MIN_TOTAL));
    }

    @Test
    void testIsValidTotalAtMax() {
        assertTrue(pricingService.isValidTotal(PricingService.MAX_TOTAL));
    }

    @Test
    void testIsValidTotalInRange() {
        assertTrue(pricingService.isValidTotal(BigDecimal.valueOf(500.00)));
    }

    @Test
    void testIsValidTotalBelowMin() {
        assertFalse(pricingService.isValidTotal(BigDecimal.valueOf(0.99)));
        assertFalse(pricingService.isValidTotal(BigDecimal.ZERO));
        assertFalse(pricingService.isValidTotal(BigDecimal.valueOf(-1.0)));
    }

    @Test
    void testIsValidTotalAboveMax() {
        assertFalse(pricingService.isValidTotal(BigDecimal.valueOf(100000.00)));
    }

    // isValidItemPrice()

    @Test
    void testIsValidItemPriceTrue() {
        assertTrue(pricingService.isValidItemPrice(BigDecimal.valueOf(0.01)));
        assertTrue(pricingService.isValidItemPrice(BigDecimal.valueOf(1.00)));
        assertTrue(pricingService.isValidItemPrice(BigDecimal.valueOf(999.99)));
    }

    @Test
    void testIsValidItemPriceZero() {
        assertFalse(pricingService.isValidItemPrice(BigDecimal.ZERO));
    }

    @Test
    void testIsValidItemPriceNegative() {
        assertFalse(pricingService.isValidItemPrice(BigDecimal.valueOf(-0.01)));
        assertFalse(pricingService.isValidItemPrice(BigDecimal.valueOf(-100.0)));
    }

    // Constants

    @Test
    void testMinTotalConstant() {
        assertEquals(BigDecimal.valueOf(1.0), PricingService.MIN_TOTAL);
    }

    @Test
    void testMaxTotalConstant() {
        assertEquals(BigDecimal.valueOf(99999.99), PricingService.MAX_TOTAL);
    }
}
