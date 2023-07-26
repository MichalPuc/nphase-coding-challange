package com.nphase.service;

import com.nphase.entity.Product;
import com.nphase.entity.ShoppingCart;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ShoppingCartService {

    private int bulkItemThreshold; // The number of items required to get the bulk discount
    private double bulkItemDiscount; // The percentage discount for bulk items

    // Map to store the discount policy for each category
    private Map<String, DiscountPolicy> categoryDiscountPolicies;

    public ShoppingCartService(int bulkItemThreshold, double bulkItemDiscount) {
        this.bulkItemThreshold = bulkItemThreshold;
        this.bulkItemDiscount = bulkItemDiscount;
        this.categoryDiscountPolicies = new HashMap<>();
    }

    // Add a discount policy for a specific category
    public void addCategoryDiscountPolicy(String category, int itemThreshold, double discountPercentage) {
        DiscountPolicy discountPolicy = new DiscountPolicy(itemThreshold, discountPercentage);
        categoryDiscountPolicies.put(category, discountPolicy);
    }

    public BigDecimal calculateTotalPrice(ShoppingCart shoppingCart) {
        BigDecimal total = BigDecimal.ZERO;

        for (Product product : shoppingCart.getProducts()) {
            int quantity = product.getQuantity();
            BigDecimal pricePerUnit = product.getPricePerUnit();
            BigDecimal productTotal;

            productTotal = pricePerUnit.multiply(BigDecimal.valueOf(quantity));

            // Check if there's a discount policy for the product's category
            if (product.getCategory() != null && categoryDiscountPolicies.containsKey(product.getCategory())) {
                DiscountPolicy discountPolicy = categoryDiscountPolicies.get(product.getCategory());
                productTotal = productTotal.subtract(calculateProductDiscount(quantity, pricePerUnit, discountPolicy));
            }

            // Check if there's a bulk dicscount
            productTotal = productTotal.subtract(calculateBulkDiscount(quantity, pricePerUnit));
            total = total.add(productTotal);
        }

        return total;
    }

    private BigDecimal calculateProductDiscount(int quantity, BigDecimal pricePerUnit, DiscountPolicy discountPolicy) {
        int discountedQuantity = quantity;
        if (quantity > discountPolicy.itemThreshold) {
            // Calculate the number of items to apply the discount to
            discountedQuantity = quantity;
            //- (quantity / discountPolicy.itemThreshold);

        }

        // Calculate the product total after applying the discount
        BigDecimal discountedPricePerUnit = pricePerUnit.multiply(BigDecimal.valueOf(discountPolicy.discountPercentage));

        return discountedPricePerUnit.multiply(BigDecimal.valueOf(discountedQuantity));
    }

    private BigDecimal calculateBulkDiscount(int quantity, BigDecimal pricePerUnit) {
        if (quantity > bulkItemThreshold) {
            BigDecimal discountedPricePerUnit = pricePerUnit.multiply(BigDecimal.valueOf(bulkItemDiscount));
            return discountedPricePerUnit.multiply(BigDecimal.valueOf(quantity));
        }
        return BigDecimal.ZERO;
    }

    private static class DiscountPolicy {
        private final int itemThreshold;
        private final double discountPercentage;

        public DiscountPolicy(int itemThreshold, double discountPercentage) {
            this.itemThreshold = itemThreshold;
            this.discountPercentage = discountPercentage;
        }
    }
}
