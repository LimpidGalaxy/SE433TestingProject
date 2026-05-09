package shoppingcart.model;

import java.math.BigDecimal; 

public enum ShippingOption {
    STANDARD(BigDecimal.valueOf(10.0), BigDecimal.valueOf(50.0)), // cost, free threshold
    NEXT_DAY(BigDecimal.valueOf(25.0), BigDecimal.valueOf(Double.MAX_VALUE)); // no free threshold

    private final BigDecimal cost;
    private final BigDecimal freeThreshold;

    ShippingOption(BigDecimal cost, BigDecimal freeThreshold) {
        this.cost = cost;
        this.freeThreshold = freeThreshold;
    }

    public BigDecimal getCost(BigDecimal rawTotal) {
        return rawTotal.compareTo(freeThreshold) >= 0 ? BigDecimal.ZERO : cost;
    }
}