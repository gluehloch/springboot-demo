package de.winkler.springboot.order;

import jakarta.persistence.*;

@Entity(name = "OrderItem")
@Table(name = "ORDERITEM")
public class OrderItemEntity implements de.winkler.springboot.persistence.Id<OrderItemEntity> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Embedded
    private ISIN isin;

    @Embedded
    private OrderQuantity orderQuantity;

    @ManyToOne(fetch = FetchType.LAZY)
    private OrderBasketEntity order;

    @Override
    public Long id() {
        return id;
    }

    @Override
    public OrderItemEntity type() {
        return this;
    }

    public Long getId() {
        return id;
    }

    public ISIN getIsin() {
        return isin;
    }

    public void setIsin(ISIN isin) {
        this.isin = isin;
    }

    public OrderQuantity getOrderQuantity() {
        return orderQuantity;
    }

    public void setOrderQuantity(OrderQuantity orderQuantity) {
        this.orderQuantity = orderQuantity;
    }

    public OrderBasketEntity getOrder() {
        return order;
    }

    public void setOrder(OrderBasketEntity order) {
        this.order = order;
    }

}
