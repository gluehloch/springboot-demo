package de.winkler.springboot.order;

import javax.persistence.*;

@Entity(name = "OrderItem")
@Table(name = "ORDERITEM")
public class OrderItemEntity implements de.winkler.springboot.persistence.Id<OrderItemEntity> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Embedded
    private ISIN isin;

    @Column(name = "quantity", length = 50, nullable = false)
    private int quantity;

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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public OrderBasketEntity getOrder() {
        return order;
    }

    public void setOrder(OrderBasketEntity order) {
        this.order = order;
    }

}
