package com.graphaware.pizzeria.discounts;

import com.graphaware.pizzeria.model.Purchase;

/**
 *
 * @author mitch
 */
public interface IDiscount {
    
    /**
     * Comutes a discount based on purchase model
     * @param purchase 
     * @return the discount amount
     */
    public double compute(Purchase purchase);
}
