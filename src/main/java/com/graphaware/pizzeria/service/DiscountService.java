/*
 *  Copyright (C) LogiSpin Italia srl - 2020
 *  
 *  All rights reserved
 */
package com.graphaware.pizzeria.service;

import com.graphaware.pizzeria.discounts.CheapestDiscount;
import com.graphaware.pizzeria.discounts.IDiscount;
import com.graphaware.pizzeria.discounts.PineappleDiscount;
import com.graphaware.pizzeria.model.Purchase;
import java.util.LinkedList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 *
 * @author mitch
 */
@Service
public class DiscountService {

    private final List<IDiscount> discounts = new LinkedList<>();

    //TODO: those should be dynamics, for example on DB or on application config
    //The service should also support promotions priority
    public DiscountService() {
        discounts.add(new PineappleDiscount());
        discounts.add(new CheapestDiscount());
    }

    public double computeDiscount(Purchase purchase) {
        double amount = 0.0D;

        for (IDiscount discount : discounts) {
            amount += discount.compute(purchase);
        }

        return amount;
    }
}
