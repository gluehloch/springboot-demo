package de.winkler.springboot.order;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class OrderQuantity {

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

}
