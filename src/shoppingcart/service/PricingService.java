package shoppingcart.service;

import java.math.BigDecimal;
import shoppingcart.model.ShippingOption;
import shoppingcart.model.StateTax;

public class PricingService {

    public static final BigDecimal MIN_TOTAL = BigDecimal.valueOf(1.0);
    public static final BigDecimal MAX_TOTAL = BigDecimal.valueOf(99999.99);

    public BigDecimal calculateTotal(BigDecimal rawTotal, StateTax stateTax, ShippingOption shippingOption) {
        BigDecimal tax = rawTotal.multiply(stateTax.getRate());
        BigDecimal shipping = shippingOption.getCost(rawTotal);
        return rawTotal.add(tax).add(shipping);
    }

    public boolean isValidQuantity(int quantity) {
        return quantity >= 1;
    }

    public boolean isValidTotal(BigDecimal total) {
        return total.compareTo(MIN_TOTAL) >= 0 && total.compareTo(MAX_TOTAL) <= 0;
    }

    public boolean isValidItemPrice(BigDecimal price) {
        return price.compareTo(BigDecimal.ZERO) > 0; // assuming positive price
    }
}