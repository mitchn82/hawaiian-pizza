package com.graphaware.pizzeria.discounts;

import com.graphaware.pizzeria.model.Pizza;
import com.graphaware.pizzeria.model.Purchase;
import java.util.Comparator;
import java.util.List;

/**
 * Computes the cheapest of the 3 pizzas discount rule
 * 
 * @author mitch
 */
public class CheapestDiscount implements IDiscount {

    @Override
    public double compute(Purchase purchase) {
        List<Pizza> pizzas = purchase.getPizzas();

        double discount = 0;
        if (pizzas == null) {
            return 0.0;
        }
        
        if (pizzas.size() == 3) {
            Pizza cheapestPizza = pizzas.stream().min(Comparator.comparingDouble(Pizza::getPrice)).get();
            discount = cheapestPizza.getPrice();
        }

        return discount;
    }

}
