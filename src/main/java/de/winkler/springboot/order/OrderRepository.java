package de.winkler.springboot.order;

import de.winkler.springboot.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends CrudRepository<OrderBasketEntity, Long> {

    @Query("SELECT o FROM OrderBasket as o LEFT JOIN FETCH o.orderItems WHERE o.id = :id")
    OrderBasketEntity findOrderBasket(@Param("id") long id);

    Page<UserEntity> findAll(Pageable pageable);

    /*
    public Page<UserDto> findAll(Pageable p) {
    Page<User> page = userRepository.findAll(p); // Page<User>
    return new PageImpl<UserDto>(UserConverter.convert(page.getContent()), p, page.getTotalElements());
}
     */
}
