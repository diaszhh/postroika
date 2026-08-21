package com.example.postroika.repository;

import com.example.postroika.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {
    boolean existsByOrderNumber(String orderNumber);
}
