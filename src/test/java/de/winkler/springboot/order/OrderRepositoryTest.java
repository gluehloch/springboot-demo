package de.winkler.springboot.order;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import jakarta.transaction.Transactional;

import de.winkler.springboot.user.Nickname;
import de.winkler.springboot.user.internal.UserEntity;
import de.winkler.springboot.user.internal.UserRepository;

@SpringBootTest
class OrderRepositoryTest {

    @Autowired
    private OrderBasketRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("Repository test: Find all orders")
    @Test
    @Tag("repository")
    @Transactional
    @Rollback
    void findOrder() {
        UserEntity user = UserEntity.UserBuilder
                .of(Nickname.of("Frosch"), "password")
                .firstname("Kermit")
                .name("Frogger")
                .build();
        userRepository.save(user);

        OrderBasketEntity orderBasket = new OrderBasketEntity();
        orderBasket.setUuid(UUID.randomUUID());
        orderBasket.setUser(user);
        orderBasket = orderRepository.save(orderBasket);
        assertThat(orderBasket.getId()).isNotNull();

        OrderBasketEntity orderEntity = orderRepository.findById(orderBasket.getId()).orElseThrow();
        assertThat(orderEntity).isNotNull();
        assertThat(orderEntity).isEqualTo(orderBasket);
        assertThat(orderEntity).hasSameHashCodeAs(orderBasket);

        OrderBasketEntity orderEntity1 = orderRepository.findOrderBasket(orderBasket.getId());
        assertThat(orderEntity1.getOrderItems()).hasSize(0);

        assertThat(orderEntity.getOrderItems()).hasSize(0);
    }

}
