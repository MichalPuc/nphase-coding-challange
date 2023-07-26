package com.nphase.service;

import com.nphase.entity.Product;
import com.nphase.entity.ShoppingCart;
import com.nphase.service.ShoppingCartService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;

public class ShoppingCartServiceTest {

    private ShoppingCartService service;

    @BeforeEach
    public void setup() {
        service = new ShoppingCartService(3, 0.10);
        service.addCategoryDiscountPolicy("drinks", 3, 0.10);
    }
    @Test
    public void calculateTotalPriceWithoutDiscount() {
        ShoppingCart cart = new ShoppingCart(Arrays.asList(
                new Product("Tea", BigDecimal.valueOf(5.0), 2, null),
                new Product("Coffee", BigDecimal.valueOf(6.5), 1, null)
        ));

        BigDecimal result = service.calculateTotalPrice(cart);

        Assertions.assertEquals(BigDecimal.valueOf(16.5), result);
    }

    @Test
    public void calculateTotalPriceWithBulkDiscount() {
        ShoppingCart cart = new ShoppingCart(Arrays.asList(
                new Product("Tea", BigDecimal.valueOf(5.0), 5, null),
                new Product("Coffee", BigDecimal.valueOf(3.5), 3, null)
        ));

        BigDecimal result = service.calculateTotalPrice(cart);

        // 10% discount for 5 Tea items (5 * 5.0 * 0.9) + 10.5 (3 * 3.5)
        BigDecimal expected = BigDecimal.valueOf(22.5).add(BigDecimal.valueOf(10.5));

        // Use stripTrailingZeros() to remove any trailing zeros for comparison
        Assertions.assertEquals(expected.stripTrailingZeros(), result.stripTrailingZeros());
    }

    @Test
    public void calculateTotalPriceWithCategoryDiscount() {
        ShoppingCart cart = new ShoppingCart(Arrays.asList(
                new Product("Tea", BigDecimal.valueOf(5.3), 2, "drinks"),
                new Product("Coffee", BigDecimal.valueOf(3.5), 2, "drinks"),
                new Product("Cheese", BigDecimal.valueOf(8), 2, "food")
        ));

        BigDecimal result = service.calculateTotalPrice(cart);

        // 10% discount for 4 drinks (2 * 5.3 * 0.9) + 6.3 (2 * 3.5 * 0.9) + 16 (2 * 8)
        Assertions.assertEquals(BigDecimal.valueOf(9.54).add(BigDecimal.valueOf(6.3)).add(BigDecimal.valueOf(16)), result);
    }

    @Test
    public void calculateTotalPriceWithBulkAndCategoryDiscount() {
        ShoppingCart cart = new ShoppingCart(Arrays.asList(
                new Product("Tea", BigDecimal.valueOf(5.3), 5, "drinks"),
                new Product("Coffee", BigDecimal.valueOf(3.5), 2, "drinks"),
                new Product("Cheese", BigDecimal.valueOf(8), 2, "food")
        ));

        BigDecimal result = service.calculateTotalPrice(cart);

        // 10% discount for 7 drinks, 10% discount for 5 Tea items
        BigDecimal expected = BigDecimal.valueOf(21.2).add(BigDecimal.valueOf(6.3)).add(BigDecimal.valueOf(16));

        // Use stripTrailingZeros() to remove any trailing zeros for comparison
        Assertions.assertEquals(expected.stripTrailingZeros(), result.stripTrailingZeros());
    }
}
