package de.winkler.springboot.order;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import de.winkler.springboot.user.internal.UserEntity;

public interface OrderBasketRepository extends CrudRepository<OrderBasketEntity, Long> {

    @Query("SELECT o FROM OrderBasket as o LEFT JOIN FETCH o.orderItems WHERE o.id = :id")
    OrderBasketEntity findOrderBasket(@Param("id") long id);

    @Query("SELECT o FROM OrderBasket as o LEFT JOIN FETCH o.orderItems WHERE o.uuid = :uuid")
    Optional<OrderBasketEntity> findOrderBasket(@Param("uuid") UUID uuid);

    @Query("SELECT o FROM OrderBasket as o LEFT JOIN FETCH o.user WHERE o.user = :user AND o.closed = false")
    Optional<OrderBasketEntity> findOpenBasket(@Param("user") UserEntity user);
    
    Page<OrderBasketEntity> findAll(Pageable pageable);

    /*
    public Page<UserDto> findAll(Pageable p) {
    Page<User> page = userRepository.findAll(p); // Page<User>
    return new PageImpl<UserDto>(UserConverter.convert(page.getContent()), p, page.getTotalElements());
}
     */
}
