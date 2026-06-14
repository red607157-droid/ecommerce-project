package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems i LEFT JOIN FETCH i.product WHERE o.user = :user ORDER BY o.createdAt DESC")
    List<Order> findByUserWithItems(User user);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems i LEFT JOIN FETCH i.product WHERE o.id = :id")
    Optional<Order> findByIdWithItems(Long id);
}
