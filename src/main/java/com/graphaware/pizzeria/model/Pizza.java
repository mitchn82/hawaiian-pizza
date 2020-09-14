package com.graphaware.pizzeria.model;

import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
public class Pizza {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;

    @Convert(converter = ToppingConverter.class)
    @Column(length = 10485760)
    private List<String> toppings;

    @NotNull
    private Double price;
}
