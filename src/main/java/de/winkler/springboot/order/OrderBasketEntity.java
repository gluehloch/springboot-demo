package de.winkler.springboot.order;

import de.winkler.springboot.user.UserEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity(name = "OrderBasket")
@Table(name = "ORDER_BASKET")
public class OrderBasketEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order")
    private List<OrderItemEntity> orderItems = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public List<OrderItemEntity> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemEntity> orderItems) {
        this.orderItems = orderItems;
    }

    public void addOrderItem(OrderItemEntity orderItem) {
        orderItem.setOrder(this);
        orderItems.add(orderItem);
    }

    public void remoteOrderItem(OrderItemEntity orderItem) {
        orderItems.remove(orderItem);
        orderItem.setOrder(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof OrderBasketEntity)) {
            return false;
        }

        OrderBasketEntity other = (OrderBasketEntity) o;
        return Objects.equals(getId(), other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

}
