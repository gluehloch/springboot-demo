package de.winkler.springboot.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
    void findOrder() {
        OrderEntity order = new OrderEntity();
        order = orderRepository.save(order);
        assertThat(order.getId()).isNotNull();

        OrderEntity orderEntity = orderRepository.findById(order.getId()).orElseThrow();
        assertThat(orderEntity).isNotNull();
        assertThat(orderEntity).isEqualTo(order);
        assertThat(orderEntity).hasSameHashCodeAs(order);

        OrderEntity orderEntity1 = orderRepository.findOrder(order.getId());
        assertThat(orderEntity1.getShares()).hasSize(0);

        assertThat(orderEntity.getShares()).hasSize(0);
    }

}
