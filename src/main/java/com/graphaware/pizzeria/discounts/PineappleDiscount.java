package com.graphaware.pizzeria.discounts;

import com.graphaware.pizzeria.model.Pizza;
import com.graphaware.pizzeria.model.Purchase;
import java.util.List;

/**
 *
 * @author mitch
 */
public class PineappleDiscount implements IDiscount {

    @Override
    public double compute(Purchase purchase) {
        List<Pizza> pizzas = purchase.getPizzas();

        double discount = 0;
        if (pizzas == null) {
            return 0.0;
        }
        // buy a pineapple pizza, get 10% off the others
        boolean applyPineappleDiscount = pizzas.stream()
                .filter(p -> p.getToppings().contains("pineapple"))
                .count() > 0;

        for (Pizza pizza : pizzas) {
            if (applyPineappleDiscount
                    && !pizza.getToppings().contains("pineapple")) {
                discount += pizza.getPrice() * 0.1;
            }
        }
        return discount;
    }

}
