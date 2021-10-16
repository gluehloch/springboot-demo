package de.winkler.springboot.order;

import de.winkler.springboot.user.Nickname;
import de.winkler.springboot.user.UserEntity;
import de.winkler.springboot.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.util.Optional;
import java.util.UUID;

import static de.winkler.springboot.logger.ExceptionMessageFormatter.format;

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
    public OrderResult createNewBasket(Nickname nickname, ISIN isin, int quantity) {
        UserEntity user = userRepository.findByNickname(nickname).orElseThrow(IllegalArgumentException::new);

        OrderItemEntity orderItem = new OrderItemEntity();
        orderItem.setIsin(isin);
        orderItem.setQuantity(quantity);

        return createNewBasket(user, orderItem);
    }

    @Transactional
    public OrderResult createNewBasket(UserEntity user, OrderItemEntity orderItem) {
        UserEntity userEntity = userRepository.findByNickname(user.getNickname()).orElseThrow(IllegalStateException::new);

        Optional<OrderBasketEntity> unprocessedBasket = orderRepository.findOpenBasket(user);


        if (unprocessedBasket != null && unprocessedBasket.isProcessed()) {
            throw new IllegalStateException(format("There is an unprocessed basket for user=[{}] with UUID=[{}]", user.getNickname(), unprocessedBasket.getUuid()));
        }

        OrderBasketEntity orderBasketEntity = new OrderBasketEntity();
        orderBasketEntity.setUuid(UUID.randomUUID());
        orderBasketEntity.setUser(userEntity);
        orderBasketEntity.addOrderItem(orderItem);

        return orderRepository.save(orderBasketEntity);
    }

    @Transactional
    public OrderResult addOrderItem(OrderBasketEntity orderBasket, OrderItemEntity orderItem) {
        // ... TODO ...

        return orderBasket;
    }

    public interface Result<RESULT_TYPE, ERROR_TYPE> {
        RESULT_TYPE getResult();
        ERROR_TYPE getError();
        String getErrorMessage();
    }

    public interface OrderResult extends Result<OrderBasketJson, OrderResult.Error> {
        enum Error {
            DENIED,
            CANT_CREATE_BASKET
        }

        @Override
        OrderBasketJson getResult();

        @Override
        Error getError();

        @Override
        String getErrorMessage();

        default boolean success() {
            return getResult() != null && getError() == null;
        }
    }

}
