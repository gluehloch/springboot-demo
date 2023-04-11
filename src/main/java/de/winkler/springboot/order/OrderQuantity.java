package de.winkler.springboot.order;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class OrderQuantity {

    public OrderQuantity() {
    }

    public OrderQuantity(int value) {
        this.value = value;
    }

    @Column(name = "quantity", length = 50, nullable = false)
    private int value;

    public int value() {
        return getValue();
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "OrderQuantity{" +
                "value=" + value +
                '}';
    }

    public static OrderQuantity of(int quantity) {
        return new OrderQuantity(quantity);
    }

}
