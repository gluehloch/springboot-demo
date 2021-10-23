package de.winkler.springboot.order;

import de.winkler.springboot.user.UserEntity;
import de.winkler.springboot.user.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.util.Optional;
import java.util.UUID;

import static de.winkler.springboot.logger.ExceptionMessageFormatter.format;

@Service
public class OrderService {

    private final OrderItemRepository orderItemRepository;
    private final OrderBasketRepository orderBasketRepository;
    private final UserRepository userRepository;

    @Autowired
    public OrderService(OrderItemRepository orderItemRepository, OrderBasketRepository orderBasketRepository, UserRepository userRepository) {
        this.orderItemRepository = orderItemRepository;
        this.orderBasketRepository = orderBasketRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public OrderResult createNewBasket(StockOrder stockOrder) {
        Optional<UserEntity> user = userRepository.findByNickname(stockOrder.nickname());

        OrderBasketEntity orderBasket = new OrderBasketEntity();
        orderBasket.setUser(user.get());
        orderBasket.setUuid(UUID.randomUUID());
        orderBasket.setClosed(false);

        // TODO
        return null;
    }

    @Transactional
    public OrderResult addToBasket(StockOrderWithBasket stockOrderWithBasket) {
        Optional<UserEntity> user = userRepository.findByNickname(stockOrderWithBasket.nickname());
        Optional<OrderBasketEntity> orderBasket = orderBasketRepository.findOrderBasket(stockOrderWithBasket.basketID());

        OrderItemEntity orderItem = new OrderItemEntity();
        orderItem.setOrderQuantity(stockOrderWithBasket.orderQuantity());
        orderItem.setIsin(stockOrderWithBasket.isin());
        orderBasket.get().addOrderItem(orderItem);

        return new OrderResult() {
            @Override public OrderBasketJson getResult() {
                return null;
            }

            @Override public Error getError() {
                return null;
            }

            @Override public String getErrorMessage() {
                return null;
            }
        };
    }

    @Transactional
    public OrderResult createNewBasket(UserEntity user, OrderItemJson orderItem) {
        UserEntity userEntity = userRepository.findByNickname(user.getNickname()).orElseThrow(IllegalStateException::new);

        Optional<OrderBasketEntity> unprocessedBasket = orderBasketRepository.findOpenBasket(user);

        if (unprocessedBasket != null && unprocessedBasket.get().isClosed()) {
            String errorMessage = format("There is an unprocessed basket for user=[{}] with UUID=[{}]", user.getNickname(), unprocessedBasket.get().getUuid());
        }

        OrderBasketEntity orderBasketEntity = new OrderBasketEntity();
        orderBasketEntity.setUuid(UUID.randomUUID());
        orderBasketEntity.setUser(userEntity);
        // TODO orderBasketEntity.addOrderItem();

        OrderBasketEntity save = orderBasketRepository.save(orderBasketEntity);
        // TODO assemble OrderResult
        return null;
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
