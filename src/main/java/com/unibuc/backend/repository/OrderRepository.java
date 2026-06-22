package com.unibuc.backend.repository;

import com.unibuc.backend.model.Order;
import com.unibuc.backend.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUserId(Long userId, Pageable pageable);
    Page<Order> findByUserIdAndOrderStatusIn(Long userId, Collection<OrderStatus> statuses, Pageable pageable);
    Page<Order> findByStoreId(Long storeId, Pageable pageable);
    Page<Order> findByStoreIdAndOrderStatusIn(Long storeId, Collection<OrderStatus> statuses, Pageable pageable);
}
