package de.winkler.springboot.order;

import de.winkler.springboot.user.UserEntity;
import de.winkler.springboot.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public OrderBasketEntity createNewBasket(UserEntity user, OrderItemEntity orderItem) {
        // TODO

        UserEntity userEntity = userRepository.findByNickname(user.getNickname()).orElseThrow(IllegalStateException::new);

        OrderBasketEntity orderBasketEntity = new OrderBasketEntity();
        orderBasketEntity.setUser(userEntity);

        // orderRepository.find

        // orderBasketEntity.addOrderItem();

        return null;
    }

    @Transactional
    public OrderBasketEntity addOrderItem(OrderBasketEntity orderBasket, OrderItemEntity orderItem) {
        // TODO

        return orderBasket;
    }

}
