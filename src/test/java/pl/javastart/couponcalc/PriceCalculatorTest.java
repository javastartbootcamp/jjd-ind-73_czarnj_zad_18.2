package pl.javastart.couponcalc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PriceCalculatorTest {

    private PriceCalculator priceCalculator = new PriceCalculator();
    private List<Product> products;
    private List<Coupon> coupons;

    @BeforeEach
    public void setUp() {
        products = new ArrayList<>();
        coupons = new ArrayList<>();
    }

    @Test
    public void shouldReturnZeroForNoProducts() {
        // given
        // when
        double result = priceCalculator.calculatePrice(null, null);

        // then
        assertThat(result).isEqualTo(0.);
    }

    @Test
    public void shouldReturnPriceForSingleProductAndNoCoupons() {
        // given
        products.add(new Product("Masło", 5.99, Category.FOOD));

        // when
        double result = priceCalculator.calculatePrice(products, null);

        // then
        assertThat(result).isEqualTo(5.99);
    }

    @Test
    public void shouldReturnPriceForSingleProductAndOneCoupon() {
        // given
        products.add(new Product("Masło", 5.99, Category.FOOD));

        coupons.add(new Coupon(Category.FOOD, 20));

        // when
        double result = priceCalculator.calculatePrice(products, coupons);

        // then
        assertThat(result).isEqualTo(4.79);
    }

    @Test
    public void shouldReturnPriceForTwoProductsAndOneCoupon() {
        // given
        products.add(new Product("Masło", 5.99, Category.FOOD));
        products.add(new Product("Dywan", 230, Category.HOME));

        coupons.add(new Coupon(Category.HOME, 10));

        // when
        double result = priceCalculator.calculatePrice(products, coupons);

        // then
        assertThat(result).isEqualTo(212.99);
    }

    @Test
    public void shouldReturnPriceForThreeProductsAndTwoCoupon() {
        // given
        products.add(new Product("Masło", 5.99, Category.FOOD));
        products.add(new Product("Dywan", 230, Category.HOME));
        products.add(new Product("Bilety do kina", 80, Category.ENTERTAINMENT));

        coupons.add(new Coupon(Category.HOME, 5));
        coupons.add(new Coupon(Category.ENTERTAINMENT, 20));

        // when
        double result = priceCalculator.calculatePrice(products, coupons);

        // then
        assertThat(result).isEqualTo(299.99);
    }

    @Test
    public void shouldReturnPriceForThreeProductsAndNoCoupon() {
        // given
        products.add(new Product("Masło", 5.99, Category.FOOD));
        products.add(new Product("Dywan", 230, Category.HOME));
        products.add(new Product("Bilety do kina", 80, Category.ENTERTAINMENT));

        // when
        double result = priceCalculator.calculatePrice(products, new ArrayList<>());

        // then
        assertThat(result).isEqualTo(315.99);
    }

}