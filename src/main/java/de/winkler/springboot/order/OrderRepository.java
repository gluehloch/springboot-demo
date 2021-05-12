package de.winkler.springboot.order;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends CrudRepository<OrderBasketEntity, Long> {

    @Query("SELECT o FROM OrderBasket as o LEFT JOIN FETCH o.orderItems WHERE o.id = :id")
    OrderBasketEntity findOrderBasket(@Param("id") long id);

}
