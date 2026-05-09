package shoppingcart.model;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set; 

public enum StateTax {
    IL(BigDecimal.valueOf(0.06)),
    CA(BigDecimal.valueOf(0.06)),
    NY(BigDecimal.valueOf(0.06)),
    OTHER(BigDecimal.ZERO);

    private final BigDecimal rate;

    private static final Set<String> VALID_STATES = new HashSet<>(Arrays.asList(
        "AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA",
        "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD",
        "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ",
        "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC",
        "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY"
    ));

    StateTax(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public static boolean isValidState(String state) {
        return VALID_STATES.contains(state.toUpperCase());
    }

    public static StateTax fromString(String state) {
        try {
            return StateTax.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            return OTHER;
        }
    }
}