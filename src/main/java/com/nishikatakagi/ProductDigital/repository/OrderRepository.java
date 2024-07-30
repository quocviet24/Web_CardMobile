package com.nishikatakagi.ProductDigital.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.nishikatakagi.ProductDigital.dto.statistic.UserStatDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nishikatakagi.ProductDigital.model.Order;
import com.nishikatakagi.ProductDigital.model.User;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUser(User user);

    Page<Order> findByStatus(String status, Pageable pageable);

    Page<Order> findByUser(User user, Pageable pageable);

    Page<Order> findAll(Pageable pageable);

    @Query("SELECT new com.nishikatakagi.ProductDigital.dto.statistic.UserStatDTO(o.user.id, o.user.username, SUM(o.totalMoney)) " +
            "FROM Order o " +
            "WHERE o.status = 'completed' " +
            "GROUP BY o.user.id, o.user.username " +
            "ORDER BY SUM(o.totalMoney) DESC")
    List<UserStatDTO> findUserWithLargestTotalMoney(Pageable pageable);

    @Query("SELECT new com.nishikatakagi.ProductDigital.dto.statistic.UserStatDTO(o.user.id, o.user.username, SUM(o.totalMoney)) " +
            "FROM Order o " +
            "WHERE o.status = 'completed' " +
            "AND o.orderDate BETWEEN :startDate AND :endDate " +
            "GROUP BY o.user.id, o.user.username " +
            "ORDER BY SUM(o.totalMoney) DESC")
    List<UserStatDTO> findUserWithLargestTotalMoney(Pageable pageable, @Param("startDate") LocalDateTime startDate,
                                                    @Param("endDate") LocalDateTime endDate);

//get count of order in 7 days before
    @Query(value = "SELECT COUNT(o.id) as numberOfOrder, DATE(o.order_date) as day " +
            "FROM orders o " +
            "WHERE o.order_date >= DATE(NOW()) - INTERVAL 7 DAY " +
            "GROUP BY DATE(o.order_date) " +
            "ORDER BY DATE(o.order_date)",
            nativeQuery = true)
    List<Object[]> getOrderCountsByDay();

    @Query("SELECT MONTH(o.orderDate) as month, SUM(o.totalMoney) as totalMoney " +
            "FROM Order o " +
            "WHERE YEAR(o.orderDate) = :year " +
            "AND o.status = 'completed'" +
            "GROUP BY MONTH(o.orderDate)")
    List<Object[]> getTotalMoneyByMonthAndYear(@Param("year") int year);

    Page<Order> findByUserAndStatus(User user, String status, Pageable pageable);
}
