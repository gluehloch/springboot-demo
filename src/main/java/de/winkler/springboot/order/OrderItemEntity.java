package de.winkler.springboot.order;

import org.hibernate.annotations.NaturalId;

import javax.persistence.*;

@Entity(name = "OrderItem")
@Table(name = "ORDERITEM")
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NaturalId
    @Column(name = "wkn", length = 50, nullable = false, unique = true)
    private String wkn;

    @ManyToOne(fetch = FetchType.LAZY)
    private OrderBasketEntity order;

    public OrderBasketEntity getOrder() {
        return order;
    }

    public void setOrder(OrderBasketEntity order) {
        this.order = order;
    }

}
