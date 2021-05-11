package de.winkler.springboot.order;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends CrudRepository<OrderEntity, Long> {

    @Query("SELECT o FROM Order as o JOIN FETCH o.shares WHERE o.id = :id")
    OrderEntity findOrder(@Param("id") long id);

}
