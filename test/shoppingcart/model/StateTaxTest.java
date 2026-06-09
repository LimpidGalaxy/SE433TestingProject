package shoppingcart.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class StateTaxTest {

    @Test
    void testILRate() {
        assertEquals(BigDecimal.valueOf(0.06), StateTax.IL.getRate());
    }

    @Test
    void testCARate() {
        assertEquals(BigDecimal.valueOf(0.06), StateTax.CA.getRate());
    }

    @Test
    void testNYRate() {
        assertEquals(BigDecimal.valueOf(0.06), StateTax.NY.getRate());
    }

    @Test
    void testOtherRate() {
        assertEquals(BigDecimal.ZERO, StateTax.OTHER.getRate());
    }

    @Test
    void testIsValidStateTrue() {
        assertTrue(StateTax.isValidState("IL"));
        assertTrue(StateTax.isValidState("CA"));
        assertTrue(StateTax.isValidState("NY"));
        assertTrue(StateTax.isValidState("TX"));
        assertTrue(StateTax.isValidState("FL"));
        assertTrue(StateTax.isValidState("AL"));
        assertTrue(StateTax.isValidState("WY"));
    }

    @Test
    void testIsValidStateFalse() {
        assertFalse(StateTax.isValidState("XX"));
        assertFalse(StateTax.isValidState("ZZ"));
        assertFalse(StateTax.isValidState(""));
        assertFalse(StateTax.isValidState("NOT_A_STATE"));
    }

    @Test
    void testIsValidStateCaseInsensitive() {
        assertTrue(StateTax.isValidState("il"));
        assertTrue(StateTax.isValidState("ca"));
        assertTrue(StateTax.isValidState("ny"));
        assertTrue(StateTax.isValidState("Il"));
        assertTrue(StateTax.isValidState("iL"));
    }

    @Test
    void testFromStringValid() {
        assertEquals(StateTax.IL, StateTax.fromString("IL"));
        assertEquals(StateTax.CA, StateTax.fromString("CA"));
        assertEquals(StateTax.NY, StateTax.fromString("NY"));
    }

    @Test
    void testFromStringCaseInsensitive() {
        assertEquals(StateTax.IL, StateTax.fromString("il"));
        assertEquals(StateTax.CA, StateTax.fromString("ca"));
        assertEquals(StateTax.NY, StateTax.fromString("ny"));
    }

    @Test
    void testFromStringOther() {
        assertEquals(StateTax.OTHER, StateTax.fromString("TX"));
        assertEquals(StateTax.OTHER, StateTax.fromString("FL"));
        assertEquals(StateTax.OTHER, StateTax.fromString("XX"));
        assertEquals(StateTax.OTHER, StateTax.fromString("ZZ"));
    }

    @Test
    void testValues() {
        StateTax[] all = StateTax.values();
        assertEquals(4, all.length);
        assertTrue(java.util.Arrays.asList(all).contains(StateTax.IL));
        assertTrue(java.util.Arrays.asList(all).contains(StateTax.CA));
        assertTrue(java.util.Arrays.asList(all).contains(StateTax.NY));
        assertTrue(java.util.Arrays.asList(all).contains(StateTax.OTHER));
    }
}
