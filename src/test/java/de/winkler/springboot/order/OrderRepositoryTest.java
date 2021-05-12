package de.winkler.springboot.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @DisplayName("Repository test: Find all orders")
    @Test
    @Tag("repository")
    @Transactional
    @Rollback
    void findOrder() {
        OrderBasketEntity orderBasket = new OrderBasketEntity();
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
