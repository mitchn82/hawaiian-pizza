/*
 *  Copyright (C) LogiSpin Italia srl - 2020
 *  
 *  All rights reserved
 */
package com.graphaware.pizzeria.discounts;

import com.graphaware.pizzeria.model.Pizza;
import com.graphaware.pizzeria.model.Purchase;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author mitch
 */
public class CheapestDiscountTest {

    public CheapestDiscountTest() {
    }

    @Test
    public void shouldDiscountFirstOne() {
        Purchase purchase = new Purchase();

        Pizza pizza1 = new Pizza();
        List<String> toppings1 = new ArrayList<>();
        toppings1.add("mozzarella");
        pizza1.setToppings(toppings1);
        pizza1.setPrice(8.0D);

        Pizza pizza2 = new Pizza();
        List<String> toppings2 = new ArrayList<>();
        toppings2.add("mozzarella");
        toppings2.add("pineapple");
        pizza2.setToppings(toppings2);
        pizza2.setPrice(9.0D);

        Pizza pizza3 = new Pizza();
        List<String> toppings3 = new ArrayList<>();
        toppings3.add("tomatoes");
        pizza3.setToppings(toppings3);
        pizza3.setPrice(10.0D);

        purchase.setPizzas(Arrays.asList(pizza1, pizza2, pizza3));

        CheapestDiscount cheapestDiscount = new CheapestDiscount();
        double discount = cheapestDiscount.compute(purchase);

        Assertions.assertEquals(8.0D, discount);
    }

    @Test
    public void shouldNotDiscount() {
        Purchase purchase = new Purchase();

        Pizza pizza1 = new Pizza();
        List<String> toppings1 = new ArrayList<>();
        toppings1.add("mozzarella");
        pizza1.setToppings(toppings1);
        pizza1.setPrice(10.0D);

        Pizza pizza3 = new Pizza();
        List<String> toppings3 = new ArrayList<>();
        toppings3.add("tomatoes");
        pizza3.setToppings(toppings3);
        pizza3.setPrice(10.0D);

        purchase.setPizzas(Arrays.asList(pizza1, pizza3));

        CheapestDiscount cheapestDiscount = new CheapestDiscount();
        double discount = cheapestDiscount.compute(purchase);

        Assertions.assertEquals(0.0D, discount);
    }

    @Test
    public void shouldDiscountTheFirstOccurrence() {
        Purchase purchase = new Purchase();

        Pizza pizza1 = new Pizza();
        List<String> toppings1 = new ArrayList<>();
        toppings1.add("mozzarella");
        pizza1.setToppings(toppings1);
        pizza1.setPrice(9.0D);

        Pizza pizza2 = new Pizza();
        List<String> toppings2 = new ArrayList<>();
        toppings2.add("mozzarella");
        toppings2.add("pineapple");
        pizza2.setToppings(toppings2);
        pizza2.setPrice(9.0D);

        Pizza pizza3 = new Pizza();
        List<String> toppings3 = new ArrayList<>();
        toppings3.add("tomatoes");
        pizza3.setToppings(toppings3);
        pizza3.setPrice(10.0D);

        purchase.setPizzas(Arrays.asList(pizza1, pizza2, pizza3));

        CheapestDiscount cheapestDiscount = new CheapestDiscount();
        double discount = cheapestDiscount.compute(purchase);

        Assertions.assertEquals(9.0D, discount);
    }
}
