package de.winkler.springboot.order;

import de.winkler.springboot.user.internal.UserEntity;
import de.winkler.springboot.user.internal.UserRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

import static de.winkler.springboot.logger.ExceptionMessageFormatter.format;

@Service
public class OrderService {

    private final OrderItemRepository orderItemRepository;
    private final OrderBasketRepository orderBasketRepository;
    private final UserRepository userRepository;

    public OrderService(OrderItemRepository orderItemRepository, OrderBasketRepository orderBasketRepository, UserRepository userRepository) {
        this.orderItemRepository = orderItemRepository;
        this.orderBasketRepository = orderBasketRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public OrderResult createNewBasket(StockOrder stockOrder) {
        Optional<UserEntity> user = userRepository.findByNickname(stockOrder.nickname());

        Function<UserEntity, OrderBasketEntity> createBasket = (u) -> {
            OrderBasketEntity orderBasket = new OrderBasketEntity();
            orderBasket.setUser(u);
            orderBasket.setUuid(UUID.randomUUID());
            orderBasket.setClosed(false);
            return orderBasket;
        };

        // TODO Unsinnger Name: stockOrder2
        BiFunction<StockOrder, OrderBasketEntity, OrderBasketEntity> addOrderItem = (stockOrder2, orderBasket) -> {
            OrderItemEntity oie = new OrderItemEntity();
            oie.setIsin(stockOrder2.isin());
            oie.setOrderQuantity(stockOrder2.orderQuantity());
            orderBasket.addOrderItem(oie);
            return orderBasket;
        };

        // TODO Das geht nicht: user.map(createBasket).map(addOrderItem);

        Function<OrderBasketEntity, OrderResult> createOrderResult = (ob) -> DefaultOrderResult.of(ob, OrderResult.ResultState.SUCCESS, "ok");

        Function<OrderBasketEntity, OrderResult> createOrderResult2 = (ob) -> {
            return DefaultOrderResult.of(ob, OrderResult.ResultState.SUCCESS, "ok");
        };

        Optional<OrderResult> orderResult1 = user.map(createBasket).map(createOrderResult);

        // TODO
        return orderResult1.get();
    }

    @Transactional
    public OrderResult addToBasket(StockOrderWithBasket stockOrderWithBasket) {
        Optional<UserEntity> user = userRepository.findByNickname(stockOrderWithBasket.nickname());
        Optional<OrderBasketEntity> orderBasket = orderBasketRepository.findOrderBasket(stockOrderWithBasket.basketID());

        OrderItemEntity orderItem = new OrderItemEntity();
        orderItem.setOrderQuantity(stockOrderWithBasket.orderQuantity());
        orderItem.setIsin(stockOrderWithBasket.isin());
        orderBasket.get().addOrderItem(orderItem);

        return null; // TODO
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
        ERROR_TYPE getResultState();
        String getMessage();
    }

    public interface OrderResult extends Result<OrderBasketEntity, OrderResult.ResultState> {
        enum ResultState {
            SUCCESS,
            ERROR,
            DENIED,
            CANT_CREATE_BASKET
        }

        @Override
        OrderBasketEntity getResult();

        @Override
        ResultState getResultState();

        @Override
        String getMessage();

        default boolean success() {
            return getResultState().equals(ResultState.SUCCESS);
        }

        default boolean error() {
            return !success();
        }
    }

    public static class DefaultOrderResult implements OrderResult {
        private final OrderBasketEntity orderBasket;
        private final ResultState resultState;
        private final String message;

        static OrderResult of(OrderBasketEntity basket, ResultState resultState, String message) {
            return new DefaultOrderResult(basket, resultState, message);
        }

        private DefaultOrderResult(OrderBasketEntity orderBasket, ResultState resultState, String message) {
            this.orderBasket = orderBasket;
            this.resultState = resultState;
            this.message = message;
        }

        @Override
        public OrderBasketEntity getResult() {
            return orderBasket;
        }

        @Override
        public ResultState getResultState() {
            return resultState;
        }

        @Override
        public String getMessage() {
            return message;
        }
    }

}
