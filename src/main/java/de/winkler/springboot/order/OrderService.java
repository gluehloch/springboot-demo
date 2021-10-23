package de.winkler.springboot.order;

import de.winkler.springboot.user.Nickname;
import de.winkler.springboot.user.UserEntity;
import de.winkler.springboot.user.UserRepository;

import org.hibernate.criterion.Order;
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

    public OrderResult addToBasket(StockOrder stockOrder) {
        Optional<UserEntity> user = userRepository.findByNickname(stockOrder.nickname());
        Optional<OrderBasketEntity> orderBasket = orderBasketRepository.findOrderBasket(stockOrder.basketID());

        OrderItemEntity orderItem = new OrderItemEntity();
        orderItem.setOrderQuantity(stockOrder.orderQuantity());
        orderItem.setIsin(stockOrder.isin());
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
    public OrderResult createNewBasket(Nickname nickname, ISIN isin, int quantity) {
        UserEntity user = userRepository.findByNickname(nickname).orElseThrow(IllegalArgumentException::new); // TODO Das ist jetzt eine schlechte Variante!

        OrderItemJson orderItem = new OrderItemJson();
        orderItem.setIsin(isin);
        orderItem.setQuantity(quantity);

        return createNewBasket(user, orderItem);
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
