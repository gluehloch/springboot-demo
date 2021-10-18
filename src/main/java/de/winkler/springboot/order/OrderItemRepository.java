package de.winkler.springboot.order;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import de.winkler.springboot.persistence.Id;

public interface OrderItemRepository extends CrudRepository<OrderItemEntity, Long> {

    default Optional<OrderItemEntity> findById(Id<OrderItemEntity> orderItemId) {
        return findById(orderItemId.id());
    }

}
