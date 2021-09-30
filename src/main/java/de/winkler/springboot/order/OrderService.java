package de.winkler.springboot.order;

import de.winkler.springboot.user.Nickname;
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
    public OrderBasketEntity createNewBasket(Nickname nickname, ISIN isin, int quantity) {
        UserEntity user = userRepository.findByNickname(nickname).orElseThrow(IllegalArgumentException::new);
        OrderItemEntity orderItem = new OrderItemEntity();
        orderItem.setIsin(isin);
        orderItem.setQuantity(quantity);
        return createNewBasket(user, orderItem);
    }

    @Transactional
    public OrderBasketEntity createNewBasket(UserEntity user, OrderItemEntity orderItem) {
        UserEntity userEntity = userRepository.findByNickname(user.getNickname()).orElseThrow(IllegalStateException::new);
        OrderBasketEntity basket = orderRepository.findOpenBasket(user);
        if (basket != null && basket.isProcessed()) {
            throw new IllegalStateException("");
        }


        OrderBasketEntity orderBasketEntity = new OrderBasketEntity();
        orderBasketEntity.setUser(userEntity);
        orderBasketEntity.addOrderItem(orderItem);

        // orderRepository.find

        // orderBasketEntity.addOrderItem();

        return basket;
    }

    @Transactional
    public OrderBasketEntity addOrderItem(OrderBasketEntity orderBasket, OrderItemEntity orderItem) {
        // TODO

        return orderBasket;
    }

}
