package de.winkler.springboot.order;

import de.winkler.springboot.user.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public OrderBasketEntity createNewBasket(UserEntity user, OrderItemEntity orderItem) {
        // TODO

        return null;
    }

    @Transactional
    public OrderBasketEntity addOrderItem(OrderBasketEntity orderBasket, OrderItemEntity orderItem) {
        // TODO

        return orderBasket;
    }

}
