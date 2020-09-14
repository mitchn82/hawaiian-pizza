package com.graphaware.pizzeria.service;

import com.graphaware.pizzeria.model.Pizza;
import com.graphaware.pizzeria.model.PizzeriaUser;
import com.graphaware.pizzeria.model.Purchase;
import com.graphaware.pizzeria.model.PurchaseState;
import com.graphaware.pizzeria.repository.PizzeriaUserRepository;
import com.graphaware.pizzeria.repository.PurchaseRepository;
import com.graphaware.pizzeria.security.PizzeriaUserPrincipal;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class PurchaseService {

    //cache for the ongoing order
    private final Map<PizzeriaUser, Purchase> ongoingPurchases = new HashMap<>();

    private final PurchaseRepository purchaseRepository;
    private final PizzeriaUserRepository pizzeriaUserRepository;

    @Autowired
    private DiscountService discountService;

    public PurchaseService(PurchaseRepository purchaseRepository, PizzeriaUserRepository pizzeriaUserRepository) {
        this.purchaseRepository = purchaseRepository;
        this.pizzeriaUserRepository = pizzeriaUserRepository;
    }

    @PreAuthorize("hasAuthority('ADD_PIZZA')")
    @Transactional
    public Purchase addPizzaToPurchase(Pizza pizza) {
        PizzeriaUser currentUser = getCurrentUser();

        List<Purchase> purchases = purchaseRepository.findAllByStateEqualsAndCustomer_Id(PurchaseState.DRAFT, currentUser.getId());
        if (purchases.size() > 1) {
            throw new PizzeriaException();
        }
        Purchase purchase;
        if (purchases.isEmpty()) {
            purchase = new Purchase();
            purchase.setCustomer(currentUser);
            purchase.setState(PurchaseState.DRAFT);
        }
        else {
            purchase = purchases.get(0);
        }
        if (purchase.getPizzas() == null) {
            purchase.setPizzas(new LinkedList<>());
        }
        purchase.setCreationDate(new Date());
        //TODO: create pizza model based on passed id
        //actually this method updates models trusting client request, which is bad!
        purchase.getPizzas().add(pizza);
        purchaseRepository.save(purchase);
        return purchase;
    }

    @PreAuthorize("hasAuthority('CONFIRM_PURCHASE')")
    public void confirmPurchase() {
        PizzeriaUser currentUser = getCurrentUser();
        List<Purchase> purchases = purchaseRepository.findAllByStateEqualsAndCustomer_Id(PurchaseState.DRAFT, currentUser.getId());
        if (purchases.size() != 1) {
            throw new PizzeriaException();
        }
        Purchase purchase = purchases.get(0);
        purchase.setState(PurchaseState.PLACED);
        purchaseRepository.save(purchase);
    }

    @PreAuthorize("hasAuthority('PICK_PURCHASE')")
    public Purchase pickPurchase() {
        PizzeriaUser currentUser = getCurrentUser();
        Purchase purchase = purchaseRepository.findFirstByStateEquals(PurchaseState.PLACED);
        if (purchase == null) {
            throw new PizzeriaException("Placed Purchase not found");
        }
        purchase.setWorker(currentUser);
        purchase.setState(PurchaseState.ONGOING);
        //can work only on a single order!
        if (ongoingPurchases.containsKey(currentUser)) {
            throw new PizzeriaException();
        }
        ongoingPurchases.put(currentUser, purchase);
        return purchaseRepository.save(purchase);
    }

    @PreAuthorize("hasAuthority('PIZZA_MAKER')")
    public Purchase completePurchase(long id) {
        PizzeriaUser currentUser = getCurrentUser();

        Purchase purchase = purchaseRepository.findById(id).orElseThrow(PizzeriaException::new);

        if (!purchase.getState().equals(PurchaseState.ONGOING)) {
            throw new PizzeriaException();
        }
        if (ongoingPurchases.get(currentUser) != null
                && ongoingPurchases.get(currentUser).getId() != purchase.getId()) {
            throw new PizzeriaException();
        }
        purchase.setCheckoutDate(new Date());
        purchase.setState(PurchaseState.SERVED);
        purchase.setAmount(computeAmount(purchase));
        purchaseRepository.save(purchase);
        ongoingPurchases.remove(currentUser);

        try {
            new EmailService().sendConfirmationEmail(currentUser, purchase);
        }
        catch (Exception e) {
        }
        //added purchase confirm 
        return purchase;
    }

    private Double computeAmount(Purchase purchase) {
        List<Pizza> pizzas = purchase.getPizzas();
        double totalPrice = 0;
        if (pizzas == null) {
            return 0.0;
        }

        totalPrice = pizzas.stream().mapToDouble(Pizza::getPrice).sum();
        double discount = discountService.computeDiscount(purchase);

        return totalPrice - discount;
    }

    @PreAuthorize("hasAuthority('PIZZA_MAKER')")
    public Purchase getCurrentPurchase() {
        return ongoingPurchases.get(getCurrentUser());
    }

    private PizzeriaUser getCurrentUser() {
        return ((PizzeriaUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
    }
}
