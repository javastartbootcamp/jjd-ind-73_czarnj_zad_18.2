package pl.javastart.couponcalc;

import org.apache.commons.math3.util.Precision;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static java.util.stream.Collectors.groupingBy;

public class PriceCalculator {

    public double calculatePrice(List<Product> products, List<Coupon> coupons) {
        if (coupons == null || coupons.isEmpty()) {
            return calculateSumOfPrices(products);
        } else if (coupons.size() == 1 && coupons.get(0) == null) {
            double productsPrice = calculateSumOfPrices(products);
            return productsPrice - calculateDiscountValue(productsPrice, coupons.get(0).getDiscountValueInPercents());
        } else {
            Map<Category, List<Product>> productsGroupedByCategory = groupProductsByCategory(products);
            Map<Category, Double> discountValuePerCategory = calculateDiscountValuePerCategory(productsGroupedByCategory,
                    coupons);
            Map.Entry<Category, Double> bestDiscount = findBestDiscountForClient(discountValuePerCategory);

            Predicate<Map.Entry<Category, List<Product>>> bestDiscountFilter = entry -> entry.getKey() != bestDiscount.getKey();
            Predicate<Map.Entry<Category, List<Product>>> noBestDiscountFilter = Predicate.not(bestDiscountFilter);
            double categoriesWithoutDiscountPrice = sumPricesByCategoryWithFilter(productsGroupedByCategory, bestDiscountFilter);
            double categoryWithDiscountPrice = sumPricesByCategoryWithFilter(productsGroupedByCategory, noBestDiscountFilter);

            return categoriesWithoutDiscountPrice + categoryWithDiscountPrice - bestDiscount.getValue();
        }
    }

    private Map<Category, Double> calculateDiscountValuePerCategory(Map<Category, List<Product>> productsGroupedByCategory,
                                                                    List<Coupon> coupons) {
        Map<Category, Double> discountValuePerCategory = new HashMap<>();
        for (Category category : productsGroupedByCategory.keySet()) {
            List<Product> categoryProducts = productsGroupedByCategory.get(category);
            Coupon coupon = findCouponWithCategory(coupons, category);
            if (coupon == null) {
                discountValuePerCategory.put(category, 0.0);
            } else {
                double productsPrice = calculateSumOfPrices(categoryProducts);
                discountValuePerCategory.put(category, calculateDiscountValue(productsPrice, coupon.getDiscountValueInPercents()));
            }
        }
        return  discountValuePerCategory;
    }

    private double calculateSumOfPrices(List<Product> products) {
        if (products == null || products.isEmpty()) {
            return 0;
        }
        return products
                .stream()
                .map(Product::getPrice)
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    private double calculateDiscountValue(double productsPrice, int discount) {
        return Precision.round(productsPrice * discount / 100, 2);
    }

    private Map<Category, List<Product>> groupProductsByCategory(List<Product> products) {
        return products
                .stream()
                .collect(groupingBy(Product::getCategory));
    }

    private Coupon findCouponWithCategory(List<Coupon> coupons, Category category) {
        return coupons
                .stream()
                .filter(coupon1 -> coupon1.getCategory() == category)
                .findFirst()
                .orElse(null);
    }

    private Map.Entry<Category, Double> findBestDiscountForClient(Map<Category, Double> discountValuePerCategory) {
        return discountValuePerCategory.entrySet()
                .stream()
                .max(new MaxDiscountComparator())
                .get();
    }

    private double sumPricesByCategoryWithFilter(Map<Category, List<Product>> productsByCategory, Predicate<Map.Entry<Category, List<Product>>> predicate) {
        return productsByCategory.entrySet()
                .stream()
                .filter(predicate)
                .map(Map.Entry::getValue)
                .flatMap(Collection::stream)
                .map(Product::getPrice)
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    private static class MaxDiscountComparator implements Comparator<Map.Entry<Category, Double>> {
        @Override
        public int compare(Map.Entry<Category, Double> o1, Map.Entry<Category, Double> o2) {
            return Double.compare(o1.getValue(), o2.getValue());
        }
    }
}