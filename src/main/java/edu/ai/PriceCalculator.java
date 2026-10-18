package edu.ai;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

public final class PriceCalculator {
    private static final BigDecimal MAX_SUBTOTAL = new BigDecimal("10000");
    private static final BigDecimal MINIMUM_STUDENT_SUBTOTAL = new BigDecimal("50");
    private static final BigDecimal STUDENT_DISCOUNT_RATE = new BigDecimal("0.10");
    private static final BigDecimal MAX_STUDENT_DISCOUNT = new BigDecimal("20.00");

    private PriceCalculator() {}

    public static BigDecimal calculate(BigDecimal subtotal, String coupon) {
        if (subtotal == null || subtotal.signum() < 0
                || subtotal.compareTo(MAX_SUBTOTAL) > 0
                || subtotal.stripTrailingZeros().scale() > 2) {
            throw new IllegalArgumentException("Invalid subtotal");
        }
        String code = coupon == null ? "" : coupon.strip().toUpperCase(Locale.ROOT);
        if ("STUDENT10".equals(code) && subtotal.compareTo(MINIMUM_STUDENT_SUBTOTAL) >= 0) {
            BigDecimal discount = subtotal.multiply(STUDENT_DISCOUNT_RATE)
                    .setScale(2, RoundingMode.HALF_UP)
                    .min(MAX_STUDENT_DISCOUNT);
            return subtotal.subtract(discount).setScale(2, RoundingMode.UNNECESSARY);
        }
        return subtotal.setScale(2, RoundingMode.UNNECESSARY);
    }
}
