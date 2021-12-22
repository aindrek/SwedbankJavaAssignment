package com.indrek.swedbank.swedbankjava.purchase;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    List<Purchase> findPurchaseByAcquiredDateBetween(LocalDate start, LocalDate end);
    List<Purchase> findPurchaseByEmployeeIdAndAcquiredDateBetween(String employee, LocalDate start, LocalDate end);
    List<Purchase> findPurchaseByEmployeeId(String employee);
}
