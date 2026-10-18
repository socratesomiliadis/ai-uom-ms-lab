package edu.ai;

import java.math.BigDecimal;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PriceCalculatorTest {
    @ParameterizedTest
    @CsvSource({
        "49.99, 49.99",
        "50.00, 45.00",
        "50.05, 45.04",
        "50.15, 45.13",
        "500.00, 480.00"
    })
    void calculatesStudentDiscount(String subtotal, String expected) {
        BigDecimal result = PriceCalculator.calculate(new BigDecimal(subtotal), "STUDENT10");

        assertEquals(new BigDecimal(expected), result);
        assertEquals(2, result.scale());
    }

    @ParameterizedTest
    @CsvSource({
        "-0.01",
        "10000.01",
        "50.001"
    })
    void rejectsInvalidSubtotal(String subtotal) {
        assertThrows(IllegalArgumentException.class,
                () -> PriceCalculator.calculate(new BigDecimal(subtotal), "STUDENT10"));
    }

    @Test
    void rejectsNullSubtotal() {
        assertThrows(IllegalArgumentException.class,
                () -> PriceCalculator.calculate(null, "STUDENT10"));
    }

    @Test
    void acceptsTrailingZeroesAndReturnsScaleTwo() {
        BigDecimal result = PriceCalculator.calculate(new BigDecimal("50.000"), "STUDENT10");

        assertEquals(new BigDecimal("45.00"), result);
        assertEquals(2, result.scale());
    }

    @Test
    void normalizesCouponAndIgnoresUnknownOrMissingCoupons() {
        assertEquals(new BigDecimal("90.00"),
                PriceCalculator.calculate(new BigDecimal("100.00"), "  student10  "));
        assertEquals(new BigDecimal("100.00"),
                PriceCalculator.calculate(new BigDecimal("100.00"), "OTHER"));
        assertEquals(new BigDecimal("100.00"),
                PriceCalculator.calculate(new BigDecimal("100.00"), null));
        assertEquals(new BigDecimal("100.00"),
                PriceCalculator.calculate(new BigDecimal("100.00"), "  "));
    }

    @Test
    void couponNormalizationIsIndependentOfDefaultLocale() {
        Locale previous = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            assertEquals(new BigDecimal("90.00"),
                    PriceCalculator.calculate(new BigDecimal("100.00"), "student10"));
        } finally {
            Locale.setDefault(previous);
        }
    }
}
